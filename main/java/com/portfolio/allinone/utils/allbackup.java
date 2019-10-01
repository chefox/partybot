package com.portfolio.allinone.utils;

import com.portfolio.allinone.models.BirthDay;
import com.portfolio.allinone.models.Party;
import com.portfolio.allinone.repo.BirthRepo;
import com.portfolio.allinone.repo.PartyRepo;

public class allbackup {
    public static String getAllDates(JpaRepository repo){
        String allPartyList="";
        if(repo instanceof BirthRepo){
            for(BirthDay b: (BirthRepo)repo){
                allPartyList+=b.getName()+"    "+b.getDate()+"\n";
            }
        }
        else{
            for(Party p: (PartyRepo)repo){
                allPartyList+=p.getName()+"    "+p.getDate()+"\n";
            }
        }
        return allPartyList;

    }
}
