package com.innowise.userservice.service;

import com.innowise.userservice.dto.request.UserCreateRequest;
import com.innowise.userservice.dto.request.UserUpdateRequest;
import com.innowise.userservice.dto.response.UserResponse;
import com.innowise.userservice.dto.response.UserWithCardsResponse;
import com.innowise.userservice.entity.UserEntity;
import com.innowise.userservice.exception.DuplicateEmailException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PaymentCardRepository paymentCardRepository;
    @Mock private UserMapper userMapper;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void createUser() {
        var req = new UserCreateRequest("John", "Doe", LocalDate.of(1990, 1, 1), "j@mail.com");
        var entity = new UserEntity();
        var resp = new UserResponse(1L, "John", "Doe", null, "j@mail.com", true, null, null);
        when(userRepository.existsByEmail("j@mail.com")).thenReturn(false);
        when(userMapper.toEntity(req)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(resp);
        assertThat(userService.createUser(req)).isEqualTo(resp);
    }

    @Test
    void createUserDuplicateEmail() {
        when(userRepository.existsByEmail("j@mail.com")).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(new UserCreateRequest("J", "D", null, "j@mail.com")))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void getUserById() {
        var entity = new UserEntity();
        entity.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userMapper.toResponseWithCards(entity)).thenReturn(new UserWithCardsResponse(1L, "J", "D", null, "e", true, null, null, List.of()));
        assertThat(userService.getUserById(1L).id()).isEqualTo(1L);
    }

    @Test
    void getUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(1L)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void deleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }
}
