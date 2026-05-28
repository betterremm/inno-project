package com.innowise.userservice.service;

import com.innowise.userservice.config.CacheNames;
import com.innowise.userservice.dto.request.PaymentCardCreateRequest;
import com.innowise.userservice.dto.response.PaymentCardResponse;
import com.innowise.userservice.entity.PaymentCardEntity;
import com.innowise.userservice.entity.UserEntity;
import com.innowise.userservice.exception.CardLimitExceededException;
import com.innowise.userservice.exception.InactiveUserException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceTest {

    @Mock private PaymentCardRepository paymentCardRepository;
    @Mock private UserService userService;
    @Mock private PaymentCardMapper paymentCardMapper;
    @Mock private CacheManager cacheManager;
    @InjectMocks private PaymentCardServiceImpl paymentCardService;

    @Test
    void createCardLimitExceeded() {
        var user = new UserEntity();
        user.setId(1L);
        user.setActive(true);
        when(userService.findUserOrThrow(1L)).thenReturn(user);
        when(paymentCardRepository.countByUserId(1L)).thenReturn(5L);
        assertThatThrownBy(() -> paymentCardService.createCard(1L,
                new PaymentCardCreateRequest("4111-1111-1111-1111", "H", LocalDate.now().plusYears(1))))
                .isInstanceOf(CardLimitExceededException.class);
    }

    @Test
    void createCardInactiveUser() {
        var user = new UserEntity();
        user.setId(1L);
        user.setActive(false);
        when(userService.findUserOrThrow(1L)).thenReturn(user);
        assertThatThrownBy(() -> paymentCardService.createCard(1L,
                new PaymentCardCreateRequest("4111-1111-1111-1111", "H", LocalDate.now().plusYears(1))))
                .isInstanceOf(InactiveUserException.class);
    }

    @Test
    void deleteCardEvictsCache() {
        var user = new UserEntity();
        user.setId(1L);
        var card = new PaymentCardEntity();
        card.setId(10L);
        card.setUser(user);
        when(paymentCardRepository.findByIdWithUser(10L)).thenReturn(Optional.of(card));
        when(cacheManager.getCache(CacheNames.USERS)).thenReturn(mock(Cache.class));
        paymentCardService.deleteCard(10L);
        verify(paymentCardRepository).delete(card);
    }
}
