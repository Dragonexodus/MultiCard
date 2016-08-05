package application.applet;

/**
 * Created by Patrick on 07.07.2015.
 */
public enum AccessRestrictedRoom {
    ClassicBar(110), // 01-10
    Casino(177), // 01-77
    Pool(5), // 00-05
    SkyBar(1201), // 12-01
    Wellness(12); // 00-12

    private final int value;

    AccessRestrictedRoom(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
