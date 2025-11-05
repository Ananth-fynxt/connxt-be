package nexxus.fi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.fi.dto.FiDto;
import nexxus.fi.service.FiService;
import nexxus.permission.annotations.RequiresScope;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/fi")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM"})
public class FiController {

  private final FiService fiService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(@Validated @RequestBody @NotNull FiDto dto) {
    log.info("Received request to create FI: {}", dto.getName());
    return responseBuilder.successResponse(fiService.create(dto));
  }
}
