package com.sciquizapp.sciquizserver;

//graphics classes
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DisplayQuestion extends JComponent {
	private static final long serialVersionUID = 1L;
    private Image image = null;
    JLabel question_label;
    JLabel answer1_label;
    JLabel answer2_label;
    JLabel answer3_label;
    JLabel answer4_label;
	JTextField question_text;
	JTextField answer1_text;
	JTextField answer2_text;
	JTextField answer3_text;
	JTextField answer4_text;
	ImageIcon icon;
	JLabel thumb;
    
    public DisplayQuestion (JPanel panel_disquest){
    	Box box = Box.createVerticalBox();
    	panel_disquest.add( box );
    	question_label = new JLabel("Question:");
    	question_text = new JTextField("");
    	answer1_label = new JLabel("Réponse 1:");
    	answer1_text = new JTextField("");
    	answer2_label = new JLabel("Réponse 2:");
    	answer2_text = new JTextField("");
    	answer3_label = new JLabel("Réponse 3:");
    	answer3_text = new JTextField("");
    	answer4_label = new JLabel("Réponse 4:");
    	answer4_text = new JTextField("");
    	//Image image=GenerateImage.toImage(true);  //this generates an image file
    	//icon = new ImageIcon(image); 
    	thumb = new JLabel();
    	//thumb.setIcon(icon);
    	box.add(question_label);
    	box.add(question_text);
    	box.add(answer1_label);
    	box.add(answer1_text);
    	box.add(answer2_label);
    	box.add(answer2_text);
    	box.add(answer3_label);
    	box.add(answer3_text);
    	box.add(answer4_label);
    	box.add(answer4_text);
    	box.add(thumb);
    }
    public void ShowQuestion (Question question_to_display, JFrame parentFrame, JPanel panel_disquest) {
    	question_text.setText(question_to_display.getQUESTION());
    	answer1_text.setText(question_to_display.getOPTA());
    	answer2_text.setText(question_to_display.getOPTB());
    	answer3_text.setText(question_to_display.getOPTC());
    	answer4_text.setText(question_to_display.getOPTD());
    	
    	try {
            File image2 = new File(question_to_display.getIMAGE());
            image = ImageIO.read(image2);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    	image = image.getScaledInstance(100, 100,
    	        Image.SCALE_SMOOTH);
    	icon = new ImageIcon(image);
    	thumb.setIcon(icon);
        //parentFrame.add(this);
        //parentFrame.setVisible(true);
//        System.out.println("trying to display question");
    }
    /*public void paintComponent (Graphics g){
        if(image == null) return;
        //int imageWidth = image.getWidth(this);
        //int imageHeight = image.getHeight(this);
        
        g.drawImage(image, 0, 0, 100, 100, this);

        for (int i = 0; i*imageWidth <= getWidth(); i++)
            for(int j = 0; j*imageHeight <= getHeight();j++)
                if(i+j>0) g.copyArea(0, 0, imageWidth, imageHeight, i*imageWidth, j*imageHeight);
    }*/
}