package com.sciquizapp.sciquizserver;

import com.sciquizapp.sciquizserver.questions.QuestionMultipleChoice;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by maximerichard on 28/03/17.
 */
public class Classroom {
    private ArrayList<Student> students_array = null;
    private ArrayList<String> students_addresses = null;
    private ArrayList<QuestionMultipleChoice> current_set_quest_mult_choice = null;

    public Classroom() {
        students_array = new ArrayList<Student>();
        students_addresses = new ArrayList<String>();
        current_set_quest_mult_choice = new ArrayList<QuestionMultipleChoice>();
    }
    public void addQuestMultChoice(QuestionMultipleChoice questionMultipleChoice) {
        current_set_quest_mult_choice.add(questionMultipleChoice);
    }
    public ArrayList<QuestionMultipleChoice> getCurrent_set_quest_mult_choice() {
        return current_set_quest_mult_choice;
    }
    public void addStudent(Student student) {
        students_array.add(student);
    }
    public void addStudentIfNotInClass(Student student) {
        int students_addresses_size = readStudents_addresses();
        System.out.println("students addresses size: "+ students_addresses_size);
        if (students_addresses_size > 0) {
            if (!students_addresses.contains(student.getAddress())) {
                System.out.println("students addresses content: " + students_addresses.get(0) + " student address: " + student.getAddress());
                students_array.add(student);
            } else {
                int index = students_addresses.indexOf(student.getAddress());
                students_array.remove(index);
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

    public void pruneLastStudentIfAlreadyInClass() {
        readStudents_addresses();
        String last_address = students_addresses.get(students_addresses.size() - 1);
        students_addresses.remove(students_addresses.size() - 1);
        if (students_addresses.contains(last_address)) {
            students_array.remove(students_array.size() - 1);
        }
    }

    public Boolean studentAlreadyInClass (Student student) {
        int students_addresses_size = readStudents_addresses();
        if (students_addresses_size > 0) {
            if (!students_addresses.contains(student.getAddress())) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void updateStudent (Student student) {
        int i = indexOfStudentWithAddress(student.getAddress());
        if (i >= 0) {
            students_array.get(i).setConnection(student.getConnection());
            try {
                students_array.get(i).openStreams();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("A problem occured: student not in class when trying to update infos");
        }
    }

    public int indexOfStudentWithAddress (String address) {
        int index = -1;
        for (int i = 0;i < students_array.size(); i++) {
            if (students_array.get(i).getAddress().equals(address)) {
                index = i;
            }
        }
        return index;
    }

    private int readStudents_addresses() {
        students_addresses.clear();
        for (int i = 0; i < students_array.size(); i++) {
            students_addresses.add(students_array.get(i).getAddress());
        }
        return students_addresses.size();
    }
}
