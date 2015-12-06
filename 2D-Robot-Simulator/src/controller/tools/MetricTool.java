package controller.tools;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bots._Behaviour.Metric;
import controller.SimController;
import model.SimModel;

/**
 * Enables the user to choose one of the three metrics: linear, quadratic and
 * arc-based.
 * 
 * @author 150021237
 *
 */
public class MetricTool extends Tool {

	private static MetricTool instance = null;
	private JPanel panel = new JPanel();
	private JLabel label = new JLabel("Metric:");
	private JComboBox<String> comboMetrics = new JComboBox<String>();

	private MetricTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static MetricTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new MetricTool(model, controller);
		return instance;
	}

	@Override
	public JComponent getComponent() {
		comboMetrics.addItem("Linear");
		comboMetrics.addItem("Quadratic");
		comboMetrics.addItem("Arc-Based");
		comboMetrics.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String str = (String) e.getItem();
					if (str.equals("Linear")) {
						controller.setMetric(Metric.Linear);
					} else if (str.equals("Quadratic")) {
						controller.setMetric(Metric.Quadratic);
					} else if (str.equals("Arc-Based")) {
						controller.setMetric(Metric.Arc);
					} else {
						throw new IllegalStateException();
					}
					panel.requestFocusInWindow();
				}
			}
		});
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(label);
		panel.add(comboMetrics);
		return panel;
	}
}
