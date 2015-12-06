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

/**
 * Enables the user to place moving obstacles in form of red circles.
 * 
 * @author 150021237
 *
 */
public class MovingObstacleTool extends Tool {

	private static MovingObstacleTool instance = null;
	private JButton obstacleButton = new JButton("MovingBall");

	private MovingObstacleTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static MovingObstacleTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new MovingObstacleTool(model, controller);
		return instance;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Coordinate mouseClick = new Coordinate(e.getX(), e.getY());
		Robot r = new Robot(mouseClick.x+Robot.robotSize, mouseClick.y+Robot.robotSize, 0.0, model, true);
		model.addMovingObstacle(r);
	}

	@Override
	public JComponent getComponent() {
		obstacleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setTool(MovingObstacleTool.this);
			}
		});
		return obstacleButton;
	}
}
