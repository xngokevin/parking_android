package parking.app.data;

import android.util.Log;

/**
 * Created by atran on 2/26/2017.
 */

public class PurchaseDetail {

    private int id;
    private Location location;
    private int amount;
    private int location_id;
    private int parking_space_id;
    private boolean in_progress;
    private String start_time;
    private String end_time;
    private String email;

    public PurchaseDetail() {

    }

    public PurchaseDetail(int id, String email, Location location, int amount, int location_id, int parking_space_id, String start_time, String end_time, boolean in_progress) {
        this.id = id;
        this.location = location;
        this.amount = amount;
        this.parking_space_id = parking_space_id;
        this.in_progress = in_progress;
        this.start_time = start_time;
        this.end_time = end_time;
        this.email = email;
        this.location_id = location_id;
    }

    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public int getAmount() {
        return amount;
    }

    public int getSpaceId() {
        return parking_space_id;
    }

    public boolean getProgress(){
        return in_progress;
    }

    public void setStart_time(String start){
        start_time = start;
    }

    public String getStart_time(){
        return start_time;
    }

    public static String parseTimeToString(String start_time){
        String date = start_time.substring(0, start_time.indexOf("T"));
        String time = start_time.substring(start_time.indexOf("T")+1, start_time.indexOf("Z")-4);
        Log.d("PurchaseDetail",date);
        Log.d("PurchaseDetail",time);
        String result = date+", "+time;
        Log.d("PurchaseDetail",result);
        return result;
    }

    public String getEnd_time(){
        return end_time;
    }

    public void setEmail(String address){
        email = address;
    }

    public String getEmail(){
        return email;
    }

    public int getLocation_id(){
        return location_id;
    }
}
