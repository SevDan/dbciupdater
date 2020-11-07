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

package com.dbciupdater.folderswalker;

import com.dbciupdater.argsselector.Argument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.err;

public class ScriptFinder {

    public List<SqlUpdateScript> findScripts(List<Argument> arguments) {
        String scriptsFolderPath = extractScriptsFolderPath(arguments);

        Path folderPath = Paths.get(scriptsFolderPath);

        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectory(folderPath);
            } catch (IOException e) {
                err.println("Cannot create folder with path" + folderPath.getFileName());
                throw new RuntimeException(e);
            }
        }

        List<SqlUpdateScript> result = new ArrayList<>();

        try {
            Stream<Path> walk = Files.walk(folderPath, 1);

            List<Path> collect = walk.collect(Collectors.toList());

            for (Path filePath : collect) {
                if (!Files.isReadable(filePath)) {
                    err.println("Cannot read file " + filePath.getFileName() + " | skipped ");
                    continue;
                }

                String fileName = filePath.getFileName().toString();

                if (!fileName.endsWith(".sql")) {
                    err.println("Wrong file extension. Should be .sql " + fileName + " | skipped ");
                    continue;
                }

                SqlUpdateScript script = buildScript(filePath, fileName);

                result.add(script);
            }
        } catch (IOException e) {
            err.println("Cannot walk in folder (maybe has no rights)");
            throw new RuntimeException(e);
        }

        result.sort(Comparator.comparing(SqlUpdateScript::getFileName));

        return result;
    }

    private SqlUpdateScript buildScript(Path filePath, String fileName) throws IOException {
        List<String> scriptLines = Files.readAllLines(filePath);
        String resultScript = String.join(" ", scriptLines);

        SqlUpdateScript script = new SqlUpdateScript();
        script.setFileName(fileName);
        script.setScript(resultScript);
        return script;
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
