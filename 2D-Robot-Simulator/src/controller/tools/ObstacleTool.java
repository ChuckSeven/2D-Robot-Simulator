package controller.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import controller.SimController;
import model.SimModel;

/**
 * Enables the user to place obstacles in form of squares.
 * @author 150021237
 *
 */
public class ObstacleTool extends Tool {

	private static ObstacleTool instance = null;
	private JButton obstacleButton = new JButton("Square");
	
	private ObstacleTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static ObstacleTool getInstance(SimModel model, SimController controller) {
		if(instance == null) 
			instance = new ObstacleTool(model, controller);
		return instance;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		Coordinate mouseClick = new Coordinate(e.getX(), e.getY());
		GeometricShapeFactory gsf = new GeometricShapeFactory();
		gsf.setSize(50);
		gsf.setNumPoints(100);
		gsf.setBase(new Coordinate(mouseClick.x - 25, mouseClick.y - 25));
		model.addObstacle(gsf.createRectangle());
	}

	@Override
	public JComponent getComponent() {
		obstacleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setTool(ObstacleTool.this);
			}
		});
		return obstacleButton;
	}
}
