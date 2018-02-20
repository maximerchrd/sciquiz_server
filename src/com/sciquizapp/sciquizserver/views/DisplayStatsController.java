package com.sciquizapp.sciquizserver.views;


import com.sciquizapp.sciquizserver.database_management.DbTableIndividualQuestionForStudentResult;
import com.sciquizapp.sciquizserver.database_management.DbTableStudents;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Created by maximerichard on 21.12.17.
 */
public class DisplayStatsController implements Initializable {
    private Vector<String> studentsVector;

    private JFXPanel fxPanel;

    @FXML private ComboBox chart_type;
    @FXML private ComboBox time_step;
    @FXML private TreeView students_tree;
    @FXML private BarChart <String, Number> bar_chart;

    String usa = "";
    DisplayStatsController displayStats_singleton;


    public void initialize(URL location, ResourceBundle resources) {
        //combobox with types of values to consider for the chart
        chart_type.getItems().addAll("Evaluation vs objective", "Evaluation vs subject");
        chart_type.getSelectionModel().select("Evaluation vs objective");

        //tree with students
        studentsVector = DbTableStudents.getStudentNames();
        TreeItem<String> rootItem = new TreeItem<String> ("Inbox");
        rootItem.setExpanded(true);
        for (int i = 0; i < studentsVector.size(); i++) {
            TreeItem<String> item = new TreeItem<String> (studentsVector.get(i));
            rootItem.getChildren().add(item);
        }
        students_tree.setRoot(rootItem);

        //combobox with time span
        time_step.getItems().addAll("All");
        time_step.getSelectionModel().select("All");
    }

    public void displayChartButtonClicked() {
        /*if (fxPanel == null) {
            fxPanel = new JFXPanel();
            displayStats_singleton.add(fxPanel);
            initAndShowGUI(chart_entries_box.getSelectedItem().toString(), students_entries_box.getSelectedItem().toString());
        } else {
            displayStats_singleton.remove(fxPanel);
            fxPanel = null;
            fxPanel = new JFXPanel();
            displayStats_singleton.add(fxPanel);
            initAndShowGUI(chart_entries_box.getSelectedItem().toString(),students_entries_box.getSelectedItem().toString());
        }*/
        createScene(chart_type.getSelectionModel().getSelectedItem().toString(), students_tree.getSelectionModel().getSelectedItems().toArray());
    }

    private void initAndShowGUI(String valuesType, Object[] students) {
        // This method is invoked on the EDT thread
        //fxPanel = new JFXPanel();
        //this.add(fxPanel);
        //DisplayStatsController displayStats = new DisplayStatsController();
        //Scene scene = createScene(valuesType, student);
        //fxPanel.setScene(scene);

        /*Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });*/
    }

    private Scene createScene(String valuesType, Object[] students) {
        usa = String.valueOf(System.currentTimeMillis());
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0,100,10);
        XYChart.Series series1 = new XYChart.Series();
        if (valuesType.contentEquals("Evaluation vs subject")) {
            xAxis.setLabel("Subjects");
            yAxis.setLabel("Evaluation [%]");
            Vector<Vector<String>> studentResultsPerSubject = DbTableStudents.getStudentResultsPerSubject(students[0].toString());
            Vector<String> subjects = studentResultsPerSubject.get(0);
            Vector<String> results = studentResultsPerSubject.get(1);
            series1.setName(students[0].toString());
            for (int i = 0; i < subjects.size(); i++) {
                series1.getData().add(new XYChart.Data(subjects.get(i), Double.parseDouble(results.get(i))));
            }
            System.out.println("displaying eval vs subjects");
        } else if (valuesType.contentEquals("Evaluation vs objective")) {
            xAxis.setLabel("Learning objectives");
            yAxis.setLabel("Evaluation [%]");
            Vector<Vector<String>> studentResultsPerObjective = DbTableStudents.getStudentResultsPerObjective(students[0].toString());
            Vector<String> objectives = studentResultsPerObjective.get(0);
            Vector<String> results = studentResultsPerObjective.get(1);
            series1.setName(students[0].toString());
            for (int i = 0; i < objectives.size(); i++) {
                if (objectives.get(i).length() > 16) {
                    objectives.set(i, objectives.get(i).substring(0,15) + "\n" + objectives.get(i).substring(15,objectives.get(i).length()));
                    if (objectives.get(i).length() > 31) {
                        objectives.set(i, objectives.get(i).substring(0, 30) + "\n" + objectives.get(i).substring(30, objectives.get(i).length()));
                        if (objectives.get(i).length() > 46) {
                            objectives.set(i, objectives.get(i).substring(0, 45) + "...");
                        }
                    }
                }
                series1.getData().add(new XYChart.Data(objectives.get(i), Double.parseDouble(results.get(i))));
            }
            System.out.println("displaying eval vs objectives");
        }






        int screenWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        int screenHeight = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight());

        Scene scene = new Scene(bar_chart, screenWidth * 0.4, screenHeight * 0.37);
        bar_chart.getData().addAll(series1);

        return (scene);
    }
}
