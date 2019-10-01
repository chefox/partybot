package com.portfolio.allinone.models;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

//дата хранится в форме long
@Entity
@Table(name = "party")
public class Party {
    @Id
    private String name;
    private Date date;
    public Party() {
    }

    public Party(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDate(Date date) {
        this.date = date;
    }
}