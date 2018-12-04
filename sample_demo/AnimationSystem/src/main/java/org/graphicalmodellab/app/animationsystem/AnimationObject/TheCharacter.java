package org.graphicalmodellab.app.animationsystem.AnimationObject;

import java.util.*;
import java.io.*;

import org.graphicalmodellab.app.animationsystem.IKSolver.CCD;

import javax.media.opengl.GLAutoDrawable;

public class TheCharacter extends Drawable {
	/** Information of Root joint for Path Change **/
	public float[][] original_position;
	public Quaternion[] original_quat;
	
	/* */
	public Root root;
	public float scale;
	
	public float color[];
	
	public HashMap<String, Joint> jointMap;
	
	/** base positions **/
	float[] draw_position;
	public TheCharacter(Root data,HashMap<String, Joint> jointMap){
		this.color = new float[4];
		this.color[0] = Bone.RED[0];
		this.color[1] = Bone.RED[1];
		this.color[2] = Bone.RED[2];
		this.color[3] = Bone.RED[3];
		this.scale = 0.4f;
		this.draw_position = new float[3];
		this.root = data;
		this.jointMap = jointMap;
		
		//this.setPathEditingInfo();
	}
	public TheCharacter(Root data){
		this.color = new float[4];
		this.color[0] = Bone.RED[0];
		this.color[1] = Bone.RED[1];
		this.color[2] = Bone.RED[2];
		this.color[3] = Bone.RED[3];
		
		this.scale = 0.4f;
		this.draw_position = new float[3];
		this.root = data;
		this.jointMap = new HashMap<String,Joint>();
		//this.setPathEditingInfo();
	}
	public TheCharacter(){
		this.color = new float[4];
		this.color[0] = Bone.RED[0];
		this.color[1] = Bone.RED[1];
		this.color[2] = Bone.RED[2];
		this.color[3] = Bone.RED[3];
		this.scale = 0.4f;
		
		this.draw_position = new float[3];
		this.jointMap = new HashMap<String,Joint>();
	}
	public void shift(float x, float y, float z){
		root.shift(x, y, z);
	}
	public void rotate(double degree,double x, double y, double z){
		root.rotate(degree, x, y, z);
	}
	
	public int numOfFrame(){
		return root.numOfFrames;
	}
	
	public TheCharacter replicate(int numOfFrame){
		TheCharacter result = new TheCharacter();
		result.root = root.replicate(numOfFrame,result.jointMap);
		result.scale = this.scale;
		return result;
	}
	/**
	 * 
	 * @param frame
	 * @param startJoint
	 * @param endJointName
	 * @param x
	 * @param y
	 * @param z
	 * @return false if one of joints are not in joint map
	 */
	public boolean solveIKCCD(int frame,String startJoint, String endJointName, double x, double y, double z){
		if(jointMap.containsKey(startJoint) && jointMap.containsKey(startJoint)){
			CCD.solve(frame, jointMap.get(startJoint), endJointName, x, y, z);
			return true;
		}
		return false;
	}
	public void setScale(float scale){
		this.scale = scale;
	}
	public void downScale(float down){
		this.scale -= down;
	}
	public void upScale(float up){
		this.scale += up;
	}
	
	public int getNumOfFrames(){
		return root.numOfFrames;
	}
	/**
	 * pick an appropriate frame from all motions and display it.
	 * @param no
	 * @param drawable
	 */
	public void positionAtFrame(int frame, GLAutoDrawable drawable){
		frame = frame % root.numOfFrames;
		
		if(frame == 0){
			this.draw_position[0] += this.root.position[1][0] - this.root.position[0][0];
			this.draw_position[1] = this.root.position[0][1];
			this.draw_position[2] += this.root.position[1][2] - this.root.position[0][2];

		}else{
			this.draw_position[0] += this.root.position[frame][0] - this.root.position[frame-1][0];
			this.draw_position[1] = this.root.position[frame][1];
			this.draw_position[2] += this.root.position[frame][2] - this.root.position[frame-1][2];
		}
		root.Draw(frame, scale, this.draw_position,this.color,drawable);
	}
	/**
	 * pick an appropriate frame from all motions and display it.
	 * @param no
	 * @param drawable
	 */
	public void positionAtFrame(int frame, float[] positions, GLAutoDrawable drawable){
		frame = frame % root.numOfFrames;
		
		if(frame == 0){
			positions[0] += this.root.position[1][0] - this.root.position[0][0];
			positions[1] = this.root.position[0][1];
			positions[2] += this.root.position[1][2] - this.root.position[0][2];
		}else{
			positions[0] += this.root.position[frame][0] - this.root.position[frame-1][0];
			positions[1] = this.root.position[frame][1];
			positions[2] += this.root.position[frame][2] - this.root.position[frame-1][2];
		}
		root.Draw(frame, scale, positions,this.color,drawable);
	}
	public void setColor(float a1,float a2, float a3, float a4){
		System.out.println("Change Color! to ("+a1+","+a2+","+a3+","+a4);
		this.color[0] = a1;
		this.color[1] = a2;
		this.color[2] = a3;
		this.color[3] = a4;
	}
	
