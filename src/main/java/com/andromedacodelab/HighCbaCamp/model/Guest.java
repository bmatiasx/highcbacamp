package com.andromedacodelab.HighCbaCamp.model;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "\"GUESTS\"")
public class Guest {

    @Id
    @Column(name = "\"ID\"")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "\"FIRST_NAME\"")
    private String firstName;

    @Column(name = "\"LAST_NAME\"")
    private String lastName;

    @Column(name = "\"EMAIL\"")
    private String email;

    @Column(name = "\"IS_RESERVATION_HOLDER\"")
    private Boolean isReservationHolder;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isReservationHolder() {
        if (isReservationHolder == null) {
            isReservationHolder = false;
        }
        return isReservationHolder;
    }

    public void setReservationHolder(Boolean reservationHolder) {
        isReservationHolder = reservationHolder;
    }
}
