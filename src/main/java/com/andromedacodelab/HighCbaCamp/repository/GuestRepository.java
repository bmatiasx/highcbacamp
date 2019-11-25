package com.andromedacodelab.HighCbaCamp.repository;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Integer> {
}
