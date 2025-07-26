package com.smartbank.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loanType;
    private double amount;
    private String dueDate;
    private String status;

    @ManyToOne
    private User user;

    public Loan() {}

    public Long getId() { return id; }
    public String getLoanType() { return loanType; }
    public double getAmount() { return amount; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public User getUser() { return user; }

    public void setId(Long id) { this.id = id; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setStatus(String status) { this.status = status; }
    public void setUser(User user) { this.user = user; }
}
