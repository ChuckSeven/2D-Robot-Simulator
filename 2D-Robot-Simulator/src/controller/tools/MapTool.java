package controller.tools;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.SimController;
import controller.tools.SaveTool.DataObject;
import controller.tools.SaveTool.MoveableObstacle;
import model.Robot;
import model.SimModel;

/**
 * Enables the user to load a map. Maps must be in the root folder.
 * @author 150021237
 *
 */
public class MapTool extends Tool {

	private static MapTool instance = null;
	private JPanel panel = new JPanel();
	private JLabel label = new JLabel("Map:");
	private JComboBox<File> comboAgents = new JComboBox<File>();

	private MapTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static MapTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new MapTool(model, controller);
		return instance;
	}

	@Override
	public JComponent getComponent() {
		File dir = new File(".");
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(SaveTool.ENDING);
			}
		});
		comboAgents = new JComboBox<File>(files);
		comboAgents.setSelectedItem(null);
		comboAgents.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					DataObject o;
					try {
						FileInputStream fin = new FileInputStream((File) e.getItem());
						ObjectInputStream ois = new ObjectInputStream(fin);
						o = (DataObject) ois.readObject();
						ois.close();
						controller.clear();
						if (o.obstacles != null)
							model.addAllObstacles(o.obstacles);
						if (o.movingObstacles != null) {
							for(MoveableObstacle mo : o.movingObstacles) {
								Robot r = new Robot(mo.x, mo.y, 0.0, model, true);
								r.setvLeft(mo.vl);
								r.setvRight(mo.vr);
								model.addMovingObstacle(r);
							}
						}
						if (o.goal != null)
							model.setGoal(o.goal);
						if (model.getRobot() == null) {
							model.setRobot(new Robot(o.start.x, o.start.y, o.angle, model, false));
						} else {
							if (o.start != null) {
								model.getRobot().setCenter(o.start);
								model.getRobot().setAngle(o.angle);
							}
						}
						controller.currMapName = ((File) e.getItem()).getName();
						controller.setRobotAndMapLabel();
						comboAgents.setSelectedItem(null);
						panel.requestFocusInWindow();
					} catch (Exception ex) {
//						ex.printStackTrace();
					}
				}
			}
		});
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(label);
		panel.add(comboAgents);
		return panel;
	}
}
