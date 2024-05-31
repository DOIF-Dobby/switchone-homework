package org.mj.switchwon.domain.wallet.using;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 지갑 사용 이력 Repository
 */
public interface UsingLogRepository extends JpaRepository<UsingLog, Long> {
}
