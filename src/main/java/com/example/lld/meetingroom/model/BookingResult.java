package com.example.lld.meetingroom.model;

public record BookingResult(
        boolean success,
        String message,
        String bookingId
) {
    public static BookingResult failure(String message) {
        return new BookingResult(false, message, null);
    }

    public static BookingResult success(Booking booking) {
        return new BookingResult(true, "Room booked successfully", booking.bookingId());
    }
}
