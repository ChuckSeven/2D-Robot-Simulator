package controller.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.vividsolutions.jts.geom.Coordinate;

import controller.SimController;
import model.SimModel;

/**
 * Enables the user to set a goal on the render component.
 * 
 * @author 150021237
 *
 */
public class GoalTool extends Tool {

	private static GoalTool instance = null;
	private JButton goalBtn = new JButton("Goal");

	private GoalTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static GoalTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new GoalTool(model, controller);
		return instance;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Coordinate mouseClick = new Coordinate(e.getX(), e.getY());
		model.setGoal(mouseClick);
	}

	@Override
	public JComponent getComponent() {

		goalBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setTool(GoalTool.this);
			}
		});

		return goalBtn;
	}

}
