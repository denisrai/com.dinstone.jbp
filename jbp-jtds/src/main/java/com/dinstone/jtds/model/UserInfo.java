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
public class UserInfo {

    private int userId;

    private String name;

    private int age;

    /**
     * the userId to get
     * 
     * @return the userId
     * @see UserInfo#userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * the userId to set
     * 
     * @param userId
     * @see UserInfo#userId
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * the name to get
     * 
     * @return the name
     * @see UserInfo#name
     */
    public String getName() {
        return name;
    }

    /**
     * the name to set
     * 
     * @param name
     * @see UserInfo#name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * the age to get
     * 
     * @return the age
     * @see UserInfo#age
     */
    public int getAge() {
        return age;
    }

    /**
     * the age to set
     * 
     * @param age
     * @see UserInfo#age
     */
    public void setAge(int age) {
        this.age = age;
    }

}
