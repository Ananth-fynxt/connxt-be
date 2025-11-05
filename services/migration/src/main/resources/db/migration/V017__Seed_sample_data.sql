-- Insert users first for System, FI and Brand (dummy password: d2ZZa17IF8ZGoVtL)
INSERT INTO users (id, email, password, created_at, updated_at, created_by, updated_by) VALUES
('usr_sys_001', 'system.admin@connxt.com', 'Vlm9Fn9CHZDLlxvP70Hhusr1hBM5igoMtluuCL68RgGmXHhhBNFjXfZJzM4=', NOW(), NOW(), 'system', 'system'),
('usr_sys_002', 'system.operator@connxt.com', 'Vlm9Fn9CHZDLlxvP70Hhusr1hBM5igoMtluuCL68RgGmXHhhBNFjXfZJzM4=', NOW(), NOW(), 'system', 'system'),
('usr_fi_001', 'admin@connxt.com', 'Vlm9Fn9CHZDLlxvP70Hhusr1hBM5igoMtluuCL68RgGmXHhhBNFjXfZJzM4=', NOW(), NOW(), 'system', 'system'),
('usr_fi_002', 'ananth@connxt.com', 'Vlm9Fn9CHZDLlxvP70Hhusr1hBM5igoMtluuCL68RgGmXHhhBNFjXfZJzM4=', NOW(), NOW(), 'system', 'system'),
('usr_fi_003', 'lingesh@connxt.com', 'Vlm9Fn9CHZDLlxvP70Hhusr1hBM5igoMtluuCL68RgGmXHhhBNFjXfZJzM4=', NOW(), NOW(), 'system', 'system'),
('usr_fi_004', 'deepan@connxt.com', 'Vlm9Fn9CHZDLlxvP70Hhusr1hBM5igoMtluuCL68RgGmXHhhBNFjXfZJzM4=', NOW(), NOW(), 'system', 'system'),
('usr_brand_001', 'john.admin@fynxt.com', 'Vlm9Fn9CHZDLlxvP70Hhusr1hBM5igoMtluuCL68RgGmXHhhBNFjXfZJzM4=', NOW(), NOW(), 'system', 'system'),
('usr_brand_002', 'jane.manager@fynxt.com', 'Vlm9Fn9CHZDLlxvP70Hhusr1hBM5igoMtluuCL68RgGmXHhhBNFjXfZJzM4=', NOW(), NOW(), 'system', 'system'),
('usr_brand_003', 'bob.agent@fynxt.com', 'Vlm9Fn9CHZDLlxvP70Hhusr1hBM5igoMtluuCL68RgGmXHhhBNFjXfZJzM4=', NOW(), NOW(), 'system', 'system');

INSERT INTO system_users (id, name, email, user_id, scope, status, created_at, updated_at, created_by, updated_by) VALUES
('sys_001', 'System Administrator', 'system.admin@connxt.com', 'usr_sys_001', 'SYSTEM', 'ACTIVE', NOW(), NOW(), 'system', 'system'),
('sys_002', 'System Operator', 'system.operator@connxt.com', 'usr_sys_002', 'SYSTEM', 'ACTIVE', NOW(), NOW(), 'system', 'system');

INSERT INTO fi (id, name, email, user_id, scope, status, created_at, updated_at, created_by, updated_by) VALUES
('fi_001', 'Connxt Financial Group', 'admin@connxt.com', 'usr_fi_001', 'FI', 'ACTIVE', NOW(), NOW(), 'system', 'system'),
('fi_002', 'Connxt Financial Group', 'ananth@connxt.com', 'usr_fi_002', 'FI', 'ACTIVE', NOW(), NOW(), 'system', 'system'),
('fi_003', 'Connxt Financial Group', 'lingesh@connxt.com', 'usr_fi_003', 'FI', 'ACTIVE', NOW(), NOW(), 'system', 'system'),
('fi_004', 'Connxt Financial Group', 'deepan@connxt.com', 'usr_fi_004', 'FI', 'ACTIVE', NOW(), NOW(), 'system', 'system');

INSERT INTO brands (id, fi_id, currencies, name, email, created_at, updated_at, created_by, updated_by) VALUES
('brn_001', 'fi_001', ARRAY['USD', 'EUR', 'GBP'], 'Connxt Payment Gateway', 'brand@fynxt.com', NOW(), NOW(), 'system', 'system'),
('brn_002', 'fi_002', ARRAY['USD', 'EUR', 'GBP'], 'Connxt Payment Gateway', 'brand@fynxt.com', NOW(), NOW(), 'system', 'system'),
('brn_003', 'fi_003', ARRAY['USD', 'EUR', 'GBP'], 'Connxt Payment Gateway', 'brand@fynxt.com', NOW(), NOW(), 'system', 'system'),
('brn_004', 'fi_004', ARRAY['USD', 'EUR', 'GBP'], 'Connxt Payment Gateway', 'brand@fynxt.com', NOW(), NOW(), 'system', 'system');

INSERT INTO environments (id, name, secret, token, origin, success_redirect_url, failure_redirect_url, brand_id, created_at, updated_at, created_by, updated_by) VALUES
('env_uat_001', 'Connxt UAT Environment', 'sec_connxt_uat_123', 'tok_connxt_uat_456', 'https://salescp.fynxt.com', 'https://salescp.fynxt.com/MyWallet/Success/0', 'https://salescp.fynxt.com/MyWallet/Fail/0', 'brn_001', NOW(), NOW(), 'system', 'system'),
('env_uat_002', 'Connxt UAT Environment', 'sec_connxt_uat_1234', 'tok_connxt_uat_4567', 'https://salescp.fynxt.com', 'https://salescp.fynxt.com/MyWallet/Success/0', 'https://salescp.fynxt.com/MyWallet/Fail/0', 'brn_002', NOW(), NOW(), 'system', 'system'),
('env_uat_003', 'Connxt UAT Environment', 'sec_connxt_uat_1235', 'tok_connxt_uat_4568', 'https://salescp.fynxt.com', 'https://salescp.fynxt.com/MyWallet/Success/0', 'https://salescp.fynxt.com/MyWallet/Fail/0', 'brn_003', NOW(), NOW(), 'system', 'system'),
('env_uat_004', 'Connxt UAT Environment', 'sec_connxt_uat_1236', 'tok_connxt_uat_4569', 'https://salescp.fynxt.com', 'https://salescp.fynxt.com/MyWallet/Success/0', 'https://salescp.fynxt.com/MyWallet/Fail/0', 'brn_004', NOW(), NOW(), 'system', 'system');

