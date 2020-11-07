/*
 * Copyright 2020 SevDan (Daniil Sevostyanov)
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

package com.dbciupdater.argsselector;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Argument implements Serializable {

    private static final long serialVersionUID = 7907292696098935858L;

    public static final Set<String> AVAILABLE_ARGS = new HashSet<>();

    static {
        AVAILABLE_ARGS.addAll(Arrays.asList(
                "-dbms",
                "-dbname",
                "-port",
                "-scripts",
                "-user",
                "-password"
        ));
    }

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
