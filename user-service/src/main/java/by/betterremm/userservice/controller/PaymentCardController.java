package by.betterremm.userservice.controller;

import by.betterremm.userservice.dto.request.PaymentCardCreateRequest;
import by.betterremm.userservice.dto.request.PaymentCardUpdateRequest;
import by.betterremm.userservice.dto.response.PaymentCardResponse;
import by.betterremm.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @PostMapping("/api/v1/users/{userId}/cards")
    public ResponseEntity<PaymentCardResponse> createCard(@PathVariable Long userId, @Valid @RequestBody PaymentCardCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentCardService.createCard(userId, request));
    }

    @GetMapping("/api/v1/users/{userId}/cards")
    public ResponseEntity<List<PaymentCardResponse>> getCardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentCardService.getCardsByUserId(userId));
    }

    @GetMapping("/api/v1/cards")
    public ResponseEntity<Page<PaymentCardResponse>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(paymentCardService.getAllCards(pageable));
    }

    @GetMapping("/api/v1/cards/{id}")
    public ResponseEntity<PaymentCardResponse> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCardService.getCardById(id));
    }

    @PutMapping("/api/v1/cards/{id}")
    public ResponseEntity<PaymentCardResponse> updateCard(@PathVariable Long id, @Valid @RequestBody PaymentCardUpdateRequest request) {
        return ResponseEntity.ok(paymentCardService.updateCard(id, request));
    }

    @PatchMapping("/api/v1/cards/{id}/activate")
    public ResponseEntity<PaymentCardResponse> activateCard(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCardService.activateCard(id));
    }

    @PatchMapping("/api/v1/cards/{id}/deactivate")
    public ResponseEntity<PaymentCardResponse> deactivateCard(@PathVariable Long id) {
        return ResponseEntity.ok(paymentCardService.deactivateCard(id));
    }

    @DeleteMapping("/api/v1/cards/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        paymentCardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
