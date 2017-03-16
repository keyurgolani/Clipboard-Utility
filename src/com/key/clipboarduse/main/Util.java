package com.key.clipboarduse.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;


public class Util extends JFrame implements ActionListener, NativeKeyListener, NativeMouseInputListener, NativeMouseWheelListener, ItemListener, WindowListener {
	
	JTextArea textArea;
	boolean isWindowHold = false;
	boolean isCtrlHold = false;
	boolean isWinShiftHold = false;
	Stack items;
	int iterate = 0;

	public void nativeKeyPressed(NativeKeyEvent arg0) {
		if(arg0.getKeyCode() == NativeKeyEvent.VK_WINDOWS) {
			isWindowHold = true;
		} else if(arg0.getKeyCode() == NativeKeyEvent.VK_CONTROL) {
			isCtrlHold = true;
		} else if(arg0.getKeyCode() == NativeKeyEvent.VK_SHIFT) {
			if(isWindowHold) {
				this.setVisible(false);
				if(items.size() - iterate <= 0) {
					iterate = 1;
				} else {
					iterate++;
				}
				textArea.setText((String)items.get(items.size() - iterate));
				this.setVisible(true);
			}
		} else if(arg0.getKeyCode() == NativeKeyEvent.VK_E) {
			if(isWinShiftHold) {
				this.setVisible(false);
				System.exit(0);
			}
		}
		
	}
	
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		if(isWindowHold) {
			if(arg0.getKeyCode() == NativeKeyEvent.VK_WINDOWS) {
				isWindowHold = false;
				this.setVisible(false);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection((String)items.get(items.size() - iterate)), null);
				iterate = 0;
			} else if(arg0.getKeyCode() == NativeKeyEvent.VK_SHIFT) {
				isWinShiftHold = true;
			}
		} else if(isCtrlHold) {
			if(arg0.getKeyCode() == NativeKeyEvent.VK_C) {
				try {
					items.push(Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
					isCtrlHold = false;
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		
	}
	
	public void nativeMouseClicked(NativeMouseEvent arg0) {
		
	}
	
	public void nativeMouseDragged(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void nativeMouseMoved(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void nativeMousePressed(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void nativeMouseReleased(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void nativeMouseWheelMoved(NativeMouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public Util() {
		setTitle("Choose Your Clip to Paste or Press SHIFT for next Clip.");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(600, 300);
		setAlwaysOnTop(true);
		setLocation(Toolkit.getDefaultToolkit().getScreenSize().width/2 - 300, Toolkit.getDefaultToolkit().getScreenSize().height/2 - 150);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		setResizable(false);
		
		items = new Stack();
		
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(375, 125));
		add(scrollPane, BorderLayout.CENTER);
		
		try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.getInstance().addNativeKeyListener(this);
			GlobalScreen.getInstance().addNativeMouseListener(this);
			GlobalScreen.getInstance().addNativeMouseMotionListener(this);
			GlobalScreen.getInstance().addNativeMouseWheelListener(this);
		} catch (NativeHookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Util();
			}
		});
	}
	
}
