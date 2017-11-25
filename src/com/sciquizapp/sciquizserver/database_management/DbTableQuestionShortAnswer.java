package com.sciquizapp.sciquizserver.database_management;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableQuestionShortAnswer {
    static public void createTableQuestionShortAnswer(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "DROP TABLE IF EXISTS 'short_answers_questions'; CREATE TABLE IF NOT EXISTS short_answers_questions " +
                    "(ID_QUESTION       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_GLOBAL      INT     NOT NULL, " +
                    " SUBJECT           TEXT    NOT NULL, " +
                    " LEVEL      INT     NOT NULL, " +
                    " QUESTION           TEXT    NOT NULL, " +
                    " ANSWER           TEXT    NOT NULL, " +
                    " IMAGE_PATH           TEXT    NOT NULL) ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}
