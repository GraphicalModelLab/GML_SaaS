package org.graphicalmodellab.app.animationsystem.AnimationObject;


import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

public class Ground extends Obj{

  public static final float TILE_BLACK[][] = {
	  	{ 0.3f, 0.3f, 0.3f, 1.0f },
	  	{ 0.5f, 0.5f, 0.5f, 1.0f }
  };	
  public static final float TILE_RED[][] = {
	    { 0.3f, 0.0f, 0.0f, 1.0f },
	    { 0.5f, 0.0f, 0.0f, 1.0f }
  };
  public static final float TILE_BLUE[][] = {
	    { 0.0f, 0.0f, 0.3f, 1.0f },
	    { 0.0f, 0.0f, 0.5f, 1.0f }
  };
  
  /* Tile Color */
  private float[][] tile_color;
  
  /* Number of Tiles */
  private int numOfTiles;
  
  /**
   * Constructor
   * @param tile_color
   * @param numOfTiles
   */
  public Ground(float[][] tile_color, int numOfTiles){
	  this.tile_color = tile_color;
	  this.numOfTiles = numOfTiles;
  }
  
  /**
   * Display tiles as ground
   * @param drawable
   */
  public void Draw(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    gl.glPushMatrix();
    
    gl.glBegin(GL2.GL_QUADS);
    gl.glNormal3d(0.0, 1.0, 0.0);
    int len = 1;
    float height = -0f;
    for (int i = -numOfTiles; i <= numOfTiles; ++i) {
      for (int j = -numOfTiles; j < numOfTiles; ++j) {
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, this.tile_color[Math.abs((j+i) % 2)], 0);
        gl.glVertex3d(j, height, i);
        gl.glVertex3d(j, height, i + len);
        gl.glVertex3d(j + len, height, i + len);
        gl.glVertex3d(j + len, height, i);
      }
    }
    gl.glEnd();
    gl.glPopMatrix();
  }
}