package org.graphicalmodellab.app.animationsystem.AnimationObject;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import java.util.*;
class Transform{
	public static int SCALE = 1;
	public static int TRANSLATE = 2;
	public static int ROTATE = 3;
	
	int type;
	float x,y,z;
	double theta;
	
	public Transform(int type,float x, float y, float z){
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Transform(int type,float x, float y, float z,double theta){
		this(type,x,y,z);
		this.theta = theta;
	}
	
	public void doTransform(GL2 gl){
		if(type == SCALE){
			gl.glScaled(x, y, z);
		}else if(type == TRANSLATE){
			gl.glTranslated(x, y, z);
		}else if(type == ROTATE){
			gl.glRotated(theta, x, y, z);
		}
	}
}

public class Obj {
	String name;
	ArrayList<Transform> transList;
	
	public Obj(){
		this.transList = new ArrayList<Transform>();
	}
	public void translate(float x,float y, float z){
		transList.add(new Transform(Transform.TRANSLATE,x,y,z));
	}
	public void rotate(double theta,float x, float y, float z){
		transList.add(new Transform(Transform.ROTATE,x,y,z,theta));	
	}
	public void scale(float x, float y, float z){
		transList.add(new Transform(Transform.SCALE,x,y,z));	
	}
	public void transObj(GLAutoDrawable drawable){
		 GL2 gl = drawable.getGL().getGL2();
		 Iterator iterator = transList.iterator();
		 while(iterator.hasNext()){
		    	Transform child = (Transform)(iterator.next());
		    	child.doTransform(gl);
		 }
	}
	public void Draw(float[] color,GLAutoDrawable drawable){
		
	}
}
