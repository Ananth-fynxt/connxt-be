package connxt.user.service;

import connxt.user.dto.UpdatePasswordRequest;
import connxt.user.dto.UserRequest;
import connxt.user.entity.User;

public interface UserService {

  UserRequest createUser(UserRequest request);

  UserRequest getUserById(String id);

  UserRequest getUserByEmail(String email);

  UserRequest updatePassword(String userId, UpdatePasswordRequest request);

  User findByEmailForAuthentication(String email);

  User findByIdForAuthentication(String id);
}
