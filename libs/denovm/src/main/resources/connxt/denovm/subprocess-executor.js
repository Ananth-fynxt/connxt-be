// Deno executor (single-run and pool modes)
// Usage:
//   - Single run: deno run ... subprocess-executor.js
//   - Worker mode: deno run ... subprocess-executor.js deno-vm-worker

// Store for logs and HTTP requests
const __STORE = { logs: [], http: [] };

// Safe JSON stringify function
const JSONStringifySafe = (value) => {
	try {
		return { success: true, value: JSON.stringify(value) };
	} catch (error) {
		return { success: false, error: error.message };
	}
};

// Logging function
const __LOG = (level, msg) => {
	const result = JSONStringifySafe(msg);

	const message = result.success ? result.value : "[Error] Invalid message";

	__STORE.logs.push({ level, message, timestamp: new Date().toISOString() });
};

// Logger
const logger = {
	debug: (msg) => __LOG("debug", msg),
	log: (msg) => __LOG("info", msg),
	info: (msg) => __LOG("info", msg),
	warn: (msg) => __LOG("warn", msg),
	error: (msg) => __LOG("error", msg)
};

// HTTP
const fetchHelper = async (url, options = {}) => {
	try {
		const response = await fetch(url, options);

		const { status, headers } = response;

		let data;

		try {
			data = await response.json();
		} catch {
			data = { message: "Invalid JSON response", type: "JSONParseError" };
		}

		if (!response.ok) {
			return { success: false, status, headers, data };
		}

		return { success: true, status, headers, data };
	} catch (error) {
		if (error instanceof TypeError) {
			return {
				success: false,
				status: 422,
				headers: {},
				data: { message: error.message, type: "NetworkError", error }
			};
		}

		return {
			success: false,
			status: 500,
			headers: {},
			data: { message: "Something went wrong", type: "UnknownError", error }
		};
	}
};

const __HTTP = async (url, options) => {
	const startTime = Date.now();

	const result = await fetchHelper(url, options);

	const duration = Date.now() - startTime;

	__STORE.http.push({
		request: {
			url,
			method: options.method || null,
			headers: options.headers || {},
			data: options.body
		},
		response: {
			status: result.status,
			headers: result.headers,
			data: result.data
		},
		duration
	});

	return result;
};

const http = {
	get: (url, options = {}) => __HTTP(url, { ...options, method: "GET" }),
	post: (url, options = {}) => __HTTP(url, { ...options, method: "POST" }),
	put: (url, options = {}) => __HTTP(url, { ...options, method: "PUT" }),
	patch: (url, options = {}) => __HTTP(url, { ...options, method: "PATCH" }),
	delete: (url, options = {}) => __HTTP(url, { ...options, method: "DELETE" })
};

// Response helpers
const response = {
	success: (data) => ({ __type: "success", data }),
	failure: (message, status = 400) => ({ __type: "failure", message, status }),
	redirect: (url, data = {}) => ({ __type: "redirect", url, data })
};

