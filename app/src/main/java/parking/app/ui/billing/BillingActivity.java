package parking.app.ui.billing;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import parking.app.R;
import parking.app.api.ParkingRestClient;
import parking.app.ui.base.BaseActivity;

import parking.app.util.SessionManager;

public class BillingActivity extends BaseActivity {

    //Users Session
    SessionManager manager;

    //Api
    ParkingRestClient client;

    //Http Parameters
    private final String BILLING_ID = "id";
    private final String ACCESS_TOKEN = "x-access-token";
    private final String NUMBER = "number";
    private final String EXP_MONTH = "exp_month";
    private final String EXP_YEAR = "exp_year";
    private final String CVC = "cvc";
    private final String ADDRESS_ZIP = "address_zip";

    private String billingId;

    //UI References
    private LinearLayout llBillingForm;
    private LinearLayout llBillingInfo;
    private EditText etBillingCardNumber;
    private EditText etBillingCardExpMonth;
    private EditText etBillingCardExpYear;
    private EditText etBillingCardCVV;
    private EditText etBillingCardZip;
    private TextView tvBillingBrand;
    private TextView tvBillingExpiration;
    private TextView tvBillingCardLastFour;

    private Button btnBillingAddSubmit;
    private Button btnBillingDeleteSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        ButterKnife.bind(this);
        setupToolbar();

        manager = new SessionManager(getApplicationContext());
        client = new ParkingRestClient();

        llBillingForm = (LinearLayout) findViewById(R.id.billing_form);
        llBillingInfo = (LinearLayout) findViewById(R.id.billing_info);

        //Set token in header
        client.addHeader(ACCESS_TOKEN, manager.getToken());
        getBilling();

        etBillingCardNumber = (EditText) findViewById(R.id.billing_card_number);
        etBillingCardExpMonth = (EditText) findViewById(R.id.billing_card_expiration_month);
        etBillingCardExpYear = (EditText) findViewById(R.id.billing_card_expiration_year);
        etBillingCardCVV = (EditText) findViewById(R.id.billing_card_cvv);
        etBillingCardZip = (EditText) findViewById(R.id.billing_card_zip);
        tvBillingBrand = (TextView) findViewById(R.id.billing_card_brand_display);
        tvBillingCardLastFour = (TextView) findViewById(R.id.billing_card_last_four_display);
        tvBillingExpiration = (TextView) findViewById(R.id.billing_card_expiration_display);
        btnBillingAddSubmit = (Button) findViewById(R.id.billing_add_submit);
        btnBillingDeleteSubmit = (Button) findViewById(R.id.billing_delete_submit);

        btnBillingAddSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyBillingInfo();
            }
        });
        btnBillingDeleteSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postBillingDelete();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_billing;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    private void verifyBillingInfo() {
        //Do some basic form handling here
        String billing_card_number = etBillingCardNumber.getText().toString();
        String billing_card_expiration_month = etBillingCardExpMonth.getText().toString();
        String billing_card_expiration_year = etBillingCardExpYear.getText().toString();
        String billing_card_cvv = etBillingCardCVV.getText().toString();
        String billing_card_zip = etBillingCardZip.getText().toString();
        postBillingAdd(billing_card_number, billing_card_expiration_month, billing_card_expiration_year, billing_card_cvv, billing_card_zip);
    }

    private void getBilling() {

        client.get("stripe/card", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Log.d("GET BILLING", response.toString());
                    if(response.length() == 0) {
                        llBillingForm.setVisibility(LinearLayout.VISIBLE);
                        llBillingInfo.setVisibility(LinearLayout.GONE);
                    }
                    else {
                        String expiration = "**" + response.getJSONObject(0).getString("last4");
                        llBillingForm.setVisibility(LinearLayout.GONE);
                        llBillingInfo.setVisibility(LinearLayout.VISIBLE);
                        tvBillingBrand.setText(response.getJSONObject(0).getString("brand"));
                        tvBillingCardLastFour.setText(expiration);
                        tvBillingExpiration.setText(response.getJSONObject(0).getString("exp_month") + "/" + response.getJSONObject(0).getString("exp_year"));
                        billingId = response.getJSONObject(0).getString("id");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
                try {
                    Toast.makeText(getApplicationContext(), responseError.getString("message"), Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                throwable.printStackTrace();
            }
        });
    }

    private void postBillingAdd(String number, String exp_month, String exp_year, String cvc, String address_zip) {

        RequestParams params = new RequestParams();
        params.put(NUMBER, number);
        params.put(EXP_MONTH, exp_month);
        params.put(EXP_YEAR, exp_year);
        params.put(CVC, cvc);
        params.put(ADDRESS_ZIP, address_zip);
        client.post("stripe/card", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("POST BILLING", response.toString());
                    if(!response.isNull("type") && response.getString("type").equals("StripeCardError")) {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                    else {
                        etBillingCardNumber.setText("");
                        etBillingCardExpMonth.setText("");
                        etBillingCardExpYear.setText("");
                        etBillingCardCVV.setText("");
                        etBillingCardZip.setText("");
                        getBilling();
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
                try {
                    Toast.makeText(getApplicationContext(), responseError.getString("message"), Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                throwable.printStackTrace();
            }
        });
    }

    private void postBillingDelete() {
        RequestParams params = new RequestParams();
        params.put(BILLING_ID, billingId);
        client.delete("stripe/card", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("DELETE BILLING", response.toString());
                    if(response.getInt("status") == 200) {
                        getBilling();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
                try {
                    Toast.makeText(getApplicationContext(), responseError.getString("message"), Toast.LENGTH_LONG).show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                throwable.printStackTrace();
            }
        });
    }
}
