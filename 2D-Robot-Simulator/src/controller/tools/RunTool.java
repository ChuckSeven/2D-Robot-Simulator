package controller.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import controller.SimController;
import model.SimModel;

/**
 * Enables the user to start the robot and watch how it reacts to different
 * obstacles.
 * 
 * @author 150021237
 *
 */
public class RunTool extends Tool {

	private static RunTool instance = null;
	private JButton startStopButton = new JButton("Start");

	private RunTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static RunTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new RunTool(model, controller);
		return instance;
	}

	@Override
	public JComponent getComponent() {
		startStopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				toggle();
			}
		});
		return startStopButton;
	}

	public void toggle() {
		if (controller.isRunning()) {
			stop();
		} else {
			start();
		}
	}

	public void stop() {
		controller.setNotRunning();
		RunTool.this.startStopButton.setText("Start");
		RunTool.this.startStopButton.repaint();
	}

	public void start() {
		controller.start();
		RunTool.this.startStopButton.setText("Stop");
		RunTool.this.startStopButton.repaint();
	}
}
