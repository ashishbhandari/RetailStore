package com.crazymin2.retailstore.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.crazymin2.retailstore.R;


/**
 * Created by b_ashish on 11-Jan-16.
 */
public class CollectionRecyclerView extends RecyclerView {


    private int mContentTopClearance = 0;
    private int mInternalPadding;

    public CollectionRecyclerView(Context context) {
        this(context, null);
    }

    public CollectionRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollectionRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            final TypedArray xmlArgs = context.obtainStyledAttributes(attrs, R.styleable.CollectionView, defStyle, 0);
            mInternalPadding = xmlArgs.getDimensionPixelSize(R.styleable.CollectionView_internalPadding, 0);
            mContentTopClearance = xmlArgs.getDimensionPixelSize(R.styleable.CollectionView_contentTopClearance, 0);
            xmlArgs.recycle();
        }
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
}