INSERT INTO brand_roles (id, brand_id, environment_id, name, permission, created_at, updated_at, created_by, updated_by) VALUES
('role_admin_001', 'brn_001', 'env_uat_001', 'admin', '{
  "brands": {"actions": ["create", "read", "update", "delete"]},
  "brand_users": {"actions": ["create", "read", "update", "delete"]},
  "brand_roles": {"actions": ["create", "read", "update", "delete"]},
  "customers": {"actions": ["create", "read", "update"]},
  "transactions": {"actions": ["read", "update"]},
  "psps": {"actions": ["create", "read", "update"]},
  "psp_groups": {"actions": ["create", "read", "update", "delete"]},
  "routing_rules": {"actions": ["create", "read", "update", "delete"]},
  "conversion_rates": {"actions": ["create", "read", "update", "delete"]},
  "risk_rules": {"actions": ["create", "read", "update", "delete"]},
  "fees": {"actions": ["create", "read", "update", "delete"]},
  "webhooks": {"actions": ["create", "read", "update", "delete"]},
  "webhook_logs": {"actions": ["read"]},
  "auto_approvals": {"actions": ["create", "read", "update", "delete"]},
  "transaction_limits": {"actions": ["create", "read", "update", "delete"]},
  "reports": {"actions": ["read"]},
  "analytics": {"actions": ["read"]},
  "audit_logs": {"actions": ["read"]}
}', NOW(), NOW(), 'system', 'system'),
('role_manager_001', 'brn_001', 'env_uat_001', 'manager', '{
  "brands": {"actions": ["read"]},
  "brand_users": {"actions": ["create", "read", "update"]},
  "brand_roles": {"actions": ["read"]},
  "customers": {"actions": ["create", "read", "update"]},
  "transactions": {"actions": ["read", "update"]},
  "psps": {"actions": ["read", "update"]},
  "psp_groups": {"actions": ["create", "read", "update"]},
  "routing_rules": {"actions": ["create", "read", "update"]},
  "conversion_rates": {"actions": ["read", "update"]},
  "risk_rules": {"actions": ["read", "update"]},
  "fees": {"actions": ["read", "update"]},
  "webhooks": {"actions": ["read", "update"]},
  "webhook_logs": {"actions": ["read"]},
  "auto_approvals": {"actions": ["read", "update"]},
  "transaction_limits": {"actions": ["read", "update"]},
  "reports": {"actions": ["read"]},
  "analytics": {"actions": ["read"]},
  "audit_logs": {"actions": ["read"]}
}', NOW(), NOW(), 'system', 'system'),
('role_agent_001', 'brn_001', 'env_uat_001', 'agent', '{
  "brands": {"actions": ["read"]},
  "brand_users": {"actions": ["read"]},
  "brand_roles": {"actions": ["read"]},
  "customers": {"actions": ["create", "read", "update"]},
  "transactions": {"actions": ["read"]},
  "psps": {"actions": ["read"]},
  "psp_groups": {"actions": ["read"]},
  "routing_rules": {"actions": ["read"]},
  "conversion_rates": {"actions": ["read"]},
  "risk_rules": {"actions": ["read"]},
  "fees": {"actions": ["read"]},
  "webhooks": {"actions": ["read"]},
  "webhook_logs": {"actions": ["read"]},
  "auto_approvals": {"actions": ["read"]},
  "transaction_limits": {"actions": ["read"]},
  "reports": {"actions": ["read"]},
  "analytics": {"actions": ["read"]},
  "audit_logs": {"actions": ["read"]}
}', NOW(), NOW(), 'system', 'system');

INSERT INTO brand_users (id, brand_id, environment_id, brand_role_id, name, email, user_id, scope, status, created_at, updated_at, created_by, updated_by) VALUES
('user_admin_001', 'brn_001', 'env_uat_001', 'role_admin_001', 'John Admin', 'john.admin@connxt.com', 'usr_brand_001', 'BRAND', 'ACTIVE', NOW(), NOW(), 'system', 'system'),
('user_manager_001', 'brn_001', 'env_uat_001', 'role_manager_001', 'Jane Manager', 'jane.manager@connxt.com', 'usr_brand_002', 'BRAND', 'ACTIVE', NOW(), NOW(), 'system', 'system'),
('user_agent_001', 'brn_001', 'env_uat_001', 'role_agent_001', 'Bob Agent', 'bob.agent@connxt.com', 'usr_brand_003', 'BRAND', 'ACTIVE', NOW(), NOW(), 'system', 'system');

INSERT INTO brand_customer (id, brand_id, environment_id, name, email, tag, account_type, country, currencies, customer_meta, scope, status, created_at, updated_at, created_by, updated_by) VALUES
('cust_001', 'brn_001', 'env_uat_001', 'Premium Customer A', 'customer.a@example.com', 'premium', 'individual', 'US', ARRAY['USD', 'EUR'], '{"tier": "gold", "kyc_status": "verified"}', 'EXTERNAL', 'ACTIVE', NOW(), NOW(), 'system', 'system');

INSERT INTO flow_types (id, name, created_at, updated_at, created_by, updated_by) VALUES
('ftp_psp_001', 'PSP', NOW(), NOW(), 'system', 'system');

INSERT INTO flow_actions (id, name, steps, flow_type_id, input_schema, output_schema, created_at, updated_at, created_by, updated_by) VALUES
('fat_deposit_001', 'Deposit', ARRAY['webhook', 'initiate', 'redirect'], 'ftp_psp_001',
 '{"webhook": {"type": "object", "properties": {}, "additionalProperties": true}, "initiate": {"type": "object", "required": ["order"], "properties": {"order": {"type": "object", "required": ["id", "money", "crmData", "timestamp"], "properties": {"id": {"type": "string"}, "money": {"type": "object", "required": ["amount", "currency"], "properties": {"amount": {"type": "number"}, "currency": {"type": "string"}}, "additionalProperties": false}, "crmData": {"type": "object", "required": ["amount", "currency", "conversionRate"], "properties": {"amount": {"type": "number"}, "currency": {"type": "string"}, "conversionRate": {"type": "number"}}, "additionalProperties": false}, "timestamp": {"type": "string"}}, "additionalProperties": false}, "customer": {"type": "object", "properties": {"id": {"type": "string"}, "dob": {"type": "string"}, "email": {"type": "string", "format": "email"}, "phone": {"type": "object", "properties": {"countryCode": {"type": "string"}, "phoneNumber": {"type": "string"}}, "additionalProperties": false}, "address": {"type": "object", "properties": {"city": {"type": "string"}, "line1": {"type": "string"}, "state": {"type": "string"}, "country": {"type": "string"}, "zipCode": {"type": "string"}}, "additionalProperties": false}, "lastName": {"type": "string"}, "firstName": {"type": "string"}}, "additionalProperties": false}, "language": {"type": "string"}, "serverDelay": {"type": "number", "default": 0, "minimum": 0}}, "additionalProperties": false}, "redirect": {"type": "object", "properties": {}, "additionalProperties": true}}',
 '{"webhook": {"type": "string"}, "initiate": {"type": "object", "required": ["navigation", "orderId", "userId", "invoiceId", "isRefund"], "properties": {"userId": {"type": "string"}, "orderId": {"type": "string"}, "isRefund": {"type": "boolean", "description": "Flag indicating if this transaction is a refund operation"}, "invoiceId": {"type": "string", "description": "The invoice ID returned from the payment service provider"}, "navigation": {"type": "object", "allOf": [{"if": {"properties": {"type": {"const": "redirect"}}}, "then": {"properties": {"contentType": {"const": "url"}}}}, {"if": {"properties": {"contentType": {"const": "url"}}}, "then": {"properties": {"value": {"type": "string", "format": "uri", "pattern": "^https?://"}}}}], "required": ["type", "value", "contentType"], "properties": {"type": {"enum": ["redirect", "iframe"]}, "value": {"type": "string"}, "contentType": {"enum": ["url", "html"]}}, "additionalProperties": false}}, "additionalProperties": false}, "redirect": {"type": "object", "required": ["orderId", "timestamp", "status", "message"], "properties": {"status": {"type": "string"}, "message": {"type": "string"}, "orderId": {"type": "string"}, "timestamp": {"type": "string"}}, "additionalProperties": false}}',
 NOW(), NOW(), 'system', 'system'),
 ('fat_withdraw_001', 'Withdraw', ARRAY['webhook', 'initiate', 'redirect'], 'ftp_psp_001',
 '{"webhook": {"type": "object", "properties": {}, "additionalProperties": true}, "initiate": {"type": "object", "required": ["order"], "properties": {"order": {"type": "object", "required": ["id", "money", "crmData", "timestamp"], "properties": {"id": {"type": "string"}, "money": {"type": "object", "required": ["amount", "currency"], "properties": {"amount": {"type": "number"}, "currency": {"type": "string"}}, "additionalProperties": false}, "crmData": {"type": "object", "required": ["amount", "currency", "conversionRate"], "properties": {"amount": {"type": "number"}, "currency": {"type": "string"}, "conversionRate": {"type": "number"}}, "additionalProperties": false}, "timestamp": {"type": "string"}}, "additionalProperties": false}, "customer": {"type": "object", "properties": {"id": {"type": "string"}, "dob": {"type": "string"}, "email": {"type": "string", "format": "email"}, "phone": {"type": "object", "properties": {"countryCode": {"type": "string"}, "phoneNumber": {"type": "string"}}, "additionalProperties": false}, "address": {"type": "object", "properties": {"city": {"type": "string"}, "line1": {"type": "string"}, "state": {"type": "string"}, "country": {"type": "string"}, "zipCode": {"type": "string"}}, "additionalProperties": false}, "lastName": {"type": "string"}, "firstName": {"type": "string"}}, "additionalProperties": false}, "language": {"type": "string"}, "serverDelay": {"type": "number", "default": 0, "minimum": 0}}, "additionalProperties": false}, "redirect": {"type": "object", "properties": {}, "additionalProperties": true}}',
 '{"webhook": {"type": "string"}, "initiate": {"type": "object", "required": ["navigation", "orderId", "userId"], "properties": {"userId": {"type": "string"}, "orderId": {"type": "string"}, "navigation": {"type": "object", "allOf": [{"if": {"properties": {"type": {"const": "redirect"}}}, "then": {"properties": {"contentType": {"const": "url"}}}}, {"if": {"properties": {"contentType": {"const": "url"}}}, "then": {"properties": {"value": {"type": "string", "format": "uri", "pattern": "^https?://"}}}}], "required": ["type", "value", "contentType"], "properties": {"type": {"enum": ["redirect", "iframe"]}, "value": {"type": "string"}, "contentType": {"enum": ["url", "html"]}}, "additionalProperties": false}}, "additionalProperties": false}, "redirect": {"type": "object", "required": ["orderId", "timestamp", "status", "message"], "properties": {"status": {"type": "string"}, "message": {"type": "string"}, "orderId": {"type": "string"}, "timestamp": {"type": "string"}}, "additionalProperties": false}}',
 NOW(), NOW(), 'system', 'system');

