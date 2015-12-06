package controller.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import controller.SimController;
import model.SimModel;

/**
 * Implements the clear all button. Removes all elements from the drawing
 * component.
 * 
 * @author 150021237
 *
 */
public class ClearTool extends Tool {

	private static ClearTool instance = null;
	private JButton clearBtn = new JButton("Clear");

	private ClearTool(SimModel model, SimController controller) {
		super(model, controller);
	}

	public static ClearTool getInstance(SimModel model, SimController controller) {
		if (instance == null)
			instance = new ClearTool(model, controller);
		return instance;
	}

	@Override
	public JComponent getComponent() {

		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.clear();
			}
		});

		return clearBtn;
	}

}
