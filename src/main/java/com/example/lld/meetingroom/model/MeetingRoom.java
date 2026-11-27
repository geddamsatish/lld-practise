package com.example.lld.meetingroom.model;

import com.example.lld.meetingroom.policy.ConflictPolicy;
import com.example.lld.meetingroom.policy.OverlapConflictPolicy;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class MeetingRoom {
    private final String roomId;
    private final ConflictPolicy conflictPolicy;
    private final TreeMap<LocalDateTime, Booking> bookings = new TreeMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public MeetingRoom(String roomId) {
        this(roomId, new OverlapConflictPolicy());
    }

    public MeetingRoom(String roomId, ConflictPolicy conflictPolicy) {
        this.roomId = roomId;
        this.conflictPolicy = conflictPolicy;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean isAvailable(LocalDateTime start, LocalDateTime end) {
        lock.lock();
        try {
            return isAvailableInternal(start, end);
        } finally {
            lock.unlock();
        }
    }

    private boolean isAvailableInternal(LocalDateTime start, LocalDateTime end) {
        return bookings.values().stream()
                .noneMatch(existing -> conflictPolicy.isConflict(existing, start, end));
    }

    public Optional<Booking> tryBook(LocalDateTime start, LocalDateTime end, String userId) {
        if (!start.isBefore(end)) {
            return Optional.empty();
        }
        lock.lock();
        try {
            if (!isAvailableInternal(start, end)) {
                return Optional.empty();
            }
            Booking booking = new Booking(
                    UUID.randomUUID().toString(),
                    roomId,
                    userId,
                    start,
                    end
            );
            bookings.put(start, booking);
            return Optional.of(booking);
        } finally {
            lock.unlock();
        }
    }
}
