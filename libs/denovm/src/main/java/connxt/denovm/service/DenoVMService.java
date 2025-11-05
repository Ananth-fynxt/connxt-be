package connxt.denovm.service;

import java.util.concurrent.CompletableFuture;

import connxt.denovm.dto.DenoVMRequest;
import connxt.denovm.dto.DenoVMResult;

/** Simplified Deno VM service for executing VM code */
public interface DenoVMService {

  /** Execute VM code with full configuration */
  DenoVMResult executeCode(DenoVMRequest request);

  /** Execute VM code asynchronously */
  CompletableFuture<DenoVMResult> executeCodeAsync(DenoVMRequest request);
}
