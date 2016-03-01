package com.crazymin2.retailstore.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crazymin2.retailstore.ApplicationController;
import com.crazymin2.retailstore.home.data.Category;
import com.crazymin2.retailstore.home.data.Product;

import java.util.ArrayList;

import static com.crazymin2.retailstore.util.LogUtils.LOGE;
import static com.crazymin2.retailstore.util.LogUtils.makeLogTag;

public class DatabaseManager {

    private static final String TAG = makeLogTag(DatabaseManager.class);
    private static DatabaseManager singleInstance;

    /**
     * Category Table which defines all the columns of this table
     */
    interface CATEGORY {
        public static String TABLE_NAME = "category";
        public static String _ID = "_id";
        public static String CATEGORY_ID = "category_id";
        public static String IMAGE_URL_ORIGINAL = "image_url_original";
        public static String IMAGE_URL_THUMB = "image_url_thumb";
        public static String IMAGE_URL_SMALL = "image_url_small";
        public static String IMAGE_URL_MEDIUM = "image_url_medium";
        public static String NAME = "name";
    }

    /**
     * Product Table which defines all the columns of this table
     */
    interface PRODUCT {
        public static String TABLE_NAME = "product";
        public static String _ID = "_id";
        public static String CATEGORY_ID = "category_id";
        public static String PRODUCT_ID = "product_id";
        public static String PRICE = "price";
        public static String IMAGE_URL_ORIGINAL = "image_url_original";
        public static String IMAGE_URL_THUMB = "image_url_thumb";
        public static String IMAGE_URL_SMALL = "image_url_small";
        public static String IMAGE_URL_MEDIUM = "image_url_medium";
        public static String NAME = "name";
    }

    /**
     * Cart Table which defines all the columns of this table
     */
    interface CART_PRODUCT {
        public static String TABLE_NAME = "cart_product";
        public static String _ID = "_id";
        public static String CATEGORY_ID = "category_id";
        public static String PRODUCT_ID = "product_id";
        public static String TOTAL_PRICE = "total_price";
    }

    /**
     * Singleton object of the class in order to access the database assets and resources.
     */
    public static DatabaseManager getInstance() {
        if (singleInstance == null) {
            singleInstance = new DatabaseManager();
        }
        return singleInstance;
    }

    /**
     * get Column Index value of the database column
     *
     * @return int
     */
    private int getColumnIndex(Cursor cursor, String columnName) {
        return cursor.getColumnIndex(columnName);
    }

    /**
     * get String value of the database column
     *
     * @return String
     */
    private String getStringValue(Cursor cursor, String columnName) {
        return cursor.getString(getColumnIndex(cursor, columnName));
    }

    /**
     * get long value of the database column
     *
     * @return String
     */
    private long getLongValue(Cursor cursor, String columnName) {
        return cursor.getLong(getColumnIndex(cursor, columnName));
    }

