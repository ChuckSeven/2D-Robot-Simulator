package controller.tools;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import controller.SimController;
import model.SimModel;

/**
 * Abstract class so the tools don't need to implement unused methods.
 * 
 * @author 150021237
 *
 */
public abstract class Tool implements MouseListener, KeyListener, MouseMotionListener {

	SimModel model;
	SimController controller;

	Tool(SimModel model, SimController controller) {
		this.model = model;
		this.controller = controller;
	}

	public abstract JComponent getComponent();

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
