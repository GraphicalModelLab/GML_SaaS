package org.graphicalmodellab.app.animationsystem.AnimationObject;


import javax.media.opengl.GLAutoDrawable;
import java.util.ArrayList;


public class MotionNode{
	public String name;
	public TheCharacter motion;
	public ArrayList<MotionNode> next;
	
	public MotionNode(TheCharacter motion){
		this.name = "";
		this.motion = motion;
		next = new ArrayList<MotionNode>();
	}
	public MotionNode(String name,TheCharacter motion){
		this.name = name;
		this.motion = motion;
		next = new ArrayList<MotionNode>();
	}
	public void add(MotionNode n){
		this.next.add(n);
	}
	
	public void Draw(int frame,float[] base,GLAutoDrawable drawable){
		System.out.println("scale="+motion.scale+" posi at "+base[0]+","+base[1]+","+base[2]);
		motion.positionAtFrame(frame, base, drawable);
	}
	
	public void DrawWithStop(int frame,float[] base,GLAutoDrawable drawable){
		System.out.println("scale="+motion.scale+" stop posi at "+base[0]+","+base[1]+","+base[2]);
		motion.positionAtFrameWithStop(frame, base,drawable);
	}
}