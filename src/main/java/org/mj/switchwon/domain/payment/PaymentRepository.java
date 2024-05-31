package org.mj.switchwon.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 결제 정보 Repository
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