// Encoding
const encoding = {
	base64: { encode: (data) => btoa(data), decode: (data) => atob(data) },
	base64url: {
		encode: (data) =>
			btoa(data).replace(/\+/g, "-").replace(/\//g, "_").replace(/=/g, ""),
		decode: (data) => {
			const padded = data + "=".repeat((4 - (data.length % 4)) % 4);
			return atob(padded.replace(/-/g, "+").replace(/_/g, "/"));
		}
	}
};

const sdk = {
	logger: Object.freeze(logger),
	http: Object.freeze(http),
	encoding: Object.freeze(encoding),
	response: Object.freeze(response)
};

// Meta store
const store = () => {
	const logs = __STORE.logs.reduce((acc, item, idx) => {
		acc[`${idx}`] = item;
		return acc;
	}, {});

	const http = __STORE.http.reduce((acc, item, idx) => {
		acc[`${idx}`] = item;
		return acc;
	}, {});

	return { logs, http };
};

// Error utilities
const cleanStack = (str, file) => {
	if (!str) return null;

	return str
		.split("\n")
		.reduce((acc, line) => {
			const sandbox = line.includes(import.meta.url);
			if (sandbox) return acc;
			const replaced = file
				? line.replace(`file://${file}`, "script.js")
				: line;
			return `${acc}${replaced}\n`;
		}, "")
		.trim();
};

const errEnvelope = (id, err, file) => {
	let error;

	if (err instanceof Error) {
		error = {
			name: err.name,
			message: err.message,
			stack: cleanStack(err.stack, file)
		};
	} else if (Array.isArray(err)) {
		error = { name: "ValidationError", details: err };
	} else if (typeof err === "string") {
		error = { name: "Error", message: err };
	} else {
		error = { name: "UnknownError", message: String(err) };
	}

	return { id: id ?? null, success: false, error, meta: store() };
};

const toResult = async (promise) => {
	try {
		const value = await promise;
		return { success: true, value };
	} catch (error) {
		return { success: false, error };
	}
};

const validateHandlerResult = (result) => {
	if (!result || typeof result !== "object")
		return { success: false, error: "Handler must return an object" };

	if (!result.__type)
		return {
			success: false,
			error: "Handler result must have __type property"
		};

	const validTypes = ["success", "failure", "redirect"];

	if (!validTypes.includes(result.__type))
		return { success: false, error: `Invalid __type: ${result.__type}` };

	return { success: true, value: result };
};

const runHandler = async (ctx, step) => {
	const { id, file } = ctx;

	const mod = await toResult(import(`file://${file}`));

	if (!mod.success) return errEnvelope(id, mod.error, file);

	const handler = mod.value[step];

	if (!handler)
		return errEnvelope(id, `Must export a function named '${step}'`, file);

	if (typeof handler !== "function")
		return errEnvelope(id, "The exported handler is not a function", file);

	if (handler.constructor.name !== "AsyncFunction")
		return errEnvelope(
			id,
			"The exported handler is not an async function",
			file
		);

	const result = await toResult(
		handler(
			{
				id: ctx.id,
				credential: ctx.credential,
				data: ctx.data,
				urls: ctx.urls
			},
			sdk
		)
	);

	if (!result.success) return errEnvelope(id, result.error, file);

	const validated = validateHandlerResult(result.value);

	if (!validated.success) return errEnvelope(id, validated.error, file);

	return { id, success: true, data: validated.value, meta: store() };
};

// Pool loop
const pooledLoop = async () => {
	const decoder = new TextDecoder();

	const reader = globalThis.Deno?.stdin?.readable?.getReader?.();

	if (!reader)
		throw new Error("Deno.stdin.readable.getReader is not available.");

	let buffer = "";

	while (true) {
		const { value, done } = await reader.read();

		if (done) break;

		buffer += decoder.decode(value);

		let idx;

		while ((idx = buffer.indexOf("\n")) !== -1) {
			const line = buffer.slice(0, idx).trim();

			buffer = buffer.slice(idx + 1);

			if (line.length === 0) continue;

			let ctx;

			try {
				ctx = JSON.parse(line);
			} catch (e) {
				console.log(
					JSON.stringify(errEnvelope(null, `Invalid JSON: ${String(e)}`))
				);
				continue;
			}

			if (ctx && ctx.cmd === "shutdown") {
				console.log(
					JSON.stringify({
						id: ctx.id || null,
						success: true,
						data: { cmd: "shutdown" },
						meta: store()
					})
				);
				Deno.exit(0);
			}

			try {
				const out = await runHandler(ctx, ctx.step);

				console.log(JSON.stringify(out));
			} catch (e) {
				console.log(
					JSON.stringify(
						errEnvelope(
							ctx && ctx.id ? ctx.id : null,
							e,
							ctx && ctx.file ? ctx.file : null
						)
					)
				);
			}

			__STORE.logs.length = 0;
			__STORE.http.length = 0;
		}
	}
};

// Single-run
const singleRun = async () => {
	const decoder = new TextDecoder();

	const reader = globalThis.Deno?.stdin?.readable?.getReader?.();

	if (!reader)
		throw new Error(
			"Deno.stdin.readable.getReader is not available in this environment."
		);

	const { value } = await reader.read();

	if (!value) throw new Error("No input received from stdin.");

	const executionContext = JSON.parse(decoder.decode(value));

	const { id, file, credential, data, step, urls } = executionContext;

	try {
		const ctx = { id, file, credential, data, urls };

		const out = await runHandler(ctx, step);

		console.log(JSON.stringify(out));
	} catch (error) {
		console.log(JSON.stringify(errEnvelope(id, error, file)));
	}
};

// Entry
if ((globalThis.Deno?.args || [])[0] === "deno-vm-worker") {
	await pooledLoop();
} else {
	await singleRun();
}