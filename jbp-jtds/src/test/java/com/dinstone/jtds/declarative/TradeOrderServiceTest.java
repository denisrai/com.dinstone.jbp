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

package com.dinstone.jtds.declarative;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dinstone.jtds.model.TradeOrderData;
import com.dinstone.jtds.model.UserInfo;

/**
 * @author guojf
 * @version 1.0.0.2013-6-4
 */
public class TradeOrderServiceTest {

    private ClassPathXmlApplicationContext context;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        String configLocation = "declarative-mode-context.xml";
        context = new ClassPathXmlApplicationContext(configLocation);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        context.close();
    }

    /**
     * Test method for
     * {@link com.dinstone.jtds.declarative.TradeOrderService#updateTradeOrder(com.dinstone.jtds.model.TradeOrderData)}
     * .
     */
    @Test
    public void testUpdateTradeOrder() {
        TradeOrderService service = (TradeOrderService) context.getBean("tradeService");
        UserInfo user = new UserInfo();
        user.setUserId(4);
        user.setName("dinstone1");
        user.setAge(48);

        TradeOrderData order = new TradeOrderData();
        order.setOrderId(12323L);
        order.setUser(user);

        try {
            service.updateTradeOrder(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
