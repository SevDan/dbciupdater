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

package com.dbciupdater.folderswalker;

import com.dbciupdater.argsselector.Argument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ScriptFinder {

    public List<SqlUpdateScript> findScripts(List<Argument> arguments) {
        String scriptsFolderPath = extractScriptsFolderPath(arguments);

        Path folderPath = Paths.get(scriptsFolderPath);

        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectory(folderPath);
            } catch (IOException e) {
                System.err.println("Cannot create folder with path" + folderPath.getFileName());
                throw new RuntimeException(e);
            }
        }

        // todo : finish scripts finding
        return null;
    }

    private String extractScriptsFolderPath(List<Argument> arguments) {
        for (Argument argument : arguments) {
            if ("-scripts".equals(argument.getName())) {
                return argument.getValue();
            }
        }

        return "./update/";
    }
}
