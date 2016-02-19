package com.crazymin2.retailstore.home;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crazymin2.retailstore.R;
import com.crazymin2.retailstore.database.DatabaseManager;
import com.crazymin2.retailstore.home.data.Category;
import com.crazymin2.retailstore.ui.widget.CollectionRecyclerView;
import com.crazymin2.retailstore.ui.widget.DrawShadowFrameLayout;
import com.crazymin2.retailstore.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.crazymin2.retailstore.util.LogUtils.LOGE;

/**
 * Created by ashish (Min2) on 06/02/16.
 */
public class CategoryTabFragment extends CommonTabFragment implements LoaderManager.LoaderCallbacks<List<Category>> {

    @Bind(R.id.list)
    CollectionRecyclerView mCollectionView;

    private static final int TAG_METADATA_TOKEN = 0x8;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof TabListener) {
            ((TabListener) getActivity()).onTabFragmentViewCreated(this);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof TabListener) {
            ((TabListener) getActivity()).onTabFragmentAttached(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof TabListener) {
            ((TabListener) getActivity()).onTabFragmentDetached(this);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(TAG_METADATA_TOKEN, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fragment, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().overridePendingTransition(0, 0);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
        int actionBarSize = UIUtils.calculateActionBarSize(getActivity()) * 2;
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
    public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
        return new CategoryListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Category>> loader, List<Category> data) {
        ((MainActivity) getActivity()).mAdapter.setData((List<Category>) data);
    }

    @Override
    public void onLoaderReset(Loader<List<Category>> loader) {
        ((MainActivity) getActivity()).mAdapter.setData(null);
    }

    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class CategoryListLoader extends AsyncTaskLoader<List<Category>> {

        private static final String TAG = CategoryListLoader.class.getSimpleName();

        List<Category> mApps;


        public CategoryListLoader(Context context) {
            super(context);
        }

        /**
         * This is where the bulk of our work is done. This function is called in a background thread
         * and should generate a new set of data to be published by the loader.
         */
        @Override
        public List<Category> loadInBackground() {

            ArrayList<Category> result = DatabaseManager.getInstance().getCategories();

            try {
                return result;

            } catch (Exception e) {
                LOGE(TAG, "Type of exception ", e);
            }

            return result;
        }

        /**
         * Called when there is new data to deliver to the client. The super class will take care of
         * delivering it; the implementation here just adds a little more logic.
         */
        @Override
        public void deliverResult(List<Category> apps) {
            if (isReset()) {
                // An async query came in while the loader is stopped. We
                // don't need the result.
                if (apps != null) {
                    onReleaseResources(apps);
                }
            }
            List<Category> oldApps = apps;
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
        public void onCanceled(List<Category> apps) {
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
        protected void onReleaseResources(List<Category> apps) {
            // For a simple List<> there is nothing to do. For something
            // like a Cursor, we would close it here.
        }
    }

    static class TabAdapter<T> extends RecyclerView.Adapter<TabAdapter.ViewHolder> {

        Context mContext;
        OnItemClickListener mItemClickListener;
        List<Category> formControlMaterList = new ArrayList<Category>();


        public TabAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<T> data) {
            if (data != null) {
                formControlMaterList = (List<Category>) data;
                this.notifyDataSetChanged();
            }
        }

        @Override
        public TabAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_dashboard, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TabAdapter.ViewHolder holder, final int position) {

            final Category place = formControlMaterList.get(position);

            holder.placeName.setText(place.name);
            colorRow(holder);

        }

        private void colorRow(TabAdapter.ViewHolder holder) {
            int mutedLight = mContext.getResources().getColor(R.color.item_label_bg_light_color);
            holder.placeNameHolder.setBackgroundColor(mutedLight);
            holder.moduleIconContainer.setBackgroundColor(mContext.getResources().getColor(R.color.item_module_bg_light_color));

        }

        @Override
        public int getItemCount() {
            return formControlMaterList.size();
        }

        public int getCategoryId(int position) {
            Category category = formControlMaterList.get(position);
            return category.categoryId;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @Bind(R.id.mainHolder)
            LinearLayout placeHolder;
            @Bind(R.id.placeNameHolder)
            LinearLayout placeNameHolder;
            @Bind(R.id.module_icon_container)
            LinearLayout moduleIconContainer;
            @Bind(R.id.placeName)
            TextView placeName;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                placeHolder.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(itemView, getPosition());
                }
            }
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
        }
    }
}
