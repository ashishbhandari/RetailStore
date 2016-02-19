package com.crazymin2.retailstore;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by ashish (Min2) on 09/02/16.
 */
public class CommonConstants {


    public static final String ROOT_PATH = "file:///android_asset/product/";

    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###.##");

    public static String getDecimalString(String value) {

        try {
            double result = Double.parseDouble(value); // Make use of autoboxing.  It's also easier to read.
            String output = decimalFormat.format(result);
            return output;
        } catch (NumberFormatException e) {
            // value did not contain a valid double
            return "";
        }
//        return "" + (value != null ? decimalFormat.format(value) : "");
    }

}