	/**
	 * Display a character without moving
	 * @param frame
	 * @param drawable
	 */
	public void positionAtFrameWithStop(int frame, GLAutoDrawable drawable){
		frame = frame % root.numOfFrames;
		root.Draw(frame, scale, this.draw_position,this.color,drawable);
	}
	
	public void positionAtFrameWithStop(int frame,float[] position, GLAutoDrawable drawable){
		frame = frame % root.numOfFrames;
		root.Draw(frame, scale, position,this.color,drawable);
	}
	public void initDrawPosition(){
		this.draw_position[0] = root.position[0][0];
		this.draw_position[1] = root.position[0][1];
		this.draw_position[2] = root.position[0][2];
	}
	public void writeBVH(String filename,float frametime){
		StringBuffer result = new StringBuffer();
		result.append(root.expHierarchy());
		result.append("\n");
		result.append("MOTION\n");
		result.append("Frames: "+root.numOfFrames+"\n");
		result.append("Frame Time: "+frametime+"\n");
		result.append(root.expMotionData());
		
		try{
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename))));
			
			pw.write(result.toString());
			
			pw.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	
	/** The followings are for changing path **/
	public void setPathEditingInfo(){
		int numOfFrame = root.numOfFrames;
		original_position = new float[numOfFrame][3];
		original_quat = new Quaternion[numOfFrame];
		
		for(int i=0;i<numOfFrame;i++){
			original_position[i][0] = root.position[i][0];
			original_position[i][1] = root.position[i][1];
			original_position[i][2] = root.position[i][2];
			
			original_quat[i] = root.quat[i].clone();
		}
	}
	public void transitionToLinearPath(double theta){
		System.out.println("Before change to "+theta);
		theta = theta % 360;
		System.out.println("After change to "+theta);
		int numOfFrame = this.original_position.length;
		/** Linear path change by a circle, z = "0" should be the original **/
		double offset = 0;
		if(original_position[0][2] < 0) offset = -original_position[0][2];
	
		/** Change positions **/
		double ta = Math.tan(theta*Math.PI/180);
		for(int i=0;i<numOfFrame;i++){
			root.position[i][0] = (float)Math.sqrt((this.original_position[i][2]+offset)*(this.original_position[i][2]+offset)/(1+ta*ta));
			root.position[i][2] = (float)(ta*root.position[i][0] - offset);
		}
		
		/** Change rotation **/
		for(int i=0;i<numOfFrame-1;i++){
			double y_angle = getEuler(root.position[i],root.position[i+1]);
			float[] euler = this.original_quat[i].getEuler();
			euler[1] += (float)y_angle;
			root.quat[i].setEuler(euler[0], euler[1], euler[2]);
			
			/*
			Quaternion rotate = getRotation(root.position[i],root.position[i+1]);
			rotate.mul(this.original_quat[i]);
			root.quat[i].setQuat(rotate);
			*/
		}
	}

	public static double getEuler(float[] pos1, float[] pos2){
		double d_x = Math.abs(pos2[0] - pos1[0]);
		double d_z = Math.abs(pos2[2] - pos1[2]);
		return Math.atan2(d_x, d_z)*180/Math.PI;
	}
	public static Quaternion getRotation(float[] pos1, float[] pos2){
		double d_x = Math.abs(pos2[0] - pos1[0]);
		double d_z = Math.abs(pos2[2] - pos1[2]);
		double app_angle = Math.atan2(d_x, d_z)*180/Math.PI;
		
		return Quaternion.axisAngle(app_angle, 0, 1, 0);
	}
}
