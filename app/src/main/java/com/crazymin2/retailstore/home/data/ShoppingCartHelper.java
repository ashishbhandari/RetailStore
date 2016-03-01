package com.crazymin2.retailstore.home.data;


public class ShoppingCartHelper {


    public interface OnCustomStateListener {

        void stateChanged();

    }

    private OnCustomStateListener mListener;
    private boolean mState;

    private static ShoppingCartHelper singleInstance;


    /**
     * Singleton object of the class in order to access the database assets and resources.
     */
    public static ShoppingCartHelper getInstance() {
        if (singleInstance == null) {
            singleInstance = new ShoppingCartHelper();
        }
        return singleInstance;
    }

//    public void setListener(OnCustomStateListener listener) {
//        mListener = listener;
//    }
//
//    public void changeState(boolean state) {
//        if (mListener != null) {
//            mState = state;
//            notifyStateChange();
//        }
//    }
//
//    public boolean getState() {
//        return mState;
//    }
//
//    private void notifyStateChange() {
//        mListener.stateChanged();
//    }
}
