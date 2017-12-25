package com.sciquizapp.sciquizserver.database_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableStudents {
    static public void createTableSubject(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS students " +
                    "(ID_STUDENT       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_STUDENT_GLOBAL      INT     NOT NULL, " +
                    " MAC_ADDRESS      TEXT     NOT NULL, " +
                    " FIRST_NAME      TEXT     NOT NULL, " +
                    " SURNAME      TEXT     NOT NULL, " +
                    " DATE_BIRTH      TEXT     NOT NULL, " +
                    " QUANTITATIVE_EVAL      TEXT     NOT NULL, " +
                    " QUALITATIVE_EVAL           TEXT    NOT NULL, " +
                    " UNIQUE (MAC_ADDRESS))";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public void addStudent(String address, String name) {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = 	"INSERT OR IGNORE INTO students (ID_STUDENT_GLOBAL,MAC_ADDRESS,FIRST_NAME,SURNAME,DATE_BIRTH,QUANTITATIVE_EVAL,QUALITATIVE_EVAL) " +
                    "VALUES ('" +
                    2000000 + "','" +
                    address + "','" +
                    name + "','none','none','none','none');";
            stmt.executeUpdate(sql);
            sql = "UPDATE students SET ID_STUDENT_GLOBAL = 2000000 + ID_STUDENT WHERE ID_STUDENT = (SELECT MAX(ID_STUDENT) FROM students)";
            stmt.executeUpdate(sql);
            sql = "UPDATE students SET FIRST_NAME = '" + name + "' WHERE MAC_ADDRESS = '" + address + "';";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public Vector<String> getStudentNames() {
        Vector<String> student_names = new Vector<>();
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String query = "SELECT FIRST_NAME FROM students;";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                student_names.add(rs.getString("FIRST_NAME"));
            }
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return student_names;
    }
    static public Vector<Vector<String>> getStudentResultsPerSubject(String student_name) {
        Vector<String> student_ids = new Vector<>();
        Vector<String> subjects = new Vector<>();
        Vector<String> results = new Vector<>();
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            String query = "SELECT ID_STUDENT_GLOBAL FROM students WHERE FIRST_NAME='" + student_name +"';";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                student_ids.add(rs.getString("ID_STUDENT_GLOBAL"));
            }

            int id_student = 0;
            if (!student_ids.isEmpty()) {
                id_student = Integer.parseInt(student_ids.get(0));
            }
            query = "SELECT ID_GLOBAL,QUANTITATIVE_EVAL FROM individual_question_for_student_result WHERE ID_STUDENT_GLOBAL='" + id_student +"';";
            rs = stmt.executeQuery(query);
            Vector<String> id_questions = new Vector<>();
            Vector<String> evaluations_for_each_question = new Vector<>();
            while (rs.next()) {
                id_questions.add(rs.getString("ID_GLOBAL"));
                evaluations_for_each_question.add(rs.getString("QUANTITATIVE_EVAL"));
            }
            Vector<String> subject_for_question = new Vector<>();
            for (int i = 0; i < id_questions.size(); i++) {
                query = "SELECT SUBJECT FROM subjects INNER JOIN question_subject_relation ON subjects.ID_SUBJECT_GLOBAL = question_subject_relation.ID_SUBJECT_GLOBAL " +
                        "WHERE question_subject_relation.ID_GLOBAL = '" + id_questions.get(i) + "';";
                rs = stmt.executeQuery(query);
                while (rs.next()) {
                    subject_for_question.add(rs.getString("SUBJECT"));
                    evaluations_for_each_question.insertElementAt(evaluations_for_each_question.get(subject_for_question.size() - 1), subject_for_question.size());
                }
                evaluations_for_each_question.remove(evaluations_for_each_question.get(subject_for_question.size()));
            }
            for (int i = 0; i < subject_for_question.size(); i++) {
                if (!subjects.contains(subject_for_question.get(i))) {
                    subjects.add(subject_for_question.get(i));
                    results.add(evaluations_for_each_question.get(i));
                } else {
                    int old_result_index = subjects.indexOf(subject_for_question.get(i));
                    double old_result = Double.parseDouble(results.get(old_result_index));
                    old_result += Double.parseDouble(evaluations_for_each_question.get(i));
                    results.set(old_result_index,String.valueOf(old_result));
                }
            }
            for (int i = 0; i < results.size(); i++) {
                double result_for_averaging = Double.parseDouble(results.get(i));
                int number_occurences = Collections.frequency(subject_for_question,subjects.get(i));
                results.set(i,String.valueOf(result_for_averaging/number_occurences));
            }
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        Vector<Vector<String>> vectors = new Vector<Vector<String>>();
        vectors.add(subjects);
        vectors.add(results);
        return vectors;
    }
}