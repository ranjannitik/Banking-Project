package com.smartbank.user.repository;

import com.smartbank.user.model.Loan;
import com.smartbank.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUser(User user);
    void deleteAllByUser(User user); // ✅ Added for cascade deletion
}
