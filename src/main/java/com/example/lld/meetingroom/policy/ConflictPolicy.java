package com.example.lld.meetingroom.policy;

import com.example.lld.meetingroom.model.Booking;

import java.time.LocalDateTime;

/**
 * Strategy that decides whether a requested slot clashes with an existing booking.
 */
public interface ConflictPolicy {
    boolean isConflict(Booking existing, LocalDateTime start, LocalDateTime end);
}
