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

import com.dbciupdater.api.ApplicationFactory;
import com.dbciupdater.api.ArgumentsParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ArgumentsParserBeanTest {

    private ArgumentsParser parser;

    {
        var app = ApplicationFactory.getDefaultFactoryInstance();
        parser = app.getArgumentsParser();
    }


    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void testUniDbms() {
        // arrange
        var args = new String[]{"-dbms", "some"};
        var expectedArg = new Argument();
        expectedArg.setKey("-dbms");
        expectedArg.setValue("some");

        // act
        List<Argument> actual = parser.parseArguments(args);
        Argument uniActual = actual.get(0);

        // assert
        assertEquals(1, actual.size());
        assertEquals(expectedArg.getKey(), uniActual.getKey());
        assertEquals(expectedArg.getValue(), uniActual.getValue());
    }

    @Test
    public void testUnknownArgument() {
        // arrange
        var args = new String[]{"-unknk123own", "some"};

        expect.expect(IllegalArgumentException.class);
        // act
        List<Argument> actual = parser.parseArguments(args);

        // assert
        expect = ExpectedException.none();
    }

    @Test
    public void testAllArguments() {
        // arrange
        List<String> availableArgsKeys = new ArrayList<>(Argument.AVAILABLE_ARGS);
        int availableArgsCount = availableArgsKeys.size();
        assertNotEquals(availableArgsCount, 0);

        List<Argument> expectedArguments = new ArrayList<>();
        var args = new String[availableArgsCount * 2];

        for (int i = 0; i < availableArgsCount; ++i) {
            String key = availableArgsKeys.get(i);
            String value = i + " " + i;

            var currentArg = new Argument();
            currentArg.setKey(key);
            currentArg.setValue(value);
            expectedArguments.add(currentArg);

            args[i * 2] = key;
            args[i * 2 + 1] = value;
        }

        // act
        var actual = parser.parseArguments(args);


        expectedArguments.sort(Comparator.comparing(Argument::getKey));
        actual.sort(Comparator.comparing(Argument::getKey));

        // assert
        assertEquals(availableArgsCount, actual.size());
        assertEquals(availableArgsCount, expectedArguments.size());

        for (int i = 0; i < availableArgsCount; ++i) {
            assertEquals(expectedArguments.get(i).getKey(), actual.get(i).getKey());
            assertEquals(expectedArguments.get(i).getValue(), actual.get(i).getValue());
        }
    }
}