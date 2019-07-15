package com.portfolio.allinone.repo;

import com.portfolio.allinone.models.TeleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface TeleUserRepo extends JpaRepository<TeleUser, Integer>
{
    Integer countByTeleuser(Long userid);

    @Transactional
    Long deleteByTeleuser(Long userid);
    @Transactional
    TeleUser findByTeleuser(Long userid);
    @Transactional
    TeleUser findByTelerole(Integer role);

    @Transactional
    boolean existsByTeleuser(Long userid);

}
