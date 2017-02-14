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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import javafx.scene.layout.BorderPane;

import com.sciquizapp.sciquizserver.ChooseDropActionDemo;
import com.sciquizapp.sciquizserver.AWTCounter;
import com.sciquizapp.sciquizserver.DBManager;
import com.sciquizapp.sciquizserver.DisplayQuestion;

public class MyServer {

    public static void main(String[] args) throws Exception {

        //start bluetooth network in new thread
        NetworkCommunication CommunicationWithClients = new NetworkCommunication();
        Thread networkThread = new Thread() {
            public void run() {
                try {
                    CommunicationWithClients.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        networkThread.start();

        //does db stuffs
        DBManager dao = new DBManager();
        dao.createDBIfNotExists();
        dao.createQuestionsTableIfNotExists();
        dao.getAllQuestions();

        //declares jpanels for different parts of the window
        JPanel panel_for_questlist = new JPanel(); // useless now, do something later?
        JPanel panel_for_counter = new JPanel();
        JPanel panel_for_disquest = new JPanel();

        //Setup the table

        //Create and set up the window.
        JFrame frame = new JFrame("Learning Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        Table TableUserVsQuest = new Table();
        TableUserVsQuest.setOpaque(true); //content panes must be opaque
        //frame.setContentPane(TableUserVsQuest);


        //Turn off metal's use of bold fonts
        ChooseDropActionDemo newChooseDropAction = new ChooseDropActionDemo(frame, panel_for_questlist, panel_for_disquest, CommunicationWithClients);
        UIManager.put("swing.boldMetal", Boolean.FALSE);


        // implements the splitting of the window
        /*JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, newChooseDropAction.p, TableUserVsQuest);
		frame.getContentPane().add(splitPane);*/


        AWTCounter app = new AWTCounter(TableUserVsQuest, frame, newChooseDropAction, panel_for_counter);

        //implement the division of the window with borderlayout
        JPanel parent = new JPanel();
        parent.setLayout(new GridLayout(0, 2));
        parent.add(panel_for_questlist);
        //parent.add(newChooseDropAction.panel_for_copy);
        parent.add(TableUserVsQuest);
        parent.add(panel_for_counter);
        parent.add(panel_for_disquest);
        //parent.add(app.btnSetQuestNumber, BorderLayout.EAST);
        //Display the window.
        //frame.pack();
        frame.setContentPane(parent);
        frame.setBounds(0, 0, 1000, 500);
        frame.setVisible(true);
//		//Sends question to clients
//		ServerSocket serverSocket = null;
//		Socket socket = null;
//		DataInputStream dataInputStream = null;
//		DataOutputStream dataOutputStream = null;
//
//		try {
//			serverSocket = new ServerSocket(8080);
//			System.out.println("Listening :8080");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		while(true){
//			try {
//				socket = serverSocket.accept();  //the program stops here waiting for a connection
//				dataInputStream = new DataInputStream(socket.getInputStream());
//				dataOutputStream = new DataOutputStream(socket.getOutputStream());
//				System.out.println("ip: " + socket.getInetAddress());
//				String textToWrite = dataInputStream.readUTF();
//				//TableUserVsQuest.addUser(String.valueOf(socket.getInetAddress()));
//				if (!TableUserVsQuest.IsUserInTable(textToWrite)) {
//					TableUserVsQuest.addUser(textToWrite.split(";")[0]);
//				}
//
//				app.editTextField(textToWrite);
//				dataOutputStream.writeUTF(String.valueOf(app.getQuestionNumber()));
//				if (textToWrite.endsWith(";")) {
//					TableUserVsQuest.addAnswerForUser(textToWrite);
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			finally{
//				if( socket!= null){
//					try {
//						socket.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//
//				if( dataInputStream!= null){
//					try {
//						dataInputStream.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//
//				if( dataOutputStream!= null){
//					try {
//						dataOutputStream.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}
    }
}