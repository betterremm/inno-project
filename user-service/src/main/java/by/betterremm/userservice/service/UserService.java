package by.betterremm.userservice.service;

import by.betterremm.userservice.dto.request.UserCreateRequest;
import by.betterremm.userservice.dto.request.UserUpdateRequest;
import by.betterremm.userservice.dto.response.UserResponse;
import by.betterremm.userservice.dto.response.UserWithCardsResponse;
import by.betterremm.userservice.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserWithCardsResponse getUserById(Long id);

    Page<UserResponse> getAllUsers(String name, String surname, Pageable pageable);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    UserResponse activateUser(Long id);

    UserResponse deactivateUser(Long id);

    void deleteUser(Long id);

    UserEntity findUserOrThrow(Long id);
}
