package com.sciquizapp.sciquizserver.database_management;

import com.sciquizapp.sciquizserver.Student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by maximerichard on 24.11.17.
 */
public class DbTableClasses {
    static public void createTableClasses(Connection connection, Statement statement) {
        try {
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS classes " +
                    "(ID_CLASS       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_CLASS_GLOBAL      INT     NOT NULL, " +
                    " NAME      TEXT     NOT NULL, " +
                    " LEVEL      TEXT, " +
                    " YEAR      TEXT," +
                    " UNIQUE (NAME) ) ";
            statement.executeUpdate(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    static public void addClass(String name, String level, String year) {
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String sql = 	"INSERT OR IGNORE INTO classes (ID_CLASS_GLOBAL,NAME,LEVEL,YEAR) " +
                    "VALUES ('" +
                    2000000 + "','" +
                    name + "','" +
                    level + "','" +
                    year + "');";
            stmt.executeUpdate(sql);
            sql = "UPDATE classes SET ID_CLASS_GLOBAL = 2000000 + ID_CLASS WHERE ID_CLASS = (SELECT MAX(ID_CLASS) FROM classes)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public List<String> getAllClasses() {
        List<String> classes = new ArrayList<>();
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String query = "SELECT NAME FROM classes;";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                classes.add(rs.getString("NAME"));
            }
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        if (classes.size() == 0) classes.add("no class yet");
        return classes;
    }
    static public Vector<Student> getStudentsInClass(String className) {
        Vector<Student> classes = new Vector<>();
        Connection c = null;
        Statement stmt = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:learning_tracker.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            String query = "SELECT students.ID_STUDENT_GLOBAL,FIRST_NAME FROM students " +
                    " INNER JOIN class_students_relation ON students.ID_STUDENT_GLOBAL = class_students_relation.ID_STUDENT_GLOBAL " +
                    " INNER JOIN classes ON classes.ID_CLASS_GLOBAL = class_students_relation.ID_CLASS_GLOBAL " +
                    " WHERE classes.NAME = '" + className + "';";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Student newStudent = new Student();
                newStudent.setName(rs.getString("FIRST_NAME"));
                newStudent.setStudentID(rs.getInt("ID_STUDENT_GLOBAL"));
                classes.add(newStudent);
            }
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return classes;
    }
}
