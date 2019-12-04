package com.andromedacodelab.HighCbaCamp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "\"RESERVATION_STATUSES\"")
public class ReservationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"")
    @JsonIgnore
    private Integer id;

    public ReservationStatus() {
    }

    public ReservationStatus(String name) {
        this.name = name;
    }

    public ReservationStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    @Column(name = "\"NAME\"")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "\"" + name + "\"";
    }
}
