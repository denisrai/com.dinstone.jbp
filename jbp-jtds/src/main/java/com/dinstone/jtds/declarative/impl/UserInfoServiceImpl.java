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

import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.dinstone.jtds.declarative.UserInfoService;
import com.dinstone.jtds.model.UserInfo;

/**
 * @author guojf
 * @version 1.0.0.2013-6-4
 */
public class UserInfoServiceImpl implements UserInfoService {

    private SimpleJdbcTemplate simpleJdbcTemplate;

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.jtds.declarative.UserInfoService#addUserInfo(com.dinstone.jtds.model.UserInfo)
     */
    public int addUserInfo(UserInfo user) {
        throw new RuntimeException("addUserInfo error");
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.jtds.declarative.UserInfoService#getUserInfo(java.lang.String)
     */
    public UserInfo getUserInfo(String userName) {
        String sql = "select * from user where name=?";
        UserInfo ut = simpleJdbcTemplate.queryForObject(sql,
            ParameterizedBeanPropertyRowMapper.newInstance(UserInfo.class), userName);

        return ut;
    }

    /**
     * the simpleJdbcTemplate to set
     * 
     * @param simpleJdbcTemplate
     * @see UserInfoServiceImpl#simpleJdbcTemplate
     */
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