INSERT INTO flow_targets (id, name, logo, status, credential_schema, input_schema, currencies, countries, payment_methods, flow_type_id, created_at, updated_at, created_by, updated_by) VALUES
('ftg_enovipay_001', 'Enovipay', 'https://logos.s3.amazonaws.com/enovipay.png', 'ENABLED',
 '{"type": "object", "required": ["brandId", "token"], "properties": {"token": {"type": "string"}, "brandId": {"type": "string"}}}',
 '{"DEPOSIT": {"type": "object", "properties": {"currency": {"type": "array", "items": {"enum": ["USD"], "type": "string"}}, "conversion": {"type": "boolean", "default": true}}, "additionalProperties": false}, "WITHDRAW": {"type": "object", "properties": {"currency": {"type": "array", "items": {"enum": ["USD"], "type": "string"}}, "conversion": {"type": "boolean", "default": true}}, "additionalProperties": false}}',
 ARRAY['USD'], ARRAY['US', 'CA', 'GB'], ARRAY['credit_card', 'debit_card', 'digital_wallet'], 'ftp_psp_001', NOW(), NOW(), 'system', 'system'),
('ftg_sticpay_001', 'SticPay', 'https://logos.s3.amazonaws.com/sticpay.png', 'ENABLED',
 '{"type": "object", "required": ["merchantEmail", "apiKey"], "properties": {"merchantEmail": {"type": "string", "format": "email"}, "apiKey": {"type": "string"}}}',
 '{"DEPOSIT": {"type": "object", "properties": {"currency": {"type": "array", "items": {"enum": ["USD"], "type": "string"}}, "conversion": {"type": "boolean", "default": true}}, "additionalProperties": false}, "WITHDRAW": {"type": "object", "required": ["customerEmail"], "properties": {"currency": {"type": "array", "items": {"enum": ["USD"], "type": "string"}}, "conversion": {"type": "boolean", "default": true}, "customerEmail": {"type": "string", "format": "email"}}, "additionalProperties": false}}',
 ARRAY['USD'], ARRAY['US', 'CA', 'GB', 'EU'], ARRAY['credit_card', 'debit_card', 'digital_wallet', 'bank_transfer'], 'ftp_psp_001', NOW(), NOW(), 'system', 'system'),
('ftg_korapay_001', 'Korapay', 'https://logos.s3.amazonaws.com/korapay.png', 'ENABLED',
 '{"type": "object", "required": ["publicKey", "secretKey", "encryptionKey"], "properties": {"publicKey": {"type": "string"}, "secretKey": {"type": "string"}, "encryptionKey": {"type": "string"}}}',
 '{"REFUND": {}, "DEPOSIT": {"type": "object", "properties": {"currency": {"type": "array", "items": {"enum": ["NGN", "GHS"], "type": "string"}}, "conversion": {"type": "boolean", "default": true}}, "additionalProperties": false}, "WITHDRAW": {"type": "object", "required": ["bankCode", "accountNumber"], "properties": {"bankCode": {"type": "string"}, "currency": {"type": "array", "items": {"enum": ["NGN", "GHS"], "type": "string"}}, "conversion": {"type": "boolean", "default": true}, "accountNumber": {"type": "string"}, "ExternalBankAccount": {"type": "string"}}, "additionalProperties": false}}',
 ARRAY['NGN', 'GHS'], ARRAY['NG', 'GH'], ARRAY['bank_transfer', 'mobile_money', 'card'], 'ftp_psp_001', NOW(), NOW(), 'system', 'system'),
('ftg_bridgerpay_001', 'BridgerPay', 'https://logos.s3.amazonaws.com/bridgerpay.png', 'ENABLED',
 '{"type": "object", "required": ["username", "password", "apiKey", "cashierKey"], "properties": {"apiKey": {"type": "string"}, "password": {"type": "string"}, "username": {"type": "string"}, "cashierKey": {"type": "string"}}}',
 '{"DEPOSIT": {"type": "object", "properties": {"currency": {"type": "array", "items": {"enum": ["USD"], "type": "string"}}, "conversion": {"type": "boolean", "default": true}}, "additionalProperties": false}, "WITHDRAW": {"type": "object", "required": ["provider", "payoutMethodType"], "properties": {"currency": {"type": "array", "items": {"enum": ["USD"], "type": "string"}}, "provider": {"type": "string"}, "conversion": {"type": "boolean", "default": true}, "payoutMethodType": {"enum": ["credit_card", "crypto", "ewallet", "bank_account"], "type": "string"}}, "additionalProperties": false}}',
 ARRAY['USD'], ARRAY['US', 'CA', 'GB', 'EU'], ARRAY['credit_card', 'debit_card', 'digital_wallet', 'crypto', 'bank_transfer'], 'ftp_psp_001', NOW(), NOW(), 'system', 'system');

