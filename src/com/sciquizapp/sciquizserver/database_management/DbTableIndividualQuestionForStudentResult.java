package com.sciquizapp.sciquizserver.database_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableIndividualQuestionForStudentResult {
    static public void createTableDirectEvaluationOfObjective(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS individual_question_for_student_result " +
                    "(ID_DIRECT_EVAL        INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_GLOBAL             INT    NOT NULL, " +
                    " ID_STUDENT_GLOBAL     INT    NOT NULL, " +
                    " DATE                  TEXT    NOT NULL, " +
                    " ANSWERS               TEXT    NOT NULL, " +
                    " TIME_FOR_SOLVING      INT    NOT NULL, " +
                    " QUESTION_WEIGHT       REAL    NOT NULL, " +
                    " EVAL_TYPE             TEXT    NOT NULL, " +
                    " QUANTITATIVE_EVAL     TEXT    NOT NULL, " +
                    " QUALITATIVE_EVAL       TEXT    NOT NULL, " +
                    " TEST_BELONGING        TEXT    NOT NULL, " +
                    " WEIGHTS_OF_ANSWERS    TEXT    NOT NULL) ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public void addIndividualQuestionForStudentResult(int id_global, String student_name, String answers) {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = 	"INSERT INTO individual_question_for_student_result (ID_GLOBAL,ID_STUDENT_GLOBAL,DATE,ANSWERS,TIME_FOR_SOLVING,QUESTION_WEIGHT,EVAL_TYPE," +
                    "QUANTITATIVE_EVAL,QUALITATIVE_EVAL,TEST_BELONGING,WEIGHTS_OF_ANSWERS) " +
                    "VALUES ('" + id_global + "','-1',date('now'),'" + answers + "','none','none','none','none','none','none','none');";
            stmt.executeUpdate(sql);
            sql = "UPDATE individual_question_for_student_result SET ID_STUDENT_GLOBAL = (SELECT ID_STUDENT_GLOBAL FROM students WHERE FIRST_NAME = "+ "'" + student_name + "') WHERE ID_DIRECT_EVAL = (SELECT MAX(ID_DIRECT_EVAL) FROM individual_question_for_student_result);";
            stmt.executeUpdate(sql);


            // correcting the answers and evaluate the multiple choice question in %
            String[] student_answers_array = answers.split("\\|\\|\\|");
            int number_answers = 0;
            String query = "SELECT OPTION0,OPTION1,OPTION2,OPTION3,OPTION4,OPTION5,OPTION6,OPTION7,OPTION8,OPTION9,NB_CORRECT_ANS FROM multiple_choice_questions WHERE ID_GLOBAL = " + id_global + ";";
            ResultSet rs = stmt.executeQuery(query);
            Vector<String> all_options_vector = new Vector<>();
            for (int i = 1; i < 11; i++) {
                if (rs.getString(i).length() > 0) {
                    all_options_vector.add(rs.getString(i));
                    number_answers++;
                }
            }
            String[] right_answers_array = new String[rs.getInt(11)];
            for (int i = 0; i < rs.getInt(11); i++) {
                right_answers_array[i] = rs.getString(i+1);
            }
            int number_rignt_checked_answers_from_student = 0;
            for (int i = 0; i < right_answers_array.length; i++) {
                if (Arrays.asList(student_answers_array).contains(right_answers_array[i])) {
                    number_rignt_checked_answers_from_student++;
                }
            }
            int number_right_unchecked_answers_from_student = 0;
            for (int i = 0; i < number_answers; i++) {
                if (!Arrays.asList(right_answers_array).contains(all_options_vector.get(i)) && !Arrays.asList(student_answers_array).contains(all_options_vector.get(i))) {
                    number_right_unchecked_answers_from_student++;
                }
            }
            double quantitative_evaluation = 100 * (number_rignt_checked_answers_from_student + number_right_unchecked_answers_from_student) / number_answers;
            System.out.println("student result: " + quantitative_evaluation);
            sql = "UPDATE individual_question_for_student_result SET QUANTITATIVE_EVAL = '" + quantitative_evaluation + "' WHERE ID_DIRECT_EVAL = (SELECT MAX(ID_DIRECT_EVAL) FROM individual_question_for_student_result);";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}
