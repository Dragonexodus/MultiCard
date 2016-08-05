package view.model;

import application.applet.AccessRestrictedRoom;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 * Created by Patrick on 07.07.2015.
 */
public class AccessModel {
    private StringProperty checkStatus = new SimpleStringProperty("Uncheckd");
    private ObjectProperty<Paint> checkStatusColor = new SimpleObjectProperty<>(Color.ORANGE);

    private ObjectProperty<AccessRestrictedRoom> selectedRoom = new SimpleObjectProperty<>();
    private ListProperty<AccessRestrictedRoom> rooms = new SimpleListProperty<>();

    private IntegerProperty ageRestriction = new SimpleIntegerProperty();

    public String getCheckStatus() {
        return checkStatus.get();
    }

    public StringProperty checkStatusProperty() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus.set(checkStatus);
    }

    public Paint getCheckStatusColor() {
        return checkStatusColor.get();
    }

    public ObjectProperty<Paint> checkStatusColorProperty() {
        return checkStatusColor;
    }

    public void setCheckStatusColor(Paint checkStatusColor) {
        this.checkStatusColor.set(checkStatusColor);
    }

    public AccessRestrictedRoom getSelectedRoom() {
        return selectedRoom.get();
    }

    public ObjectProperty<AccessRestrictedRoom> selectedRoomProperty() {
        return selectedRoom;
    }

    public void setSelectedRoom(AccessRestrictedRoom selectedRoom) {
        this.selectedRoom.set(selectedRoom);
    }

    public javafx.collections.ObservableList<AccessRestrictedRoom> getRooms() {
        return rooms.get();
    }

    public ListProperty<AccessRestrictedRoom> roomsProperty() {
        return rooms;
    }

    public void setRooms(javafx.collections.ObservableList<AccessRestrictedRoom> rooms) {
        this.rooms.set(rooms);
    }

    public int getAgeRestriction() {
        return ageRestriction.get();
    }

    public IntegerProperty ageRestrictionProperty() {
        return ageRestriction;
    }

    public void setAgeRestriction(int ageRestriction) {
        this.ageRestriction.set(ageRestriction);
    }
}
