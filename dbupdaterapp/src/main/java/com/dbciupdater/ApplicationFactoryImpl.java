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
import com.dbciupdater.api.ArgumentsParser;
import com.dbciupdater.api.ScriptsExecutor;
import com.dbciupdater.api.ScriptsFinder;
import com.dbciupdater.argsselector.ArgumentsParserBean;
import com.dbciupdater.executor.ScriptsExecutorBean;
import com.dbciupdater.folderswalker.ScriptsFinderBean;

public class ApplicationFactoryImpl implements ApplicationFactory {

    private static final ScriptsFinder SCRIPTS_FINDER_INST = new ScriptsFinderBean();
    private static final ArgumentsParser PARSER_INST = new ArgumentsParserBean();
    private static final ScriptsExecutor SCRIPTS_EXECUTOR_INST = new ScriptsExecutorBean();

    @Override
    public ScriptsFinder getScriptsFinder() {
        return SCRIPTS_FINDER_INST;
    }

    @Override
    public ArgumentsParser getArgumentsParser() {
        return PARSER_INST;
    }

    @Override
    public ScriptsExecutor getScriptsExecutor() {
        return SCRIPTS_EXECUTOR_INST;
    }
}
