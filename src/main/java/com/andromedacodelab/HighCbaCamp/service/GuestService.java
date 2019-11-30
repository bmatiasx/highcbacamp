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

    public void create(Guest guest) {
        guestRepository.save(guest);
    }

    public boolean guestExists(Guest guest) {
        return guestRepository.existsGuestByFirstNameAndLastNameAndEmail(guest.getFirstName(), guest.getLastName(), guest.getEmail());
    }

    public Integer findByExistingGuestId(String firstName, String lastName, String email) {
        return guestRepository.findGuestByFirstNameAndLastNameAndEmail(firstName, lastName, email).getId();
    }
}
