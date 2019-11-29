package com.andromedacodelab.HighCbaCamp.model.builder;

import com.andromedacodelab.HighCbaCamp.model.Guest;

public class GuestBuilder {
    private Guest guest;

    public GuestBuilder() {
        this.guest = new Guest();
    }

    public Guest build() {
        return guest;
    }

    public GuestBuilder withId(Integer id) {
        guest.setId(id);
        return this;
    }

    public GuestBuilder withFirstName(String firstName) {
        guest.setFirstName(firstName);
        return this;
    }

    public GuestBuilder withLastName(String lastName) {
        guest.setLastName(lastName);
        return this;
    }

    public GuestBuilder withEmail(String email) {
        guest.setEmail(email);
        return this;
    }

    public GuestBuilder withIsReservationHolder(boolean isHolder) {
        guest.setReservationHolder(isHolder);
        return this;
    }
}
