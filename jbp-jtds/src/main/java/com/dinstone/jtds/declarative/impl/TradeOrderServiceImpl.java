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

package com.dinstone.jtds.declarative.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.jtds.declarative.TradeOrderService;
import com.dinstone.jtds.declarative.UserInfoService;
import com.dinstone.jtds.model.TradeOrderData;
import com.dinstone.jtds.model.UserInfo;

/**
 * @author guojf
 * @version 1.0.0.2013-6-4
 */
public class TradeOrderServiceImpl implements TradeOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(TradeOrderServiceImpl.class);

    private UserInfoService userService;

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.jtds.declarative.TradeOrderService#updateTradeOrder(com.dinstone.jtds.model.TradeOrderData)
     */
    public void updateTradeOrder(TradeOrderData data) {
        UserInfo user = data.getUser();
        try {
            UserInfo u = userService.getUserInfo(user.getName());
            if (u == null) {
                userService.addUserInfo(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOG.info("updateTradeOrder ok");
    }

    /**
     * the userService to set
     * 
     * @param userService
     * @see TradeOrderServiceImpl#userService
     */
    public void setUserService(UserInfoService userService) {
        LOG.info("setting UserInfoService is {}", userService);
        this.userService = userService;
    }

}
