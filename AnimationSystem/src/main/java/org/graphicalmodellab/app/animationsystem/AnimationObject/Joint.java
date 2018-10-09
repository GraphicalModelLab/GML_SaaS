package org.graphicalmodellab.app.animationsystem.AnimationObject;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Joint {
	public String name;
	public float[] offset;
	public ArrayList<Joint> children;
	public Quaternion[] quat;
	

	//float[][] euler;
	
	public Joint(String name){
		this.name = name;
		children = new ArrayList<Joint>();
	}
	public void add(Joint child){
		this.children.add(child);
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
	public void Draw(int frame,float[] color,GLAutoDrawable drawable){
		    GL2 gl = drawable.getGL().getGL2();
		    
		    /** Remember the matrix **/
		    gl.glPushMatrix();
		    
		    /** Display the bone **/
		    
		    Obj bone = new Bone(0,0,0,offset[0],offset[1],offset[2]);
		    bone.Draw(color,drawable);
		    
		    /** Move the current bone into the appropriate position **/
	    	gl.glTranslatef(offset[0], offset[1],offset[2]);
	  
	    	/** Rotate **/
	    	gl.glMultMatrixf(quat[frame].toRotationMatrix(), 0);
	    	/*gl.glRotatef(euler[frame][0], 1.0f, .0f, 0f);
			gl.glRotatef(euler[frame][1], 0f, 1.0f, .0f);
			gl.glRotatef(euler[frame][2], .0f, 0f, 1f);
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
		
	public int setChannelInfo(ArrayList<String> channel, double[][] motion_data, int begin){
		String[] channel_string = channel.toArray(new String[channel.size()]);
		//euler = new float[motion_data.length][channel_string.length];
		quat = new Quaternion[motion_data.length];
		
		for(int j=0;j<motion_data.length;j++){
			float x=0,y=0,z=0;
			for(int i=0;i<channel_string.length;i++){
				if(channel_string[i].equals("Xrotation")){
					x = (float)motion_data[j][begin+i];
				}else if(channel_string[i].equals("Yrotation")){
					y = (float)motion_data[j][begin+i];
				}else if(channel_string[i].equals("Zrotation")){
					z = (float)motion_data[j][begin+i];
				}
			}
			
			/*
			euler[j][0] = x;
			euler[j][1] = y;
			euler[j][2] = z;
			*/
			
			quat[j] = new Quaternion(x,y,z);
			
			/*if(j == 108){
				System.out.println(x+"<>"+y+"<>"+z);
			}*/
		}
		
		
		return channel_string.length;
	}
	public void setOffset(double[] nums){
		this.offset = new float[nums.length];
		for(int i=0;i<nums.length;i++){
			this.offset[i] = (float)nums[i];
		}
	}
	
	
	/** For writing BVH file back **/
	public String expHierarchy(String space){
		StringBuffer result = new StringBuffer("");
		result.append(space+"JOINT "); result.append(name);
		result.append("\n"+space+"{\n");
		result.append(space+"OFFSET ");
		result.append(offset[0]+" "); result.append(offset[1]+" "); result.append(offset[2]+"\n"); 
		result.append(space+"CHANNELS 3 Xrotation Yrotation Zrotation\n");
		
		int size = children.size();
		for(int i=0;i<size-1;i++){
			Joint j = (Joint)(children.get(i));
			result.append(j.expHierarchy(space+"\t"));
			result.append("\n");
		}
		Joint j = (Joint)(children.get(size-1));
		result.append(j.expHierarchy(space+"\t"));
		result.append("\n"+space+"}");
		
		return result.toString();
	}
	
	public String expMotionData(int frame){
		StringBuffer result = new StringBuffer();
		float[] euler = quat[frame].getEuler();
		//float[] euler = this.euler[frame];
		result.append(euler[0]+" "+euler[1]+" "+euler[2]+" ");
		
		int size = children.size();
		for(int i=0;i<size;i++){
			Joint j = (Joint)(children.get(i));
			result.append(j.expMotionData(frame));
		}
		
		return result.toString();
	}
}
