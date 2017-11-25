package com.sciquizapp.sciquizserver.database_management;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableQuestionMultipleChoice {
    static public void createTableQuestionMultipleChoice(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS multiple_choice_questions " +
                    "(ID_QUESTION       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " SUBJECT           TEXT    NOT NULL, " +
                    " LEVEL      INT     NOT NULL, " +
                    " QUESTION           TEXT    NOT NULL, " +
                    " OPTION0           TEXT    NOT NULL, " +
                    " OPTION1           TEXT    NOT NULL, " +
                    " OPTION2           TEXT    NOT NULL, " +
                    " OPTION3           TEXT    NOT NULL, " +
                    " OPTION4           TEXT    NOT NULL, " +
                    " OPTION5           TEXT    NOT NULL, " +
                    " OPTION6           TEXT    NOT NULL, " +
                    " OPTION7           TEXT    NOT NULL, " +
                    " OPTION8           TEXT    NOT NULL, " +
                    " OPTION9           TEXT    NOT NULL, " +
                    " TRIAL0           TEXT    NOT NULL, " +
                    " TRIAL1           TEXT    NOT NULL, " +
                    " TRIAL2           TEXT    NOT NULL, " +
                    " TRIAL3           TEXT    NOT NULL, " +
                    " TRIAL4           TEXT    NOT NULL, " +
                    " TRIAL5           TEXT    NOT NULL, " +
                    " TRIAL6           TEXT    NOT NULL, " +
                    " TRIAL7           TEXT    NOT NULL, " +
                    " TRIAL8           TEXT    NOT NULL, " +
                    " TRIAL9           TEXT    NOT NULL, " +
                    " IMAGE_PATH           TEXT    NOT NULL, " +
                    " ID_GLOBAL      INT     NOT NULL) ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}
