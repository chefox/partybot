package com.portfolio.allinone.repo;

import com.portfolio.allinone.models.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PartyRepo extends JpaRepository<Party, Integer>
{
    Integer countByName(String Name);

    @Transactional
    Integer deleteByName(String Name);
    @Transactional
    Party getByName(String Name);

}
