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

/*
* MVP: - View model
* View model that provides view to activities and fragments
*/
public interface CartView<T> {

    /*
    * update total items in cart
    */
    void displayCounter(int size);

    /*
    * load all items present in cart
    */
    void displayCartItems(List<T> items);

    /*
    * this method will help to load categories items for items inside
    * e.g furniture and electronic category
    */
    void displayCategoryItems(List<T> items);

    void onProductActionResponse(boolean isActionSuccessful);

    /*
    * On successful deletion of an item from cart
    */
    void onSuccessfulDeletion(Object item);


}
