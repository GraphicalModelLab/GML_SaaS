package org.graphicalmodellab.app.animationsystem;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class AddScriptWindow extends JFrame implements ActionListener{
	JButton addCharacter;
	JButton select;
	JTextField filename;
	JTextField frameNo;
	MotionViewer parent;
	
	public AddScriptWindow(MotionViewer parent){
		this.setTitle("Add Script");
		
		JPanel big = new JPanel(new GridLayout(2,1));
		
		JPanel panel = new JPanel(new GridLayout(1,3));
		
		this.select = new JButton("select");
		this.select.addActionListener(this);
		panel.add(select);
		
		this.filename = new JTextField("");
		panel.add(this.filename);
		
		this.frameNo = new JTextField("");
		panel.add(this.frameNo);
		
		big.add(panel);
		
		this.addCharacter = new JButton("Add Script");
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
				 try{
					 int frameNo = Integer.parseInt(this.frameNo.getText());
					 this.parent.addScript(this.filename.getText(), frameNo);
				 }catch(Exception ee){
					 
				 }
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
