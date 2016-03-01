/*
 *
 *  * Copyright (C) 2014 Ashish Bhandari.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.crazymin2.retailstore.framework;

import java.util.List;

/**
 * MVP: - Presenter model
 * <p>
 * Created by b_ashish on 23-Feb-16.
 */
public class CartPresenterImpl implements CartPresenter, OnCartResponseListener {

    private CartView cartView;
    private CartInteractor cartInteractor;

    public CartPresenterImpl(CartView cartView) {
        this.cartView = cartView;
        this.cartInteractor = new CartInteractorImpl();
    }

    @Override
    public void onSuccess() {
        if (cartView != null) {
            cartView.onProductActionResponse(true);
        }
    }

    @Override
    public void onSuccessfulRemoved(Object item) {
        if (cartView != null) {
            cartView.onSuccessfulDeletion(item);
        }
    }

    @Override
    public void onError() {
        if (cartView != null) {
            cartView.onProductActionResponse(false);
        }
    }

    @Override
    public void onDisplayItemsOnCart(List items) {
        if (cartView != null && items != null) {
            cartView.displayCartItems(items);
        }
    }

    @Override
    public void onDisplayCategoryItems(List items) {
        if (cartView != null && items != null) {
            cartView.displayCategoryItems(items);
        }
    }

    @Override
    public void onDisplayTotalItem(int size) {
        if (cartView != null) {
            cartView.displayCounter(size);
        }
    }

    @Override
    public void addItem(Object item) {
        cartInteractor.saveItemInCart(item, this);
    }

    @Override
    public void removeItem(Object item) {
        cartInteractor.removeItemFromCart(item, this);
    }

    @Override
    public void showMeCategoryItems(int categoryId) {
        cartInteractor.getCategoryItems(this, categoryId);
    }

    @Override
    public void onDestroy() {
        cartView = null;
    }

    @Override
    public void showMeItemsInCart() {
        cartInteractor.getTotalItemsInCart(this);
    }

    @Override
    public void countCartItems() {
        cartInteractor.countTotalItems(this);
    }
}
