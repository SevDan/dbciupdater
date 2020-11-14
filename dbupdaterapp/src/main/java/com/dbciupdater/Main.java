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

package com.dbciupdater;

import com.dbciupdater.api.ApplicationFactory;
import com.dbciupdater.argsselector.Argument;
import com.dbciupdater.folderswalker.SingleTransactionSqlScript;

import java.util.List;

import static java.lang.System.out;

public class Main {

    // Usage:
    // java -jar app.jar \
    // -dbms postgresql  \
    // -dbname database  \
    // -port 5432        \
    // -scripts /data/   \
    // -user root        \
    // -password password
    public static void main(String[] args) {
        var app = ApplicationFactory.getDefaultFactoryInstance();

        var parser = app.getArgumentsParser();
        var scriptsFinder = app.getScriptsFinder();
        var scriptsExecutor = app.getScriptsExecutor();

        List<Argument> arguments = parser.parseArguments(args);
        List<SingleTransactionSqlScript> scripts = scriptsFinder.findScripts(arguments);
        int executedCount = scriptsExecutor.executeScripts(arguments, scripts);

        out.println("Successfully executed " + executedCount + " scripts. Database updated!");
    }
}
