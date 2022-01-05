package com.project.app.service;

import com.project.app.entity.Room;
import com.project.app.entity.RoomMember;

import java.util.List;

public interface RoomService {
    public Room create(Room room);
    public Room getRoomById(String id);
    public List<Room> getRooms();
    public String deleteRoomById(String id);

    public Room addMemberToRoom(String roomId, RoomMember roomMember);
}
