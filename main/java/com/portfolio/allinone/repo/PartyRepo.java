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

    //запрос для поиска в диапазоне
    @Transactional
    List<Party> findAllByDateBetween(Long x, Long y);

    //запрос для поиска событий без даты (указывается 0)
    @Transactional
    List<Party> findAllByDate(Long d);


    //запрос для скрытия прошедших дат
    @Transactional
    List<Party> findAllByDateAfter(Long d);


}
