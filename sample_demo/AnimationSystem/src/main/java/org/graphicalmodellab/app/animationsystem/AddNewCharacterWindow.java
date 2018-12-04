package org.graphicalmodellab.app.animationsystem;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.io.*;
import java.util.*;

import org.graphicalmodellab.app.animationsystem.AnimationObject.TheCharacter;
import org.graphicalmodellab.app.animationsystem.MotionCapReader.BVHReader;

public class AddNewCharacterWindow extends JFrame implements ActionListener{
	JButton addCharacter;
	JButton select;
	JTextField filename;
	MotionViewer parent;
	
	public AddNewCharacterWindow(MotionViewer parent){
		this.setTitle("Add a new Character");
		
		JPanel big = new JPanel(new GridLayout(2,1));
		
		JPanel panel = new JPanel(new GridLayout(1,2));
		
		this.select = new JButton("select");
		this.select.addActionListener(this);
		panel.add(select);
		
		this.filename = new JTextField("");
		panel.add(this.filename);
		
		big.add(panel);
		
		this.addCharacter = new JButton("Add a new Character");
		this.addCharacter.addActionListener(this);
		big.add(this.addCharacter);
		
		this.add(big);
		this.parent = parent;
		this.setSize(400, 300);
		this.setVisible(true);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == addCharacter){
			if(this.filename.getText().length()>0){
				 /** Construct a new list of characters **/
				 TheCharacter added = BVHReader.readBVH(this.filename.getText(), "UTF-8");
				 this.parent.addDrawable(added);
				 /** close the window **/
				 this.dispose();
			}
		}else if(e.getSource() == select){
			JFileChooser fmanager = new JFileChooser();
			fmanager.setLocale(Locale.ENGLISH);
			File f = null;
			int selected = fmanager.showOpenDialog(this);
			if (selected == JFileChooser.APPROVE_OPTION){
			      f = fmanager.getSelectedFile();
			      this.filename.setText(f.getPath());
			}else if (selected == JFileChooser.CANCEL_OPTION){
				  return;
			}else if (selected == JFileChooser.ERROR_OPTION){
				  return;
			}
		}
	}
}
