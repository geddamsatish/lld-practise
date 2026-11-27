package com.example.lld.meetingroom;

import com.example.lld.meetingroom.listener.BookingListener;
import com.example.lld.meetingroom.model.Booking;
import com.example.lld.meetingroom.model.BookingResult;
import com.example.lld.meetingroom.model.Floor;
import com.example.lld.meetingroom.model.MeetingRoom;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class MeetingSchedular {
    private final Map<String, MeetingRoom> rooms;
    private final List<BookingListener> listeners = new CopyOnWriteArrayList<>();

    public MeetingSchedular(List<Floor> floors) {
        Map<String, MeetingRoom> index = new LinkedHashMap<>();
        for (Floor floor : floors) {
            for (MeetingRoom room : floor.getRooms()) {
                index.putIfAbsent(room.getRoomId(), room);
            }
        }
        this.rooms = Collections.unmodifiableMap(index);
    }

    public void addBookingListener(BookingListener listener) {
        listeners.add(listener);
    }

    public List<MeetingRoom> findAvailableMeetingRooms(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            return Collections.emptyList();
        }
        return rooms.values().stream()
                .filter(room -> room.isAvailable(start, end))
                .toList();
    }

    public BookingResult bookRoom(String roomId, String userId, LocalDateTime start, LocalDateTime end) {
        MeetingRoom room = rooms.get(roomId);
        if (room == null) {
            return BookingResult.failure("Meeting Room Not Found");
        }
        if (!isValidSlot(start, end)) {
            return BookingResult.failure("Invalid meeting time slot");
        }
        Optional<Booking> booking = room.tryBook(start, end, userId);
        if (booking.isEmpty()) {
            return BookingResult.failure("Meeting Room is not available for the requested slot");
        }
        notifyListeners(booking.get());
        return BookingResult.success(booking.get());
    }

    private static boolean isValidSlot(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null && start.isBefore(end);
    }

    private void notifyListeners(Booking booking) {
        for (BookingListener listener : listeners) {
            try {
                listener.onBookingConfirmed(booking);
            } catch (RuntimeException e) {
                // a failing listener must not break the booking
            }
        }
    }
}
