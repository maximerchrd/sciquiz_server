package com.sciquizapp.sciquizserver;

//graphics classes
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class DisplayQuestion extends JComponent {
	private static final long serialVersionUID = 1L;
    private Image image;
    
    public DisplayQuestion (Question question_to_display, JFrame parentFrame){
        try {
            File image2 = new File(question_to_display.getIMAGE());
            image = ImageIO.read(image2);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        parentFrame.add(this);
        parentFrame.setVisible(true);
        System.out.println("trying to display question");
    }
    public void paintComponent (Graphics g){
        if(image == null) return;
        int imageWidth = image.getWidth(this);
        int imageHeight = image.getHeight(this);

        g.drawImage(image, 50, 50, this);

        for (int i = 0; i*imageWidth <= getWidth(); i++)
            for(int j = 0; j*imageHeight <= getHeight();j++)
                if(i+j>0) g.copyArea(0, 0, imageWidth, imageHeight, i*imageWidth, j*imageHeight);
    }
}