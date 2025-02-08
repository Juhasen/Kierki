package pl.juhas.kierki;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String id;
    private List<Client> players = new ArrayList<>();

    public Room(String id) {
        this.id = id;
        this.players = null;
    }

    public String getId() {
        return id;
    }

}
