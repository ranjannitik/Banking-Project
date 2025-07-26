package com.smartbank.user.repository;

import com.smartbank.user.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification, String> {
}
