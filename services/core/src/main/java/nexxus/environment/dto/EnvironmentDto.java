package nexxus.environment.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentDto {
  private String id;

  @NotBlank(message = "Environment name is required")
  private String name;

  private String secret;

  private String token;

  private String origin;

  private String successRedirectUrl;

  private String failureRedirectUrl;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  private String createdBy;

  private String updatedBy;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;
}
