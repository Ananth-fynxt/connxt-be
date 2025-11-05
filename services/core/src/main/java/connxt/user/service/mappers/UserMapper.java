package connxt.user.service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import connxt.shared.db.mappers.MapperCoreConfig;
import connxt.user.dto.UpdatePasswordRequest;
import connxt.user.dto.UserRequest;
import connxt.user.entity.User;

@Mapper(config = MapperCoreConfig.class)
public interface UserMapper {

  UserRequest toUserRequest(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", ignore = true)
  User toUser(UserRequest request);

  void updateUserPassword(UpdatePasswordRequest request, @MappingTarget User user);
}
