package org.graphicalmodellab.app.animationsystem;
import java.awt.*;

import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Locale;


import org.graphicalmodellab.app.animationsystem.AnimationObject.Drawable;

public class AddMotionWindow extends JFrame implements ActionListener{
	JButton[] select;
	JTextField[] filename;
	JButton addMotion;
	MotionViewer parent;
	public AddMotionWindow(int numOfSequences, MotionViewer parent){
		this.setTitle("Add Motions");
		this.select = new JButton[numOfSequences];
		this.filename = new JTextField[numOfSequences];
		JPanel big = new JPanel(new GridLayout(numOfSequences+1,1));
		for(int i=0;i<numOfSequences;i++){
			JPanel panel = new JPanel(new GridLayout(1,2));
			
			select[i] = new JButton("Character "+(i+1));
			select[i].addActionListener(this);
			
			panel.add(select[i]);
			
			filename[i] = new JTextField("");
			
			panel.add(filename[i]);
			
			big.add(panel);
		}
		
		this.addMotion = new JButton("Add!!");
		this.addMotion.addActionListener(this);
		big.add(this.addMotion);
		
		this.add(big);
		
		this.parent = parent;
		
		this.setSize(400, 300);
		this.setVisible(true);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == addMotion){
			/** Get all characters currently moving on the ground **/
			Drawable[] f = this.parent.getCharacters();
			
			/** Add the specified motions into the selected characters **/
			int minimumFrame = Integer.MAX_VALUE;
			for(int i=0;i<this.filename.length;i++){
				if(filename[i].getText().length() > 0){
					/*
					Format fm = MotionCaptureDataReader.readBVH(filename[i].getText(), "UTF-8");
					fm.setScale(0.04); 
					f[i].add(fm);
					
					if(minimumFrame > f[i].getTotalNumberOfFrames()){
						minimumFrame = f[i].getTotalNumberOfFrames();
					}
					*/
				}
			}
			this.parent.setNumOfFrame(minimumFrame);
			
			/** close the window **/
			this.dispose();
		}else{
			for(int i=0;i<this.select.length;i++){
				if(e.getSource() == select[i]){
					JFileChooser fmanager = new JFileChooser();
					fmanager.setLocale(Locale.ENGLISH);
					File f = null;
					int selected = fmanager.showOpenDialog(this);
					if (selected == JFileChooser.APPROVE_OPTION){
					      f = fmanager.getSelectedFile();
					      this.filename[i].setText(f.getPath());
					}else if (selected == JFileChooser.CANCEL_OPTION){
						  return;
					}else if (selected == JFileChooser.ERROR_OPTION){
						  return;
					}
					
				}
			}
		}
	}
}
