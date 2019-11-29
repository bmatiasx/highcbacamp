package com.andromedacodelab.HighCbaCamp.repository;

import com.andromedacodelab.HighCbaCamp.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationStatusesRepository extends JpaRepository<ReservationStatus, Integer> {
    ReservationStatus findByName(String statusName);
}
