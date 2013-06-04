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

package com.dinstone.jtds.local;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dinstone.jtds.model.UserInfo;

/**
 * @author guojf
 * @version 1.0.0.2013-6-3
 */
public class LocalTransactionServiceComponentTest {

    private ClassPathXmlApplicationContext context;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        String configLocation = "local-mode-context.xml";
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
     * {@link com.dinstone.jtds.local.LocalTransactionServiceComponent#updateUserInfo(com.dinstone.jtds.model.UserInfo)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateUserInfo() throws Exception {
        LocalTransactionServiceComponent service = (LocalTransactionServiceComponent) context
            .getBean("localTransactionServiceComponent");
        UserInfo user = new UserInfo();
        user.setUserId(3);
        user.setName("dinstone");
        user.setAge(28);
        service.updateUserInfo(user);
    }

}
