package parking.app.ui.home;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import parking.app.R;
import parking.app.api.ParkingRestClient;
import parking.app.data.Location;
import parking.app.style.DividerItemDecoration;
import parking.app.ui.login.LoginActivity;
import parking.app.util.SessionManager;


public class HomeLocationFragment extends Fragment {

    private static final String TAG = "HomeLocationFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String LOCATION_DETAIL_FRAGMENT = "location__detail_fragment";

    //Http Parameters
    private final String ACCESS_TOKEN = "x-access-token";

    //Users Session
    private SessionManager manager;

    private RecyclerView location_list_view;
    private LocationAdapter location_adapter;

    private List<Location> location_list;
    private HomeLocationDetailFragment home_location_detail_fragment;

    public HomeLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home_location, container, false);
        manager = new SessionManager(getActivity().getApplicationContext());
        getLocation();
        location_list_view = (RecyclerView) view.findViewById(R.id.recycler_location_view);
        location_adapter = new LocationAdapter(location_list);
        location_list_view.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        location_list_view.setAdapter(location_adapter);
        final LinearLayoutManager layout_manager = new LinearLayoutManager(getActivity().getApplicationContext());
        location_list_view.setLayoutManager(layout_manager);
        return view;
    }

    private class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder>{
        private List<Location> location_list;

        public LocationAdapter(List<Location> location_list) {
            this.location_list = location_list;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView location_name, location_description;

            MyViewHolder(View view) {
                super(view);
                location_name = (TextView) view.findViewById(R.id.home_location_name);
                location_description = (TextView) view.findViewById(R.id.home_location_description);
            }
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the custom layout
            View location_view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.list_item_location, parent, false);
            // Return a new holder instance
            return new MyViewHolder(location_view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final Location location = location_list.get(position);
            holder.location_name.setText(location.getName());
            holder.location_description.setText(location.getDescription());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                int id = location.getId();
                String name = location.getName();
                String address = location.getAddress();
                String description = location.getDescription();
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity().getApplicationContext(), ""+id, Toast.LENGTH_LONG).show();
                    //Open LocationDetail Fragment
                    home_location_detail_fragment = HomeLocationDetailFragment.newInstance(id, name, address, description);
                    setFragment(home_location_detail_fragment, LOCATION_DETAIL_FRAGMENT);
                }
            });

        }

        @Override
        public int getItemCount() {
            return location_list.size();
        }
    }

    private void getLocation() {
        location_list = new ArrayList<>();

        //Http Client
        ParkingRestClient client = new ParkingRestClient();
        client.addHeader(ACCESS_TOKEN, manager.getToken());
        client.get("parking/location", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.d(TAG, response.toString());
                    for(int i = 0; i < response.length(); i++){
                        JSONObject jsonObj = response.getJSONObject(i);
                        int id = jsonObj.getInt("id");
                        String name = jsonObj.getString("name");
                        String description = jsonObj.getString("description");
                        String address = jsonObj.getString("address");
                        String image = jsonObj.getString("image");
                        Location location = new Location(id, name, description, address);
                        location_list.add(location);
                    }
                    location_adapter.notifyDataSetChanged();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //Do stuff with JSON object
                    String id = response.getString("id");
                    Log.d(TAG, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
                Toast.makeText(getActivity().getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                if(responseError != null) {
                    Log.d(TAG, responseError.toString());
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    manager.logOut();
                    startActivity(intent);
                }
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
                Log.d(TAG, responseError);
                Toast.makeText(getActivity().getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });
    }
    private void setFragment(Fragment frag, String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.home_fragment, frag, tag);
        ft.addToBackStack(null);
        ft.commit();
    }

}
