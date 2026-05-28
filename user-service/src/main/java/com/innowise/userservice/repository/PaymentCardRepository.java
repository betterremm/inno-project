package com.innowise.userservice.repository;

import com.innowise.userservice.entity.PaymentCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCardEntity, Long> {

    @Query("SELECT c FROM PaymentCardEntity c JOIN FETCH c.user WHERE c.id = :id")
    Optional<PaymentCardEntity> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT c FROM PaymentCardEntity c WHERE c.user.id = :userId")
    List<PaymentCardEntity> findAllByUserId(@Param("userId") Long userId);

    @Query(name = PaymentCardEntity.COUNT_ACTIVE_BY_USER)
    long countActiveByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE payment_cards SET active = false WHERE user_id = :userId", nativeQuery = true)
    int deactivateAllByUserId(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
