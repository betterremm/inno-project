package com.innowise.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
@NamedQuery(
        name = PaymentCardEntity.COUNT_ACTIVE_BY_USER,
        query = "SELECT COUNT(c) FROM PaymentCardEntity c WHERE c.user.id = :userId AND c.active = true"
)
public class PaymentCardEntity extends AuditableEntity {

    public static final String COUNT_ACTIVE_BY_USER = "PaymentCardEntity.countActiveByUser";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 19)
    private String number;

    @Column(nullable = false, length = 100)
    private String holder;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private boolean active;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getHolder() { return holder; }
    public void setHolder(String holder) { this.holder = holder; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
