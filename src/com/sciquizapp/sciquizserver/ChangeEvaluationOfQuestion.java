package com.sciquizapp.sciquizserver;


import com.sciquizapp.sciquizserver.database_management.DbTableIndividualQuestionForStudentResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Created by maximerichard on 06.02.18.
 */
public class ChangeEvaluationOfQuestion extends JPanel implements ActionListener {
    final JFrame ChangeEvaluationFrame;
    JPanel changeEvaluationPanel;
    GridBagLayout gridBagLayout;
    JLabel OldEvalLabel;
    JTextArea OldEvalTextArea;
    JLabel NewEvalLabel;
    JTextArea NewEvalTextArea;
    JButton SaveNewEval;


    public ChangeEvaluationOfQuestion(Integer globalID, Integer globalStudentID, NetworkCommunication networkCommunication) {
        ChangeEvaluationFrame = new JFrame("Change Evaluation");
        changeEvaluationPanel = new JPanel();
        gridBagLayout = new GridBagLayout();
        changeEvaluationPanel.setLayout(gridBagLayout);

        ChangeEvaluationFrame.add(changeEvaluationPanel);

        String evalAndIdentifier = DbTableIndividualQuestionForStudentResult.getEvalForQuestionAndStudentIDs(globalID, globalStudentID);
        if (evalAndIdentifier != null && evalAndIdentifier.split("///").length > 1) {
            Double oldEval = Double.valueOf(evalAndIdentifier.split("///")[0]);
            String identifier = evalAndIdentifier.split("///")[1];
            OldEvalLabel = new JLabel("Current evaluation:");
            GridBagConstraints OldEvalLabel_constraints = new GridBagConstraints();
            OldEvalLabel_constraints.gridx = 0;
            OldEvalLabel_constraints.gridy = 0;
            changeEvaluationPanel.add(OldEvalLabel, OldEvalLabel_constraints);

            OldEvalTextArea = new JTextArea(String.valueOf(oldEval));
            OldEvalTextArea.setEditable(false);
            GridBagConstraints OldEvalTextArea_constraints = new GridBagConstraints();
            OldEvalTextArea_constraints.gridx = 1;
            OldEvalTextArea_constraints.gridy = 0;
            changeEvaluationPanel.add(OldEvalTextArea, OldEvalTextArea_constraints);

            NewEvalLabel = new JLabel("Modified evaluation:");
            GridBagConstraints NewEvalLabel_constraints = new GridBagConstraints();
            NewEvalLabel_constraints.gridx = 0;
            NewEvalLabel_constraints.gridy = 1;
            changeEvaluationPanel.add(NewEvalLabel, NewEvalLabel_constraints);

            NewEvalTextArea = new JTextArea("");
            NewEvalTextArea.setPreferredSize(new Dimension(40, 15));
            NewEvalTextArea.setEditable(true);
            GridBagConstraints NewEvalTextArea_constraints = new GridBagConstraints();
            NewEvalTextArea_constraints.gridx = 1;
            NewEvalTextArea_constraints.gridy = 1;
            changeEvaluationPanel.add(NewEvalTextArea, NewEvalTextArea_constraints);

            SaveNewEval = new JButton("Save New Evaluation");
            SaveNewEval.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DbTableIndividualQuestionForStudentResult.setEvalForQuestionAndStudentIDs(Double.valueOf(NewEvalTextArea.getText()), identifier);
                    networkCommunication.UpdateEvaluation(Double.valueOf(NewEvalTextArea.getText()), globalID, globalStudentID);
                    ChangeEvaluationFrame.dispatchEvent(new WindowEvent(ChangeEvaluationFrame, WindowEvent.WINDOW_CLOSING));
                }
            });
            GridBagConstraints SaveNewEval_constraints = new GridBagConstraints();
            SaveNewEval_constraints.gridx = 0;
            SaveNewEval_constraints.gridy = 2;
            changeEvaluationPanel.add(SaveNewEval, SaveNewEval_constraints);

            ChangeEvaluationFrame.setBounds(0, 0, 250, 200);
            ChangeEvaluationFrame.setVisible(true);
        }
     }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
