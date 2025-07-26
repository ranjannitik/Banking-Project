package com.smartbank.user.repository;

import com.smartbank.user.model.BillPayment;
import com.smartbank.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {
    List<BillPayment> findByUser(User user);
    void deleteAllByUser(User user); // ✅ Added for cascade deletion
}
