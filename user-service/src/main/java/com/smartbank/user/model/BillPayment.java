package com.smartbank.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bill_payment")
public class BillPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String billType;
    private String billNumber;
    private double amount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() { return id; }

    public String getBillType() { return billType; }
    public void setBillType(String billType) { this.billType = billType; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
