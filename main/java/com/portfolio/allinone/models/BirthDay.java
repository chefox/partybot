package com.portfolio.allinone.models;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;


//дата хранится в отдельных полях дня и месяца
@Entity
@Table(name = "birthday")
public class BirthDay {
    @Id
    private String name;
    private Date date;
    public BirthDay() {
    }

    public BirthDay(String name, Date date) {
        this.name = name;
        this.date=date;
    }


    public String getName() {
        return name;
    }


    public Date getDate() {
        return date;
    }
}