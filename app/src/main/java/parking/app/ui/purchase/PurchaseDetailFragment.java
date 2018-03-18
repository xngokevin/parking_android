package parking.app.ui.purchase;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageInstaller;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import parking.app.R;
import parking.app.api.ParkingRestClient;
import parking.app.data.Location;
import parking.app.data.LocationDetail;
import parking.app.data.PurchaseDetail;
import parking.app.style.DividerItemDecoration;
import parking.app.ui.base.BaseFragment;
import parking.app.util.SessionManager;

//TODO: Show details of purchase onClick
//TODO: Make views
public class PurchaseDetailFragment extends BaseFragment {

    //Http Parameters
    private final String ACCESS_TOKEN = "x-access-token";

    private static final String TAG = "PurchaseDetailFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";
    private static final String ARG_PARAM7 = "param7";
    private static final String ARG_PARAM8 = "param8";
    private static final String ARG_PARAM9 = "param9";


    @Bind(R.id.detail_email)
    TextView email_view;

    @Bind(R.id.detail_location_name)
    TextView name_view;

    @Bind(R.id.detail_location_address)
    TextView address_view;

    @Bind(R.id.detail_starttime)
    TextView start_view;

    @Bind(R.id.detail_endtime)
    TextView end_view;

    @Bind(R.id.detail_space_id)
    TextView space_view;

    @Bind(R.id.detail_amount)
    TextView amount_view;

    @Bind(R.id.end_session_button)
    Button btnSession;

    private String location_name;
    private String location_address;
    private int amount;
    private int location_id;
    private int parking_space_id;
    private boolean in_progress;
    private String start_time;
    private String end_time;
    private String email;

    private SessionManager manager;

    public PurchaseDetailFragment() {
        // Required empty public constructor
    }

    public static PurchaseDetailFragment newInstance(String email, String location_name, String location_address, int amount, int location_id, int parking_space_id, String start, String end, boolean in_progress) {
        PurchaseDetailFragment fragment = new PurchaseDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
        args.putString(ARG_PARAM2, location_name);
        args.putString(ARG_PARAM3, location_address);
        args.putInt(ARG_PARAM4, amount);
        args.putInt(ARG_PARAM9, location_id);
        args.putInt(ARG_PARAM5, parking_space_id);
        args.putString(ARG_PARAM6, start);
        args.putString(ARG_PARAM7, end);
        args.putBoolean(ARG_PARAM8, in_progress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new SessionManager(getActivity().getApplicationContext());
        if (getArguments() != null) {
            //Get id key here
            email = getArguments().getString(ARG_PARAM1);
            location_name = getArguments().getString(ARG_PARAM2);
            location_address = getArguments().getString(ARG_PARAM3);
            amount = getArguments().getInt(ARG_PARAM4);
            parking_space_id = getArguments().getInt(ARG_PARAM5);
            start_time = getArguments().getString(ARG_PARAM6);
            end_time = getArguments().getString(ARG_PARAM7);
            in_progress = getArguments().getBoolean(ARG_PARAM8);
            location_id = getArguments().getInt(ARG_PARAM9);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_purchase_detail, container, false);
        email_view = (TextView)rootView.findViewById(R.id.detail_email);
        email_view.setText(email);

        name_view = (TextView)rootView.findViewById(R.id.detail_location_name);
        name_view.setText(location_name);

        address_view = (TextView)rootView.findViewById(R.id.detail_location_address);
        address_view.setText(location_address);

        start_view = (TextView)rootView.findViewById(R.id.detail_starttime);
        start_view.setText("Start time: "+PurchaseDetail.parseTimeToString(start_time));

        end_view = (TextView)rootView.findViewById(R.id.detail_endtime);
        end_view.setText("End time: "+PurchaseDetail.parseTimeToString(end_time));

        space_view = (TextView)rootView.findViewById(R.id.detail_space_id);
        space_view.setText("Parking Space ID: "+parking_space_id);

        amount_view = (TextView)rootView.findViewById(R.id.detail_amount);
        amount_view.setText("Total: "+NumberFormat.getCurrencyInstance().format(amount/100));

        btnSession = (Button)rootView.findViewById(R.id.end_session_button);
        if(in_progress == true){
            btnSession.setVisibility(View.VISIBLE);
        }
        else{
            btnSession.setVisibility(View.GONE);
        }

        btnSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //End session here
                try {
                    postEndSession();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    void postEndSession() throws JSONException {
        ParkingRestClient client = new ParkingRestClient();
        RequestParams params = new RequestParams();
        params.put("space_id", parking_space_id);
        params.put("location_id", location_id);
        client.addHeader(ACCESS_TOKEN, manager.getToken());
        client.put("parking/unoccupy", params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(getActivity().getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    PurchaseFragment fragment = new PurchaseFragment();
                    getFragmentManager().beginTransaction().replace(R.id.purchase_fragment, fragment).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
                try {
                    Log.d(TAG, responseError.getString("message"));
                    Toast.makeText(getActivity().getApplicationContext(), responseError.getString("message"), Toast.LENGTH_LONG).show();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
                Log.d(TAG, "" + statusCode);
                Log.d(TAG, responseError);
                Log.d(TAG, Arrays.toString(headers));

                Toast.makeText(getActivity().getApplicationContext(), responseError, Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });

    }
}
