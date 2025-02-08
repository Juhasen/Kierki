package pl.juhas.kierki;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


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
        for(Room room : client.getRooms()) {
            roomsContainer.getChildren().add(new Label(room.getId()));
        }
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
