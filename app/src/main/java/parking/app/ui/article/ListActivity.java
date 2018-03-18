//package parking.app.ui.article;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.ActionBar;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.loopj.android.http.JsonHttpResponseHandler;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import cz.msebera.android.httpclient.Header;
//import parking.app.R;
//import parking.app.api.ParkingRestClient;
//import parking.app.ui.base.BaseActivity;
//import parking.app.util.LogUtil;
//import parking.app.util.SessionManager;
//
///**
// * Lists all available quotes. This Activity supports a single pane (= smartphones) and a two pane mode (= large screens with >= 600dp width).
// *
// * Created by Andreas Schrade on 14.12.2015.
// */
//public class ListActivity extends BaseActivity implements ArticleListFragment.Callback {
//    /**
//     * Whether or not the activity is running on a device with a large screen
//     */
//    private boolean twoPaneMode;
//
//    //Http Parameters
//    private final String ACCESS_TOKEN = "x-access-token";
//
//    private String id_request;
//
//    public static final List<ParkingStructItem> ITEMS = new ArrayList<>();
//
//    public static final Map<String, ParkingStructItem> ITEM_MAP = new HashMap<>(10);
//
//    //Users Session
//    SessionManager manager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_list);
//
//        manager = new SessionManager(getApplicationContext());
//
//        setupToolbar();
//        getParkingStruct();
//        if (isTwoPaneLayoutUsed()) {
//            twoPaneMode = true;
//            LogUtil.logD("TEST","TWO POANE TASDFES");
//            enableActiveItemState();
//        }
//
//        if (savedInstanceState == null && twoPaneMode) {
//            setupDetailFragment();
//        }
//    }
//
//    /**
//     * Called when an item has been selected
//     *
//     * @param id the selected home ID
//     */
//    @Override
//    public void onItemSelected(String id) {
//        if (twoPaneMode) {
//            // Show the home detail information by replacing the DetailFragment via transaction.
//            ArticleDetailFragment fragment = ArticleDetailFragment.newInstance(id);
//            getFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
//        } else {
//            // Start the detail activity in single pane mode.
//            Intent detailIntent = new Intent(this, ArticleDetailActivity.class);
//            detailIntent.putExtra(ArticleDetailFragment.ARG_ITEM_ID, id);
//            startActivity(detailIntent);
//        }
//    }
//
//    private void setupToolbar() {
//        final ActionBar ab = getActionBarToolbar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
//        ab.setDisplayHomeAsUpEnabled(true);
//    }
//
//    private void setupDetailFragment() {
//        ArticleDetailFragment fragment =  ArticleDetailFragment.newInstance(ITEMS.get(0).getId());
//        getFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
//    }
//
//    /**
//     * Enables the functionality that selected items are automatically highlighted.
//     */
//    private void enableActiveItemState() {
//        ArticleListFragment fragmentById = (ArticleListFragment) getFragmentManager().findFragmentById(R.id.article_list);
//        fragmentById.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//    }
//
//    /**
//     * Is the container present? If so, we are using the two-pane layout.
//     *
//     * @return true if the two pane layout is used.
//     */
//    private boolean isTwoPaneLayoutUsed() {
//        return findViewById(R.id.article_detail_container) != null;
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.sample_actions, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                openDrawer();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected int getSelfNavDrawerItem() {
////        return R.id.nav_quotes;
//        return 0;
//    }
//
//    @Override
//    public boolean providesActivityToolbar() {
//        return true;
//    }
//
//
//    /**
//     * Initialize list of parking locations
//     */
//    private void getParkingStruct() {
//        ITEMS.clear();
//        Log.d("getParkingStruct", "Enter");
//        //Http Client
//        ParkingRestClient client = new ParkingRestClient();
//
//        //Set token in header
//        Log.d("ACCESS-TOKEN",ACCESS_TOKEN);
//        client.addHeader(ACCESS_TOKEN, manager.getToken());
//        client.get("parking/location", null, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                try {
//                    //Loads parking locations
//                    Log.d("Home_Array", "Success");
//                    for(int i = 0; i < response.length(); i++){
//                        JSONObject jsonObj = response.getJSONObject(i);
//                        addItem(new ParkingStructItem(jsonObj));
//                    }
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//                for(ParkingStructItem j : ITEMS){
//                    Log.d("Home_Array",j.getName());
//                }
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                try {
//                    //Do stuff with JSON object
//                    addItem(new ParkingStructItem(response));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
//                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
//                Log.d("JSON_ERR", responseError.toString());
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
//                Log.d("Home", responseError);
//                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
//                throwable.printStackTrace();
//            }
//        });
//    }
//
//    private static void addItem(ParkingStructItem item) {
//        ITEMS.add(item);
//        ITEM_MAP.put(item.getId(), item);
//    }
//}
