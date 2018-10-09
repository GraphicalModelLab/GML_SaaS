package org.graphicalmodellab.app.animationsystem;

import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.swing.*;

//import com.jogamp.opengl.aw
import javax.media.opengl.awt.GLCanvas;
//import com.jogamp.opengl.glu.GLU;
import java.util.*;

//import IMSL.SemanticAnalysis;
//import IMSL.result.AnimationObject;
//import IMSL.result.Null;

import com.jogamp.opengl.util.Animator;
import org.graphicalmodellab.app.animationsystem.AnimationObject.*;

public class MotionViewer extends JFrame implements GLEventListener, ActionListener, KeyListener {
  /* light position */
  private float LIGHT_POSITION[] = { 3.0f, 4.0f, 5.0f, 1.0f };
  private Animator animator;

  /* Initial Frame Position */
  private final int INITIAL_FRAME_POSITION = 1;
  
  /* Characters */
  private Drawable[] characters;
 
  /* Script */
  private HashMap<Integer,String> scriptMap;
  
  /* My Perspective Camera */
  private MyCamera camera = new MyPerspectiveCamera(19.5,16.0/9.0,1,50);
  private double xWinTheta = 90;
  private double yWinTheta = 0;
  private double zWinTheta = 0;
  private float camX = 24f;
  private float camY = -4.0f;
  private float camZ = 2.0f;
  
  /* The current frame of the animation */
  private int currentFrame;
  
  /* The total number of frames */
  private int numOfFrame;
  
  /* milli second, so multiplied by 100 */
  private int frameTime;
  
  /* Ground where characters are making actions */
  private Ground ground = new Ground(Ground.TILE_BLACK,50);
 
  /* 3D canvas */
  GLCanvas canvas;
  
  /* Path Changing */
  private double theta = 810;
  
  /* Menu Items */
  MenuItem frame_rate;
  MenuItem motion_add;
  MenuItem character_new;
  MenuItem saveAsBVH;
  MenuItem PLeditor;
  MenuItem script_add;
  MenuItem control_show;
  
  
  /** Stop flag **/
  private boolean nostop = true;
  public MotionViewer(TheCharacter[] fms, int numOfFrame, int frameTime){
	    this.characters = fms;
	    this.initMotionViewer(numOfFrame, frameTime);
  }
  
  public MotionViewer(int numOfFrame, int frameTime){
	   	this.characters = new TheCharacter[0];
	    this.initMotionViewer(numOfFrame, frameTime);
  }
  private void initMotionViewer(int numOfFrame, int frameTime){
	    this.setTitle("Motion Capture");
	    this.currentFrame = INITIAL_FRAME_POSITION;
	    this.numOfFrame = numOfFrame;
	    this.frameTime = frameTime;
	   
	    this.scriptMap = new HashMap<Integer,String>();
	   
	    MenuBar menubar = new MenuBar();
	    
	    /** Frame rate menu **/
	    Menu frame = new Menu("Frame");
	    frame_rate = new MenuItem("Change rate");
	    frame_rate.addActionListener(this);
	    frame.add(frame_rate);
	    menubar.add(frame);
	    
	    /** Motion menu **/
	    Menu motion = new Menu("Motion");
	    motion_add = new MenuItem("Add a motion to the existing motions");
	    motion_add.addActionListener(this);
	    motion.add(motion_add);
	    menubar.add(motion);
	    
	    /** Character menu **/
	    Menu character = new Menu("Character");
	    this.character_new = new MenuItem("Create a new character");
	    character_new.addActionListener(this);
	    character.add(character_new);
	    menubar.add(character);
	    
	    
	    /** Save BVH menu **/
	    Menu bvh = new Menu("BVH");
	    this.saveAsBVH = new MenuItem("Save as BVH");
	    this.saveAsBVH.addActionListener(this);
	    bvh.add(this.saveAsBVH);
	    menubar.add(bvh);
	    
	    /** Programming Menu **/
	    Menu PL = new Menu("Programming");
	    this.PLeditor = new MenuItem("Open Editor");
	    this.PLeditor.addActionListener(this);
	    PL.add(this.PLeditor);
	    menubar.add(PL);
	    
	    /** Script Menu **/
	    Menu script = new Menu("Script");
	    this.script_add = new MenuItem("Add a script");
	    this.script_add.addActionListener(this);
	    script.add(this.script_add);
	    menubar.add(script);
	    
	    /** Control Menu **/
	    Menu control = new Menu("Control");
	    this.control_show = new MenuItem("Show a controller");
	    this.control_show.addActionListener(this);
	    control.add(this.control_show);
	    menubar.add(control);

	    canvas = new GLCanvas();
	    canvas.addGLEventListener(this);
	    canvas.addKeyListener(this);
	    this.add(canvas);

	    this.setMenuBar(menubar);
	    this.setSize(500, 500);
	    this.clearAllObj();
	    
	    /** Set Animator **/
	    animator = new Animator(canvas);
	    animator.start();
	    this.addWindowListener(new WindowAdapter() {
	      public void windowClosing(WindowEvent e) {
	        animator.stop();
	        System.exit(0);
	      }
	    });
	    this.setVisible(true); 
  }
  
