package org.graphicalmodellab.app.animationsystem;
//import IMSL.SemanticAnalysis;
import java.io.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class ProgrammingWindow extends JFrame implements ActionListener{
	MotionViewer parent;
	//Program
	JTextArea program;
	int font_size = 15;
	int row;
	int col;
	//Error
	JTextArea error;
	//Menu
	//Menu
	JMenuBar mbar;
	JMenu run,file;
	JMenuItem execute; // JMenu run
	JMenuItem save_as,save,open; //JMenu file
	//file
	File f;
	String encording;
	JFileChooser fmanager;
	public ProgrammingWindow(MotionViewer parent,int width,int height){
		this.parent = parent;
		// init --start---
		this.row = 20;
		this.col = 40;
		Font f = new Font("Serif", Font.PLAIN,this.font_size);
		encording = "EUC-JP";
		setSize(width,height);
        
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setLayout(new FlowLayout());
		// init --end-----
		
		Container content = this.getContentPane();
		
		JPanel whole = new JPanel();
		whole.setLayout(new BoxLayout(whole,BoxLayout.Y_AXIS));
		
		
		// A textarea for program
		program = new JTextArea(this.row,this.col);
		program.setFont(f);
		program.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(program);
		whole.add(scroll);

		
		JLabel l = new JLabel("Messages and Errors");
	    whole.add(l);
		
		error = new JTextArea(10,col);
		error.setEditable(false);
		
		JScrollPane scroll_error = new JScrollPane(error);
		whole.add(scroll_error);
		
		content.add(whole);
		
		mbar = new JMenuBar();
		
		// JMenu run 
  		run = new JMenu("run_ program");
		execute = new JMenuItem("execute");
		execute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        execute.addActionListener(this);
		run.add(execute);
		
		mbar.add(run);
		
		 // JMenu file
        file = new JMenu("file");

		open = new JMenuItem("open");
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        file.add(open);
        open.addActionListener(this);
        save_as = new JMenuItem("save as...");
        save_as.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        file.add(save_as);
        save_as.addActionListener(this);
        save = new JMenuItem("save");
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        file.add(save);
        save.addActionListener(this);

        mbar.add(file);
		//create JMenu
		setJMenuBar(mbar);

		
		
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e){
		JMenuItem itm = (JMenuItem)e.getSource();
		if(itm == execute){
			clear_error();
//			SemanticAnalysis an = new SemanticAnalysis(this.parent,f.getPath(),encording);
//			an.error = error;
//			Thread bb = new Thread(an);
//		    an.main = an;
//		    bb.start();
		}else if(itm == save_as){
			fmanager = new JFileChooser();
			int selected = fmanager.showSaveDialog(this);
			if (selected == JFileChooser.APPROVE_OPTION){
			      f = fmanager.getSelectedFile();
			}else if (selected == JFileChooser.CANCEL_OPTION){
				  return;
			}else if (selected == JFileChooser.ERROR_OPTION){
				  return;
			}
			fmanager.setVisible(true);
			setTitle(f.getName());
			file_write();
		}else if(itm == save){
			file_write();
		}else if(itm == open){
			fmanager = new JFileChooser();
			int selected = fmanager.showOpenDialog(this);
			if (selected == JFileChooser.APPROVE_OPTION){
			      f = fmanager.getSelectedFile();
			}else if (selected == JFileChooser.CANCEL_OPTION){
				  return;
			}else if (selected == JFileChooser.ERROR_OPTION){
				  return;
			}
			fmanager.setVisible(true);
			setTitle(f.getName());
			file_read();
		}
	}
	public void file_write(){
		   try{
			FileOutputStream fos = new FileOutputStream(f);
			OutputStreamWriter out = new OutputStreamWriter(fos, encording);
			out.write(program.getText(),0,(program.getText()).length());

			out.close();
			fos.close();
                   }catch(IOException ie){
                                System.out.println("output error!");
                   }
	}
	public void file_read(){
		try{
		FileInputStream fis  = new FileInputStream(f);
		InputStreamReader in = new InputStreamReader(fis, encording);
		
		String p = "";
		int c;
		while ((c = in.read()) != -1) {
			p += (char)c;
		}
		program.setText(p);

		in.close();
		fis.close();
		}catch(IOException ie){
                                System.out.println("output error!");
                }
	}
	public void clear_error(){
		 error.setText("");
	}

}