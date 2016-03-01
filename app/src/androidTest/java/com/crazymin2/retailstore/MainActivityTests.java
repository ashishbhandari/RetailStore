package com.crazymin2.retailstore;

import android.test.ActivityInstrumentationTestCase2;

import com.crazymin2.retailstore.home.MainActivity;

/**
 * Created by b_ashish on 01-Mar-16.
 */
public class MainActivityTests extends ActivityInstrumentationTestCase2 {

    public MainActivityTests(Class activityClass) {
        super(MainActivity.class);
    }

    public void testActivityExists() {
        MainActivity activity = (MainActivity) getActivity();
        assertNotNull(activity);
    }

}
