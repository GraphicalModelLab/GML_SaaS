package org.graphicalmodellab.app.animationsystem.AnimationObject;

/**
 * http://www.euclideanspace.com/ is referred a lot.
 * @author itoumao
 *
 */
public class Quaternion {
	public float x,y,z,w;
	
	public static void main(String[] args){
		Quaternion q = new Quaternion(10,30,59);
		
		float[] euler = q.getEuler();
		System.out.println(euler[0]+"<>"+euler[1]+"<>"+euler[2]);
	}
	
	/**
	 * Construct Quaternion by Euler angles
	 * @param x, degree (not radian)
	 * @param y, degree (not radian)
	 * @param z, degree (not radian)
	 */
	public Quaternion(double x, double y, double z){
		x = x*Math.PI/180;
		y = y*Math.PI/180;
		z = z*Math.PI/180;
		
		double c1 = Math.cos(y/2);
		double c2 = Math.cos(z/2);
		double c3 = Math.cos(x/2);
		double s1 = Math.sin(y/2);
		double s2 = Math.sin(z/2);
		double s3 = Math.sin(x/2);
		
		this.w = (float)(c1*c2*c3-s1*s2*s3);
		this.x = (float)(s1*s2*c3+c1*c2*s3);
		this.y = (float)(s1*c2*c3+c1*s2*s3);
		this.z = (float)(c1*s2*c3-s1*c2*s3);
		
	}
	
	public Quaternion(){
		
	}
	
	public Quaternion clone(){
		Quaternion quat = new Quaternion();
		quat.x = this.x;
		quat.y = this.y;
		quat.z = this.z;
		
		return quat;
	}
	public void setEuler(double x, double y, double z){
		x = x*Math.PI/180;
		y = y*Math.PI/180;
		z = z*Math.PI/180;
		
		double c1 = Math.cos(y/2);
		double c2 = Math.cos(z/2);
		double c3 = Math.cos(x/2);
		double s1 = Math.sin(y/2);
		double s2 = Math.sin(z/2);
		double s3 = Math.sin(x/2);
		
		this.w = (float)(c1*c2*c3-s1*s2*s3);
		this.x = (float)(c1*c2*s3+s1*s2*c3);
		this.y = (float)(s1*c2*c3+c1*s2*s3);
		this.z = (float)(c1*s2*c3-s1*c2*s3);
	}
	public float[] getEuler(){
		float[] angles = new float[3];
		
		angles[0] = (float)Math.atan2(2*x*w-2*y*z, 1-2*x*x-2*z*z);
		angles[1] = (float)Math.atan2(2*y*w-2*x*z, 1-2*y*y-2*z*z);
		
		double temp = 2*x*y+2*z*w;
		if(temp > 1){
			int ang = ((int)temp)*90;
			ang = ang%360;
			
			angles[2] = (float)Math.asin(temp - (int)temp) + ang;
		}else if(temp < -1){
			int ang = -((int)temp)*90;
			ang = ang%360;
			
			angles[2] = (float)Math.asin(temp - (int)temp) - ang;
		}else{
			angles[2] = (float)Math.asin(2*x*y+2*z*w);
		}
		
		if(x*y+z*w == 0.5){
			x = (float)(2*Math.atan2(x, w));
			z = 0;
		}else if(x*y + z*w == -0.5){
			x = (float)(-2*Math.atan2(x, w));
			z = 0;
		}
		
		angles[0] = (float)(180*angles[0]/Math.PI);
		angles[1] = (float)(180*angles[1]/Math.PI);
		angles[2] = (float)(180*angles[2]/Math.PI);
		return angles;
	}
	
	/**
	 * return 4 by 4 rotation Matrix
	 * @return
	 */
	public float[] toRotationMatrix(){
		float[] result = new float[16];
		float xx = x*x;
		float xy = x*y;
		float xz = x*z;
		float xw = x*w;
		float yy = y*y;
		float yz = y*z;
		float yw = y*w;
		float zz = z*z;
		float zw = z*w;
		
		result[0] = 1-2*(yy+zz);
		result[4] = 2*(xy-zw);
		result[8] = 2*(xz+yw);
		
		result[1] = 2*(xy+zw);
		result[5] = 1-2*(xx+zz);
		result[9] = 2*(yz - xw);
		
		result[2] = 2*(xz-yw);
		result[6] = 2*(yz+xw);
		result[10] = 1-2*(xx+yy);
		
		result[15] = 1;
		
		return result;
	}
	
	/** Arithmetic Operation **/
	public void normalize(){
		double n = Math.sqrt(x*x+y*y+z*z+w*w);
		x /= n;
		y /= n;
		z /= n;
		w /= n;
		
	}
	
	/**
	 * Multi "q", i.e. rotate, such that this =  this * q
	 * @param q
	 */
	public void mul(Quaternion q) {
		float temp_x,temp_y,temp_z,temp_w;
		
		temp_x =  x * q.w + y * q.z - z * q.y + w * q.x;
	    temp_y = -x * q.z + y * q.w + z * q.x + w * q.y;
	    temp_z =  x * q.y - y * q.x + z * q.w + w * q.z;
	    temp_w = -x * q.x - y * q.y - z * q.z + w * q.w;
	    
	    x = temp_x;
	    y = temp_y;
	    z = temp_z;
	    w = temp_w;
	}
	
	public void add(Quaternion q) {
	    x = x + q.x;
	    y = y + q.y;
	    z = z + q.z;
	    w = w + q.w;
	}
	
	public void setQuat(Quaternion q){
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;
	}
	
	public static Quaternion axisAngle(double degree, double x, double y, double z){
		Quaternion axis = new Quaternion();
		
		double sn = Math.sin(degree/2);
		double cs = Math.cos(degree/2);
		
		axis.x = (float)(x*sn);
		axis.y = (float)(y*sn);
		axis.z = (float)(z*sn);
		axis.w = (float)cs;
		axis.normalize();
		return axis;
	}
}
