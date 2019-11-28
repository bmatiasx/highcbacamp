package com.andromedacodelab.HighCbaCamp.model;

import com.andromedacodelab.HighCbaCamp.util.CampApiUtility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "\"RESERVATIONS\"")
public class Reservation {

    @Id
    @Column(name = "\"BOOKING_ID\"")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int bookingId;

    @Column(name = "\"ARRIVAL\"")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a")
    private LocalDateTime arrival;

    @Column(name = "\"DEPARTURE\"")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a")
    private LocalDateTime departure;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "\"STATUS_ID\"")
    @JsonRawValue
    private ReservationStatus status;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "\"GUESTS_BY_RESERVATION\"",
            joinColumns = @JoinColumn(name = "\"BOOKING_ID\""),
            inverseJoinColumns = @JoinColumn(name = "\"GUEST_ID\"")
    )
    private Set<Guest> guests;

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDateTime getArrival() {
        return arrival;
    }

    public void setArrival(LocalDateTime arrival) {
        this.arrival = arrival;
    }

    public LocalDateTime getDeparture() {
        return CampApiUtility.addWholeDay(departure);
    }

    public void setDeparture(LocalDateTime departure) {
        this.departure = departure;
    }

    public Set<Guest> getGuests() {
        return guests;
    }

    public void setGuests(Set<Guest> guests) {
        this.guests = guests;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
