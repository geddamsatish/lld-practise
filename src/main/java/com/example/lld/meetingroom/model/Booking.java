package com.example.lld.meetingroom.model;

import java.time.LocalDateTime;

public record Booking(
        String bookingId,
        String roomId,
        String userId,
        LocalDateTime start,
        LocalDateTime end
) {
}
