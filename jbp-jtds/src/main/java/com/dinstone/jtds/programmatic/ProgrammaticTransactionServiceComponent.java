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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.dinstone.jtds.model.UserInfo;

/**
 * @author guojf
 * @version 1.0.0.2013-6-3
 */
public class ProgrammaticTransactionServiceComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ProgrammaticTransactionServiceComponent.class);

    private SimpleJdbcTemplate simpleJdbcTemplate;

    private PlatformTransactionManager txManager;

    public void updateUserInfo(final UserInfo user) throws Exception {
        TransactionTemplate transactionTemplate = new TransactionTemplate(txManager);
        transactionTemplate.setName("ProgrammaticTransactionService");

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                doUpdate(user);
                findUserInfo(user);
                deException(user);
            }

        });

    }

    /**
     * @param user
     * @return
     */
    public UserInfo findUserInfo(final UserInfo user) {
        String sql = "select * from user where user_id=?";
        UserInfo ut = simpleJdbcTemplate.queryForObject(sql,
            ParameterizedBeanPropertyRowMapper.newInstance(UserInfo.class), user.getUserId());
        LOG.info("user info id={},name={},age={}", new String[] { "" + ut.getUserId(), ut.getName(), "" + ut.getAge() });

        return ut;
    }

    /**
     * @param user
     */
    private void doUpdate(UserInfo user) {
        String sql = "update user set name=?, age=? where user_id=?";
        simpleJdbcTemplate.update(sql, user.getName(), user.getAge(), user.getUserId());
    }

    /**
     * @param user
     */
    private void deException(UserInfo user) {
        String sql = "update nontable set name=?, age=? where user_id=?";
        simpleJdbcTemplate.update(sql, user.getName(), user.getAge(), user.getUserId());
    }

    /**
     * the simpleJdbcTemplate to set
     * 
     * @param simpleJdbcTemplate
     * @see ProgrammaticTransactionServiceComponent#simpleJdbcTemplate
     */
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    /**
     * the txManager to set
     * 
     * @param txManager
     * @see ProgrammaticTransactionServiceComponent#txManager
     */
    public void setTxManager(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

}
