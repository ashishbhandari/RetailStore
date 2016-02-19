package com.crazymin2.retailstore.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.crazymin2.retailstore.R;

import static com.crazymin2.retailstore.util.LogUtils.makeLogTag;

public class CollectionView extends ListView implements OnScrollListener {


    private static final String TAG = makeLogTag(CollectionView.class);

    private int mContentTopClearance = 0;
    private int mInternalPadding;

    /**
     * Listener that will receive notifications every timeInSeconds the list scrolls.
     */
    private OnScrollListener mOnScrollListener;
    // Listener to process load more items when user reaches the end of the list
    private OnLoadMoreListener mOnLoadMoreListener;

    // To know if the list is loading more items
    private boolean mIsLoadingMore = false;
    private int mCurrentScrollState;

    // footer view
    private RelativeLayout mFooterView;
    // private TextView mLabLoadMore;
    private ProgressBar mProgressBarLoadMore;
    private LayoutInflater mInflater;

    public CollectionView(Context context) {
        this(context, null);
        init(context);
    }

    public CollectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public CollectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDivider(null);
        setDividerHeight(0);
        setItemsCanFocus(false);
        setChoiceMode(ListView.CHOICE_MODE_NONE);
        setSelector(android.R.color.transparent);

        if (attrs != null) {
            final TypedArray xmlArgs = context.obtainStyledAttributes(attrs,
                    R.styleable.CollectionView, defStyle, 0);
            mInternalPadding = xmlArgs.getDimensionPixelSize(
                    R.styleable.CollectionView_internalPadding, 0);
            mContentTopClearance = xmlArgs.getDimensionPixelSize(
                    R.styleable.CollectionView_contentTopClearance, 0);
            xmlArgs.recycle();
        }
        init(context);
    }

    /**
     * Programmatically sets a clearance space above the element.
     *
     * @param clearance Space to clear above the element in pixels.
     */
    public void setContentTopClearance(int clearance) {
        if (mContentTopClearance != clearance) {
            mContentTopClearance = clearance;
            setPadding(getPaddingLeft(), mContentTopClearance,
                    getPaddingRight(), getPaddingBottom());

            notifyAdapterDataSetChanged();
        }
    }

    private void notifyAdapterDataSetChanged() {
        // We have to set up a new adapter (as opposed to just calling notifyDataSetChanged()
        // because we might need MORE view types than before, and ListView isn't prepared to
        // handle the case where its existing adapter suddenly needs to increase the number of
        // view types it needs.
//        setAdapter(new MyListAdapter());
    }

    private void init(Context context) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // footer
        mFooterView = (RelativeLayout) mInflater.inflate(R.layout.load_more_footer, this, false);
        /*
         * mLabLoadMore = (TextView) mFooterView
		 * .findViewById(R.id.load_more_lab_view);
		 */
        mProgressBarLoadMore = (ProgressBar) mFooterView.findViewById(R.id.load_more_progressBar);

        addFooterView(mFooterView);

        super.setOnScrollListener(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    /**
     * Set the listener that will receive notifications every timeInSeconds the list
     * scrolls.
     *
     * @param l The scroll listener.
     */
    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * Register a callback to be invoked when this list reaches the end (last
     * item be visible)
     *
     * @param onLoadMoreListener The callback to run.
     */

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //bug fix: listview was not clickable after scroll
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            view.invalidateViews();
        }

        mCurrentScrollState = scrollState;

        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if (mOnLoadMoreListener != null) {

            if (visibleItemCount == totalItemCount) {
                mProgressBarLoadMore.setVisibility(View.GONE);
                // mLabLoadMore.setVisibility(View.GONE);
                return;
            }

            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

            if (!mIsLoadingMore && loadMore && mCurrentScrollState != SCROLL_STATE_IDLE) {
                mProgressBarLoadMore.setVisibility(View.VISIBLE);
                // mLabLoadMore.setVisibility(View.VISIBLE);
                mIsLoadingMore = true;
                onLoadMore();
            }

        }
    }

    /**
     * Interface definition for a callback to be invoked when list reaches the
     * last item (the user load more items in the list)
     */
    public interface OnLoadMoreListener {
        /**
         * Called when the list reaches the last item (the last item is visible
         * to the user)
         */
        public void onLoadMore();
    }


    public void onLoadMore() {
        Log.d(TAG, "onLoadMore");
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    /**
     * Notify the loading more operation has finished
     */
    public void onLoadMoreComplete() {
        mIsLoadingMore = false;
        mProgressBarLoadMore.setVisibility(View.GONE);
    }

}
