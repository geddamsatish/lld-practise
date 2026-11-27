package com.example.lld.meetingroom.model;

import java.util.List;

public class Floor {
    private final String floorId;
    private final List<MeetingRoom> rooms;

    public Floor(String floorId, List<MeetingRoom> rooms) {
        this.floorId = floorId;
        this.rooms = List.copyOf(rooms);
    }

    public String getFloorId() { return floorId; }
    public List<MeetingRoom> getRooms() { return rooms; }
}
