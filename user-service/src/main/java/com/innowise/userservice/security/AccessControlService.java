package com.innowise.userservice.security;

import com.innowise.userservice.repository.PaymentCardRepository;
import org.springframework.stereotype.Component;

@Component("accessControl")
public class AccessControlService {

    private final PaymentCardRepository paymentCardRepository;

    public AccessControlService(PaymentCardRepository paymentCardRepository) {
        this.paymentCardRepository = paymentCardRepository;
    }

    public boolean isAdmin() {
        return SecurityUtils.isAdmin();
    }

    public boolean isOwnUser(Long userId) {
        return SecurityUtils.currentUserId().equals(userId);
    }

    public boolean isAdminOrOwnUser(Long userId) {
        return isAdmin() || isOwnUser(userId);
    }

    public boolean isAdminOrOwnsCard(Long cardId) {
        if (isAdmin()) {
            return true;
        }
        return paymentCardRepository.findUserIdByCardId(cardId)
                .map(ownerId -> ownerId.equals(SecurityUtils.currentUserId()))
                .orElse(false);
    }
}
