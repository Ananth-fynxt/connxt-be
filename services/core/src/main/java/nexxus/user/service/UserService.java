package nexxus.user.service;

import nexxus.user.dto.UpdatePasswordRequest;
import nexxus.user.dto.UserRequest;
import nexxus.user.entity.User;

public interface UserService {

  UserRequest createUser(UserRequest request);

  UserRequest getUserById(String id);

  UserRequest getUserByEmail(String email);

  UserRequest updatePassword(String userId, UpdatePasswordRequest request);

  User findByEmailForAuthentication(String email);

  User findByIdForAuthentication(String id);
}