INSERT INTO flow_definitions (id, flow_action_id, flow_target_id, description, code, created_at, updated_at, created_by, updated_by) VALUES
('fld_deposit_enovipay_001', 'fat_deposit_001', 'ftg_enovipay_001', 'SYS-PSP-DEPOSIT-ENOVIPAY', 'export const initiate = async (ctx, sdk) => {
	const {
		id: tnxId,
		credential,
		data,
		urls: { server }
	} = ctx;

	const { http, logger } = sdk;

	const { token, brandId } = credential;

	const {
		body: {
			order: {
				id: externalOrderId,
				money: { amount, currency }
			},
			customer: { email, id: userId }
		}
	} = data;

	const payload = {
		client: {
			email
		},
		purchase: {
			currency,
			products: [
				{
					name: "xxxxx",
					price: amount
				}
			]
		},
		brand_id: brandId,
		success_redirect: `${server.redirect}?orderId=${tnxId}`,
		failure_redirect: `${server.redirect}?orderId=${tnxId}`,
		cancel_redirect: `${server.redirect}?orderId=${tnxId}`,
		success_callback: `${server.webhook}?orderId=${tnxId}`
	};

	const headers = {
		"Content-Type": "application/json",
		Authorization: `Bearer ${token}`
	};

	const response = await http.post(
		"https://gate.enovipay.com/api/v1/purchases/",
		{
			headers,
			body: JSON.stringify(payload)
		}
	);

	if (!response.success) {
		logger.error(
			`Enovipay Deposit Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		if (response.data?.errors?.length) {
			const customErrorMessage =
				`${response.data.errors[0].field} ${response.data.errors[0].defaultMessage}` ||
				"Something went wrong";

			return sdk.response.failure(customErrorMessage, response.status);
		}

		return sdk.response.failure(
			response.data.message || response.data.error,
			response.status
		);
	}

	if (!response.data?.checkout_url) {
		logger.error(
			`Enovipay Deposit Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Redirect URL", response.status);
	}

	logger.log(`Enovipay Deposit Initiate Script: ${JSON.stringify(response)}`);

	return sdk.response.success({
		navigation: {
			type: "redirect",
			contentType: "url",
			value: response.data.checkout_url
		},
		status: "success",
		orderId: tnxId,
		userId
	});
};

export const redirect = async (ctx, sdk) => {
	const {
		data,
		urls: { server }
	} = ctx;

	const { orderId } = data.query;

	return sdk.response.redirect(server.successRedirectUrl, {
		status: "success",
		orderId,
		timestamp: new Date().getTime().toString()
	});
};

export const webhook = async (ctx, sdk) => {
	const { data } = ctx;

	const { logger } = sdk;

	const { id: pspTransactionId, order_id, status, payment = {} } = data.body;

	const { orderId, userId, userEmail } = data.query;

	const { amount, currency, description: comment = "", paid_on } = payment;

	if (!amount) {
		logger.error(
			`Enovipay Deposit Webhook Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Order Amount", 400);
	}

	if (!currency) {
		logger.error(
			`Enovipay Deposit Webhook Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Currency", 400);
	}

	const crmData = {
		type: "Deposit",
		status,
		comment,
		data,
		order: {
			id: order_id || orderId,
			amount: Number.parseFloat(amount),
			currency,
			timestamp: paid_on ? String(paid_on * 1000) : String(Date.now())
		},
		user: {
			id: userId,
			email: userEmail
		},
		psp: {
			transactionId: pspTransactionId,
			conversionRate: null,
		}
	};
	
	logger.log(`Enovipay Deposit Webhook Response: ${JSON.stringify(crmData)}`);

	return sdk.response.success({
		navigation: {
			type: "object",
			contentType: "object",
			value: crmData
		},
		status: "success",
		orderId,
		userId
	});
};
', NOW(), NOW(), 'system', 'system'),
('fld_withdraw_enovipay_001', 'fat_withdraw_001', 'ftg_enovipay_001', 'SYS-PSP-WITHDRAW-ENOVIPAY', 'export const initiate = async (ctx, sdk) => {
	const { id: tnxId, credential, data, urls: { server } } = ctx;

	const { http, logger } = sdk;

	const { token, brandId } = credential;

	const {
		body: {
			order: {
				id: externalOrderId,
				money: { amount, currency }
			},
			customer: { email, id: userId }
		}
	} = data;

	const payload = {
		client: {
			email
		},
		payment: {
			amount,
			currency
		},
		brand_id: brandId
	};

	const headers = {
		"Content-Type": "application/json",
		Authorization: `Bearer ${token}`
	};

	const response = await http.post(
		"https://gate.enovipay.com/api/v1/payouts/",
		{
			headers,
			body: JSON.stringify(payload)
		}
	);

	if (!response.success) {
		logger.error(
			`Enovipay Withdraw Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		if (response.data?.errors?.length) {
			const customErrorMessage =
				`${response.data.errors[0].field} ${response.data.errors[0].defaultMessage}` ||
				"Something went wrong";

			return sdk.response.failure(customErrorMessage, response.status);
		}

		return sdk.response.failure(
			response.data.message || response.data.error,
			response.status
		);
	}

	logger.log(`Enovipay Withdraw Initiate Script: ${JSON.stringify(response)}`);

	return sdk.response.success({
		navigation: {
			type: "redirect",
			contentType: "url",
			value: response?.data?.checkout_url || server.successRedirectUrl
		},
		status: "success",
		orderId: tnxId,
		userId
	});
};
', NOW(), NOW(), 'system', 'system'),
('fld_deposit_sticpay_001', 'fat_deposit_001', 'ftg_sticpay_001', 'SYS-PSP-DEPOSIT-STICPAY', 'import { crypto } from "jsr:@std/crypto";

const generateSign = async (signString) => {
	const encoder = new TextEncoder();
	const data = encoder.encode(signString);

	const buffer = await crypto.subtle.digest("MD5", data);

	const hashArray = Array.from(new Uint8Array(buffer));
	const hashHex = hashArray
		.map((b) => b.toString(16).padStart(2, "0"))
		.join("");

	return hashHex;
};

const formatTimestamp = (ts) => {
	return new Date(Number(ts)).toISOString().replace("T", " ").substring(0, 19);
};

export const initiate = async (ctx, sdk) => {
	const {
		id: tnxId,
		credential,
		data,
		urls: { server }
	} = ctx;

	const { logger, http } = sdk;

	const { merchantEmail, apiKey } = credential;

	const {
		body: {
			order: {
				id: externalOrderId,
				money: { amount, currency },
				timestamp
			},
			customer: { id: userId }
		}
	} = data;

	const orderTime = formatTimestamp(timestamp);

	const signString = `merchant_email=${merchantEmail}&order_no=${tnxId}&order_time=${orderTime}&order_amount=${amount}&order_currency=${currency}&key=${apiKey}`;

	const sign = await generateSign(signString);

	const payload = {
		sign,
		merchant_email: merchantEmail,
		order_no: tnxId,
		order_time: orderTime,
		order_amount: amount,
		order_currency: currency,
		interface_version: "sandbox",
		success_url: `${server.redirect}?orderId=${tnxId}`,
		failure_url: `${server.redirect}?orderId=${tnxId}`,
		referrer_url: `${server.redirect}?orderId=${tnxId}`,
		callback_url: `${server.webhook}?orderId=${tnxId}`
	};

	const headers = {
		"Content-Type": "application/json"
	};

	const response = await http.post("https://api.sticpay.com/rest_pay/pay", {
		headers,
		body: JSON.stringify(payload)
	});

	if (!response.data.success) {
		logger.error(
			`SticPay Deposit Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		if (response.data?.errors?.length) {
			const customErrorMessage =
				`${response.data.errors[0].field} ${response.data.errors[0].defaultMessage}` ||
				"Something went wrong";

			return sdk.response.failure(customErrorMessage, response.status);
		}

		return sdk.response.failure(
			response.data.message || response.data.error,
			response.status
		);
	}

	logger.log(`SticPay Deposit Initiate Script: ${JSON.stringify(response)}`);

	if (!response.data?.link) {
		logger.error(
			`SticPay Deposit Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Redirect URL", response.status);
	}

	return sdk.response.success({
		navigation: {
			type: "redirect",
			contentType: "url",
			value: response.data.link
		},
        status: "success",
		orderId: tnxId,
		userId
	});
};

export const redirect = async (ctx, sdk) => {
	const {
		data,
		urls: { server }
	} = ctx;

	const { callback } = data.body;

	const { message, parameters } = JSON.parse(callback);

	const { order_no, order_time } = JSON.parse(parameters);

	const statusMessage = JSON.parse(message);

	const redirectUrl = statusMessage === "success" ? server.successRedirectUrl : server.failureRedirectUrl;

	return sdk.response.redirect(redirectUrl, {
		status: "success",
		orderId: order_no,
		timestamp: new Date(order_time).getTime().toString()
	});
};

export const webhook = async (ctx, sdk) => {
	const { data } = ctx;

	const { logger } = sdk;

	const {
		callback: {
			code,
			message,
			parameters: {
				order_no,
				order_amount,
				order_currency,
				fx_rate,
				transaction_details: {
					transaction_code,
					transaction_time,
					status,
					comment,
					customer_email,
					customer_id
				}
			}
		}
	} = data.body;

	const { orderId, userId, userEmail } = data.query;

	if (!order_amount) {
		logger.error(
			`SticPay Deposit Webhook Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Order Amount", 400);
	}

	if (!order_currency) {
		logger.error(
			`SticPay Deposit Webhook Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Order Currency", 400);
	}

	if (!transaction_code) {
		logger.error(
			`SticPay Deposit Webhook Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid PSP Transaction ID", 400);
	}

	const crmData = {
		type: "Deposit",
		order: {
			id: order_no || orderId,
			amount: Number.parseFloat(order_amount),
			currency: order_currency,
			timestamp: transaction_time
				? new Date(transaction_time).getTime().toString()
				: Date.now().toString()
		},
		user: {
			id: customer_id || userId,
			email: customer_email || userEmail
		},
		psp: {
			transactionId: transaction_code,
			conversionRate: Number.parseFloat(fx_rate) || null,
			status: {
				code: status,
				comment: comment
			},
			responseLog: {
				code: code,
				comment: message
			}
		}
	};

	logger.log(`SticPay Deposit Webhook Response: ${JSON.stringify(crmData)}`);

    return sdk.response.success({
		navigation: {
			type: "object",
			contentType: "object",
			value: crmData
		},
		status: "success",
		orderId,
		userId
	});
};
', NOW(), NOW(), 'system', 'system'),
('fld_withdraw_sticpay_001', 'fat_withdraw_001', 'ftg_sticpay_001', 'SYS-PSP-WITHDRAW-STICPAY', 'import { crypto } from "jsr:@std/crypto";

const generateSign = async (signString)=> {
	const encoder = new TextEncoder();
	const data = encoder.encode(signString);

	const buffer = await crypto.subtle.digest("MD5", data);

	const hashArray = Array.from(new Uint8Array(buffer));
	const hashHex = hashArray
		.map((b) => b.toString(16).padStart(2, "0"))
		.join("");
	return hashHex;
};

export const initiate = async (ctx, sdk) => {
	const {
		id: tnxId,
		credential,
		data,
		urls: { server }
	} = ctx;

	const { logger, http } = sdk;

	const { merchantEmail, apiKey } = credential;

	const {
		body: {
			order: {
				id: externalOrderId,
				money: { amount, currency }
			},
			customer: { id: userId },
			customAttributes
		}
	} = data;

	if (!customAttributes.customerEmail) {
		logger.error(
			`SticPay Withdraw Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Sticpay customer email is not valid", 400);
	}

	const customerEmail = customAttributes.customerEmail;

	const signString = `merchant=${merchantEmail}&customer=${customerEmail}&amount=${amount}&currency_code=${currency}&order_id=${tnxId}&interface_version=sandbox&key=${apiKey}`;

	const sign = await generateSign(signString);

	const payload = {
		sign,
		interface_version: "sandbox",
		order_id: tnxId,
		currency_code: currency,
		amount,
		customer: customerEmail,
		merchant: merchantEmail
	};

	const headers = {
		"Content-Type": "application/json"
	};

	const response = await http.post(
		"https://api.sticpay.com/rest_withdraw/withdraw",
		{
			headers,
			body: JSON.stringify(payload)
		}
	);

	if (!response.data.success) {
		logger.error(
			`SticPay Withdraw Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		if (response.data?.errors?.length) {
			const customErrorMessage =
				`${response.data.errors[0].field} ${response.data.errors[0].defaultMessage}` ||
				"Something went wrong";

			return sdk.response.failure(customErrorMessage, response.status);
		}

		return sdk.response.failure(
			response.data.message || response.data.error,
			response.status
		);
	}

	return sdk.response.success({
		navigation: {
			type: "redirect",
			contentType: "url",
			value: response.data.link || server.successRedirectUrl
		},
		status: "success",
		orderId: tnxId,
		userId
	});
};
', NOW(), NOW(), 'system', 'system'),
('fld_deposit_bridgerpay_001', 'fat_deposit_001', 'ftg_bridgerpay_001', 'SYS-PSP-DEPOSIT-BRIDGERPAY', 'const generateAccessToken = async (http, username, password) => {
	const headers = {
		"Content-Type": "application/json",
		Accept: "application/json"
	};

	const payload = {
		user_name: username,
		password
	};

	const response = await http.post("https://api.bridgerpay.com/v2/auth/login", {
		headers,
		body: JSON.stringify(payload)
	});

	return response?.data?.result?.access_token?.token || null;
};

export const initiate = async (ctx, sdk) => {
	const { credential, data, id: tnxId } = ctx;

	const { http, logger } = sdk;

	const { username, password, apiKey, cashierKey } = credential;

	const { order, customer, language } = data.body;

	const {
		id: externalOrderId,
		money: { amount, currency },
		crmData: {
			amount: crmAmount,
			currency: crmCurrency,
			conversionRate: crmConversionRate
		}
	} = order;

	const {
		firstName,
		lastName,
		email,
		phone: { phoneNumber },
		id: userId,
		address: { line1, city, state, zipCode, country }
	} = customer;

	const checkoutUrl =
		"https://checkout.bridgerpay.com/v2/?cashierKey={{cashier_key}}&cashierToken={{cashier_token}}";

	const accessToken = await generateAccessToken(http, username, password);

	if (!accessToken) {
		logger.error(
			`BridgerPay Deposit Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Access Token", 400);
	}

	const serverInitPayload = {
		cashier_key: cashierKey,
		order_id: tnxId,
		amount: amount.toString(),
		currency,
		country,
		first_name: firstName,
		last_name: lastName,
		email,
		language,
		state,
		address: line1,
		city,
		zip_code: zipCode,
		phone: phoneNumber,
		amount_lock: true,
		currency_lock: true,
		custom_data: {
			tnxId,
			crmAmount,
			crmCurrency,
			crmConversionRate
		}
	};

	const response = await http.post(
		`https://api.bridgerpay.com/v2/cashier/session/create/${apiKey}`,
		{
			headers: {
				"Content-Type": "application/json",
				Authorization: `Bearer ${accessToken}`
			},
			body: JSON.stringify(serverInitPayload)
		}
	);

	if (!response.success) {
		logger.error(
			`BridgerPay Deposit Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		if (response.data?.errors?.length) {
			const customErrorMessage =
				`${response.data.errors[0].field} ${response.data.errors[0].defaultMessage}` ||
				"Something went wrong";

			return sdk.response.failure(customErrorMessage, response.status);
		}

		return sdk.response.failure(
			response.data.message || response.data.error,
			response.status
		);
	}

	const cashierToken = response?.data?.result?.cashier_token || null;

	logger.log(`BridgerPay Deposit Initiate Script: ${JSON.stringify(response)}`);

	if (!cashierToken) {
		logger.error(
			`BridgerPay Deposit Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Cashier Token", 400);
	}

	const iframeUrl = checkoutUrl
		.replace("{{cashier_key}}", cashierKey)
		.replace("{{cashier_token}}", cashierToken);

	return sdk.response.success({
		navigation: {
			type: "iframe",
			contentType: "url",
			value: iframeUrl
		},
		status: "success",
		orderId: tnxId,
		userId
	});
};

export const redirect = async (ctx, sdk) => {
	const {
		data,
		urls: { server }
	} = ctx;

	const { orderId, status } = data.query;

	return sdk.response.redirect(server.successRedirectUrl, {
		status: "success",
		orderId,
		timestamp: Date.now().toString()
	});
};

export const webhook = async (ctx, sdk) => {
	const { data } = ctx;

	const { logger } = sdk;

	const {
		webhook: { type: eventType },
		data: {
			order_id: orderId,
			charge: {
				order_id: orderIdFromCharge,
				attributes: {
					amount: orderAmount,
					currency,
					created_at: txTime,
					source: { email: customer_email, customer_id }
				},
				id: pspTransactionId
			}
		},
		meta: { server_time: serverTime, server_timezone: serverTimezone }
	} = data.body;

	const { orderId: queryOrderId, userId, userEmail } = data.query;

	if (!orderAmount) {
		logger.error(
			`BridgerPay Deposit Webhook Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Order Amount", 400);
	}

	if (!currency) {
		logger.error(
			`BridgerPay Deposit Webhook Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Currency", 400);
	}

	const crmData = {
		type: "Deposit",
		order: {
			id: orderId || orderIdFromCharge || queryOrderId,
			amount: Number.parseFloat(orderAmount),
			currency: currency,
			timestamp: txTime
				? new Date(txTime * 1000).getTime().toString()
				: Date.now().toString()
		},
		user: {
			id: customer_id || userId,
			email: customer_email || userEmail
		},
		psp: {
			transactionId: pspTransactionId,
			conversionRate: null,
			status: {
				code: eventType,
				comment: "Status received"
			},
			responseLog: {
				code: serverTime,
				comment: `Timezone: ${serverTimezone || "unknown"}`
			}
		}
	};

	logger.log(`BridgerPay Deposit Webhook Response: ${JSON.stringify(crmData)}`);

	return sdk.response.success({
		navigation: {
			type: "object",
			contentType: "object",
			value: crmData
		},
		status: "success",
		orderId: orderId || orderIdFromCharge || queryOrderId,
		userId: customer_id || userId
	});
};
', NOW(), NOW(), 'system', 'system');

INSERT INTO wallet (id, brand_id, environment_id, brand_customer_id, name, currency, balance, available_balance, hold_balance, created_at, updated_at, created_by, updated_by) VALUES
('wallet_001', 'brn_001', 'env_uat_001', 'cust_001', 'USD Wallet', 'USD', 10000.00, 9500.00, 500.00, NOW(), NOW(), 'system', 'system');

INSERT INTO psps (id, name, description, logo, credential, timeout, block_vpn_access, block_data_center_access, failure_rate, failure_rate_threshold, failure_rate_duration_minutes, ip_address, brand_id, environment_id, flow_target_id, status, created_at, updated_at, created_by, updated_by) VALUES
('psp_001', 'Enovipay UAT', 'Enovipay payment gateway for UAT', 'https://logos.s3.amazonaws.com/enovipay.png', '{"brandId": "dhfAB36xGnSRiMQWUJVgnFdeTMkwpdABZYJV32DTKwVTjmAFThe/9fAxXGsMTHPkebsgJcv7O+BXFjgC9oNCaQ==", "token": "f8PJRtNYn8YnIM8ijbR8jqIuL9MvXAlRoX7pnaRmg1cGlAiA/oJgunvorpeVw4osYgNYrCrQy2YxNNQ/SQEHQYLxjccfUXbsG1tnBAaGl1Al3C8d5rcOa/xMeFOpYtOzCcF6iWIk/Ni/U/x8tREL5n86nxY="}', 300, false, false, false, 0, 60, ARRAY['54.187.174.169', '54.187.205.235'], 'brn_001', 'env_uat_001', 'ftg_enovipay_001', 'ENABLED', NOW(), NOW(), 'system', 'system'),
('psp_002', 'SticPay UAT', 'SticPay payment gateway for UAT', 'https://logos.s3.amazonaws.com/sticpay.png', '{"merchantEmail": "OBk2z6xg57zYcfu/SgHR5izad6/U73dkdurHeg2Qzx2MOqdBiIJYtMGDj4iYCsRwOjjRExBR5NY=", "apiKey": "8+d2MKs+ldE/V1CJ2f6bbJfDA4mXzDJKDLzjkXyuGujF9zM3xuFci7LRt3lRhjG2xB4Rjyczpmt7uqx5c3JeqTMQW8tr3bspIAiYPzD/yo8pg6qbQ5GfrREONPc="}', 300, false, false, false, 0, 60, ARRAY['192.168.1.100', '192.168.1.101'], 'brn_001', 'env_uat_001', 'ftg_sticpay_001', 'ENABLED', NOW(), NOW(), 'system', 'system'),
('psp_003', 'Korapay UAT', 'Korapay payment gateway for UAT', 'https://logos.s3.amazonaws.com/korapay.png', '{"publicKey": "o0JkcMBlzGkFSSxnHaYec4+FHGQeIPc8zoMegIlWdQxo+6hnwwC2/pn/mfeem4Ne9bayisnzM5NeroXBrPl7x2RmfmmomTej1Px7wQ==", "secretKey": "65Nxau6lUiphbJvwrAWihGmbCtTX/5WREvtJ7zu/um3C9RIONVCf9rQlsAjnzWv6qPbGZTlAq7EsvZ52Cv8epcNbJOTs+VDajnQywQ==", "encryptionKey": "d/aOCKhymtBRHiiSaDge2XTNJGv9caXNPgKTh1cHHM1Q+eLzzF4/9RYzXYMRu3LA5Ya7Q/FaCQzVIPFW"}', 300, false, false, false, 0, 60, ARRAY['52.31.139.75', '52.49.173.169'], 'brn_001', 'env_uat_001', 'ftg_korapay_001', 'ENABLED', NOW(), NOW(), 'system', 'system'),
('psp_004', 'BridgerPay UAT', 'BridgerPay payment gateway for UAT', 'https://logos.s3.amazonaws.com/bridgerpay.png', '{"username": "dhfAB36xGnSRiMQWUJVgnFdeTMkwpdABZYJV32DTKwVTjmAFThe/9fAxXGsMTHPkebsgJcv7O+BXFjgC9oNCaQ==", "password": "f8PJRtNYn8YnIM8ijbR8jqIuL9MvXAlRoX7pnaRmg1cGlAiA/oJgunvorpeVw4osYgNYrCrQy2YxNNQ/SQEHQYLxjccfUXbsG1tnBAaGl1Al3C8d5rcOa/xMeFOpYtOzCcF6iWIk/Ni/U/x8tREL5n86nxY=", "apiKey": "OBk2z6xg57zYcfu/SgHR5izad6/U73dkdurHeg2Qzx2MOqdBiIJYtMGDj4iYCsRwOjjRExBR5NY=", "cashierKey": "8+d2MKs+ldE/V1CJ2f6bbJfDA4mXzDJKDLzjkXyuGujF9zM3xuFci7LRt3lRhjG2xB4Rjyczpmt7uqx5c3JeqTMQW8tr3bspIAiYPzD/yo8pg6qbQ5GfrREONPc="}', 300, false, false, false, 0, 60, ARRAY['52.31.139.75', '52.49.173.169'], 'brn_001', 'env_uat_001', 'ftg_bridgerpay_001', 'ENABLED', NOW(), NOW(), 'system', 'system');

INSERT INTO psp_operations (psp_id, flow_action_id, flow_definition_id, currencies, countries, status) VALUES
('psp_001', 'fat_deposit_001', 'fld_deposit_enovipay_001', ARRAY['USD'], ARRAY['US', 'CA', 'GB'], 'ENABLED'),
('psp_001', 'fat_withdraw_001', 'fld_withdraw_enovipay_001', ARRAY['USD'], ARRAY['US', 'CA', 'GB'], 'ENABLED'),
('psp_002', 'fat_deposit_001', 'fld_deposit_sticpay_001', ARRAY['USD'], ARRAY['US', 'CA', 'GB', 'EU'], 'ENABLED'),
('psp_002', 'fat_withdraw_001', 'fld_withdraw_sticpay_001', ARRAY['USD'], ARRAY['US', 'CA', 'GB', 'EU'], 'ENABLED');

INSERT INTO fixer_api_currency_pairs (id, source_currency, target_currency) VALUES
(1, 'USD', ARRAY['EUR', 'GBP', 'JPY', 'AUD', 'CAD', 'CHF', 'CNY', 'SEK', 'NZD', 'INR']),
(2, 'EUR', ARRAY['USD', 'GBP', 'JPY', 'AUD', 'CAD', 'CHF', 'CNY', 'SEK', 'NZD', 'INR']),
(3, 'GBP', ARRAY['USD', 'EUR', 'JPY', 'AUD', 'CAD', 'CHF', 'CNY', 'SEK', 'NZD', 'INR']),
(4, 'JPY', ARRAY['USD', 'EUR', 'GBP', 'AUD', 'CAD', 'CHF', 'CNY', 'SEK', 'NZD', 'INR']),
(5, 'INR', ARRAY['USD', 'EUR', 'GBP', 'JPY', 'AUD', 'CAD', 'CHF', 'CNY', 'SEK', 'NZD']);

INSERT INTO conversion_rate_raw_data (id, version, source_currency, target_currency, time_range, amount, created_at, updated_at, created_by, updated_by) VALUES
('conv_raw_001', 1, 'USD', 'EUR', NOW(), 1000.00, NOW(), NOW(), 'system', 'system'),
('conv_raw_002', 1, 'EUR', 'USD', NOW(), 850.00, NOW(), NOW(), 'system', 'system'),
('conv_raw_003', 1, 'USD', 'GBP', NOW(), 800.00, NOW(), NOW(), 'system', 'system');

INSERT INTO conversion_rate (id, version, brand_id, environment_id, status, source_currency, target_currency, value, created_at, updated_at, created_by, updated_by) VALUES
('conv_001', 1, 'brn_001', 'env_uat_001', 'ENABLED', 'USD', 'EUR', 0.85, NOW(), NOW(), 'system', 'system'),
('conv_002', 1, 'brn_001', 'env_uat_001', 'ENABLED', 'EUR', 'USD', 1.18, NOW(), NOW(), 'system', 'system'),
('conv_003', 1, 'brn_001', 'env_uat_001', 'ENABLED', 'USD', 'GBP', 0.80, NOW(), NOW(), 'system', 'system');

INSERT INTO webhooks (id, status_type, url, retry, brand_id, environment_id, status, created_at, updated_at, created_by, updated_by) VALUES
('webhook_001', 'SUCCESS', 'https://api.connxt.fynxt.io/api/v1/health', 3, 'brn_001', 'env_uat_001', 'ENABLED', NOW(), NOW(), 'system', 'system'),
('webhook_002', 'FAILURE', 'https://api.connxt.fynxt.io/api/v1/health', 3, 'brn_001', 'env_uat_001', 'ENABLED', NOW(), NOW(), 'system', 'system'),
('webhook_003', 'NOTIFICATION', 'https://api.connxt.fynxt.io/api/v1/health', 3, 'brn_001', 'env_uat_001', 'ENABLED', NOW(), NOW(), 'system', 'system');

UPDATE flow_definitions SET flow_configuration = '{
   "FAILED": [],
   "NEW": [
     "CREATED"
   ],
   "CREATED": [
     "INITIATED"
   ],
   "SUCCESS": [
     "CREDITED_TO_WALLET"
   ],
   "APPROVED": [
     "SUCCESS"
   ],
   "REJECTED": [],
   "ABANDONED": [],
   "INITIATED": [
     "PG_ACCEPTED",
     "PG_REJECTED",
     "ABANDONED"
   ],
   "PG_FAILED": [
     "FAILED"
   ],
   "PG_SUCCESS": [
     "PENDING_FOR_APPROVAL"
   ],
   "PG_ACCEPTED": [
     "PG_SUCCESS",
     "PG_FAILED"
   ],
   "AUTO_APPROVED": [
     "SUCCESS"
   ],
   "CREDITED_TO_WALLET": [],
   "PENDING_FOR_APPROVAL": [
     "AUTO_APPROVED",
     "REJECTED",
     "APPROVED"
   ]
 }' WHERE id = 'fld_deposit_enovipay_001';
 UPDATE flow_definitions SET flow_configuration = '{
   "FAILED": [],
   "NEW": [
     "CREATED"
   ],
   "CREATED": [
     "INITIATED"
   ],
   "SUCCESS": [
     "CREDITED_TO_WALLET"
   ],
   "APPROVED": [
     "SUCCESS"
   ],
   "REJECTED": [],
   "ABANDONED": [],
   "INITIATED": [
     "PG_ACCEPTED",
     "PG_REJECTED",
     "ABANDONED"
   ],
   "PG_FAILED": [
     "FAILED"
   ],
   "PG_SUCCESS": [
     "PENDING_FOR_APPROVAL"
   ],
   "PG_ACCEPTED": [
     "PG_SUCCESS",
     "PG_FAILED"
   ],
   "AUTO_APPROVED": [
     "SUCCESS"
   ],
   "CREDITED_TO_WALLET": [],
   "PENDING_FOR_APPROVAL": [
     "AUTO_APPROVED",
     "REJECTED",
     "APPROVED"
   ]
 }' WHERE id = 'fld_deposit_sticpay_001';

UPDATE flow_definitions SET flow_configuration = '{"NEW":["CREATED"],"CREATED":["HOLD_BALANCE"],"HOLD_BALANCE":["PENDING_FOR_APPROVAL"],"PENDING_FOR_APPROVAL":["REJECTED","APPROVED"],"SUCCESS":[],"APPROVED":["INITIATED"],"INITIATED":["SUCCESS"]}' WHERE id = 'fld_withdraw_enovipay_001';

UPDATE flow_definitions SET flow_configuration = '{"NEW":["CREATED"],"CREATED":["HOLD_BALANCE"],"HOLD_BALANCE":["PENDING_FOR_APPROVAL"],"PENDING_FOR_APPROVAL":["REJECTED","APPROVED"],"SUCCESS":[],"APPROVED":["INITIATED"],"INITIATED":["SUCCESS"]}' WHERE id = 'fld_withdraw_sticpay_001';

INSERT INTO flow_definitions (id, flow_action_id, flow_target_id, description, code, created_at, updated_at, created_by, updated_by) VALUES
('fld_deposit_korapay_001', 'fat_deposit_001', 'ftg_korapay_001', 'SYS-PSP-DEPOSIT-KORAPAY', 'export const initiate = async (ctx, sdk) => {
	const {
		id: tnxId,
		credential,
		data,
		urls: { server }
	} = ctx;

	const { http, logger } = sdk;

	const { secretKey } = credential;

	const {
		body: {
			order: {
				id: externalOrderId,
				money: { amount, currency }
			},
			customer: { email, id: userId, firstName, lastName }
		}
	} = data;

	const payload = {
		amount,
		redirect_url: `${server.redirect}?orderId=${tnxId}`,
		currency,
		reference: tnxId,
		narration: `Payment for order ${tnxId}`,
		merchant_bears_cost: false,
		customer: {
			name: `${firstName} ${lastName}`,
			email
		},
		notification_url: `${server.webhook}?orderId=${tnxId}`
	};

	const headers = {
		"Content-Type": "application/json",
		Authorization: `Bearer ${secretKey}`
	};

	const response = await http.post(
		"https://api.korapay.com/merchant/api/v1/charges/initialize",
		{
			headers,
			body: JSON.stringify(payload)
		}
	);

	if (!response.success) {
		logger.error(
			`Korapay Deposit Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		if (response.data?.errors?.length) {
			const customErrorMessage =
				`${response.data.errors[0].field} ${response.data.errors[0].defaultMessage}` ||
				"Something went wrong";

			return sdk.response.failure(customErrorMessage, response.status);
		}

		return sdk.response.failure(
			response.data?.message ||
				response.data?.error ||
				"Payment initialization failed",
			response.status
		);
	}

	if (!response.data?.data?.checkout_url) {
		logger.error(
			`Korapay Deposit Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Invalid Redirect URL", response.status);
	}

	logger.log(`Korapay Deposit Initiate Script: ${JSON.stringify(response)}`);

	return sdk.response.success({
		navigation: {
			type: "redirect",
			contentType: "url",
			value: response.data.data.checkout_url
		},
		status: "success",
		orderId: tnxId,
		userId
	});
};

export const redirect = async (ctx, sdk) => {
	const {
		data,
		urls: { server }
	} = ctx;

	const { orderId } = data.query;

	return sdk.response.redirect(server.successRedirectUrl, {
		status: "success",
		orderId,
		timestamp: Date.now().toString()
	});
};

export const webhook = async (ctx, sdk) => {
	const { data } = ctx;

	const { logger } = sdk;

	const {
		event,
		data: { payment_reference, currency, amount, status }
	} = data.body;

	const { orderId, userId, userEmail } = data.query;

	const crmData = {
		type: "Deposit",
		order: {
			id: orderId,
			amount: Number.parseFloat(amount),
			currency,
			timestamp: String(Date.now())
		},
		user: {
			id: userId,
			email: userEmail || null
		},
		psp: {
			transactionId: payment_reference,
			conversionRate: null,
			status: {
				code: status,
				comment: `Payment ${status}`
			},
			responseLog: {
				code: event,
				comment: JSON.stringify(data.body)
			}
		}
	};

	logger.log(`Korapay Deposit Webhook Response: ${JSON.stringify(crmData)}`);

	return sdk.response.success({
		navigation: {
			type: "object",
			contentType: "object",
			value: crmData
		},
		status: "success",
		orderId,
		userId
	});
};
', NOW(), NOW(), 'system', 'system'),
('fld_withdraw_korapay_001', 'fat_withdraw_001', 'ftg_korapay_001', 'SYS-PSP-WITHDRAW-KORAPAY', 'export const initiate = async (ctx, sdk) => {
	const {
		id: tnxId,
		credential,
		data,
		urls: { server }
	} = ctx;

	const { http, logger } = sdk;

	const { secretKey } = credential;

	const {
		body: {
			order: {
				id: externalOrderId,
				money: { amount, currency }
			},
			customer: { id: userId },
			customAttributes
		}
	} = data;

	if (!customAttributes.bankCode || !customAttributes.accountNumber) {
		logger.error(
			`Korapay Withdraw Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		return sdk.response.failure("Bank code and account number are required", 400);
	}

	const payload = {
		amount,
		currency,
		reference: tnxId,
		narration: `Withdrawal for order ${tnxId}`,
		bank_code: customAttributes.bankCode,
		account_number: customAttributes.accountNumber,
		recipient: {
			account_number: customAttributes.accountNumber,
			bank_code: customAttributes.bankCode
		}
	};

	const headers = {
		"Content-Type": "application/json",
		Authorization: `Bearer ${secretKey}`
	};

	const response = await http.post(
		"https://api.korapay.com/merchant/api/v1/transfers/",
		{
			headers,
			body: JSON.stringify(payload)
		}
	);

	if (!response.success) {
		logger.error(
			`Korapay Withdraw Initiate Script - Failed At: ${new Date().toISOString()}`
		);

		if (response.data?.errors?.length) {
			const customErrorMessage =
				`${response.data.errors[0].field} ${response.data.errors[0].defaultMessage}` ||
				"Something went wrong";

			return sdk.response.failure(customErrorMessage, response.status);
		}

		return sdk.response.failure(
			response.data?.message ||
				response.data?.error ||
				"Withdrawal initialization failed",
			response.status
		);
	}

	logger.log(`Korapay Withdraw Initiate Script: ${JSON.stringify(response)}`);

	return sdk.response.success({
		navigation: {
			type: "redirect",
			contentType: "url",
			value: response.data?.data?.checkout_url || server.successRedirectUrl
		},
		status: "success",
		orderId: tnxId,
		userId
	});
};

export const redirect = async (ctx, sdk) => {
	const {
		data,
		urls: { server }
	} = ctx;

	const { orderId } = data.query;

	return sdk.response.redirect(server.successRedirectUrl, {
		status: "success",
		orderId,
		timestamp: Date.now().toString()
	});
};

export const webhook = async (ctx, sdk) => {
	const { data } = ctx;

	const { logger } = sdk;

	const {
		event,
		data: { transfer_reference, currency, amount, status }
	} = data.body;

	const { orderId, userId, userEmail } = data.query;

	const crmData = {
		type: "Withdraw",
		order: {
			id: orderId,
			amount: Number.parseFloat(amount),
			currency,
			timestamp: String(Date.now())
		},
		user: {
			id: userId,
			email: userEmail || null
		},
		psp: {
			transactionId: transfer_reference,
			conversionRate: null,
			status: {
				code: status,
				comment: `Transfer ${status}`
			},
			responseLog: {
				code: event,
				comment: JSON.stringify(data.body)
			}
		}
	};

	logger.log(`Korapay Withdraw Webhook Response: ${JSON.stringify(crmData)}`);

	return sdk.response.success({
		navigation: {
			type: "object",
			contentType: "object",
			value: crmData
		},
		status: "success",
		orderId,
		userId
	});
};
', NOW(), NOW(), 'system', 'system');

UPDATE flow_definitions SET flow_configuration = '{
   "FAILED": [],
   "NEW": [
     "CREATED"
   ],
   "CREATED": [
     "INITIATED"
   ],
   "SUCCESS": [
     "CREDITED_TO_WALLET"
   ],
   "APPROVED": [
     "SUCCESS"
   ],
   "REJECTED": [],
   "ABANDONED": [],
   "INITIATED": [
     "PG_ACCEPTED",
     "PG_REJECTED",
     "ABANDONED"
   ],
   "PG_FAILED": [
     "FAILED"
   ],
   "PG_SUCCESS": [
     "PENDING_FOR_APPROVAL"
   ],
   "PG_ACCEPTED": [
     "PG_SUCCESS",
     "PG_FAILED"
   ],
   "AUTO_APPROVED": [
     "SUCCESS"
   ],
   "CREDITED_TO_WALLET": [],
   "PENDING_FOR_APPROVAL": [
     "AUTO_APPROVED",
     "REJECTED",
     "APPROVED"
   ]
 }' WHERE id = 'fld_deposit_korapay_001';

UPDATE flow_definitions SET flow_configuration = '{"NEW":["CREATED"],"CREATED":["HOLD_BALANCE"],"HOLD_BALANCE":["PENDING_FOR_APPROVAL"],"PENDING_FOR_APPROVAL":["REJECTED","APPROVED"],"SUCCESS":[],"APPROVED":["INITIATED"],"INITIATED":["SUCCESS"]}' WHERE id = 'fld_withdraw_korapay_001';

UPDATE flow_definitions SET flow_configuration = '{
   "FAILED": [],
   "NEW": [
     "CREATED"
   ],
   "CREATED": [
     "INITIATED"
   ],
   "SUCCESS": [
     "CREDITED_TO_WALLET"
   ],
   "APPROVED": [
     "SUCCESS"
   ],
   "REJECTED": [],
   "ABANDONED": [],
   "INITIATED": [
     "PG_ACCEPTED",
     "PG_REJECTED",
     "ABANDONED"
   ],
   "PG_FAILED": [
     "FAILED"
   ],
   "PG_SUCCESS": [
     "PENDING_FOR_APPROVAL"
   ],
   "PG_ACCEPTED": [
     "PG_SUCCESS",
     "PG_FAILED"
   ],
   "AUTO_APPROVED": [
     "SUCCESS"
   ],
   "CREDITED_TO_WALLET": [],
   "PENDING_FOR_APPROVAL": [
     "AUTO_APPROVED",
     "REJECTED",
     "APPROVED"
   ]
 }' WHERE id = 'fld_deposit_bridgerpay_001';

INSERT INTO psp_operations (psp_id, flow_action_id, flow_definition_id, currencies, countries, status) VALUES
('psp_003', 'fat_deposit_001', 'fld_deposit_korapay_001', ARRAY['NGN', 'GHS'], ARRAY['NG', 'GH'], 'ENABLED'),
('psp_003', 'fat_withdraw_001', 'fld_withdraw_korapay_001', ARRAY['NGN', 'GHS'], ARRAY['NG', 'GH'], 'ENABLED'),
('psp_004', 'fat_deposit_001', 'fld_deposit_bridgerpay_001', ARRAY['USD'], ARRAY['US', 'CA', 'GB', 'EU'], 'ENABLED');