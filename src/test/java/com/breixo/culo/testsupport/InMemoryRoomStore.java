package com.breixo.culo.testsupport;

import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.port.output.room.RoomRetrievalPersistencePort;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** The Class In Memory Room Store. */
public class InMemoryRoomStore implements RoomSavePersistencePort, RoomRetrievalPersistencePort {

    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    /**
     * Seed.
     *
     * @param room the room
     */
    public void seed(final Room room) {
        this.rooms.put(room.roomLobby().code(), room);
    }

    /** {@inheritDoc} */
    @Override
    public Room save(final Room room) {
        this.rooms.put(room.roomLobby().code(), room);
        return room;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Room> findByCode(final String roomCode) {
        return Optional.ofNullable(this.rooms.get(roomCode));
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Room> findAll() {
        return this.rooms.values();
    }
}
