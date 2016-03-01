package com.crazymin2.retailstore.framework;

import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.crazymin2.retailstore.database.DatabaseManager;
import com.crazymin2.retailstore.home.data.Product;
import com.crazymin2.retailstore.home.data.ShoppingCartHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.crazymin2.retailstore.util.LogUtils.LOGD;

/**
 * * MVP: - Model architecture
 * <p>
 * Created by b_ashish on 23-Feb-16.
 */
public class CartInteractorImpl implements CartInteractor {


    private static final String TAG = CartInteractorImpl.class.getSimpleName();

    @Override
    public void saveItemInCart(Object item, OnCartResponseListener listener) {

        boolean persistProduct = DatabaseManager.getInstance().saveCartProduct((Product) item);
        if (persistProduct) {
            LOGD(TAG, "Item added into local database");
            listener.onSuccess();
        } else {
            LOGD(TAG, "Item not added into local database");
            listener.onError();
        }
    }

    @Override
    public void removeItemFromCart(Object item, OnCartResponseListener listener) {

        boolean deleteCartProduct = DatabaseManager.getInstance().deleteCartProduct((Product) item);
        if (deleteCartProduct) {
            LOGD(TAG, "Item successfully deleted from local database");
            listener.onSuccessfulRemoved((Product) item);
        } else {
            LOGD(TAG, "Item not deleted from local database");
            listener.onError();
        }
    }

    @Override
    public void getTotalItemsInCart(final OnCartResponseListener listener) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<Product> cartProducts = DatabaseManager.getInstance().getCartProducts();
                listener.onDisplayItemsOnCart(cartProducts);
            }
        });
    }

    @Override
    public void countTotalItems(OnCartResponseListener listener) {
        int productCount = DatabaseManager.getInstance().getProductCount();
        listener.onDisplayTotalItem(productCount);
    }

    @Override
    public void getCategoryItems(final OnCartResponseListener listener, final int categoryId) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                ArrayList<Product> cartProducts = DatabaseManager.getInstance().getCartProducts();
                ArrayList<Product> productsByCategory = DatabaseManager.getInstance().getProductsByCategory(categoryId);

                int position = 0;
                for (Product product : productsByCategory) {
                    if (!cartProducts.contains(product)) {
                        productsByCategory.get(position).isInCart = false;
                        LOGD(TAG, "item not matched");
                    } else {
                        productsByCategory.get(position).isInCart = true;
                        LOGD(TAG, "item matched");
                    }
                    position++;
                }
                listener.onDisplayCategoryItems(productsByCategory);
            }
        });
    }
}
