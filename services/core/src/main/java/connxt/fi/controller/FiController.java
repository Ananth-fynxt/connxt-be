package connxt.fi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.fi.dto.FiDto;
import connxt.fi.service.FiService;
import connxt.permission.annotations.RequiresScope;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

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
