package controller.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import controller.SimController;
import model.SimModel;

/**
 * Enables the user to reset the robot to its start position.
 * 
 * @author 150021237
 *
 */
public class ResetTool extends Tool {

	private static ResetTool instance = null;
	private JButton resetButton = new JButton("Reset");

	private ResetTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static ResetTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new ResetTool(model, controller);
		return instance;
	}

	@Override
	public JComponent getComponent() {
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		return resetButton;
	}

	public void reset() {
		model.removePellets();
		model.getRobot().setAngle(model.getStartAngle());
		model.getRobot().setCenter(model.getStart());
		model.getRobot().resetStepCount();
		model.getRobot().resetLengthCount();
	}
}
