package com.example.lld.meetingroom;

import com.example.lld.meetingroom.listener.BookingListener;
import com.example.lld.meetingroom.model.Booking;
import com.example.lld.meetingroom.model.BookingResult;
import com.example.lld.meetingroom.model.Floor;
import com.example.lld.meetingroom.model.MeetingRoom;
import com.example.lld.meetingroom.policy.BufferedConflictPolicy;
import com.example.lld.meetingroom.policy.OverlapConflictPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeetingSchedularTest {

    private static final LocalDateTime DAY = LocalDateTime.of(2026, 11, 27, 0, 0);

    private MeetingSchedular scheduler;
    private List<Booking> confirmed;

    @BeforeEach
    void setUp() {
        MeetingRoom r101 = new MeetingRoom("R101", new OverlapConflictPolicy());
        MeetingRoom r102 = new MeetingRoom("R102", new OverlapConflictPolicy());
        // R201 keeps a 15-minute buffer between meetings (e.g. for cleaning).
        MeetingRoom r201 = new MeetingRoom("R201", new BufferedConflictPolicy(Duration.ofMinutes(15)));

        scheduler = new MeetingSchedular(List.of(
                new Floor("F1", List.of(r101, r102)),
                new Floor("F2", List.of(r201))
        ));

        confirmed = new ArrayList<>();
        BookingListener collector = confirmed::add;
        scheduler.addBookingListener(collector);
    }

    private static LocalDateTime at(int hour, int minute) {
        return DAY.withHour(hour).withMinute(minute);
    }

    @Test
    void bookingFreeSlotSucceedsAndNotifiesListener() {
        BookingResult result = scheduler.bookRoom("R101", "alice", at(9, 0), at(10, 0));

        assertTrue(result.success());
        assertNotNull(result.bookingId());
        assertEquals(1, confirmed.size());
        assertEquals("alice", confirmed.get(0).userId());
    }

    @Test
    void overlappingBookingIsRejectedAndNotNotified() {
        scheduler.bookRoom("R101", "alice", at(9, 0), at(10, 0));

        BookingResult result = scheduler.bookRoom("R101", "bob", at(9, 30), at(10, 30));

        assertFalse(result.success());
        assertEquals("Meeting Room is not available for the requested slot", result.message());
        assertEquals(1, confirmed.size(), "Only alice's booking should be confirmed");
    }

    @Test
    void backToBackBookingIsAllowedUnderOverlapPolicy() {
        scheduler.bookRoom("R101", "alice", at(9, 0), at(10, 0));

        BookingResult result = scheduler.bookRoom("R101", "erin", at(10, 0), at(11, 0));

        assertTrue(result.success());
        assertEquals(2, confirmed.size());
    }

    @Test
    void endBeforeStartIsRejected() {
        BookingResult result = scheduler.bookRoom("R102", "carol", at(10, 0), at(9, 0));

        assertFalse(result.success());
        assertEquals("Invalid meeting time slot", result.message());
    }

    @Test
    void unknownRoomIsRejected() {
        BookingResult result = scheduler.bookRoom("R999", "dave", at(9, 0), at(10, 0));

        assertFalse(result.success());
        assertEquals("Meeting Room Not Found", result.message());
    }

    @Test
    void availableRoomsExcludeRoomsThatAreBooked() {
        scheduler.bookRoom("R101", "alice", at(9, 0), at(10, 0));

        List<String> available = scheduler.findAvailableMeetingRooms(at(9, 30), at(10, 30))
                .stream()
                .map(MeetingRoom::getRoomId)
                .toList();

        assertEquals(List.of("R102", "R201"), available);
    }

    @Test
    void bufferedPolicyRejectsBookingInsideBuffer() {
        scheduler.bookRoom("R201", "frank", at(11, 0), at(12, 0));

        // Starts 5 minutes after frank ends: inside the 15-minute buffer.
        BookingResult result = scheduler.bookRoom("R201", "gina", at(12, 5), at(13, 0));

        assertFalse(result.success());
    }

    @Test
    void bufferedPolicyAllowsBookingExactlyAtBufferEdge() {
        scheduler.bookRoom("R201", "frank", at(11, 0), at(12, 0));

        // Starts exactly 15 minutes after frank ends: the buffer has just cleared.
        BookingResult result = scheduler.bookRoom("R201", "hank", at(12, 15), at(13, 0));

        assertTrue(result.success());
    }

    @Test
    void overlapPolicyTreatsTouchingIntervalsAsNonConflicting() {
        Booking existing = new Booking("id", "R", "alice", at(9, 0), at(10, 0));
        OverlapConflictPolicy policy = new OverlapConflictPolicy();

        assertFalse(policy.isConflict(existing, at(10, 0), at(11, 0)));
        assertTrue(policy.isConflict(existing, at(9, 59), at(11, 0)));
    }
}
