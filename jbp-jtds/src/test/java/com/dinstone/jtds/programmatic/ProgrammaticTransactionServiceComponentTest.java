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

package com.dinstone.jtds.programmatic;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dinstone.jtds.model.UserInfo;

/**
 * @author guojf
 * @version 1.0.0.2013-6-3
 */
public class ProgrammaticTransactionServiceComponentTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProgrammaticTransactionServiceComponentTest.class);

    private ClassPathXmlApplicationContext context;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        String configLocation = "programmatic-mode-context.xml";
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
     * {@link com.dinstone.jtds.programmatic.ProgrammaticTransactionServiceComponent#updateUserInfo(com.dinstone.jtds.model.UserInfo)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testUpdateUserInfo() throws Exception {
        ProgrammaticTransactionServiceComponent service = (ProgrammaticTransactionServiceComponent) context
            .getBean("programmaticTransactionServiceComponent");
        UserInfo user = new UserInfo();
        user.setUserId(4);
        user.setName("xxxx");
        user.setAge(48);

        UserInfo expected = service.findUserInfo(user);
        try {
            service.updateUserInfo(user);
        } catch (Exception e) {
            LOG.warn("have an excepiton", e);
        }
        UserInfo actual = service.findUserInfo(user);
        Assert.assertEquals(expected.getName(), actual.getName());
    }
}
