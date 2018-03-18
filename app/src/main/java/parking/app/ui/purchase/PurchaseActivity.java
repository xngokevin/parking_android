package parking.app.ui.purchase;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import parking.app.R;
import parking.app.ui.base.BaseActivity;

public class PurchaseActivity extends BaseActivity {

    private String PURCHASE_FRAGMENT = "purchase_fragment";
    PurchaseFragment purchase_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        setupToolbar();

        purchase_fragment = new PurchaseFragment();
        setFragment(purchase_fragment, PURCHASE_FRAGMENT);

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
        return R.id.nav_purchases;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    private void setFragment(Fragment frag, String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.purchase_fragment, frag, tag);
        ft.commit();
    }
}
