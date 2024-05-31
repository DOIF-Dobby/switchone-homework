package org.mj.switchwon.domain.wallet.charging;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 지갑 충전 이력 Repository
 */
public interface ChargingLogRepository extends JpaRepository<ChargingLog, Long> {
}
