package by.betterremm.userservice.service;

import by.betterremm.userservice.config.CacheConfig;
import by.betterremm.userservice.dto.request.PaymentCardCreateRequest;
import by.betterremm.userservice.dto.response.PaymentCardResponse;
import by.betterremm.userservice.entity.PaymentCardEntity;
import by.betterremm.userservice.entity.UserEntity;
import by.betterremm.userservice.exception.CardLimitExceededException;
import by.betterremm.userservice.exception.InactiveUserException;
import by.betterremm.userservice.mapper.PaymentCardMapper;
import by.betterremm.userservice.repository.PaymentCardRepository;
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
    void createCard_limitExceeded() {
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
    void createCard_inactiveUser() {
        var user = new UserEntity();
        user.setId(1L);
        user.setActive(false);
        when(userService.findUserOrThrow(1L)).thenReturn(user);
        assertThatThrownBy(() -> paymentCardService.createCard(1L,
                new PaymentCardCreateRequest("4111-1111-1111-1111", "H", LocalDate.now().plusYears(1))))
                .isInstanceOf(InactiveUserException.class);
    }

    @Test
    void deleteCard_evictsCache() {
        var user = new UserEntity();
        user.setId(1L);
        var card = new PaymentCardEntity();
        card.setId(10L);
        card.setUser(user);
        when(paymentCardRepository.findByIdWithUser(10L)).thenReturn(Optional.of(card));
        when(cacheManager.getCache(CacheConfig.USER_CACHE)).thenReturn(mock(Cache.class));
        paymentCardService.deleteCard(10L);
        verify(paymentCardRepository).delete(card);
    }
}
