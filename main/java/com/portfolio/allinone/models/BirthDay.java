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
    private Integer month;
    private Integer day;
    public BirthDay() {
    }

    public BirthDay(String name, Integer month, Integer day) {
        this.name = name;
        this.day = day;
        this.month = month;
    }


    public String getName() {
        return name;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }
}