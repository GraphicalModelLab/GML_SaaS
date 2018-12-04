package org.graphicalmodellab.app.animationsystem;
import javax.swing.*;
import javax.swing.event.*;

public class FrameChangeWindow extends JFrame implements ChangeListener{
	JSlider change_slider;
	MotionViewer parent;
	public FrameChangeWindow(MotionViewer parent, int initialRate){
		this.setTitle("Frame Rate Changer");
		this.change_slider = new JSlider();
		this.change_slider.setValue(initialRate);
		
		this.change_slider.addChangeListener(this);
		this.add(this.change_slider);
	
		this.parent = parent;
		
		this.setSize(400, 50);
		this.setVisible(true);
	}
	public void stateChanged(ChangeEvent e){
		System.out.println(this.change_slider.getValue());
		this.parent.setFrameTime(this.change_slider.getValue());
	}
}
