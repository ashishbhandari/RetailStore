package com.crazymin2.retailstore.home;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.crazymin2.retailstore.R;
import com.crazymin2.retailstore.framework.CartPresenter;
import com.crazymin2.retailstore.framework.CartPresenterImpl;
import com.crazymin2.retailstore.framework.CartView;
import com.crazymin2.retailstore.ui.BaseActivity;
import com.crazymin2.retailstore.ui.ProductActivity;
import com.crazymin2.retailstore.ui.widget.CollectionRecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.crazymin2.retailstore.util.LogUtils.LOGD;

public class MainActivity extends BaseActivity implements CommonTabFragment.TabListener {


    private static final String TAG = MainActivity.class.getSimpleName();

    public StaggeredGridLayoutManager mStaggeredLayoutManager;
    // View pager and adapter (for narrow mode)
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.sliding_tabs)
    TabLayout mTabLayout;


    OurViewPagerAdapter mViewPagerAdapter = null;
    private Set<CommonTabFragment> mTabFragments = new HashSet<CommonTabFragment>();

    // titles for tab layout items (indices must correspond to the above)
    private static final int[] TAB_TITLE_RES_ID = new int[]{
            R.string.tab_item_my_category
    };

    private CollectionRecyclerView mRecyclerView;
    public CategoryTabFragment.TabAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        renderTabsWithPage();
    }

    private void renderTabsWithPage() {

        mViewPagerAdapter = new OurViewPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout.setTabsFromPagerAdapter(mViewPagerAdapter);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mViewPager.setCurrentItem(tab.getPosition(), true);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });
        mViewPager.setPageMargin(getResources()
                .getDimensionPixelSize(R.dimen.home_page_margin));
        mViewPager.setPageMarginDrawable(R.drawable.page_margin);

        createTabLayoutContentDescriptions();
    }

    private void createTabLayoutContentDescriptions() {
        for (int i = 0; i < TAB_TITLE_RES_ID.length; i++) {
            mTabLayout.setContentDescription(getString(TAB_TITLE_RES_ID[i]));
        }
    }

    @Override
    public void onTabFragmentViewCreated(Fragment fragment) {
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView = (CollectionRecyclerView) fragment.getView().findViewById(R.id.list);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CategoryTabFragment.TabAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(onItemClickListener);
        enableActionBarAutoHide(mRecyclerView);
    }

    private CategoryTabFragment.TabAdapter.OnItemClickListener onItemClickListener = new CategoryTabFragment.TabAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            int categoryId = mAdapter.getCategoryId(position);

            Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
            intent.putExtra(ProductActivity.SELECTED_CATEGORY_ID, categoryId);
            startActivity(intent);
        }
    };

    @Override
    public void onTabFragmentAttached(Fragment fragment) {
        mTabFragments.add((CommonTabFragment) fragment);
    }

    @Override
    public void onTabFragmentDetached(Fragment fragment) {
        mTabFragments.remove(fragment);
    }

    private class OurViewPagerAdapter extends FragmentPagerAdapter {

        private Fragment mCurrentFragment;

        public OurViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }


        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            LOGD(TAG, "Creating fragment #" + position);

            Fragment frag = new CategoryTabFragment();

            return frag;
        }

        @Override
        public int getCount() {
            return TAB_TITLE_RES_ID.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(TAB_TITLE_RES_ID[position]);
        }
    }

}

