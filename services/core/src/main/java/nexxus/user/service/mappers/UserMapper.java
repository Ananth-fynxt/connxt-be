package nexxus.user.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import nexxus.shared.db.mappers.MapperCoreConfig;
import nexxus.user.dto.UpdatePasswordRequest;
import nexxus.user.dto.UserRequest;
import nexxus.user.entity.User;

@Mapper(config = MapperCoreConfig.class)
public interface UserMapper {

  UserRequest toUserRequest(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  User toUser(UserRequest request);

  void updateUserPassword(UpdatePasswordRequest request, @MappingTarget User user);
}