  public void init(GLAutoDrawable drawable){
    GL2 gl = drawable.getGL().getGL2();
    gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_CULL_FACE);
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);
    gl.glEnable(GL2.GL_NORMALIZE);
  }

	public void dispose(GLAutoDrawable glAutoDrawable) {

	}

	public void display(GLAutoDrawable drawable){
    GL2 gl = drawable.getGL().getGL2();
    
    /** Clear buffer **/
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    
    /** set up the camera **/
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    
    camera = new MyPerspectiveCamera(19.5,16.0/9.0,1,50);   
	camera.rotate(xWinTheta,0,1,0);
	camera.rotate(yWinTheta,1,0,0);
	camera.rotate(zWinTheta,0,0,1);
	camera.translate(camX,camY,camZ);
	
	camera.setup(drawable);
	
    gl.glLoadIdentity();

    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, LIGHT_POSITION, 0);
   
    /** Start drawing **/
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    
    /** display the ground **/
    ground.Draw(drawable);
    
    if(nostop){
    	/** display the motion captured data **/
    	for(int i=0;i<this.characters.length;i++){
    		/** display the character at the specific Frame **/
    		this.characters[i].positionAtFrame(this.currentFrame, drawable);
    	}
  
    	/** execute script **/
    	if(this.scriptMap.containsKey(this.currentFrame)){
    		this.executeScriptLanguage(this.scriptMap.get(this.currentFrame));
    	}
    	
    	/** go to the next frame **/
    	this.currentFrame++;
    	if(this.currentFrame >= this.numOfFrame){
        	this.currentFrame = INITIAL_FRAME_POSITION;
        }
    }else{
    	/** display the motion captured data **/
    	for(int i=0;i<this.characters.length;i++){
    		/** display the character at the specific Frame **/
    		this.characters[i].positionAtFrameWithStop(this.currentFrame, drawable);
    	}
    }
    
    /** Key Framing **/
    try {
      Thread.sleep(this.frameTime);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
    /** corresponding to glutSwapBuffers **/
    gl.glFlush();
  }
  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
    boolean deviceChanged){

  }
  public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
  }
  
  public void actionPerformed(ActionEvent e){
		Object obj = e.getSource();
		if(obj == this.frame_rate){
			System.out.println("Frame rater changed");
			FrameChangeWindow changer = new FrameChangeWindow(this,this.frameTime);
		}else if(obj == this.motion_add){
			new AddMotionWindow(this.characters.length, this);
		}else if(obj == this.character_new){
			new AddNewCharacterWindow(this);
		}else if(obj == this.saveAsBVH){
			new WriteBVHWindow(this.characters.length,this);
		}else if(obj == this.PLeditor){
			new ProgrammingWindow(this,500,600);
		}else if(obj == this.script_add){
			new AddScriptWindow(this);
		}else if(obj == this.control_show){
			new ControlWindow(this);
		}
	}
    public void clearAllObj(){
    	this.characters = new TheCharacter[0];
    }
    public Drawable[] getCharacters(){
    	return this.characters;
    }
    public void setCharacters(Drawable[] seq){
    	this.characters = seq;
    }
    public int getNumOfFrame(){
    	return this.numOfFrame;
    }
    public void setNumOfFrame(int numOfFrame){
  	  this.numOfFrame = numOfFrame;
  	  this.currentFrame = INITIAL_FRAME_POSITION;
    }
    public void setFrameTime(int frameTime){
  	  this.frameTime = frameTime;
    }
    
    public MyCamera getCamera(){
    	return this.camera;
    }
    
    /** Key Events **/
    public void keyPressed(KeyEvent e){
       
    }

    public void keyReleased(KeyEvent e){
    }

    public void keyTyped(KeyEvent e){
        char key = e.getKeyChar();
    	System.out.println("type "+key);
        switch(key){
        case 'r':
        	
			xWinTheta = yWinTheta=zWinTheta=0;
			xWinTheta = 90;
			camX = 24f;
			camY = -4.0f;
			camZ = 2.0f;
			for(int i=0;i<this.characters.length;i++){
				/** display the character at the specific Frame **/
			    this.characters[i].initDrawPosition();
			}
			break;
        case 'A':
		case 'a':
			camX +=1.0f;
			break;
		case 'd':
		case 'D':
			camX -=1.0f;
			break;
		case 's':
		case 'S':
			camZ -= 1.0f;
			break;
		case 'w':
		case 'W':
			camZ+=1.0f;
			break;
		case 'z':
		case 'Z':
			camY+=1.0f;
			break;
		case 'q':
		case 'Q':
			camY-=1.0f;
			break;
		case 'K':
		case 'k':
			xWinTheta -= 1;
			break;
		case ';':
		case ':':
			xWinTheta += 1;
			break;
		case 'o':
		case 'O':
			yWinTheta-=1;
			break;
		case 'l':
		case 'L':
			yWinTheta+=1;
			break;
		case '/':
		case '?':
			zWinTheta +=1;
			break;
		case '\'':
		case '"':
			zWinTheta -=1;
			break;
			
		case '[':
			for(int i=0;i<this.characters.length;i++){
				this.characters[i].downScale(0.01f);
			}
			break;
			
		case ']':
			for(int i=0;i<this.characters.length;i++){
				this.characters[i].upScale(0.01f);
			}
			
			break;		
		case 'p':
			this.nostop = !this.nostop;
			break;
		case 'u':
			theta += 10;
			for(int i=0;i<this.characters.length;i++){
				/** display the character at the specific Frame **/
			    this.characters[i].transitionToLinearPath(theta);
			}
			break;
		case 'y':
			theta -= 10;
			for(int i=0;i<this.characters.length;i++){
				/** display the character at the specific Frame **/
			    this.characters[i].transitionToLinearPath(theta);
			}
			break;
		case 'b':
			for(int i=0;i<this.characters.length;i++){
				/** display the character at the specific Frame **/
			    this.characters[i].initDrawPosition();
			}
			break;
        }
    }
    public void executeScriptLanguage(String file){
//    	SemanticAnalysis an = new SemanticAnalysis(this,file,"EUC-JP");
//		Thread bb = new Thread(an);
//	    an.main = an;
//	    bb.start();
    }
    public void addScript(String file, int frameNo){
    	this.scriptMap.put(frameNo, file);
    }
    
    public void addDrawable(Drawable added){
    	 /** Construct a new list of characters **/
		 Drawable[] seq = getCharacters();
		
		 added.setScale(0.04f);
		 int max_frame = Integer.MIN_VALUE;
		 Drawable[] new_seq = new Drawable[seq.length+1];
		 for(int i=0;i<seq.length;i++){
			 new_seq[i] = seq[i];
			 new_seq[i].initDrawPosition();
			 if(max_frame < new_seq[i].getNumOfFrames()){
				 max_frame = new_seq[i].getNumOfFrames();
			 }
		 }
		 new_seq[seq.length] = added;
		 if(max_frame < new_seq[seq.length].getNumOfFrames()){
			 max_frame = new_seq[seq.length].getNumOfFrames();
		 }
		 
		 /** set the new characters **/
		 setCharacters(new_seq);
		 
		 /** change the frame **/
		 setNumOfFrame(max_frame);
	}
    public int getCurrentFrame(){
    	return this.currentFrame;
    }
}