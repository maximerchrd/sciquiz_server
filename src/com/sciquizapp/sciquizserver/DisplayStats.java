package com.sciquizapp.sciquizserver;


import com.sciquizapp.sciquizserver.database_management.DbTableStudents;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Created by maximerichard on 21.12.17.
 */
public class DisplayStats extends JPanel {
    private JComboBox chart_entries_box;
    private JComboBox students_entries_box;
    private String[] xValues_strings;
    private Vector<String> xValues_vector;

    private JFXPanel fxPanel;

    String usa = "";
    DisplayStats displayStats_singleton;

    public DisplayStats() {
        displayStats_singleton = this;
        String[] chart_entries_options = {"Evaluation vs subject for student", "Evaluation vs objective for student"};
        chart_entries_box = new JComboBox(chart_entries_options);
        this.add(chart_entries_box);

        xValues_vector = DbTableStudents.getStudentNames();
        String[] chart_students = new String[xValues_vector.size()];
        for (int i = 0; i < xValues_vector.size(); i++) {
            chart_students[i] = xValues_vector.get(i);
        }
        students_entries_box = new JComboBox(chart_students);
        this.add(students_entries_box);

        JButton display_chart = new JButton("display chart");

        display_chart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {


                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (fxPanel == null) {
                            fxPanel = new JFXPanel();
                            displayStats_singleton.add(fxPanel);
                            initAndShowGUI(chart_entries_box.getSelectedItem().toString(), students_entries_box.getSelectedItem().toString());
                        } else {
                            displayStats_singleton.remove(fxPanel);
                            fxPanel = null;
                            fxPanel = new JFXPanel();
                            displayStats_singleton.add(fxPanel);
                            initAndShowGUI(chart_entries_box.getSelectedItem().toString(),students_entries_box.getSelectedItem().toString());
                        }
                    }
                });
            }

        });
        this.add(display_chart);
    }

    private void initAndShowGUI(String combobox1_selected, String combobox2_selected) {
        // This method is invoked on the EDT thread
        //fxPanel = new JFXPanel();
        //this.add(fxPanel);
        DisplayStats displayStats = new DisplayStats();
        Scene scene = displayStats.createScene(combobox1_selected, combobox2_selected);
        fxPanel.setScene(scene);

        /*Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });*/
    }

    private void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        //DisplayStats displayStats = new DisplayStats();
        //Scene scene = displayStats.createScene();
        //fxPanel.setScene(scene);
    }

    private Scene createScene(String combobox1_selected, String combobox2_selected) {
        usa = String.valueOf(System.currentTimeMillis());
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
        XYChart.Series series1 = new XYChart.Series();
        if (combobox1_selected.contentEquals("Evaluation vs subject for student")) {
            xAxis.setLabel("Subjects");
            yAxis.setLabel("Evaluation [%]");
            Vector<Vector<String>> studentResultsPerSubject = DbTableStudents.getStudentResultsPerSubject(combobox2_selected);
            Vector<String> subjects = studentResultsPerSubject.get(0);
            Vector<String> results = studentResultsPerSubject.get(1);
            series1.setName(combobox2_selected);
            for (int i = 0; i < subjects.size(); i++) {
                series1.getData().add(new XYChart.Data(subjects.get(i), Double.parseDouble(results.get(i))));
            }
            System.out.println("displaying eval vs subjects");
        } else if (combobox1_selected.contentEquals("Evaluation vs objective for student")) {
            xAxis.setLabel("Learning objectives");
            yAxis.setLabel("Evaluation [%]");
            Vector<Vector<String>> studentResultsPerObjective = DbTableStudents.getStudentResultsPerObjective(combobox2_selected);
            Vector<String> objectives = studentResultsPerObjective.get(0);
            Vector<String> results = studentResultsPerObjective.get(1);
            series1.setName(combobox2_selected);
            for (int i = 0; i < objectives.size(); i++) {
                series1.getData().add(new XYChart.Data(objectives.get(i), Double.parseDouble(results.get(i))));
            }
            System.out.println("displaying eval vs objectives");
        }






        /*Button btn = new Button();
        btn.setLayoutX(100);
        btn.setLayoutY(100);
        btn.setText("Hello, World!");

        btn.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent event) {
                bc.setTitle("Hello, World!");
            }
        });
        Group root = new Group();

        root.getChildren().add(btn);
        root.getChildren().add(bc);
        bc.getData().addAll(series1, series2, series3);*/
        int screenWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        int screenHeight = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight());

        Scene scene = new Scene(bc, screenWidth * 0.4, screenHeight * 0.37);
        bc.getData().addAll(series1);

        return (scene);
    }
}
