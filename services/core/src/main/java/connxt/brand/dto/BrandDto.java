package connxt.brand.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDto {
  private String id;

  @NotBlank(message = "Brand name is required")
  private String name;

  @NotBlank(message = "FI ID is required")
  private String fiId;

  @NotEmpty(message = "Currency is required")
  private String[] currencies;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  @Email(message = "Invalid email Id")
  private String email;
}
