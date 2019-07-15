package com.portfolio.allinone.models;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "teleuser")
public class TeleUser
{
    @Id
    private Long teleuser;
    private Integer telerole;
// test
    public TeleUser()
    {
    }

    public TeleUser(Long userid, Integer role)
    {

        this.teleuser = userid;
        this.telerole = role;
    }

    public Long getTeleuser() {
        return teleuser;
    }

    public void setTeleuser(Long userid) {
        this.teleuser = userid;
    }

    public Integer getTelerole() {
        return telerole;
    }

    public void setTelerole(Integer role) {
        this.telerole = role;
    }
}