package com.sciquizapp.sciquizserver.database_management;

import com.sciquizapp.sciquizserver.SingleResultForTable;
import com.sciquizapp.sciquizserver.questions.QuestionShortAnswer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
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
    static public double addIndividualQuestionForStudentResult(int id_global, String student_name, String answers, String answerType) {
        double quantitative_evaluation = -1;
        answers = answers.replace("'","''");
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


            if (answerType.contains("ANSW0")) {
                // correcting the answers and evaluate the multiple choice question in %
                String[] student_answers_array = answers.split("\\|\\|\\|");
                int number_answers = 0;
                String query = "SELECT OPTION0,OPTION1,OPTION2,OPTION3,OPTION4,OPTION5,OPTION6,OPTION7,OPTION8,OPTION9,NB_CORRECT_ANS FROM multiple_choice_questions WHERE ID_GLOBAL = " + id_global + ";";
                ResultSet rs = stmt.executeQuery(query);
                Vector<String> all_options_vector = new Vector<>();
                int count = 0;

                if (rs.next()) {
                    for (int i = 1; i < 11; i++) {
                        if (!rs.getString(i).equals(" ")) {
                            all_options_vector.add(rs.getString(i));
                            number_answers++;
                        }
                    }
                    String[] right_answers_array = new String[rs.getInt(11)];
                    for (int i = 0; i < rs.getInt(11); i++) {
                        right_answers_array[i] = rs.getString(i + 1);
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
                    quantitative_evaluation = 100 * (number_rignt_checked_answers_from_student + number_right_unchecked_answers_from_student) / number_answers;

                    if (quantitative_evaluation < 100) {
                        quantitative_evaluation = 0;
                    }
                } else {
                    System.out.println("problem writing result: probably no corresponding question ID");
                }
            } else if (answerType.contains("ANSW1")) {
                QuestionShortAnswer questionShortAnswer = DbTableQuestionShortAnswer.getShortAnswerQuestionWithId(id_global);
                quantitative_evaluation = 0;
                for (int i = 0; i < questionShortAnswer.getANSWER().size() && quantitative_evaluation == 0; i++) {
                    if (answers.contentEquals(questionShortAnswer.getANSWER().get(i))) {
                        quantitative_evaluation = 100;
                    }
                }
            }

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
        return quantitative_evaluation;
    }

    static public String exportResults(String file_name) {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        PrintWriter resultsFile = null;
        try {
            resultsFile = new PrintWriter(file_name);
            resultsFile.write("");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String query = "SELECT students.FIRST_NAME,DATE,individual_question_for_student_result.QUANTITATIVE_EVAL,multiple_choice_questions.QUESTION, " +
                "individual_question_for_student_result.ANSWERS " +
                "FROM 'individual_question_for_student_result' " +
                "INNER JOIN students ON students.ID_STUDENT_GLOBAL=individual_question_for_student_result.ID_STUDENT_GLOBAL " +
                "INNER JOIN multiple_choice_questions ON multiple_choice_questions.ID_GLOBAL=individual_question_for_student_result.ID_GLOBAL;";
        try {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                for (int i = 1; i < 6; i++) {
                    resultsFile.print(rs.getString(i) + ";");
                }
                resultsFile.print("\n");
            }
            stmt.close();
            c.commit();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resultsFile.close();
        return "done";
    }

    static public String getEvalForQuestionAndStudentIDs (Integer globalID, Integer globalStudentID) {
        String evaluation = "";
        String identifier = "";
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query = "SELECT ID_DIRECT_EVAL,QUANTITATIVE_EVAL FROM individual_question_for_student_result " +
                "WHERE (ID_STUDENT_GLOBAL='" + globalStudentID + "' AND ID_GLOBAL='" + globalID + "');";
        try {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                evaluation = rs.getString("QUANTITATIVE_EVAL");
                identifier = rs.getString("ID_DIRECT_EVAL");
            }
            stmt.close();
            c.commit();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evaluation + "///" + identifier;
    }

    static public ArrayList<SingleResultForTable> getAllSingleResults () {
        ArrayList<SingleResultForTable> resultsArray = new ArrayList<>();
        String evaluation = "";
        String idGlobal = "";
        String studentID = "";
        String answers = "";
        String date = "";
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String query = "SELECT ID_GLOBAL,ID_STUDENT_GLOBAL,DATE,ANSWERS,QUANTITATIVE_EVAL FROM individual_question_for_student_result;";
        try {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                evaluation = rs.getString("QUANTITATIVE_EVAL");
                idGlobal = rs.getString("ID_GLOBAL");
                studentID = rs.getString("ID_STUDENT_GLOBAL");
                answers = rs.getString("ANSWERS");
                date = rs.getString("DATE");
                String name = "";
                String question = "";
                String query2 = "SELECT ID_GLOBAL,ID_STUDENT_GLOBAL,DATE,ANSWERS,QUANTITATIVE_EVAL FROM individual_question_for_student_result;";

                SingleResultForTable tempSingleResult = new SingleResultForTable();

            }
            stmt.close();
            c.commit();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  resultsArray;
    }

    static public void setEvalForQuestionAndStudentIDs (Double eval, String identifier) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "UPDATE individual_question_for_student_result SET QUANTITATIVE_EVAL = '" + eval + "' " +
                "WHERE ID_DIRECT_EVAL = '" + identifier + "';";

        try {
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
