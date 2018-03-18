package parking.app.data;

/**
 * Created by atran on 2/11/2017.
 */

public class LocationDetail {
    int id;
    int space_id;
    String status;

    public LocationDetail() {

    }

    public LocationDetail(int id, int space_id, String status) {
        this.id = id;
        this.space_id = space_id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getSpaceId() {
        return space_id;
    }

    public String getStatus() {
        return status;
    }

}
