/*
 * Copyright (C) 2012~2013 dinstone<dinstone@163.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dinstone.jtds.model;

/**
 * @author guojf
 * @version 1.0.0.2013-6-3
 */
public class TradeOrderData {

    private UserInfo user;

    private long orderId;

    /**
     * the user to get
     * 
     * @return the user
     * @see TradeOrderData#user
     */
    public UserInfo getUser() {
        return user;
    }

    /**
     * the user to set
     * 
     * @param user
     * @see TradeOrderData#user
     */
    public void setUser(UserInfo user) {
        this.user = user;
    }

    /**
     * the orderId to get
     * 
     * @return the orderId
     * @see TradeOrderData#orderId
     */
    public long getOrderId() {
        return orderId;
    }

    /**
     * the orderId to set
     * 
     * @param orderId
     * @see TradeOrderData#orderId
     */
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

}
