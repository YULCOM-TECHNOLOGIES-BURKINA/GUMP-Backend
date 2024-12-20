package com.yulcomtechnologies.tresorms.repositories;

import com.yulcomtechnologies.tresorms.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
