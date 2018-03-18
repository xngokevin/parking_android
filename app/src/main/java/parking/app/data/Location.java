package parking.app.data;

/**
 * Created by xngok on 1/30/2017.
 */

public class Location {

    int id;
    String name;
    String address;
    String description;

    public Location() {

    }

    public Location(int id, String name, String address, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }
}
