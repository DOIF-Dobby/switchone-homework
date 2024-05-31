package org.mj.switchwon.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 정보 Repository
 */
public interface UserRepository extends JpaRepository<User, String> {
}
