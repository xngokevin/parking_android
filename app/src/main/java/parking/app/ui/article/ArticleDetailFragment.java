//package parking.app.ui.article;
//
//import android.os.Bundle;
//import android.support.design.widget.CollapsingToolbarLayout;
//import android.support.v7.widget.Toolbar;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.loopj.android.http.JsonHttpResponseHandler;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//import cz.msebera.android.httpclient.Header;
//import parking.app.R;
//import parking.app.api.ParkingRestClient;
//import parking.app.dummy.DummyContent;
//import parking.app.ui.base.BaseActivity;
//import parking.app.ui.base.BaseFragment;
//import parking.app.util.SessionManager;
//
///**
// * Shows the home detail page.
// *
// * Created by Andreas Schrade on 14.12.2015.
// */
//public class ArticleDetailFragment extends BaseFragment {
//
//    /**
//     * The argument represents the dummy item ID of this fragment.
//     */
//    public static final String ARG_ITEM_ID = "item_id";
//
//    private final String ACCESS_TOKEN = "x-access-token";
//
//    SessionManager manager;
//
//    /**
//     * The dummy content of this fragment.
//     */
//    private DummyContent.DummyItem dummyItem;
//    private List<ParkingDetailItem> parkingItems = new ArrayList<>();
//
//    @Bind(R.id.quote)
//    TextView quote;
//
//    @Bind(R.id.author)
//    TextView author;
//
//    @Bind(R.id.backdrop)
//    ImageView backdropImg;
//
//    @Bind(R.id.collapsing_toolbar)
//    CollapsingToolbarLayout collapsingToolbar;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        manager = new SessionManager(getActivity().getApplicationContext());
//
//        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            // load dummy item by using the passed item ID.
//            dummyItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
//        }
//
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflateAndBind(inflater, container, R.layout.fragment_article_detail);
//
//        if (!((BaseActivity) getActivity()).providesActivityToolbar()) {
//            // No Toolbar present. Set include_toolbar:
//            ((BaseActivity) getActivity()).setToolbar((Toolbar) rootView.findViewById(R.id.toolbar));
//        }
//
//        if (dummyItem != null) {
//            loadBackdrop();
//            collapsingToolbar.setTitle(dummyItem.title);
//            author.setText(dummyItem.author);
//            quote.setText(dummyItem.content);
//        }
//
//        return rootView;
//    }
//
//    private void loadBackdrop() {
//        Glide.with(this).load(dummyItem.photoId).centerCrop().into(backdropImg);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.sample_actions, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // your logic
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    public static ArticleDetailFragment newInstance(String itemID) {
//        ArticleDetailFragment fragment = new ArticleDetailFragment();
//        Bundle args = new Bundle();
//        args.putString(ArticleDetailFragment.ARG_ITEM_ID, itemID);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public ArticleDetailFragment() {}
//
//    //From chosen parking location, get all parking slots
//    public void getParkingDetails() {
//
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
//                    Log.d("Detail_Array", "Success");
//                    for(int i = 0; i < response.length(); i++){
//                        JSONObject jsonObj = response.getJSONObject(i);
//                        parkingItems.add(new ParkingDetailItem(jsonObj));
//                    }
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                try {
//                    //Do stuff with JSON object
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject responseError){
//                Log.d("JSON_ERR", responseError.toString());
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseError, Throwable throwable){
//                Log.d("STR_ERR", responseError);
//                throwable.printStackTrace();
//            }
//        });
//    }
//}
