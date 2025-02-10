package pl.juhas.kierki;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;


public class LobbyController {

    @FXML
    private Label UsernameLabel;

    public VBox roomsContainer;
    private Client client;

    private String username;


    public void setUsername(String username) {
        this.username = username;
        UsernameLabel.setText(username);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void onJoinRoomButtonClick() {

    }

    public void getAvailableRooms() {
        List<String> rooms = client.getAvailableRooms();
        for (String room : rooms) {
            Label roomLabel = new Label(room);
            roomsContainer.getChildren().add(roomLabel);
        }
        System.out.println(rooms);
    }

    @FXML
    protected void onCreateRoomButtonClick() {
        client.createRoom();
        updateRooms();
    }

    private void updateRooms() {
        roomsContainer.getChildren().clear();
        getAvailableRooms();
    }
}
