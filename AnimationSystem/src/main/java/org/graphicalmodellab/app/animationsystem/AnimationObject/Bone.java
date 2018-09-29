package org.graphicalmodellab.app.animationsystem.AnimationObject;


import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Represents a bone to construct a figure of BVH
 * @author itoumao
 *
 */
public class Bone extends Obj {
  public static final float RED[] = { 0.8f, 0.2f, 0.2f, 1.0f };
  public static final float BLUE[] = { 0.2f, 0.8f, 0.2f, 1.0f };
  
  /* Two end points of the bone */
  protected double x0, y0, z0;
  protected double x1, y1, z1;
 
  
  
  public Bone(double x0, double y0, double z0, double x1, double y1, double z1) {
    this.x0 = x0; this.y0 = y0; this.z0 = z0;
    this.x1 = x1; this.y1 = y1; this.z1 = z1;
  }
  
  public void Draw(float[] color,GLAutoDrawable drawable){
	   	GL2 gl = drawable.getGL().getGL2();
	  	GLU glu = new GLU();

		gl.glPushMatrix();
	  	
		/** move to the start point of the bone **/
		gl.glTranslated( x0, y0, z0 );
	
		/** direction **/
		double dir_x = x1-x0;
		double dir_y = y1-y0;
		double dir_z = z1-z0;
		
		/** length of a bone **/
		double len = Math.sqrt(dir_x*dir_x+dir_y*dir_y+dir_z*dir_z);
		
		/** Normalize the direction **/
		dir_x /= len;
		dir_y /= len;
		dir_z /= len;
	
		/** Rotate the bone based on the cross product of "dir" & (0,0,1) **/
		gl.glRotated(Math.acos(dir_z)*180/Math.PI, -dir_y, dir_x, 0);
		
		/** Set color info **/
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, color, 0);
		/** Set up the configuration of the bone **/
		GLUquadric bone = glu.gluNewQuadric();
		
		/* The radius of the bone */
		double radius= 0.4; 
		/* the number of slices for the bone */
		int slices = 8;
		int stack = 3;
		
		glu.gluQuadricTexture(bone, false);
		glu.gluQuadricDrawStyle(bone, GLU.GLU_FILL);
		glu.gluQuadricNormals(bone, GLU.GLU_SMOOTH);
		
		/** Then, draw the bone **/
		glu.gluCylinder(bone, radius, radius, len, slices, stack ); 
		
		gl.glPopMatrix();
  }
}