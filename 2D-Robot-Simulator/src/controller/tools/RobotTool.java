package controller.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.vividsolutions.jts.geom.Coordinate;

import controller.SimController;
import model.Robot;
import model.SimModel;
import utils.SimMath;

/**
 * Enables the user to place a robot on the render component.
 * 
 * @author 150021237
 *
 */
public class RobotTool extends Tool {

	public static RobotTool instance = null;
	private JButton robotBtn = new JButton("Robot");

	private RobotTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static RobotTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new RobotTool(model, controller);
		return instance;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Coordinate mouseClick = new Coordinate(e.getX(), e.getY());
		setRobotAngle(mouseClick);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		model.setRobot(new Robot(e.getX(), e.getY(), 0, model, false));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		setRobotAngle(new Coordinate(e.getX(), e.getY()));
	}

	private void setRobotAngle(Coordinate mouseClick) {
		Robot curr = model.getRobot();
		if (curr != null) {
			double angle = SimMath.getAngleDeg(curr.getCenter(), mouseClick);
			model.setRobot(new Robot(curr.getCenter().x, curr.getCenter().y, angle, model, false));
		}
	}

	@Override
	public JComponent getComponent() {
		robotBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setTool(RobotTool.this);
			}
		});

		return robotBtn;
	}
}
