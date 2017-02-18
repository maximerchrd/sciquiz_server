package com.sciquizapp.sciquizserver;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.TableView.TableRow;


public class Table extends JPanel {
	private boolean DEBUG = false;
	DefaultTableModel model;
	JTable table;
	public Table() {
		super(new GridLayout(1,0));

		model = new DefaultTableModel(); 
		table = new JTable(model);

		// Create a couple of columns 
		model.addColumn("Utilisateurs"); 
		model.addColumn("Score"); 

		// Append a row 
		model.addRow(new Object[]{"/192.168.43.3"});
		model.addRow(new Object[]{"/192.168.43.5"});

		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

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
	}
	public void addQuestion(String Question) {
		DefaultTableModel model2 = (DefaultTableModel) this.table.getModel();
		model2.addColumn(Question);
	}
	public void addAnswerForUser(String UserAndAnswer) {
		DefaultTableModel model2 = (DefaultTableModel) this.table.getModel();
		int rowNumber = 0;
		while (!model2.getValueAt(rowNumber, 0).toString().matches(UserAndAnswer.split(";")[0])) {
			rowNumber++;
		}
		
		//if statement to prevent from answering more than once to the current question
		if (model2.getValueAt(rowNumber, model2.getColumnCount() - 1) == null) {
			model2.setValueAt(UserAndAnswer.split(";")[2], rowNumber, model2.getColumnCount() - 1);
			
			// increases score if answer right
			if (UserAndAnswer.split(";")[3].toString().matches("right")) {
				int score = (int)model2.getValueAt(rowNumber, 1);
				model2.setValueAt(score + 1, rowNumber, 1);
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
}