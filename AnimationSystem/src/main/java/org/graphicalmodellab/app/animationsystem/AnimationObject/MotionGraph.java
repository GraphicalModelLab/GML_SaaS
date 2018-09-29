package org.graphicalmodellab.app.animationsystem.AnimationObject;


import javax.media.opengl.GLAutoDrawable;
import java.util.*;

public class MotionGraph extends Drawable {
	/** node information **/
	public MotionNode current;
	
	public HashMap<String, MotionNode> map;
	/** base positions **/
	float[] draw_position;
	public MotionGraph(MotionNode start, MotionNode[] nodes){
		current = start;
		map = new HashMap<String,MotionNode>();
		for(int i=0;i<nodes.length;i++) map.put(nodes[i].name, nodes[i]);
		this.draw_position = new float[3];
		
		this.initDrawPosition();
	}
	
	public void transitionTo(MotionNode next){
		this.current = next;
	}
	
	public void positionAtFrame(int frame,GLAutoDrawable drawable){
		current.Draw(frame, draw_position, drawable);
	}
	
	public void positionAtFrameWithStop(int frame, GLAutoDrawable drawable){
		current.DrawWithStop(frame,this.draw_position,drawable);
	}
	
	public int getNumOfFrames(){
		return current.motion.getNumOfFrames();
	}
	
	public void initDrawPosition(){
		this.draw_position[0] = current.motion.root.position[0][0];
		this.draw_position[1] = current.motion.root.position[0][1];
		this.draw_position[2] = current.motion.root.position[0][2];
	}
	
	public void transitionToLinearPath(double theta){
		
	}
	public void upScale(float up){
		this.current.motion.scale += up;
	}
	public void downScale(float down){
		this.current.motion.scale -= down;
	}
	public void setScale(float scale){
		this.current.motion.scale = scale;
	}
}