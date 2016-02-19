package com.crazymin2.retailstore.ui;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazymin2.retailstore.CommonConstants;
import com.crazymin2.retailstore.R;
import com.crazymin2.retailstore.home.data.Product;
import com.crazymin2.retailstore.util.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductCartListAdapter extends ArrayAdapter<Product> {

    private final LayoutInflater mInflater;
    private final ImageLoader mImageLoader;
    private Context mContext;

    private TouchAreaClicked touchAreaClicked = null;

    public interface TouchAreaClicked {

        public abstract void onTouchArea(int position);

    }

    public ProductCartListAdapter(Context context, TouchAreaClicked listener) {
        super(context, R.layout.product_new_request_row);
        mContext = context;
        mImageLoader = new ImageLoader(mContext, R.drawable.default_logo);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        touchAreaClicked = listener;
    }

    public void setData(List<Product> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }


    /**
     * Populate new items in the list.
     */
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.product_new_request_row, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        Product item = getItem(position);

        holder.title.setText(item.name);

        holder.touchArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (touchAreaClicked != null)
                    touchAreaClicked.onTouchArea(position);

            }
        });

        holder.addCart.setChecked(item.isInCart);

        holder.addCart.setTag(item);

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
        @Bind(R.id.product_selection_checkbox)
        CheckBox addCart;
        @Bind(R.id.touch_area)
        View touchArea;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}