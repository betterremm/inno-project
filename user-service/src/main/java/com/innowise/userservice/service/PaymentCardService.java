package com.innowise.userservice.service;

import com.innowise.userservice.dto.request.PaymentCardCreateRequest;
import com.innowise.userservice.dto.request.PaymentCardUpdateRequest;
import com.innowise.userservice.dto.response.PaymentCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentCardService {

    PaymentCardResponse createCard(Long userId, PaymentCardCreateRequest request);

    PaymentCardResponse getCardById(Long id);

    Page<PaymentCardResponse> getAllCards(Pageable pageable);

    List<PaymentCardResponse> getCardsByUserId(Long userId);

    PaymentCardResponse updateCard(Long id, PaymentCardUpdateRequest request);

    PaymentCardResponse activateCard(Long id);

    PaymentCardResponse deactivateCard(Long id);

    void deleteCard(Long id);
}
