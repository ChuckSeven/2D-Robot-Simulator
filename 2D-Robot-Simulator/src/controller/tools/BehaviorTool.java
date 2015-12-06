package controller.tools;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bots.A_FreeSpaceBotWithAngle;
import bots.B_ObstaclePanicBotWithAngle;
import bots.C_FreeSpaceBotWithSamplePoints;
import bots.D_AvoidObstacleWithSamplePoints;
import bots.E_FractionalProgressWithSamplePoints;
import bots.F_PelletBot;
import bots._Behaviour;
import controller.SimController;
import model.SimModel;

/**
 * Implements the JComboBox to choose the different agent implementations.
 * 
 * @author 150021237
 *
 */
public class BehaviorTool extends Tool {

	private static BehaviorTool instance = null;
	private JPanel panel = new JPanel();
	private JLabel label = new JLabel("Agent:");
	private JComboBox<_Behaviour> comboAgents = new JComboBox<_Behaviour>();

	private BehaviorTool(SimModel model, SimController controller) {
		super(model, controller);
		comboAgents.addItem(new A_FreeSpaceBotWithAngle(model));
		comboAgents.addItem(new B_ObstaclePanicBotWithAngle(model));
		comboAgents.addItem(new C_FreeSpaceBotWithSamplePoints(model));
		comboAgents.addItem(new D_AvoidObstacleWithSamplePoints(model));
		comboAgents.addItem(new E_FractionalProgressWithSamplePoints(model));
		comboAgents.addItem(new F_PelletBot(model));
	}

	public static BehaviorTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new BehaviorTool(model, controller);
		return instance;
	}

	@Override
	public JComponent getComponent() {
		comboAgents.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					controller.setBehavior((_Behaviour) e.getItem());
					controller.setRobotAndMapLabel();
					panel.requestFocusInWindow();
				}
			}
		});
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(label);
		panel.add(comboAgents);
//		comboAgents.setSelectedItem(null);
		return panel;
	}
}
