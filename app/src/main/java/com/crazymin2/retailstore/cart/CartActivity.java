package com.crazymin2.retailstore.cart;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.crazymin2.retailstore.R;
import com.crazymin2.retailstore.ui.BaseActivity;
import com.crazymin2.retailstore.ui.widget.DrawShadowFrameLayout;
import com.crazymin2.retailstore.util.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ashish (Min2) on 06/02/16.
 */
public class CartActivity extends BaseActivity {

    private static final String TAG = LogUtils.makeLogTag(CartActivity.class);

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_act);
        ButterKnife.bind(this);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_flipped, GravityCompat.END);

        registerHideableHeaderView(findViewById(R.id.headerbar));

        // Add the back button to the toolbar.
        Toolbar toolbar = getActionBarToolbar();
        toolbar.setNavigationIcon(R.drawable.ic_up);
        toolbar.setNavigationContentDescription(R.string.close_and_go_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateUpOrBack(CartActivity.this, null);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }


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
}
