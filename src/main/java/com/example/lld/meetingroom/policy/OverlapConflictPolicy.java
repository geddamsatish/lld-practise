package com.example.lld.meetingroom.policy;

import com.example.lld.meetingroom.model.Booking;

import java.time.LocalDateTime;

/**
 * Default policy: two bookings conflict only if their time ranges overlap.
 * Back-to-back bookings (one ends exactly when the next starts) are allowed.
 */
public class OverlapConflictPolicy implements ConflictPolicy {
    @Override
    public boolean isConflict(Booking existing, LocalDateTime start, LocalDateTime end) {
        return existing.start().isBefore(end) && start.isBefore(existing.end());
    }
}
