package com.smartbank.user.repository;

import com.smartbank.user.model.Card;
import com.smartbank.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUser(User user);
    void deleteAllByUser(User user); // ✅ Added for cascade deletion
}
