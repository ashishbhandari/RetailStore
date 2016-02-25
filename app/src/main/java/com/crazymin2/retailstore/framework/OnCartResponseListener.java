package com.crazymin2.retailstore.framework;

import java.util.List;

public interface OnCartResponseListener {

    void onSuccess();

    void onError();

    void onDisplayItemsOnCart(List<String> items);

}
