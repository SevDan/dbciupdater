/*
 * Copyright 2020 SevDan
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

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

public class ArgumentsSelector {

    public List<Argument> selectArguments(String[] inputArgs) {
        List<Argument> result = new ArrayList<>();

        String currentArg = null;

        for (String inputArgument : inputArgs) {
            if (isKeySelection(currentArg)) {
                currentArg = inputArgument;

                if (!Argument.AVAILABLE_ARGS.contains(currentArg)) {
                    throw new IllegalArgumentException(String.format("Wrong argument %s", inputArgument));
                }
            } else {
                result.add(selectArgument(currentArg, inputArgument));
                currentArg = null;
            }
        }

        return result;
    }

    private Argument selectArgument(String argName, String argValue) {
        Argument argument = new Argument();
        argument.setName(argName);
        argument.setValue(argValue);
        return argument;
    }

    private boolean isKeySelection(String currentKey) {
        return Strings.isNullOrEmpty(currentKey);
    }
}
