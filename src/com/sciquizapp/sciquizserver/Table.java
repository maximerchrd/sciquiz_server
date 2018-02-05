package com.sciquizapp.sciquizserver;

import tools.CustomTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;


public class Table extends JPanel {
	private ArrayList<Student> studentArrayList;
	private boolean DEBUG = false;
	private int questionsColumnWidth = 150;
	CustomTableModel model;
	JTable table;
	private Vector<Vector<String>> policeColor = new Vector<Vector<String>>();
	int numberUsers = 0;
	int numberQuestions = 0;
	public Table() {
		super(new GridLayout(1,0));

		studentArrayList = new ArrayList<>();
		model = new CustomTableModel();
		table = new JTable(model) {
			DefaultTableCellRenderer colorred=new DefaultTableCellRenderer();
			{
				colorred.setForeground(Color.RED);
			}
			DefaultTableCellRenderer colorgreen=new DefaultTableCellRenderer();
			{
				colorgreen.setForeground(Color.GREEN);
			}
			DefaultTableCellRenderer colorblack=new DefaultTableCellRenderer();
			{
				colorblack.setForeground(Color.BLACK);
			}
			@Override
			public TableCellRenderer getCellRenderer(int arg0, int arg1) {
				if(policeColor.get(arg1).get(arg0).contains("black")) {
					return colorblack;
				} else if (policeColor.get(arg1).get(arg0).contains("red")) {
					return colorred;
				} else if (policeColor.get(arg1).get(arg0).contains("green")) {
					return colorgreen;
				} else {
					return colorblack;
				}
			}
		};

		// Create a couple of columns
		model.addColumn("Students");
		policeColor.add(new Vector<>());
		model.addColumn("Status");
		policeColor.add(new Vector<>());
		model.addColumn("Evaluation [%]");
		policeColor.add(new Vector<>());

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(110);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);


		table.setGridColor(Color.lightGray);

		if (DEBUG) {
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					printDebugData(table);
				}
			});
		}

		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		//Add the scroll pane to this panel.
		add(scrollPane);
	}

	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i=0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j=0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j));
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	/*public void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("Table");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		Table newContentPane = new Table();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}*/
	public void addUser(Student UserStudent, Boolean connection) {
		for (int i = 0; i < policeColor.size(); i++) {
			policeColor.get(i).add("black");
		}
		DefaultTableModel model2 = (DefaultTableModel) this.table.getModel();
		int i = 0;
		while (i < model2.getRowCount() && !String.valueOf(model2.getValueAt(i, 0)).equals(UserStudent.getName())) {
			i++;
		}
		//System.out.println("i after loop:" + i);
		if (i == model2.getRowCount()) {
			model2.addRow(new Object[]{UserStudent.getName()});
			if (connection) {
				model2.setValueAt("connected",i,1);
			} else {
				model2.setValueAt("not connected",i,1);
			}
		} else {
			model2.setValueAt("connected",i,1);
		}
		model2.setValueAt(0, model2.getRowCount() - 1, 2);
		numberUsers++;
		Student newStudent = new Student();
		newStudent.setName(UserStudent.getName());
		newStudent.setStudentID(UserStudent.getStudentID());
		studentArrayList.add(newStudent);
	}

	public Student getStudentWithRow (int index) {
		Student student = studentArrayList.get(index);
		return student;
	}
	public void addQuestion(String Question) {
		policeColor.add(new Vector<>());
		for (int i = 0; i < numberUsers; i++) {
			policeColor.get(policeColor.size() - 1).add("black");
		}

		DefaultTableModel model2 = (DefaultTableModel) this.table.getModel();
		model2.addColumn(Question);
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(110);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		for (int i = 3; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(questionsColumnWidth);
		}
	}
	public void removeQuestion(int index) {
		model.removeColumn(3 + index);
		policeColor.remove(3 + index);
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		table.getColumnModel().getColumn(1).setPreferredWidth(110);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		for (int i = 3; i < model.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(questionsColumnWidth);
		}
	}
	public void addAnswerForUser(Student student, String answer, String Question, double evaluation) {
		DefaultTableModel model2 = (DefaultTableModel) this.table.getModel();
		int rowNumber = 0;
		while (!model2.getValueAt(rowNumber, 0).toString().contains(student.getName())) {
			rowNumber++;
		}
		int columnNumber = 0;
		while (!model2.getColumnName(columnNumber).contains(Question) && columnNumber < model2.getColumnCount()) {
			columnNumber++;
		}

		//if statement to prevent from answering more than once to the current question
		if (model2.getValueAt(rowNumber, columnNumber) == null) {
			System.out.println("evaluation: " + evaluation);
			if (evaluation == 100) {
				policeColor.get(columnNumber).set(rowNumber,"green");
			} else {
				policeColor.get(columnNumber).set(rowNumber,"red");
			}
			table.getCellRenderer(rowNumber,columnNumber);
			model2.setValueAt(answer, rowNumber, columnNumber);

			// evaluation
			Boolean found = false;
			for (int i = 0; i < studentArrayList.size() && !found; i++) {
				if (studentArrayList.get(i).getName().contentEquals(student.getName())) {
					found = true;
					String eval = model2.getValueAt(rowNumber, 2).toString();
					Double oldEval = Double.valueOf(eval);
					Double newEval = oldEval * studentArrayList.get(i).getNumberOfAnswers() + evaluation;
					newEval = newEval / (studentArrayList.get(i).getNumberOfAnswers() + 1);
					model2.setValueAt(Math.round(newEval),rowNumber,2);
					studentArrayList.get(i).increaseNumberOfAnswers();
				}
			}
		}
	}
	public Boolean IsUserInTable(String UserAndAnswer) {
		DefaultTableModel model2 = (DefaultTableModel) this.table.getModel();
		int rowNumber = 0;
		while (rowNumber < model2.getRowCount() && !(model2.getValueAt(rowNumber, 0).toString().matches(UserAndAnswer.split(";")[0]))) {
			rowNumber++;
		}

		if (rowNumber >= model2.getRowCount()) {
			return false;
		} else { return true; }

	}
	public JTable getTable() {
		return table;
	}
	public void removeStudent(String name) {
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getValueAt(i,0).toString().contentEquals(name)) {
				((DefaultTableModel)table.getModel()).removeRow(i);
			}
		}
	}

	public void userDisconnected(Student student) {
		DefaultTableModel model2 = (DefaultTableModel) this.table.getModel();
		int rowNumber = 0;
		while (!model2.getValueAt(rowNumber, 0).toString().contains(student.getName())) {
			rowNumber++;
		}
		int columnNumber = 1;

		model2.setValueAt("disconnected", rowNumber, columnNumber);
	}
}