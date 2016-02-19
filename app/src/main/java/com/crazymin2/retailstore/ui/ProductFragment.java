package com.crazymin2.retailstore.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crazymin2.retailstore.R;
import com.crazymin2.retailstore.database.DatabaseManager;
import com.crazymin2.retailstore.home.data.Product;
import com.crazymin2.retailstore.home.data.ShoppingCartHelper;
import com.crazymin2.retailstore.ui.widget.CollectionView;
import com.crazymin2.retailstore.ui.widget.DrawShadowFrameLayout;
import com.crazymin2.retailstore.util.LogUtils;
import com.crazymin2.retailstore.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.crazymin2.retailstore.util.LogUtils.LOGD;

/**
 * Created by ashish (Min2) on 06/02/16.
 */
public class ProductFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Product>>{

    private static final String TAG = LogUtils.makeLogTag(ProductFragment.class);
    private static final int TAG_METADATA_TOKEN = 0x8;

    @Bind(R.id.collection_view)
    CollectionView mCollectionView;
    @Bind(android.R.id.empty)
    View mEmptyView;

    // This is the Adapter being used to display the list's data.
    private ProductCartListAdapter mAdapter;

    private int selectedPosition;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_detail_frag, container, false);
        ButterKnife.bind(this, rootView);

//        mCollectionView = (CollectionView) rootView.findViewById(R.id.collection_view);
//        mEmptyView = rootView.findViewById(android.R.id.empty);
        getActivity().overridePendingTransition(0, 0);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ProductCartListAdapter(getActivity(), new ProductCartListAdapter.TouchAreaClicked() {
            @Override
            public void onTouchArea(int position) {
                //No, changes will not be reflected into the original object because object has been made into parcelable. It acts as a clone of the passing object
                //parcelable doesnt work for >1mb
                selectedPosition = position;

                Product product = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), AddCartDialogActivity.class);
                intent.putExtra(AddCartDialogActivity.SELECTED_PRODUCT_ITEM, product);
                startActivityForResult(intent, AddCartDialogActivity.REQUEST_TO_ADD_IN_CART);
            }
        });
        mCollectionView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();

        int actionBarSize = UIUtils.calculateActionBarSize(getActivity());
        DrawShadowFrameLayout drawShadowFrameLayout =
                (DrawShadowFrameLayout) getActivity().findViewById(R.id.main_content);
        if (drawShadowFrameLayout != null) {
            drawShadowFrameLayout.setShadowTopOffset(actionBarSize);
        }
        setContentTopClearance(actionBarSize);
    }


    private void setContentTopClearance(int clearance) {
        if (mCollectionView != null) {
            mCollectionView.setContentTopClearance(clearance);
        }
    }

    @Override
    public Loader<List<Product>> onCreateLoader(int id, Bundle args) {
        return new MyPurchaseListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Product>> loader, List<Product> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Product>> loader) {
        mAdapter.setData(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddCartDialogActivity.REQUEST_TO_ADD_IN_CART) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                boolean isInCart = data.getBooleanExtra(AddCartDialogActivity.CART_STATUS, false);
                Product item = mAdapter.getItem(selectedPosition);
                item.isInCart = isInCart;
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }


    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class MyPurchaseListLoader extends AsyncTaskLoader<List<Product>> {

        List<Product> mApps;

        private Context mContext;

        public MyPurchaseListLoader(Context context) {
            super(context);
            mContext = context;

        }

        /**
         * This is where the bulk of our work is done. This function is called in a background thread
         * and should generate a new set of data to be published by the loader.
         */
        @Override
        public List<Product> loadInBackground() {


            ArrayList<Product> productsByCategory = null;

            if (mContext instanceof ProductActivity) {

                ArrayList<Product> cartProducts = DatabaseManager.getInstance().getCartProducts();


                int categoryId = ((ProductActivity) mContext).categoryId;
                productsByCategory = DatabaseManager.getInstance().getProductsByCategory(categoryId);

                int position = 0;
                for (Product product : productsByCategory) {
                    if (!cartProducts.contains(product)) {
                        productsByCategory.get(position).isInCart = false;
                        LOGD(TAG, "item not matched");
                    } else {
                        productsByCategory.get(position).isInCart = true;
                        LOGD(TAG, "item matched");
                    }
                    position++;
                }
            }
            return productsByCategory;
        }

        /**
         * Called when there is new data to deliver to the client. The super class will take care of
         * delivering it; the implementation here just adds a little more logic.
         */
        @Override
        public void deliverResult(List<Product> apps) {
            if (isReset()) {
                // An async query came in while the loader is stopped. We
                // don't need the result.
                if (apps != null) {
                    onReleaseResources(apps);
                }
            }
            List<Product> oldApps = apps;
            mApps = apps;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(apps);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldApps != null) {
                onReleaseResources(oldApps);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading() {
            if (mApps != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mApps);
            }
            if (takeContentChanged() || mApps == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override
        public void onCanceled(List<Product> apps) {
            super.onCanceled(apps);

            // At this point we can release the resources associated with 'apps'
            // if needed.
            onReleaseResources(apps);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override
        protected void onReset() {
            super.onReset();
            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (mApps != null) {
                onReleaseResources(mApps);
                mApps = null;
            }

        }

        /**
         * Helper function to take care of releasing resources associated with an actively loaded data
         * set.
         */
        protected void onReleaseResources(List<Product> apps) {
            // For a simple List<> there is nothing to do. For something
            // like a Cursor, we would close it here.
        }
    }
}
