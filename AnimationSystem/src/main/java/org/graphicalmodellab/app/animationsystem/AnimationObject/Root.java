package org.graphicalmodellab.app.animationsystem.AnimationObject;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import java.util.*;

public class Root extends Joint {
	public int numOfFrames;
	public float[][] position;
	public Root(String name){
		super(name);
	}
	public Root replicate(int numOfFrame,HashMap<String, Joint> jointMap){
		Root result = new Root(this.name);
		result.numOfFrames = numOfFrame;
		result.position = new float[numOfFrame][3];
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
	public void shift(float x, float y, float z){
		for(int i=0;i<numOfFrames;i++){
			position[i][0] += x;
			position[i][1] += y;
			position[i][2] += z;
		}
	}
	public void rotate(double degree,double x, double y, double z){
		Quaternion axis = Quaternion.axisAngle(degree, x, y, z);
		for(int i=0;i<numOfFrames;i++){
			quat[i].mul(axis);
			quat[i].normalize();
		}
	}
	public void Draw(int frame, float scale,float[] draw_position,float[] color,GLAutoDrawable drawable){
		 GL2 gl = drawable.getGL().getGL2();
		    
		 /** Remember the matrix **/
		 gl.glPushMatrix();
		    
		 gl.glScalef(scale, scale, scale);
		 /** Move the current bone into the appropriate position **/
		 gl.glTranslatef(draw_position[0],draw_position[1],draw_position[2]);

		 /** Rotate **/
		 gl.glMultMatrixf(quat[frame].toRotationMatrix(), 0);
	    	/*
		 gl.glRotatef(euler[frame][0], 1.0f, 0f, 0f);
		 gl.glRotatef(euler[frame][1], 0f, 1.0f, 0f);
		 gl.glRotatef(euler[frame][2], 0f, 0f, 1.0f);
	   */
		
		 /** Go down to the children and draw them **/
		 Iterator iterator = children.iterator();
		 while(iterator.hasNext()){
		    	Joint child = (Joint)(iterator.next());

		    	child.Draw(frame,color,drawable);
		    	
		 }
		    
		 /** Restore the matrix **/
		 gl.glPopMatrix();
	}
	
	
	
	
	@Override
	public int setChannelInfo(ArrayList<String> channel, double[][] motion_data, int begin){
		String[] channel_string = channel.toArray(new String[channel.size()]);
		numOfFrames = motion_data.length;
		//euler = new float[motion_data.length][3];
		quat = new Quaternion[motion_data.length];
		position = new float[motion_data.length][3];
		
		for(int j=0;j<motion_data.length;j++){
			float x=0,y=0,z=0;
			float x_pos=0,y_pos=0,z_pos=0;
			for(int i=0;i<channel_string.length;i++){
				if(channel_string[i].equals("Xrotation")){
					x = (float)motion_data[j][begin+i];
				}else if(channel_string[i].equals("Yrotation")){
					y = (float)motion_data[j][begin+i];
				}else if(channel_string[i].equals("Zrotation")){
					z = (float)motion_data[j][begin+i];
				}else if(channel_string[i].equals("Xposition")){
					x_pos = (float)motion_data[j][begin+i];
				}else if(channel_string[i].equals("Yposition")){
					y_pos = (float)motion_data[j][begin+i];
				}else if(channel_string[i].equals("Zposition")){
					z_pos = (float)motion_data[j][begin+i];
				}
			}
			
			/*
			euler[j][0] = x;
			euler[j][1] = y;
			euler[j][2] = z;
			*/
			
			position[j][0] = x_pos;
			position[j][1] = y_pos;
			position[j][2] = z_pos;
			
			quat[j] = new Quaternion(x,y,z);
		}
		
		return channel_string.length;
	}
	

	/** For writing BVH file back **/
	public String expHierarchy(){
		StringBuffer result = new StringBuffer("HIERARCHY\n");
		result.append("ROOT "); result.append(name);
		result.append("\n{\n");
		result.append("\tOFFSET ");
		result.append(offset[0]+" "); result.append(offset[1]+" "); result.append(offset[2]+"\n"); 
		result.append("CHANNELS 6 Xposition Yposition Zposition Xrotation Yrotation Zrotation\n");
		
		int size = children.size();
		for(int i=0;i<size-1;i++){
			Joint j = (Joint)(children.get(i));
			result.append(j.expHierarchy("\t"));
			result.append("\n");
		}
		Joint j = (Joint)(children.get(size-1));
		result.append(j.expHierarchy("\t"));
		result.append("\n}");
		
		return result.toString();
	}
	
	public String expMotionData(){
		StringBuffer result = new StringBuffer();
		
		for(int i=0;i<numOfFrames-1;i++){
			result.append(expMotionData(i));
			result.append("\n");
		}
		result.append(expMotionData(numOfFrames-1));
		
		return result.toString();
	}
	
	public String expMotionData(int frame){
		StringBuffer result = new StringBuffer();
		float[] euler = quat[frame].getEuler();
		result.append(position[frame][0]+" "+position[frame][1]+" "+position[frame][2]);
		result.append(" "+euler[0]+" "+euler[1]+" "+euler[2]+" ");
		
		int size = children.size();
		for(int i=0;i<size;i++){
			Joint j = (Joint)(children.get(i));
			result.append(j.expMotionData(frame));
		}
		
		return result.toString();
	}
}
