package com.crazymin2.retailstore.ui;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.crazymin2.retailstore.R;
import com.crazymin2.retailstore.ui.widget.DrawShadowFrameLayout;
import com.crazymin2.retailstore.util.LogUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ashish (Min2) on 06/02/16.
 */
public class ProductActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener/*, ShoppingCartHelper.OnCustomStateListener*/ {


    private static final String TAG = LogUtils.makeLogTag(ProductActivity.class);

    public static final String SELECTED_CATEGORY_ID = "com.crazymin2.retailstore.ui.SELECTED_CATEGORY_ID";

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.headerbar)
    View headerbar;

    ProductFragment mFragment;

    public int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_act);
        ButterKnife.bind(this);

//        registerListener();

        categoryId = getIntent().getIntExtra(SELECTED_CATEGORY_ID, 0);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_flipped, GravityCompat.END);


        registerHideableHeaderView(headerbar);
        mFragment = (ProductFragment) getFragmentManager().findFragmentById(R.id.product_request_frag);


        // Add the back button to the toolbar.
        Toolbar toolbar = getActionBarToolbar();
        toolbar.setNavigationIcon(R.drawable.ic_up);
        toolbar.setNavigationContentDescription(R.string.close_and_go_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                unregisterListener();
                navigateUpOrBack(ProductActivity.this, null);
            }
        });

    }

    @Override
    public void onBackPressed() {
//        unregisterListener();
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

//    /**
//     * this method will help you to unregister event listener for product deletion from cart
//     */
//    private void unregisterListener() {
//        ShoppingCartHelper.getInstance().setListener(null);
//    }

//    /**
//     * this method will help you to register event listener for product deletion from cart
//     */
//    private void registerListener() {
//        ShoppingCartHelper.getInstance().setListener(this);
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        enableActionBarAutoHide((ListView) findViewById(R.id.collection_view));
    }


    @Override
    protected void onActionBarAutoShowOrHide(boolean shown) {
        super.onActionBarAutoShowOrHide(shown);
        DrawShadowFrameLayout frame = (DrawShadowFrameLayout) findViewById(R.id.main_content);
        frame.setShadowVisible(shown, shown);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartPresenter.showMeCategoryItems(categoryId);
        // for refreshing the cart counter on action bar toolbar
        supportInvalidateOptionsMenu();
        mFragment.refreshFragmentAdapter();
    }

    @Override
    protected void onDestroy() {
        cartPresenter.onDestroy();
        super.onDestroy();
    }

//    @Override
//    public void stateChanged() {
//        // refreshing adapter if any product deleted from cart
////        mFragment.restartLoader();
//        cartPresenter.showMeItemsInCart();
//    }

    @Override
    public void displayCategoryItems(List items) {
        super.displayCategoryItems(items);
        mFragment.refreshData(items);
    }
}
