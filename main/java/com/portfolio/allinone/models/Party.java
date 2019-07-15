package com.portfolio.allinone.models;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
//дата хранится в форме long
@Entity
@Table(name = "party")
public class Party {
    @Id
    private String name;
    private Long date;
    public Party() {
    }

    public Party(String name, Long date) {
        this.name = name;
        this.date = date;
        System.out.println(this.date.toString());
    }

    public String getName() {
        return name;
    }

    public Long getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDate(Long date) {
        this.date = date;
    }
}