/*
 * Copyright (C) 2012~2013 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dinstone.exception.example;

import com.dinstone.exception.ErrorCode;

public enum PaymentCode implements ErrorCode {

    SERVICE_TIMEOUT(101), CREDIT_CARD_EXPIRED(102), AMOUNT_TOO_HIGH(103), INSUFFICIENT_FUNDS(104);

    private final int number;

    private PaymentCode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

}