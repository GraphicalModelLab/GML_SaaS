package org.graphicalmodellab.app.animationsystem;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;


import org.graphicalmodellab.app.animationsystem.AnimationObject.Drawable;
import org.graphicalmodellab.app.animationsystem.AnimationObject.MotionGraph;

public class ControlWindow extends JFrame implements ActionListener{
	ArrayList<JButton> select;
	ArrayList<MotionGraph> graphs;
	MotionViewer parent;
	public ControlWindow(MotionViewer parent){
		this.setTitle("Select Graph");
		this.select = new ArrayList<JButton>();
		this.graphs = new ArrayList<MotionGraph>();
		Drawable[] seq = parent.getCharacters();
			
		int count=1;
		for(int i=0;i<seq.length;i++){
			if(seq[i] instanceof MotionGraph){
				JPanel panel = new JPanel(new GridLayout(1,1));
				
				JButton selecttemp = new JButton("Graph "+count);
				selecttemp.addActionListener(this);
				
				select.add(selecttemp);
				graphs.add((MotionGraph)seq[i]);
				this.add(selecttemp);
				count++;
			}
		}
		
		
		this.parent = parent;
		
		this.setSize(400, 300);
		this.setVisible(true);
	}
	public void actionPerformed(ActionEvent e){
		int size = this.select.size();
		for(int i=0;i<size;i++){
			if(e.getSource() == select.get(i)){
				new ControllerWindow(this.graphs.get(i));
			}
		}
	}
}
