package org.graphicalmodellab.app.animationsystem.AnimationObject;

import javax.media.opengl.GLAutoDrawable;

public abstract class Drawable {
	public abstract void positionAtFrame(int frame, GLAutoDrawable drawable);
	public abstract void positionAtFrameWithStop(int frame, GLAutoDrawable drawable);
	public abstract void initDrawPosition();
	public abstract int getNumOfFrames();
	public abstract void transitionToLinearPath(double theta);
	public abstract void upScale(float scale);
	public abstract void downScale(float down);
	public abstract void setScale(float down);
}
