package org.graphicalmodellab.app.animationsystem.IKSolver;
import java.util.Iterator;

import org.graphicalmodellab.app.animationsystem.AnimationObject.Joint;
import org.graphicalmodellab.app.animationsystem.AnimationObject.Quaternion;

public class CCD {
	/**
	 * 
	 * @param startJoint
	 * @param endJointName
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void solve(int frame, Joint startJoint, String endJointName, double x, double y, double z){
		 
		 if(!(endJointName.equals(startJoint.name))){
			 /** Go down to the children and draw them **/
			 Iterator iterator = startJoint.children.iterator();
			 while(iterator.hasNext()){
			    	Joint child = (Joint)(iterator.next());
			    	solve(frame,child,endJointName,x,y,z);
			 }
		 }
		 
		 /** Change the parent joint later **/
		 float[] offset = startJoint.offset;
		 double cross_x = offset[1]*z - offset[2]*y;
		 double cross_y = offset[2]*x - offset[0]*z;
		 double cross_z = offset[0]*y - offset[1]*x;
		 
		 double temp = (offset[0]*x+offset[1]*y+offset[2]*z)/(Math.sqrt(offset[0]*offset[0]+offset[1]*offset[1]+offset[2]*offset[2])*Math.sqrt(x*x+y*y+z*z));
		 double theta = Math.acos(temp);
		 
		 Quaternion axis = Quaternion.axisAngle(theta, cross_x, cross_y, cross_z);
		 startJoint.quat[frame].mul(axis);
		 startJoint.quat[frame].normalize();
	}
	
}
