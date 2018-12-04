package org.graphicalmodellab.app.animationsystem.AnimationObject;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

public class MyPerspectiveCamera extends MyCamera{
	private double fovy, aspect;
	public MyPerspectiveCamera(double fovy, double aspect, double near, double far){
		super(near,far);
		this.fovy = fovy;
		this.aspect = aspect;
	}
	
	public MyPerspectiveCamera(double fovy, double aspect, double near, double far, double x, double y, double z){
		this(fovy,aspect,near,far);
		this.setPosition(x, y, z);
	}
	public void setup(GLAutoDrawable drawable){
		GL2 gl = drawable.getGL().getGL2();
		
	    gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
	    
	    GLU glu = new GLU();
	    glu.gluPerspective(this.fovy,this.aspect,this.near,this.far);
	    this.transObj(drawable);
	    glu.gluLookAt(this.x,this.y,this.z,this.look_x,this.look_y,this.look_z,this.up_x,this.up_y,this.up_z);
	    
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	}
	
	public double getFovy(){
		return this.fovy;
	}
	public void setFovy(double fovy){
		this.fovy = fovy;
	}
	public double getAspect(){
		return this.aspect;
	}
	public void setAspect(double aspect){
		this.aspect = aspect;
	}
}
