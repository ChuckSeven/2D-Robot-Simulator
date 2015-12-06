package controller.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.SimController;
import model.SimModel;

/**
 * Enables the user to reset the robot to its start position.
 * 
 * @author 150021237
 *
 */
public class SizeTool extends Tool {

	private static SizeTool instance = null;
	private JLabel label = new JLabel("Size:");
	private JPanel btnPanel = new JPanel();
	private JButton incSizeBtn = new JButton("+");
	private JButton decSizeBtn = new JButton("-");

	private SizeTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static SizeTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new SizeTool(model, controller);
		return instance;
	}

	@Override
	public JComponent getComponent() {
		incSizeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				incSize();
			}
		});
		decSizeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				decSize();
			}
		});
		
		btnPanel.add(decSizeBtn);
		btnPanel.add(incSizeBtn);
		
		Box b = new Box(BoxLayout.Y_AXIS);
		b.add(label);
		b.add(btnPanel);
		return b;
	}

	public void incSize() {
		model.getRobot().incRobotSize();
	}
	
	public void decSize() {
		model.getRobot().decRobotSize();
	}
}
