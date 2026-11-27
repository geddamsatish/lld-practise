package com.example.lld.meetingroom.policy;

import com.example.lld.meetingroom.model.Booking;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Policy that keeps a buffer (e.g. for room cleaning or setup) around every booking.
 * A request conflicts if it falls within the buffer of an existing booking.
 */
public class BufferedConflictPolicy implements ConflictPolicy {
    private final Duration buffer;

    public BufferedConflictPolicy(Duration buffer) {
        this.buffer = buffer;
    }

    @Override
    public boolean isConflict(Booking existing, LocalDateTime start, LocalDateTime end) {
        LocalDateTime existingStart = existing.start().minus(buffer);
        LocalDateTime existingEnd = existing.end().plus(buffer);
        return existingStart.isBefore(end) && start.isBefore(existingEnd);
    }
}
