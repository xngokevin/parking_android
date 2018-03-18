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

public class LoginFragment extends BaseFragment {

    private final String EMAIL = "email";
    private final String FIRST_NAME = "first_name";
    private final String LAST_NAME = "last_name";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private SessionManager manager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new SessionManager(getActivity().getApplicationContext());

        if(manager.isLoggedIn()) {
            Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        // Inflate the layout for this fragment
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) rootView.findViewById(R.id.email);
        mPasswordView = (EditText) rootView.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) rootView.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = rootView.findViewById(R.id.login_form);
        mProgressView = rootView.findViewById(R.id.login_progress);

        //Register Button
        Button btnRegister = (Button) rootView.findViewById(R.id.register_button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterFragment fragment = new RegisterFragment();
                //TODO: Add register to stack
                getFragmentManager().beginTransaction().replace(R.id.login_container, fragment).addToBackStack(null).commit();
            }
        });

        Button btnForgotPass = (Button) rootView.findViewById(R.id.reset_password_button);
        btnForgotPass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //TODO: PassRecoverFragment
                PassRecoverFragment fragment = new PassRecoverFragment();
                getFragmentManager().beginTransaction().replace(R.id.login_container, fragment).addToBackStack(null).commit();
            }
        });

        return rootView;
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
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
                postLoginUser(email, password);
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

    void postLoginUser(String email, String password) throws JSONException{
        ParkingRestClient client = new ParkingRestClient();

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        Log.d("LOGIN", client.getAbsoluteUrl("user/login"));
        client.post("user/login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    Log.d("Json_con", response.toString());
                    Toast.makeText(getActivity().getApplicationContext(), "Login Success", Toast.LENGTH_LONG).show();
                    manager.createLoginSession(response.getJSONObject("user").getString("token"));
                    Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(FIRST_NAME, response.getJSONObject("user").getString("first_name"));
                    intent.putExtra(LAST_NAME, response.getJSONObject("user").getString("last_name"));
                    intent.putExtra(EMAIL, response.getJSONObject("user").getString("email"));
                    startActivity(intent);

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
                Log.d("LOGIN ERROR", responseError);
                throwable.printStackTrace();
            }
        });
    }
}
