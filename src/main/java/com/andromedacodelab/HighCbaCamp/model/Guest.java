package com.andromedacodelab.HighCbaCamp.model;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "\"FIRST_NAME\"")
    private String firstName;

    @Column(name = "\"LAST_NAME\"")
    private String lastName;

    @Column(name = "\"EMAIL\"")
    private String email;

    @Column(name = "\"IS_HOLDER\"")
    private boolean isReservationHolder = false;

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

    public boolean isReservationHolder() {
        return isReservationHolder;
    }

    public void setReservationHolder(boolean reservationHolder) {
        isReservationHolder = reservationHolder;
    }
}
