package org.graphicalmodellab.app.animationsystem;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Locale;

import org.graphicalmodellab.app.animationsystem.AnimationObject.Drawable;
import org.graphicalmodellab.app.animationsystem.AnimationObject.TheCharacter;

public class WriteBVHWindow extends JFrame implements ActionListener{
	JButton[] select;
	JTextField[] filename;
	JButton save;
	MotionViewer parent;
	public WriteBVHWindow(int numOfSequences, MotionViewer parent){
		this.setTitle("Save as BVH");
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
		
		this.save = new JButton("Save");
		this.save.addActionListener(this);
		big.add(this.save);
		
		this.add(big);
		
		this.parent = parent;
		
		this.setSize(400, 300);
		this.setVisible(true);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == save){
			/** Get all characters currently moving on the ground **/
			Drawable[] f = this.parent.getCharacters();
			
			/** Add the specified motions into the selected characters **/
			for(int i=0;i<this.filename.length;i++){
				if(filename[i].getText().length() > 0){
					if(f[i] instanceof TheCharacter){
						((TheCharacter)f[i]).writeBVH(filename[i].getText(), 0.00833f);
					}
				}
			}
			/** close the window **/
			this.dispose();
		}else{
			for(int i=0;i<this.select.length;i++){
				if(e.getSource() == select[i]){
					JFileChooser fmanager = new JFileChooser();
					fmanager.setLocale(Locale.ENGLISH);
					File f = null;
					int selected = fmanager.showSaveDialog(this);
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