    /**
     * get Integer value of the database column
     *
     * @return int
     */
    private int getIntValue(Cursor cursor, String columnName) {
        try {
            return cursor.getInt(getColumnIndex(cursor, columnName));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * get long value of the database column
     *
     * @return String
     */
    private double getDoubleValue(Cursor cursor, String columnName) {
        return cursor.getDouble(getColumnIndex(cursor, columnName));
    }

    public ArrayList<Category> getCategories() {
        SQLiteDatabase database = null;
        ArrayList<Category> categories = new ArrayList<Category>();
        Cursor cursor = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(ApplicationController.getInstance());
            database = dbHelper.getReadableDatabase();
            cursor = database.query(CATEGORY.TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    Category category = new Category();
                    category.id = getIntValue(cursor, CATEGORY._ID);
                    category.categoryId = getIntValue(cursor, CATEGORY.CATEGORY_ID);
                    category.imageUrlOriginal = getStringValue(cursor, CATEGORY.IMAGE_URL_ORIGINAL);
                    category.imageUrlThumb = getStringValue(cursor, CATEGORY.IMAGE_URL_THUMB);
                    category.imageUrlSmall = getStringValue(cursor, CATEGORY.IMAGE_URL_SMALL);
                    category.imageUrlMedium = getStringValue(cursor, CATEGORY.IMAGE_URL_MEDIUM);
                    category.name = getStringValue(cursor, CATEGORY.NAME);
                    categories.add(category);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return categories;
    }

    public ArrayList<Product> getProductsByCategory(int categoryId) {
        SQLiteDatabase database = null;
        ArrayList<Product> products = new ArrayList<Product>();
        Cursor cursor = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(ApplicationController.getInstance());
            database = dbHelper.getReadableDatabase();
            String whereClause = PRODUCT.CATEGORY_ID + " = ?";
            String[] whereArgs = {"" + categoryId};
            cursor = database.query(PRODUCT.TABLE_NAME, null, whereClause, whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    Product product = new Product();

                    product.id = getIntValue(cursor, PRODUCT._ID);
                    product.categoryId = getIntValue(cursor, PRODUCT.CATEGORY_ID);
                    product.productId = getIntValue(cursor, PRODUCT.PRODUCT_ID);
                    product.imageUrlOriginal = getStringValue(cursor, PRODUCT.IMAGE_URL_ORIGINAL);
                    product.imageUrlThumb = getStringValue(cursor, PRODUCT.IMAGE_URL_THUMB);
                    product.imageUrlSmall = getStringValue(cursor, PRODUCT.IMAGE_URL_SMALL);
                    product.imageUrlMedium = getStringValue(cursor, PRODUCT.IMAGE_URL_MEDIUM);
                    product.name = getStringValue(cursor, PRODUCT.NAME);
                    product.price = getStringValue(cursor, PRODUCT.PRICE);

                    products.add(product);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return products;
    }

    public boolean saveCartProduct(Product myCartProduct) {

        SQLiteDatabase database = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(ApplicationController.getInstance());
            database = dbHelper.openDataBase();

            ContentValues contentValues = new ContentValues();

            contentValues.put(CART_PRODUCT.CATEGORY_ID, myCartProduct.categoryId);
            contentValues.put(CART_PRODUCT.PRODUCT_ID, myCartProduct.productId);
            contentValues.put(CART_PRODUCT.TOTAL_PRICE, myCartProduct.totalPrice);

            long productId = database.insert(CART_PRODUCT.TABLE_NAME, null, contentValues);
            if (productId == -1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGE(TAG, "Exception in saving product in cart ", e);
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return false;
    }

    public int getProductCount() {

        SQLiteDatabase database = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(ApplicationController.getInstance());
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.query(CART_PRODUCT.TABLE_NAME, null, null, null, null, null, null);
            return cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return 0;

    }

    public ArrayList<Product> getCartProducts() {
        SQLiteDatabase database = null;
        ArrayList<Product> products = new ArrayList<Product>();
        Cursor cursor = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(ApplicationController.getInstance());
            database = dbHelper.getReadableDatabase();

            final String MY_QUERY = "SELECT * FROM cart_product cp, product p WHERE  cp.product_id = p.product_id";
            cursor = database.rawQuery(MY_QUERY, null);

            if (cursor.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.id = getIntValue(cursor, PRODUCT._ID);
                    product.categoryId = getIntValue(cursor, PRODUCT.CATEGORY_ID);
                    product.productId = getIntValue(cursor, PRODUCT.PRODUCT_ID);
                    product.imageUrlOriginal = getStringValue(cursor, PRODUCT.IMAGE_URL_ORIGINAL);
                    product.imageUrlThumb = getStringValue(cursor, PRODUCT.IMAGE_URL_THUMB);
                    product.imageUrlSmall = getStringValue(cursor, PRODUCT.IMAGE_URL_SMALL);
                    product.imageUrlMedium = getStringValue(cursor, PRODUCT.IMAGE_URL_MEDIUM);
                    product.name = getStringValue(cursor, PRODUCT.NAME);
                    product.price = getStringValue(cursor, PRODUCT.PRICE);

                    products.add(product);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return products;
    }

    public boolean deleteCartProduct(Product myCartProduct) {

        SQLiteDatabase database = null;
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(ApplicationController.getInstance());
            database = dbHelper.openDataBase();
            String whereClause = CART_PRODUCT.PRODUCT_ID + " = ?";
            String[] whereArgs = {"" + myCartProduct.productId};

            int deleted = database.delete(CART_PRODUCT.TABLE_NAME, whereClause, whereArgs);

            if (deleted == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGE(TAG, "Exception while deleting cart product ", e);
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return false;
    }

}
