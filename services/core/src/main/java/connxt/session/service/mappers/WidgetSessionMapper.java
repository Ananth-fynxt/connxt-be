package connxt.session.service.mappers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import connxt.session.dto.FingerprintDto;
import connxt.session.dto.SessionDto;
import connxt.session.entity.WidgetSession;
import connxt.session.service.util.FingerprintEncryptionUtils;
import connxt.shared.db.mappers.MapperCoreConfig;

@Mapper(config = MapperCoreConfig.class)
public interface WidgetSessionMapper {

  default JsonNode parseFingerprintDto(FingerprintDto fingerprint) {
    if (fingerprint == null) {
      return null;
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.valueToTree(fingerprint);

      // Encrypt the fingerprint data for storage at rest
      String fingerprintJson = objectMapper.writeValueAsString(jsonNode);
      String encryptedFingerprint = FingerprintEncryptionUtils.encryptFingerprint(fingerprintJson);

      // Return the encrypted string as a text node (not parse it as JSON)
      return objectMapper.valueToTree(encryptedFingerprint);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse and encrypt fingerprint DTO", e);
    }
  }

  default FingerprintDto toFingerprintDto(JsonNode fingerprint) {
    if (fingerprint == null) {
      return null;
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();

      // Extract the encrypted string from the JsonNode
      String encryptedFingerprint = fingerprint.asText();

      // Decrypt the fingerprint data from storage
      String decryptedFingerprint =
          FingerprintEncryptionUtils.decryptFingerprint(encryptedFingerprint);

      return objectMapper.readValue(decryptedFingerprint, FingerprintDto.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to decrypt and convert JsonNode to FingerprintDto", e);
    }
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sessionTokenHash", source = "sessionTokenHash")
  @Mapping(target = "fingerprintHash", source = "fingerprintHash")
  @Mapping(target = "expiresAt", source = "expiresAt")
  @Mapping(target = "extensionCount", constant = "0")
  @Mapping(target = "maxExtensions", source = "requestDto.sessionConfig.maxExtensions")
  @Mapping(target = "timeoutMinutes", source = "timeoutMinutes")
  @Mapping(target = "autoExtend", source = "autoExtend")
  @Mapping(target = "revoked", constant = "false")
  @Mapping(
      target = "fingerprint",
      expression = "java(parseFingerprintDto(requestDto.getDeviceInfo().getFingerprint()))")
  @Mapping(target = "lastRefreshedAt", ignore = true)
  WidgetSession toWidgetSessionWithDefaults(
      SessionDto.CreateRequest requestDto,
      String sessionTokenHash,
      String fingerprintHash,
      Instant expiresAt,
      Integer timeoutMinutes,
      Boolean autoExtend);

  @Mapping(target = "sessionToken", source = "sessionToken")
  @Mapping(target = "sessionId", source = "session.id")
  @Mapping(target = "expiresAt", source = "session.expiresAt")
  @Mapping(target = "timeoutMinutes", source = "session.timeoutMinutes")
  SessionDto.CreateResponse toSessionCreateResponseDto(WidgetSession session, String sessionToken);

  @Mapping(target = "sessionToken", source = "sessionToken")
  @Mapping(target = "sessionId", source = "session.id")
  @Mapping(target = "expiresAt", source = "session.expiresAt")
  @Mapping(target = "extensionCount", source = "session.extensionCount")
  SessionDto.RefreshResponse toSessionRefreshResponseDto(
      WidgetSession session, String sessionToken);

  @Mapping(
      target = "valid",
      expression = "java(!session.getRevoked() && Instant.now().isBefore(session.getExpiresAt()))")
  @Mapping(target = "sessionId", source = "session.id")
  @Mapping(target = "revoked", source = "session.revoked")
  @Mapping(target = "expiresAt", source = "session.expiresAt")
  @Mapping(target = "lastAccessedAt", source = "session.lastAccessedAt")
  SessionDto.ValidateResponse toSessionValidateResponseDto(WidgetSession session);

  @Mapping(target = "sessionTokenHash", source = "newTokenHash")
  @Mapping(target = "fingerprintHash", source = "newFingerprintHash")
  @Mapping(target = "extensionCount", expression = "java(session.getExtensionCount() + 1)")
  @Mapping(target = "lastRefreshedAt", expression = "java(Instant.now())")
  @Mapping(target = "expiresAt", source = "newExpiresAt")
  void updateSessionForRefresh(
      @MappingTarget WidgetSession session,
      String newTokenHash,
      String newFingerprintHash,
      Instant newExpiresAt);

  @Named("calculateExpiryTime")
  default Instant calculateExpiryTime(Integer timeoutMinutes) {
    if (timeoutMinutes == null) {
      return Instant.now().plus(60, ChronoUnit.MINUTES); // Default 60 minutes
    }
    return Instant.now().plus(timeoutMinutes, ChronoUnit.MINUTES);
  }
}
