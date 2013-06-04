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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.jtds.model.UserInfo;

/**
 * @author guojf
 * @version 1.0.0.2013-6-3
 */
public class LocalTransactionServiceComponent {

    private static final Logger LOG = LoggerFactory.getLogger(LocalTransactionServiceComponent.class);

    private DataSource datasource;

    public void updateUserInfo(UserInfo user) throws Exception {
        Connection conn = null;
        // get connection from data source
        try {
            conn = datasource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get JDBC Connection", e);
        }

        PreparedStatement stmt = null;
        try {
            boolean auto = conn.getAutoCommit();
            LOG.info("default connection auto commit state is {}.", auto);

            // set auto commit is false, begin transaction
            conn.setAutoCommit(false);

            String sql = "update user set name=?, age=? where user_id=?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getName());
            stmt.setInt(2, user.getAge());
            stmt.setInt(3, user.getUserId());
            stmt.executeUpdate();

            // commit transaction
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException se) {
            }

            throw e;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }

    }

    /**
     * the datasource to set
     * 
     * @param datasource
     * @see LocalTransactionServiceComponent#datasource
     */
    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }

}
