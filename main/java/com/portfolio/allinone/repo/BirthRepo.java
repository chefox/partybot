package com.portfolio.allinone.repo;

import com.portfolio.allinone.models.BirthDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BirthRepo extends JpaRepository<BirthDay, Integer>
{
    Integer countByName(String Name);

    @Transactional
    Integer deleteByName(String Name);
    @Transactional
    BirthDay getByName(String Name);
}
