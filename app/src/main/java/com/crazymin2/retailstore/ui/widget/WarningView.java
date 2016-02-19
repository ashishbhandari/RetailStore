package com.crazymin2.retailstore.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crazymin2.retailstore.R;


/**
 * Created by b_ashish on 27-Dec-15.
 * <p/>
 * This class is used to display screen when no internet connection or no data will be available
 */
public class WarningView extends LinearLayout implements View.OnClickListener {

    private View mRoot;

    private ImageView mSignalLogo;
    private TextView mWarningText;
    private OnRetryTapListener mListener = null;
    private Button mButtonRetry;

    public static final int ANIM_DURATION = 200;

    public interface OnRetryTapListener {
        public void onRetryTap();
    }

    public WarningView(Context context) {
        super(context, null, 0);
        initialize(context, null, 0);
    }

    public WarningView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initialize(context, attrs, 0);
    }

    public WarningView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    public void setListener(OnRetryTapListener listener) {
        mListener = listener;
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflater.inflate(R.layout.warning_layout, this, true);

        mSignalLogo = (ImageView) mRoot.findViewById(R.id.signal_logo);
        mWarningText = (TextView) mRoot.findViewById(R.id.signal_logo_warning_text);
        mButtonRetry = (Button) mRoot.findViewById(R.id.button_retry);
        mRoot.setOnClickListener(this);
        mButtonRetry.setOnClickListener(this);
    }

    public void displayWarning(boolean isNoInternetConnection) {
        if (isNoInternetConnection) {
            mSignalLogo.setVisibility(View.VISIBLE);
            mWarningText.setText(R.string.no_internet_connection);
        } else {
            mSignalLogo.setVisibility(View.GONE);
            mWarningText.setText(R.string.no_record_to_show);
        }
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }
        mListener.onRetryTap();
    }

    public void dismiss() {
        dismiss(false);
    }

    public void dismiss(boolean animate) {
//        if (!animate) {
        setVisibility(View.GONE);
//        } else {
//            animate().scaleY(0.1f).alpha(0.1f).setDuration(ANIM_DURATION);
//        }
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }

}
