package parking.app.ui.login;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;
import parking.app.R;
import parking.app.api.ParkingRestClient;
import parking.app.ui.base.BaseFragment;
import parking.app.ui.home.HomeActivity;
import parking.app.util.SessionManager;


public class RegisterFragment extends BaseFragment {

    private final String EMAIL = "email";
    private final String FIRST_NAME = "first_name";
    private final String LAST_NAME = "last_name";

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordRetypeView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private View mProgressView;
    private View mRegisterFormView;
    private SessionManager manager;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        // Inflate the layout for this fragment
        // Set up the login form.
        mFirstNameView = (EditText) rootView.findViewById(R.id.register_first_name);
        mLastNameView = (EditText) rootView.findViewById(R.id.register_last_name);
        mEmailView = (AutoCompleteTextView) rootView.findViewById(R.id.register_email);
        mPasswordView = (EditText) rootView.findViewById(R.id.register_password);
        mPasswordRetypeView = (EditText) rootView.findViewById(R.id.register_password_retype);



        Button mEmailRegisterButton = (Button) rootView.findViewById(R.id.register_email_button);
        mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerEmail();
            }
        });

        mRegisterFormView = rootView.findViewById(R.id.register_form);
        mProgressView = rootView.findViewById(R.id.login_progress);

        return rootView;
    }

    private void registerEmail() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mPasswordRetypeView.setError(null);

        // Store values at the time of the login attempt.
        String first_name = mFirstNameView.getText().toString();
        String last_name = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String retype = mPasswordRetypeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(first_name)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if(TextUtils.isEmpty(last_name)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if(TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }


        //Check if retyped password matches
        if(!TextUtils.isEmpty(retype) && !password.equals(retype)) {
            mPasswordRetypeView.setError(getString(R.string.error_password_mismatch));
            focusView = mPasswordRetypeView;
            cancel = true;
        }

        if(TextUtils.isEmpty(retype)) {
            mPasswordRetypeView.setError(getString(R.string.error_field_required));
            focusView = mPasswordRetypeView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
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
                postRegisterUser(first_name, last_name, email, password);
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
    }

    void postRegisterUser(String first_name, String last_name, String email, String password) throws JSONException{
        ParkingRestClient client = new ParkingRestClient();

        RequestParams params = new RequestParams();
        params.put("first_name",first_name);
        params.put("last_name", last_name);
        params.put("email", email);
        params.put("password", password);

        client.post("user/register", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Toast.makeText(getActivity().getApplicationContext(), "Registered", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity().getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                Log.d("LOGIN ERROR", responseError);
                throwable.printStackTrace();
            }
        });
    }
}
