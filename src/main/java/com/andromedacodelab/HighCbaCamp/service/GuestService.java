package com.andromedacodelab.HighCbaCamp.service;

import com.andromedacodelab.HighCbaCamp.model.Guest;
import com.andromedacodelab.HighCbaCamp.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuestService {
    private GuestRepository guestRepository;

    @Autowired
    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public Guest findGuest(Guest guest) {
        return guestRepository.findOne(guest);
    }
}
