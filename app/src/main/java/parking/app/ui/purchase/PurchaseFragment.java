package parking.app.ui.purchase;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import parking.app.R;
import parking.app.api.ParkingRestClient;
import parking.app.data.Location;
import parking.app.data.PurchaseDetail;
import parking.app.style.DividerItemDecoration;
import parking.app.ui.base.BaseFragment;
import parking.app.ui.purchase.PurchaseDetailFragment;
import parking.app.util.SessionManager;

import static parking.app.data.PurchaseDetail.parseTimeToString;

public class PurchaseFragment extends BaseFragment {
    private static final String TAG = "PurchaseFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String PURCHASE_DETAIL_FRAGMENT = "purchase__detail_fragment";

    //Http Parameters
    private final String ACCESS_TOKEN = "x-access-token";

    //Users Session
    private SessionManager manager;

    private RecyclerView purchase_list_view;
    private PurchaseAdapter purchase_adapter;
    private List<PurchaseDetail> purchase_list;
    private PurchaseDetailFragment purchase_detail_fragment;

    public PurchaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Edit view for purchases
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_purchase, container, false);
        manager = new SessionManager(getActivity().getApplicationContext());
        getPurchases();
        purchase_list_view = (RecyclerView) view.findViewById(R.id.recycler_purchase_view);
        purchase_adapter = new PurchaseAdapter(purchase_list);
        purchase_list_view.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        purchase_list_view.setAdapter(purchase_adapter);
        final LinearLayoutManager layout_manager = new LinearLayoutManager(getActivity().getApplicationContext());
        purchase_list_view.setLayoutManager(layout_manager);
        return view;
    }

    private class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.MyViewHolder>{
        private List<PurchaseDetail> purchase_list;

        public PurchaseAdapter(List<PurchaseDetail> purchase_list) {
            this.purchase_list = purchase_list;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView purchase_location_name, purchase_location_date, purchase_location_amount, purchase_location_paid;
            MyViewHolder(View view) {
                super(view);
                purchase_location_name = (TextView) view.findViewById(R.id.purchase_location_name);
                purchase_location_date = (TextView) view.findViewById(R.id.purchase_location_date);
                purchase_location_amount = (TextView) view.findViewById(R.id.purchase_location_amount);
                purchase_location_paid = (TextView) view.findViewById(R.id.purchase_location_paid);
            }
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the custom layout
            View location_view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.list_item_purchase, parent, false);
            // Return a new holder instance
            return new MyViewHolder(location_view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final PurchaseDetail detail = purchase_list.get(position);
            holder.purchase_location_name.setText(detail.getLocation().getName());
            holder.purchase_location_date.setText(parseTimeToString(detail.getStart_time()));
            holder.purchase_location_amount.setText(NumberFormat.getCurrencyInstance().format(detail.getAmount()/100));
            if(detail.getProgress()){
                holder.purchase_location_paid.setText("In Progress");
            }
            else{
                holder.purchase_location_paid.setText("Completed");
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                int id = detail.getId();
                Location location = detail.getLocation();
                @Override
                public void onClick(View v) {
                    //Open PurchaseDetailFragment
                    purchase_detail_fragment = PurchaseDetailFragment.newInstance(detail.getEmail(), detail.getLocation().getName(), detail.getLocation().getAddress(), detail.getAmount(),
                            detail.getLocation_id(),detail.getSpaceId(), detail.getStart_time(), detail.getEnd_time(),detail.getProgress());
                    setFragment(purchase_detail_fragment, PURCHASE_DETAIL_FRAGMENT);
                }
            });

        }

        @Override
        public int getItemCount() {
            return purchase_list.size();
        }
    }

    private void getPurchases() {
        purchase_list = new ArrayList<>();

        //Http Client
        ParkingRestClient client = new ParkingRestClient();
        client.addHeader(ACCESS_TOKEN, manager.getToken());
        client.get("user/auth/transaction", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.d(TAG, "Size: "+response.length());
                    for(int i = 0; i < response.length(); i++){
                        JSONObject jsonObj = response.getJSONObject(i);
                        Log.d("PURCHASE ITERATION", ""+i);
                        int id = jsonObj.getInt("id");
                        String created = jsonObj.getString("created");
                        int amount = jsonObj.getInt("amount");
                        int parking_space_id = jsonObj.getInt("parking_space_id");
                        int location_id = jsonObj.getInt("location_id");
                        JSONObject location = jsonObj.getJSONObject("location");
                        String name = location.getString("name");
                        String address = location.getString("address");
                        String description = location.getString("description");

                        String starttime = jsonObj.getString("start_time");
                        String endtime = jsonObj.getString("end_time");
                        String email = jsonObj.getString("email");
                        Location loc = new Location(location_id, name, address, description);
                        boolean in_progress = jsonObj.getBoolean("in_progress");
                        Log.d("PURCHASE CREATED",""+created);
                        PurchaseDetail detail = new PurchaseDetail(id, email, loc, amount, location_id, parking_space_id, starttime, endtime, in_progress);
                        purchase_list.add(detail);
                    }
                    purchase_adapter.notifyDataSetChanged();
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
                if(responseError != null)
                    Log.d("JSON_ERR", responseError.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
                Log.d("JSON_ERR", responseError);
                throwable.printStackTrace();
            }
        });
    }
    private void setFragment(android.app.Fragment frag, String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.purchase_fragment, frag, tag);
        ft.addToBackStack(null);
        ft.commit();
    }
}
