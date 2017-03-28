package com.sciquizapp.sciquizserver;

import com.sciquizapp.sciquizserver.Student;

import java.util.ArrayList;

/**
 * Created by maximerichard on 28/03/17.
 */
public class Class {
    private ArrayList<Student> students_array = null;
    private ArrayList<String> students_addresses = null;

    public Class () {
        students_array = new ArrayList<Student>();
    }

    public void addStudent(Student student) {
        students_array.add(student);
    }
    public void addStudentIfNotInClass(Student student) {
        if (readStudents_addresses() > 0) {
            if (!students_addresses.contains(student.getAddress())) {
                students_array.add(student);
            }
        } else {
            students_array.add(student);
        }
    }

    public int getClassSize() {
        return students_array.size();
    }

    public ArrayList<Student> getStudents_array() {
        return students_array;
    }

    private int readStudents_addresses() {
        students_addresses = new ArrayList<String>();
        for (int i = 0; i < students_array.size(); i++) {
            students_addresses.add(students_array.get(i).getAddress());
        }
        return students_addresses.size();
    }
}
