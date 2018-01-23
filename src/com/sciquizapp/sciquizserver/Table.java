package com.sciquizapp.sciquizserver;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.TableView.TableRow;


public class Table extends JPanel {
	private boolean DEBUG = false;
	DefaultTableModel model;
	JTable table;
	private Vector<Vector<String>> policeColor = new Vector<Vector<String>>();
	int numberUsers = 0;
	int numberQuestions = 0;
	public Table() {
		super(new GridLayout(1,0));

		model = new DefaultTableModel();
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
		model.addColumn("Evaluation");
		policeColor.add(new Vector<>());

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

		table.setGridColor(Color.lightGray);

		if (DEBUG) {
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					printDebugData(table);
				}
			});
		}

		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

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
	public void addUser(String User) {
		for (int i = 0; i < policeColor.size(); i++) {
			policeColor.get(i).add("black");
		}
		DefaultTableModel model2 = (DefaultTableModel) this.table.getModel();
		int i = 0;
		//System.out.println("rowcount:" + model2.getRowCount());
		while (i < model2.getRowCount() && !String.valueOf(model2.getValueAt(i, 0)).equals(User)) {
			//System.out.println("model2.getValueAt(i, 0)  :" + model2.getValueAt(i, 0));
			//System.out.println("User  :" + User);
			i++;
		}
		//System.out.println("i after loop:" + i);
		if (i == model2.getRowCount()) {
			model2.addRow(new Object[]{User});
		}
		model2.setValueAt(0, model2.getRowCount() - 1, 1);
		numberUsers++;
	}
	public void addQuestion(String Question) {
		policeColor.add(new Vector<>());
		for (int i = 0; i < numberUsers; i++) {
			policeColor.get(policeColor.size() - 1).add("black");
		}

		DefaultTableModel model2 = (DefaultTableModel) this.table.getModel();
		model2.addColumn(Question);
	}
	public void removeQuestion(int index) {
		table.removeColumn(table.getColumnModel().getColumn(2 + index));
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
		if (model2.getValueAt(rowNumber, columnNumber)== null) {
			System.out.println("evaluation: " + evaluation);
			if (evaluation == 100) {
				policeColor.get(columnNumber).set(rowNumber,"green");
			} else {
				policeColor.get(columnNumber).set(rowNumber,"red");
			}
			table.getCellRenderer(rowNumber,columnNumber);
			model2.setValueAt(answer, rowNumber, columnNumber);

			// increases score if answer right
//			if (UserAndAnswer.split(";")[3].toString().matches("right")) {
//				int score = (int)model2.getValueAt(rowNumber, 1);
//				model2.setValueAt(score + 1, rowNumber, 1);
//			}
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
}