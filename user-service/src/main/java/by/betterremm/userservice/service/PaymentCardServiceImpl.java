package by.betterremm.userservice.service;

import by.betterremm.userservice.config.CacheConfig;
import by.betterremm.userservice.dto.request.PaymentCardCreateRequest;
import by.betterremm.userservice.dto.request.PaymentCardUpdateRequest;
import by.betterremm.userservice.dto.response.PaymentCardResponse;
import by.betterremm.userservice.entity.PaymentCardEntity;
import by.betterremm.userservice.entity.UserEntity;
import by.betterremm.userservice.exception.CardLimitExceededException;
import by.betterremm.userservice.exception.CardNotFoundException;
import by.betterremm.userservice.exception.InactiveUserException;
import by.betterremm.userservice.mapper.PaymentCardMapper;
import by.betterremm.userservice.repository.PaymentCardRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {

    private static final int MAX_CARDS_PER_USER = 5;

    private final PaymentCardRepository paymentCardRepository;
    private final UserService userService;
    private final PaymentCardMapper paymentCardMapper;
    private final CacheManager cacheManager;

    public PaymentCardServiceImpl(PaymentCardRepository paymentCardRepository, UserService userService,
                                  PaymentCardMapper paymentCardMapper, CacheManager cacheManager) {
        this.paymentCardRepository = paymentCardRepository;
        this.userService = userService;
        this.paymentCardMapper = paymentCardMapper;
        this.cacheManager = cacheManager;
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.USER_CACHE, key = "#userId")
    @Transactional
    public PaymentCardResponse createCard(Long userId, PaymentCardCreateRequest request) {
        UserEntity user = userService.findUserOrThrow(userId);
        if (!user.isActive()) {
            throw new InactiveUserException(userId);
        }
        if (paymentCardRepository.countByUserId(userId) >= MAX_CARDS_PER_USER) {
            throw new CardLimitExceededException();
        }
        PaymentCardEntity card = paymentCardMapper.toEntity(request, user);
        return paymentCardMapper.toResponse(paymentCardRepository.save(card));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentCardResponse getCardById(Long id) {
        return paymentCardMapper.toResponse(findCardOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentCardResponse> getAllCards(Pageable pageable) {
        return paymentCardRepository.findAll(pageable).map(paymentCardMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentCardResponse> getCardsByUserId(Long userId) {
        userService.findUserOrThrow(userId);
        return paymentCardRepository.findAllByUserId(userId).stream().map(paymentCardMapper::toResponse).toList();
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.USER_CACHE, key = "#result.userId()")
    @Transactional
    public PaymentCardResponse updateCard(Long id, PaymentCardUpdateRequest request) {
        PaymentCardEntity card = findCardOrThrow(id);
        paymentCardMapper.updateEntity(request, card);
        return paymentCardMapper.toResponse(paymentCardRepository.save(card));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.USER_CACHE, key = "#result.userId()")
    @Transactional
    public PaymentCardResponse activateCard(Long id) {
        PaymentCardEntity card = findCardOrThrow(id);
        card.setActive(true);
        return paymentCardMapper.toResponse(paymentCardRepository.save(card));
    }

    @Override
    @CacheEvict(cacheNames = CacheConfig.USER_CACHE, key = "#result.userId()")
    @Transactional
    public PaymentCardResponse deactivateCard(Long id) {
        PaymentCardEntity card = findCardOrThrow(id);
        card.setActive(false);
        return paymentCardMapper.toResponse(paymentCardRepository.save(card));
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        PaymentCardEntity card = findCardOrThrow(id);
        Long userId = card.getUser().getId();
        paymentCardRepository.delete(card);
        var cache = cacheManager.getCache(CacheConfig.USER_CACHE);
        if (cache != null) {
            cache.evict(userId);
        }
    }

    private PaymentCardEntity findCardOrThrow(Long id) {
        return paymentCardRepository.findByIdWithUser(id).orElseThrow(() -> new CardNotFoundException(id));
    }
}
