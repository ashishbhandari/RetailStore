package com.crazymin2.retailstore.cart;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crazymin2.retailstore.CommonConstants;
import com.crazymin2.retailstore.R;
import com.crazymin2.retailstore.database.DatabaseManager;
import com.crazymin2.retailstore.home.data.Product;
import com.crazymin2.retailstore.home.data.ShoppingCartHelper;
import com.crazymin2.retailstore.ui.AddCartDialogActivity;
import com.crazymin2.retailstore.ui.widget.CollectionView;
import com.crazymin2.retailstore.ui.widget.DrawShadowFrameLayout;
import com.crazymin2.retailstore.util.ImageLoader;
import com.crazymin2.retailstore.util.LogUtils;
import com.crazymin2.retailstore.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ashish (Min2) on 06/02/16.
 */
public class CartFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Product>> {

    private static final String TAG = LogUtils.makeLogTag(CartFragment.class);

    private static ImageLoader mImageLoader;

    @Bind(R.id.collection_view)
    CollectionView mCollectionView;
    @Bind(android.R.id.empty)
    View mEmptyView;

    // This is the Adapter being used to display the list's data.
    private CartListAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = new ImageLoader(getActivity(), R.drawable.default_logo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_detail_frag, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().overridePendingTransition(0, 0);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new CartListAdapter(getActivity(), mEmptyView, new CartListAdapter.CartClickedInterface() {
            @Override
            public void onTouchArea(int position) {

                Product product = mAdapter.getItem(position);
                product.isInCart = true;
                Intent intent = new Intent(getActivity(), AddCartDialogActivity.class);
                intent.putExtra(AddCartDialogActivity.SELECTED_PRODUCT_ITEM, product);
                startActivityForResult(intent, AddCartDialogActivity.REQUEST_TO_ADD_IN_CART);
            }

            @Override
            public void onDeletePressed(int position) {
                Product product = mAdapter.getItem(position);

                boolean deleteCartProduct = DatabaseManager.getInstance().deleteCartProduct(product);
                if (deleteCartProduct) {
                    ShoppingCartHelper.getInstance().changeState(true);
                    Toast.makeText(getActivity(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                    mAdapter.remove(product);
                    refreshAdapter();
                }
            }
        });
        // Set the new data in the adapter.
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

    public void refreshAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            mEmptyView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
        }
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
        // Set the new data in the adapter.
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
            refreshAdapter();
        }
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

            ArrayList<Product> productsByCategory = DatabaseManager.getInstance().getCartProducts();
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


    public static class CartListAdapter extends ArrayAdapter<Product> {

        private final LayoutInflater mInflater;

        private CartClickedInterface cartClickedInterface = null;

        private Context mContext;

        private View mEmptyView;


        public interface CartClickedInterface {

            public abstract void onTouchArea(int position);

            public abstract void onDeletePressed(int position);

        }

        @Override
        public boolean isEmpty() {
            return super.isEmpty();
        }

        public CartListAdapter(Context context, View emptyView, CartClickedInterface listener) {
            super(context, R.layout.cart_row);
            mContext = context;
            mEmptyView = emptyView;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cartClickedInterface = listener;
        }

        public void setData(List<Product> data) {
            clear();
            if (data != null) {
                addAll(data);
                // Show empty view if there were no Group cards.
                mEmptyView.setVisibility(data.size() > 0 ? View.GONE : View.VISIBLE);
            }
        }

        /**
         * Populate new items in the list.
         */
        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            ViewHolder holder;

            if (view == null) {
                view = mInflater.inflate(R.layout.cart_row, parent, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Product item = getItem(position);

            holder.title.setText(item.name);

            holder.deleteCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cartClickedInterface != null)
                        cartClickedInterface.onDeletePressed(position);
                }
            });

            holder.touchArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cartClickedInterface != null)
                        cartClickedInterface.onTouchArea(position);
                }
            });

            holder.deleteCart.setTag(item);

            if (TextUtils.isEmpty(item.imageUrlSmall)) {
                holder.thumbnailView.setImageResource(R.drawable.default_logo);
            } else {
                mImageLoader.loadAssetsImage(mContext, Uri.parse(CommonConstants.ROOT_PATH + item.imageUrlSmall), holder.thumbnailView);
            }

            return view;
        }

        static class ViewHolder {
            @Bind(R.id.thumbnail)
            ImageView thumbnailView;
            @Bind(R.id.title)
            TextView title;
            @Bind(R.id.delete_butt)
            ImageButton deleteCart;
            @Bind(R.id.touch_area)
            View touchArea;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

    }


}
