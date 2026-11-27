package com.example.lld.meetingroom.listener;

import com.example.lld.meetingroom.model.Booking;

public class EmailBookingListener implements BookingListener {
    @Override
    public void onBookingConfirmed(Booking booking) {
        System.out.println("Booking confirmed!" + booking);
    }
}
