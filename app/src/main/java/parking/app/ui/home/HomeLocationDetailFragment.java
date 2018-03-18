package parking.app.ui.home;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CollapsingToolbarLayout;
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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import parking.app.R;
import parking.app.api.ParkingRestClient;
import parking.app.data.Location;
import parking.app.data.LocationDetail;
import parking.app.style.DividerItemDecoration;
import parking.app.util.SessionManager;


public class HomeLocationDetailFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "name";
    private static final String ARG_PARAM3 = "address";
    private static final String ARG_PARAM4 = "description";

    private int id;
    private String name;
    private String address;
    private String description;

    //Http Parameters
    private final String ACCESS_TOKEN = "x-access-token";

    //Users Session
    private SessionManager manager;

    private List<LocationDetail> detail_list;
    private RecyclerView detail_list_view;
    private DetailAdapter detail_adapter;

    @Bind(R.id.location_title)
    TextView title_view;

    @Bind(R.id.location_title_description)
    TextView desc_view;

    @Bind(R.id.backdrop)
    ImageView backdropImg;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    public HomeLocationDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parking Detail ID
     * @return A new instance of fragment HomeLocationDetailFragment.
     */
    public static HomeLocationDetailFragment newInstance(int id, String name, String address, String description) {
        HomeLocationDetailFragment fragment = new HomeLocationDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, name);
        args.putString(ARG_PARAM3, address);
        args.putString(ARG_PARAM4, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Get id key here
            id = getArguments().getInt(ARG_PARAM1);
            name = getArguments().getString(ARG_PARAM2);
            address = getArguments().getString(ARG_PARAM3);
            description = getArguments().getString(ARG_PARAM4);
            Log.d("ARG_PARAM1",""+id);
            Log.d("ARG_PARAM2",name);
            Log.d("ARG_PARAM3",address);
            Log.d("ARG_PARAM4",description);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_location_detail, container, false);
        manager = new SessionManager(getActivity().getApplicationContext());
        getDetail();
        backdropImg = (ImageView)rootView.findViewById(R.id.home_location_backdrop);

        title_view = (TextView) rootView.findViewById(R.id.location_title);
        title_view.setText(name);
        desc_view = (TextView) rootView.findViewById(R.id.location_title_description);
        desc_view.setText(description);

        detail_list_view = (RecyclerView) rootView.findViewById(R.id.recycler_location_detail_view);
        detail_adapter = new DetailAdapter(detail_list);
        detail_list_view.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        detail_list_view.setAdapter(detail_adapter);
        final LinearLayoutManager layout_manager = new LinearLayoutManager(getActivity().getApplicationContext());
        detail_list_view.setLayoutManager(layout_manager);
        return rootView;
    }

    private void getDetail() {
        detail_list = new ArrayList<>();
        //Http Client
        ParkingRestClient client = new ParkingRestClient();
        client.addHeader(ACCESS_TOKEN, manager.getToken());
        client.get("parking/location"+"/"+id, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.d("DETAIL_FRAGMENT", response.toString());
                    for(int i = 0; i < response.length(); i++){
                        JSONObject jsonObj = response.getJSONObject(i);
                        int id = jsonObj.getInt("location_id");
                        int space_id = jsonObj.getInt("space_id");
                        String status = jsonObj.getString("status");
                        LocationDetail detail = new LocationDetail(id, space_id, status);
                        detail_list.add(detail);
                        //Log.d("Detail", detail.getSpaceId()+" "+detail.getStatus());
                    }
                    detail_adapter.notifyDataSetChanged();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    //Do stuff with JSON object
                    String id = response.getString("id");
                    Log.d("Home", id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
                Toast.makeText(getActivity().getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                if(responseError != null)
                    Log.d("JSON_ERR", responseError.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
                Log.d("HOME_FRAGMENT", responseError);
                Toast.makeText(getActivity().getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });
    }

    private class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.MyViewHolder>{
        private List<LocationDetail> location_list;

        public DetailAdapter(List<LocationDetail> location_list) {
            this.location_list = location_list;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView parking_id, parking_status;
            MyViewHolder(View view) {
                super(view);
                parking_id = (TextView) view.findViewById(R.id.home_location_detail_space);
                parking_status = (TextView) view.findViewById(R.id.home_location_detail_status);
            }
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the custom layout
            View location_view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.list_item_detail, parent, false);
            // Return a new holder instance
            return new MyViewHolder(location_view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final LocationDetail location = location_list.get(position);
            holder.parking_id.setText("Slot ID: "+String.valueOf(location.getSpaceId()));
            holder.parking_status.setText(location.getStatus().substring(0, 1).toUpperCase() + location.getStatus().substring(1));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                int id = location.getId();
                int parking_id = location.getSpaceId();
                String status = location.getStatus();
                @Override
                public void onClick(View v) {
                    if(status.equals("unoccupied")) {
                        Toast.makeText(getActivity().getApplicationContext(), "" + id, Toast.LENGTH_LONG).show();

                        //Display dialog fragment
                        HomeDialogFragment dialog = HomeDialogFragment.newInstance(id, parking_id);
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
                           @Override
                            public void onDismiss(DialogInterface di) {
                                getDetail();
                                detail_adapter.notifyDataSetChanged();
                           }
                        });
                        dialog.show(getFragmentManager(), "dialog");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return location_list.size();
        }
    }
}
