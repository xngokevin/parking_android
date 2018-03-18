package parking.app.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import parking.app.R;
import parking.app.api.ParkingRestClient;
import parking.app.util.SessionManager;


public class HomeDialogFragment extends DialogFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "location_id";
    private static final String ARG_PARAM2 = "space_id";

    private int location_id;
    private int parking_id;

    private String spinnerText = "1";

    //Http Parameters
    private final String ACCESS_TOKEN = "x-access-token";

    //Users Session
    private SessionManager manager;

    private DialogInterface.OnDismissListener onDismissListener;

    public HomeDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param location_id Parameter 1.
     * @param parking_id Parameter 2.
     * @return A new instance of fragment HomeDialogFragment.
     */
    public static HomeDialogFragment newInstance(int location_id, int parking_id) {
        HomeDialogFragment fragment = new HomeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, location_id);
        args.putInt(ARG_PARAM2, parking_id);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        manager = new SessionManager(getActivity().getApplicationContext());
        if (getArguments() != null) {
            //Get location and space id keys here
            location_id = getArguments().getInt(ARG_PARAM1);
            parking_id = getArguments().getInt(ARG_PARAM2);
        }
        // Use the Builder class for convenient dialog construction

        int maxHours = 10;
        String hours[] = new String[maxHours];
        for(int i = 1; i <= maxHours; i++){
            hours[i-1] = ""+i;
        }


        View convertView = getActivity().getLayoutInflater().inflate(R.layout.dialog_home,null);

        final Spinner spinner = (Spinner) convertView.findViewById(R.id.hours_spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, hours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerText = spinner.getSelectedItem().toString();
                Log.d("SpinnerText",spinnerText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Log.d("HomeDialogFragment","Spinner: "+spinnerText);
        final int hourSelected = Integer.parseInt(spinnerText);

        Log.d("Spinner",spinnerText+"\t"+hourSelected+"\t"+location_id+"\t"+parking_id);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Hours Parked");
        builder.setView(convertView )
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        //Assign parking slot
                        Log.d("HomeDialogFragment","POSITIVE ");
                        try{
                            postParking(location_id, parking_id, Integer.parseInt(spinnerText));
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        Log.d("HomeDialogFragment","NEGATIVE");
                        HomeDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void postParking(int location_id, int parking_id, int hours) throws JSONException {
        ParkingRestClient client = new ParkingRestClient();
        client.addHeader(ACCESS_TOKEN, manager.getToken());
        RequestParams params = new RequestParams();

        params.put("location_id", location_id);
        params.put("parking_id", parking_id);
        params.put("hours", hours);
        client.post("stripe/charge", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    Log.d("DIALOG", response.toString());
                    Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
                try {
                    Log.d("DIALOG ERROR", responseError.toString());
                    Toast.makeText(getActivity().getApplicationContext(), responseError.getString("message"), Toast.LENGTH_LONG).show();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
                Log.d("DIALOG ERROR", "" + statusCode);
                Log.d("DIALOG ERROR", responseError);
                Log.d("DIALOG ERROR", Arrays.toString(headers));

                throwable.printStackTrace();
            }
        });
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

}
