package controller.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

import controller.SimController;
import model.Robot;
import model.SimModel;

/**
 * Enables the user to save the current configuration of the world in a file.
 * This file can the be loaded with the MapTool.
 * 
 * @author 150021237
 *
 */
public class SaveTool extends Tool {

	private static SaveTool instance = null;
	private JButton saveButton = new JButton("Save");
	public static final String ENDING = "map";

	private SaveTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static SaveTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new SaveTool(model, controller);
		return instance;
	}

	@Override
	public JComponent getComponent() {
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ResetTool.getInstance(model, controller).reset();
				serialize();
			}
		});
		return saveButton;
	}

	public void serialize() {

		String name;
		name = JOptionPane.showInputDialog("File name");
		if(name == null) return;
		DataObject o = new DataObject();
		o.obstacles = model.getObstacles();
		o.movingObstacles = new ArrayList<MoveableObstacle>();
		for(Robot r : model.getMovingObstacles()) {
			o.movingObstacles.add(new MoveableObstacle(r.getCenter().x, r.getCenter().y,r.getvLeft(),r.getvRight()));
		}
		o.start = model.getStart();
		o.angle = model.getStartAngle();
		o.goal = model.getGoal();

		// Write
		FileOutputStream fout;
		try {
			if (new File(name + "." + ENDING).exists()) {
				controller.setLabel2("File already exists.");
				return;
			}

			fout = new FileOutputStream(name + "." + ENDING);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(o);
			oos.close();
			controller.setLabel2("Saved as " + name + "." + ENDING);
		} catch (IOException e) {
			controller.setLabel2("IO Error");
//			e.printStackTrace();
		}

	}

	static class DataObject implements Serializable {
		private static final long serialVersionUID = 7037422349354030163L;
		public List<Polygon> obstacles;
		public List<MoveableObstacle> movingObstacles;
		public Coordinate start;
		public double angle;
		public Coordinate goal;
	}
	static class MoveableObstacle implements Serializable{
		private static final long serialVersionUID = -6113810916351278659L;
		public double x, y, vl, vr;
		public MoveableObstacle(double x, double y, double vl, double vr) {
			this.x = x;
			this.y = y;
			this.vl = vl;
			this.vr = vr;
		}
	}
	
}
