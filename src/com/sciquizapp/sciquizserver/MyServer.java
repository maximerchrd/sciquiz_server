/*
 * TO DO LIST
 * - afficher une liste des appareils connectés
 * - afficher un avertissement quand un appareil quitte l'app
 * - pouvoir répondre à plusieurs questions de manière non sychronisée (entre les utilisateurs)
 * - écrire un fichier pour excel avec utilisateurs vs questions (et réponses dans les cases)
 * ->1 dans l'interface, faire un tableau pour la session ac utilisateurs vs questions
 * - interface pour choisir les questions: liste
 * - pouvoir choisir à l'avance plusieurs question et faire ensuite "question suivante"
 * - donner le résultat sur chaque appareil utilisateur à la fin de la session
 * - définir une "séquence de questions" (= session)
 */

package com.sciquizapp.sciquizserver;

import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.*;

import com.sciquizapp.sciquizserver.database_management.DBManager;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

public class MyServer {



    public static void main(String[] args) throws Exception {

        //does db stuffs
        DBManager dao = new DBManager();
        dao.createDBIfNotExists();
        dao.createTablesIfNotExists();
        dao.getAllQuestions();

        //declares jpanels for different parts of the window
        JPanel panel_for_questlist = new JPanel(); // useless now, do something later?
        JPanel panel_for_counter = new JPanel();
        JPanel panel_for_displayquest = new JPanel();
        JPanel panel_for_stats = new JPanel();



        //Setup the table

        //Create and set up the window.
        JFrame frame = new JFrame("Learning Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        Table TableUserVsQuest = new Table();
        TableUserVsQuest.setOpaque(true); //content panes must be opaque
        //frame.setContentPane(TableUserVsQuest);


        //start bluetooth network in new thread
        NetworkCommunication CommunicationWithClients = new NetworkCommunication(TableUserVsQuest);
//        Thread networkThread = new Thread() {
//            public void run() {
        try {
            CommunicationWithClients.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
//            }
//        };
//        networkThread.start();



        //Turn off metal's use of bold fonts
        ChooseDropActionDemo newChooseDropAction = new ChooseDropActionDemo(frame, panel_for_questlist, panel_for_displayquest, CommunicationWithClients);
        UIManager.put("swing.boldMetal", Boolean.FALSE);




        //implement the division of the window with borderlayout
        JPanel parent = new JPanel();
        parent.setLayout(new GridLayout(0, 2));
        parent.add(panel_for_questlist);
        //parent.add(newChooseDropAction.panel_for_copy);
        parent.add(TableUserVsQuest);
        parent.add(panel_for_displayquest);
        DisplayStats displayStats = new DisplayStats();
        parent.add(displayStats);
        //parent.add(app.btnSetQuestNumber, BorderLayout.EAST);
        //Display the window.
        frame.pack();
        frame.setContentPane(parent);
        frame.setBounds(0, 0, 1200, 700);
        frame.setVisible(true);

    }
}