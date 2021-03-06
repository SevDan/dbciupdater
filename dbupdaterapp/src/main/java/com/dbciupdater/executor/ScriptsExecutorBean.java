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

package com.dbciupdater.executor;

import com.dbciupdater.api.ScriptsExecutor;
import com.dbciupdater.argsselector.Argument;
import com.dbciupdater.folderswalker.SingleTransactionSqlScript;

import java.sql.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.err;
import static java.lang.System.out;

public class ScriptsExecutorBean implements ScriptsExecutor {

    private String url;
    private String username;
    private String password;

    @Override
    public int executeScripts(List<Argument> arguments, List<SingleTransactionSqlScript> sqlScripts) {
        DbmsName dbmsName = extractDbmsName(arguments);
        String port = extractPort(arguments, dbmsName);
        String databaseName = extractDatabaseName(arguments);
        String databaseUrlPart = extractDatabaseName(dbmsName);

        connectToJdbcDriver(dbmsName);

        this.url = buildDatabaseUrl(databaseUrlPart, port, databaseName);
        this.username = extractUsername(arguments);
        this.password = extractPassword(arguments);

        List<SingleTransactionSqlScript> actualScripts = actuateScripts(sqlScripts);

        for (SingleTransactionSqlScript sqlScript : actualScripts) {
            executeSingleScript(sqlScript);
        }

        return actualScripts.size();
    }

    private List<SingleTransactionSqlScript> actuateScripts(List<SingleTransactionSqlScript> inputScripts) {
        inputScripts.sort(Comparator.comparing(SingleTransactionSqlScript::getScriptFileName));

        List<SingleTransactionSqlScript> resultList;
        Connection conn = null;
        Statement stmt = null;
        try {
            try {
                conn = DriverManager.getConnection(url, username, password);

                stmt = conn.createStatement();
                createLogIfNecessary(stmt);

                ResultSet resultSet = stmt.executeQuery(
                        "SELECT script_name " +
                                "FROM dbci_update_scripts_log " +
                                "ORDER BY scriptName DESC " +
                                "LIMIT 1"
                );

                if (resultSet.next()) {
                    String lastScript = resultSet.getString("script_name");

                    resultList = inputScripts.stream()
                            .filter(script -> script.getScriptFileName().compareTo(lastScript) > 0)
                            .collect(Collectors.toList());
                } else {
                    resultList = inputScripts;
                }

                conn.commit();
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();
                }
                throw e;
            } finally {
                if (stmt != null) {
                    stmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            }
        } catch (SQLException e) {
            err.println("Failed database connection");
            throw new RuntimeException(e);
        }

        return resultList;
    }

    private void createLogIfNecessary(Statement stmt) throws SQLException {
        String createLogQuery =
                "CREATE TABLE IF NOT EXISTS `dbci_update_scripts_log` (`script_name` VARCHAR(1024));";
        stmt.executeUpdate(createLogQuery);
    }

    private void executeSingleScript(SingleTransactionSqlScript sqlScript) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, username, password);

            try {
                PreparedStatement preparedInsert = conn
                        .prepareStatement("INSERT INTO dbci_update_scripts_log (script_name) VALUES (?);");
                preparedInsert.setString(1, sqlScript.getScriptFileName());
                preparedInsert.executeUpdate();

                stmt = conn.createStatement();

                long updatedResult = stmt.executeLargeUpdate(sqlScript.getSqlScript());
                out.printf("Updated %d entities\n", updatedResult);
                conn.commit();
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();
                }
                throw e;
            } finally {
                if (stmt != null) {
                    stmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            }

        } catch (SQLException e) {
            err.println("Failed database connection");
            throw new RuntimeException(e);
        }
    }

    private void connectToJdbcDriver(DbmsName dbmsName) {
        switch (dbmsName) {
            case PostgreSQL: {
                try {
                    Class.forName("org.postgresql.Driver");
                } catch (ClassNotFoundException e) {
                    err.println("Cannot connect to database. Cannot find PostgreSQL jdbc driver:\n" + e.getMessage());
                    System.exit(1);
                }
            }
            case MySQL: {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    err.println("Cannot connect to database. Cannot find MySQL jdbc driver:\n" + e.getMessage());
                    System.exit(1);
                }
            }
            case MariaDB: {
                try {
                    Class.forName("org.mariadb.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    err.println("Cannot connect to database. Cannot find MariaDB jdbc driver:\n" + e.getMessage());
                    System.exit(1);
                }
            }
            default:
                throw new RuntimeException("ScriptExecutor wasn't connected to jdbc driver");
        }
    }

    private String extractPassword(List<Argument> arguments) {
        for (Argument argument : arguments) {
            if ("-password".equals(argument.getKey())) {
                return argument.getValue();
            }
        }

        return "password";
    }

    private String extractUsername(List<Argument> arguments) {
        for (Argument argument : arguments) {
            if ("-user".equals(argument.getKey())) {
                return argument.getValue();
            }
        }

        return "root";
    }

    private String extractDatabaseName(DbmsName dbmsName) {
        switch (dbmsName) {
            case PostgreSQL: {
                return "postgresql";
            }
            case MySQL: {
                return "mysql";
            }
            case MariaDB: {
                return "mariadb";
            }
            default:
                throw new RuntimeException("ScriptExecutor wasn't connected to jdbc driver");
        }
    }

    private String extractDatabaseName(List<Argument> arguments) {
        for (Argument argument : arguments) {
            if ("-dbname".equals(argument.getKey())) {
                return argument.getValue();
            }
        }

        return "database";
    }

    private String extractPort(List<Argument> arguments, DbmsName dbmsName) {
        for (Argument argument : arguments) {
            if ("-port".equals(argument.getKey())) {
                return argument.getValue();
            }
        }

        switch (dbmsName) {
            case MySQL:
            case MariaDB:
                return "3306";
            case PostgreSQL:
                return "5432";
            default:
                return "3333";
        }
    }

    private String buildDatabaseUrl(String databaseUrlPart, String port, String databaseName) {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:")
                .append(databaseUrlPart)
                .append("://localhost:")
                .append(port)
                .append("/")
                .append(databaseName);

        return sb.toString();
    }

    private DbmsName extractDbmsName(List<Argument> arguments) {
        Argument dbArgument = null;
        for (Argument argument : arguments) {
            if ("-dbms".equals(argument.getKey())) {
                dbArgument = argument;
            }
        }

        if (dbArgument == null) {
            throw new IllegalArgumentException("Cannot find dbms argument");
        }

        switch (dbArgument.getValue().toLowerCase()) {
            case "postgresql":
            case "postgre":
            case "postgres":
            case "pg":
            case "pgsql": {
                return DbmsName.PostgreSQL;
            }
            case "mysql":
            case "my": {
                return DbmsName.MySQL;
            }
            case "maria":
            case "mariadb": {
                return DbmsName.MariaDB;
            }
            default: {
                throw new IllegalArgumentException("Unknown database name");
            }
        }
    }
}
