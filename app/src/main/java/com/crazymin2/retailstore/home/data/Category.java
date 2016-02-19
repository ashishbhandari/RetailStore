package com.crazymin2.retailstore.home.data;

/**
 * Created by ashish (Min2) on 08/02/16.
 * <p/>
 * Avoiding internal setters/getters
 * http://developer.android.com/training/articles/perf-tips.html#GettersSetters
 */
public class Category {

    public int id;
    public int categoryId;
    public String imageUrlOriginal;
    public String imageUrlThumb;
    public String imageUrlSmall;
    public String imageUrlMedium;
    public String name;

}
