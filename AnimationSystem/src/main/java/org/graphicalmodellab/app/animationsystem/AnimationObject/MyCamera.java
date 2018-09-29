package org.graphicalmodellab.app.animationsystem.AnimationObject;


import javax.media.opengl.GLAutoDrawable;

public abstract class MyCamera extends Obj{
	protected double near,far;
	protected double x,y,z;
	protected double look_x,look_y,look_z;
	protected double up_x,up_y,up_z;
	
	public MyCamera(double near, double far){
		this.near = near;
		this.far = far;
		this.look_z = -1;
		this.up_y = 1;
	}
	
	public void setPosition(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setLookDir(double look_x, double look_y, double look_z){
		this.look_x = look_x;
		this.look_y = look_y;
		this.look_z = look_z;
	}
	
	public void setUpDir(double up_x, double up_y, double up_z){
		this.up_x = up_x;
		this.up_y = up_y;
		this.up_z = up_z;
	}
	
	public double getNear(){ 
		return this.near;
	}
	
	public double getFar(){
		return this.far;
	}
	
	public abstract void setup(GLAutoDrawable drawable);
}
