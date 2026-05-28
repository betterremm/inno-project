package com.innowise.userservice.service;

import com.innowise.userservice.dto.request.UserCreateRequest;
import com.innowise.userservice.dto.request.UserUpdateRequest;
import com.innowise.userservice.dto.response.UserResponse;
import com.innowise.userservice.dto.response.UserWithCardsResponse;
import com.innowise.userservice.entity.UserEntity;
import com.innowise.userservice.exception.DuplicateEmailException;
import com.innowise.userservice.exception.UserNotFoundException;
import com.innowise.userservice.config.CacheNames;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.repository.specification.UserSpecifications;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PaymentCardRepository paymentCardRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.paymentCardRepository = paymentCardRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        UserEntity entity = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(entity));
    }

    @Override
    @Cacheable(cacheNames = CacheNames.USERS, key = "#id")
    @Transactional(readOnly = true)
    public UserWithCardsResponse getUserById(Long id) {
        UserEntity user = findUserOrThrow(id);
        user.getCards().size();
        return userMapper.toResponseWithCards(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String name, String surname, Pageable pageable) {
        Specification<UserEntity> spec = UserSpecifications.byNameAndSurname(name, surname);
        return userRepository.findAll(spec, pageable).map(userMapper::toResponse);
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.USERS, key = "#id")
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        UserEntity user = findUserOrThrow(id);
        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateEmailException(request.email());
        }
        userMapper.updateEntity(request, user);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.USERS, key = "#id")
    @Transactional
    public UserResponse activateUser(Long id) {
        UserEntity user = findUserOrThrow(id);
        user.setActive(true);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.USERS, key = "#id")
    @Transactional
    public UserResponse deactivateUser(Long id) {
        UserEntity user = findUserOrThrow(id);
        user.setActive(false);
        paymentCardRepository.deactivateAllByUserId(id);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.USERS, key = "#id")
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findUserOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
