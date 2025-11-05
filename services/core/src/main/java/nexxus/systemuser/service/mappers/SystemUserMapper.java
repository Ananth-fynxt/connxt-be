package nexxus.systemuser.service.mappers;

import org.springframework.stereotype.Component;

import nexxus.systemuser.dto.SystemUserDto;
import nexxus.systemuser.entity.SystemUser;

@Component
public class SystemUserMapper {

  public SystemUserDto toDto(SystemUser entity) {
    return SystemUserDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .email(entity.getEmail())
        .userId(entity.getUserId())
        .scope(entity.getScope())
        .status(entity.getStatus())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }

  public SystemUser toEntity(SystemUserDto dto) {
    return SystemUser.builder()
        .id(dto.getId())
        .name(dto.getName())
        .email(dto.getEmail())
        .userId(dto.getUserId())
        .scope(dto.getScope())
        .status(dto.getStatus())
        .build();
  }
}
