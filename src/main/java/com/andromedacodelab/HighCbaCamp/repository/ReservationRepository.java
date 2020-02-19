package com.andromedacodelab.HighCbaCamp.repository;

import com.andromedacodelab.HighCbaCamp.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Override
    <S extends Reservation> S save(S s);
}
