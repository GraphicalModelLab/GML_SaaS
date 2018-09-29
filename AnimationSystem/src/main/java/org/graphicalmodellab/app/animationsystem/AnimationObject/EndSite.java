package org.graphicalmodellab.app.animationsystem.AnimationObject;
import java.util.HashMap;
import java.util.Iterator;


import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;


public class EndSite extends Joint{

	public EndSite(String name){
		super(name);
	}
	public Joint replicate(int numOfFrame,HashMap<String, Joint> jointMap){
		Joint result = new Joint(this.name);
		result.quat = new Quaternion[numOfFrame];
		for(int i=0;i<numOfFrame;i++) result.quat[i] = new Quaternion(0,0,0);
		result.offset = new float[3];

		for(int i=0;i<3;i++) result.offset[i] = this.offset[i];
		jointMap.put(name, result);
		Iterator iterator = children.iterator();
		while(iterator.hasNext()){
		    	Joint child = (Joint)(iterator.next());
		    	result.add(child.replicate(numOfFrame,jointMap));
		}
		return result;
	}
	public String expHierarchy(String space){
		StringBuffer result = new StringBuffer("");
		result.append(space+"End Site");
		result.append("\n"+space+"{\n");
		result.append(space+"OFFSET ");
		result.append(offset[0]+" "); result.append(offset[1]+" "); result.append(offset[2]+"\n"); 
		result.append(space+"}");
		return result.toString();
	}
	
	public String expMotionData(int frame){
		return "";
	}
	
	public void Draw(int frame,float[] color,GLAutoDrawable drawable){
		 GL2 gl = drawable.getGL().getGL2();
		    
		 /** Remember the matrix **/
		 gl.glPushMatrix();
		    
		 /** Display the bone **/
		 Obj bone = new Bone(0,0,0,offset[0],offset[1],offset[2]);
		 bone.Draw(color,drawable);
		    
		 /** Move the current bone into the appropriate position **/
	     gl.glTranslatef(offset[0], offset[1],offset[2]);
	    	
		 gl.glPopMatrix();
		 /*
		 
		    gl.glPushMatrix();
		    
		   gl.glTranslatef(offset[0]*scale, 2*offset[1]*scale,offset[2]*scale);
	    	if(name.equals("head")){
	    	    GLUT glut = new GLUT();
			    
			    glut.glutSolidSphere(15.0*scale,10,10);
	    	}
	    	gl.glTranslatef(0,-(float)(offset[1]*scale),0);
	    
			    
	    	 gl.glPopMatrix();
	    	 */
	}
}
