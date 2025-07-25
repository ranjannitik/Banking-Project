package com.smartbank.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;
    private String cardType;
    private String expiry;

    @ManyToOne
    private User user;

    public Card() {}

    public Long getId() { return id; }
    public String getCardNumber() { return cardNumber; }
    public String getCardType() { return cardType; }
    public String getExpiry() { return expiry; }
    public User getUser() { return user; }

    public void setId(Long id) { this.id = id; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public void setExpiry(String expiry) { this.expiry = expiry; }
    public void setUser(User user) { this.user = user; }
}
