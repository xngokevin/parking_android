package parking.app.ui.login;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import parking.app.R;
import parking.app.api.ParkingRestClient;
import parking.app.ui.base.BaseFragment;
import parking.app.ui.home.HomeActivity;
import parking.app.util.SessionManager;

public class PassRecoverFragment extends BaseFragment {

    private final String EMAIL = "email";
    private final String FIRST_NAME = "first_name";
    private final String LAST_NAME = "last_name";


    // UI references.
    private EditText mResetPasswordView;
    private SessionManager manager;

    public PassRecoverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new SessionManager(getActivity().getApplicationContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pass_recover, container, false);
        // Inflate the layout for this fragment
        // Set up the password reset form
        mResetPasswordView = (EditText) rootView.findViewById(R.id.recovery_email);

        Button mResetPasswordButton = (Button) rootView.findViewById(R.id.reset_pass_button);

        mResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptReset();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    void attemptReset(){

        // Reset errors.
        mResetPasswordView.setError(null);

        String email = mResetPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mResetPasswordView.setError(getString(R.string.error_field_required));
            focusView = mResetPasswordView;
        } else if (!isEmailValid(email)) {
            mResetPasswordView.setError(getString(R.string.error_invalid_email));
            focusView = mResetPasswordView;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // showProgress(true);
            try{
                postRecoverPass(email);
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    void postRecoverPass(String email) throws JSONException{
        ParkingRestClient client = new ParkingRestClient();
        RequestParams params = new RequestParams();
        params.put("email", email);
        client.post("user/forgotpassword", params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Toast.makeText(getActivity().getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    LoginFragment fragment = new LoginFragment();
                    getFragmentManager().beginTransaction().replace(R.id.login_container, fragment).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
                try {
                    Toast.makeText(getActivity().getApplicationContext(), responseError.getString("message"), Toast.LENGTH_LONG).show();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
                Log.d("LOGIN ERROR", "" + statusCode);
                Log.d("LOGIN ERROR", responseError);
                Log.d("LOGIN ERROR", Arrays.toString(headers));

                Toast.makeText(getActivity().getApplicationContext(), "Login Failure", Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}
