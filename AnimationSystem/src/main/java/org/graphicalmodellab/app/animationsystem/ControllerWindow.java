package org.graphicalmodellab.app.animationsystem;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import org.graphicalmodellab.app.animationsystem.AnimationObject.MotionNode;
import org.graphicalmodellab.app.animationsystem.AnimationObject.MotionGraph;

public class ControllerWindow extends JFrame implements ActionListener{
	ArrayList<JButton> select;
	ArrayList<MotionNode> nextMotion;
	MotionGraph graph;
	public ControllerWindow(MotionGraph graph){
		this.setTitle("Select a next motion - "+graph.current.name);
		this.graph = graph;
		this.nextMotion = this.graph.current.next;
		setComponent();
		this.setSize(400, 300);
		this.setVisible(true);
	}
	public void setComponent(){
		int count=1;
		int size = this.nextMotion.size();
		this.select = new ArrayList<JButton>();
		
		System.out.println("size of next motions ="+size);
		for(int i=0;i<size;i++){
			JPanel panel = new JPanel(new GridLayout(1,1));
			
			JButton selecttemp = new JButton("Motion - "+this.nextMotion.get(i).name);
			selecttemp.addActionListener(this);
			
			select.add(selecttemp);
			this.add(selecttemp);
			count++;
		}
	}
	public void actionPerformed(ActionEvent e){
		int size = this.select.size();
		for(int i=0;i<size;i++){
			if(e.getSource() == select.get(i)){
				this.selectMotion(this.nextMotion.get(i));
			}
		}
	}
	public void selectMotion(MotionNode motion){
		 this.graph.transitionTo(motion);
		
		 new ControllerWindow(this.graph);
		 /** close the window **/
		 this.dispose();
	}
}
