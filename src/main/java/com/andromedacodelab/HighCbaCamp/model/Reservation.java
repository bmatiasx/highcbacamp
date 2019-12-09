package com.andromedacodelab.HighCbaCamp.model;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.YEAR_MONTH_DAY;
import static com.andromedacodelab.HighCbaCamp.util.RestApiConstants.YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS;

@Entity
@Table(name = "\"RESERVATIONS\"")
public class Reservation {

    @Id
    @Column(name = "\"BOOKING_ID\"")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;

    @Column(name = "\"ARRIVAL\"")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = YEAR_MONTH_DAY)
    private  LocalDate arrival;

    @Column(name = "\"DEPARTURE\"")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = YEAR_MONTH_DAY)
    private LocalDate departure;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "\"STATUS_ID\"")
    @JsonRawValue
    private ReservationStatus status;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "\"GUESTS_BY_RESERVATION\"",
            joinColumns = @JoinColumn(name = "\"BOOKING_ID\""),
            inverseJoinColumns = @JoinColumn(name = "\"GUEST_ID\"")
    )
    private Set<Guest> guests;

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDate getArrival() {
        return arrival;
    }

    public void setArrival(LocalDate arrival) {
        this.arrival = arrival;
    }

    public LocalDate getDeparture() {
        return departure;
    }

    public void setDeparture(LocalDate departure) {
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
