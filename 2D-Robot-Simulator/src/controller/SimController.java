package controller;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.vividsolutions.jts.geom.LineSegment;

import bots.A_FreeSpaceBotWithAngle;
import bots._Behaviour;
import bots._Behaviour.Metric;
import controller.tools.BehaviorTool;
import controller.tools.ClearTool;
import controller.tools.GoalTool;
import controller.tools.MapTool;
import controller.tools.MetricTool;
import controller.tools.MovingObstacleTool;
import controller.tools.ObstacleTool;
import controller.tools.ResetTool;
import controller.tools.RobotTool;
import controller.tools.RunTool;
import controller.tools.SaveTool;
import controller.tools.SizeTool;
import controller.tools.Tool;
import model.Robot;
import model.SimModel;
import view.RenderView;

/**
 * Controller class which is in charge of running the simulation and calling
 * behaviour, as well as, model functions.
 * 
 * @author 150021237
 *
 */
public class SimController {

	private SimModel model;
	private SimController controller;
	private RenderView view;
	private _Behaviour behaviour;

	private Tool tool = null;
	private int count = 0;
	private volatile boolean isRunning = false;
	private Thread robotThread;
	private _Behaviour.Metric currMetric = Metric.Linear;
	private static final int NUMBER_OF_MOVES_PER_SECOND = 100;
	private static final int PATH_STROKE_RATE = 4;
	public static final double TIME_STEP = 0.01;
	public String currRobotName;
	public String currMapName = "custom";

	public SimController() {
		model = new SimModel();
		controller = this;

		view = new RenderView(model, loadTools());
		setBehavior(new A_FreeSpaceBotWithAngle(model));

		keyBehavior();
	}

	private List<Tool> loadTools() {
		LinkedList<Tool> tools = new LinkedList<Tool>();
		tools.add(MapTool.getInstance(model, controller));
		tools.add(BehaviorTool.getInstance(model, controller));
		tools.add(MetricTool.getInstance(model, controller));
		tools.add(SizeTool.getInstance(model, controller));
		tools.add(RobotTool.getInstance(model, controller));
		tools.add(GoalTool.getInstance(model, controller));
		tools.add(ObstacleTool.getInstance(model, controller));
		tools.add(MovingObstacleTool.getInstance(model, controller));
		tools.add(RunTool.getInstance(model, controller));
		tools.add(ResetTool.getInstance(model, controller));
		tools.add(ClearTool.getInstance(model, controller));
		tools.add(SaveTool.getInstance(model, controller));
		return tools;
	}

	public void setTool(Tool nextTool) {
		view.removeToolListeners(tool);
		view.addKeyListener(nextTool);
		view.addMouseListener(nextTool);
		view.addMouseMotionListener(nextTool);
		tool = nextTool;
	}

	/**
	 * Keyboard shortcut implementation.
	 */
	@SuppressWarnings("serial")
	private void keyBehavior() {
		view.getFramePanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('r'),
				"PlaceRobot");
		view.getFramePanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('g'),
				"PlaceGoal");
		view.getFramePanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('o'),
				"PlaceSquare");
		view.getFramePanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s'),
				"StartStop");
		view.getFramePanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('c'), "Clear");
		view.getFramePanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('d'), "Restart");
		view.getFramePanel().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('m'), "PlaceMovingObstacle");

		view.getFramePanel().getActionMap().put("PlaceRobot", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SimController.this.setTool(RobotTool.getInstance(model, controller));
			}
		});
		view.getFramePanel().getActionMap().put("PlaceGoal", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SimController.this.setTool(GoalTool.getInstance(model, controller));
			}
		});
		view.getFramePanel().getActionMap().put("PlaceSquare", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SimController.this.setTool(ObstacleTool.getInstance(model, controller));
			}
		});
		view.getFramePanel().getActionMap().put("StartStop", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SimController.this.setTool(RunTool.getInstance(model, controller));
				RunTool.getInstance(model, controller).toggle();
			}
		});
		view.getFramePanel().getActionMap().put("Clear", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.clear();
			}
		});
		view.getFramePanel().getActionMap().put("Restart", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SimController.this.setTool(ResetTool.getInstance(model, controller));
				ResetTool.getInstance(model, controller).reset();
			}
		});
		view.getFramePanel().getActionMap().put("PlaceMovingObstacle", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SimController.this.setTool(MovingObstacleTool.getInstance(model, controller));
			}
		});
	}

	public void clear() {
		stop();
		model.clearAll();
		view.clearPath();
	}

	public void setRobotAndMapLabel() {
		Robot r = model.getRobot();
		view.setLabel1(currRobotName + ", Map: " + currMapName + " Steps: " + (r == null ? 0 : r.getStepCount())
				+ " Length: " + (r == null ? 0 : r.getLengthCount()));
	}

	public void setNotRunning() {
		isRunning = false;
		view.setLabel2("STOP");
		if (robotThread != null)
			robotThread.interrupt();
	}

	public void setLabel2(String str) {
		view.setLabel2(str);
	}

	public void setBehavior(_Behaviour newBehaviour) {
		behaviour = newBehaviour;
		behaviour.setMetric(currMetric);
		currRobotName = behaviour.toString();
		setRobotAndMapLabel();
	}

	public void setMetric(Metric newMetric) {
		currMetric = newMetric;
		behaviour.setMetric(currMetric);
	}

	private void stop() {
		for (Tool t : view.getTools()) {
			if (t instanceof RunTool) {
				((RunTool) t).stop();
				view.setLabel2("STOP");
			}
		}
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}

	}

	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Starts a thread which constantly updates the robots position and checks
	 * if the robot has reached the goal or crashed into an obstacle.
	 */
	public void start() {
		isRunning = true;
		if (model.isRobotSet() && model.isGoalSet() && isRunning()) {
			Runnable r = () -> {
				view.setLabel2("START");

				while (isRunning && !Thread.currentThread().isInterrupted()) {
					setRobotAndMapLabel();
					// State checks
					if (model.isRobotAtGoal()) {
						stop();
						view.setLabel2("ROBOT IS IN GOAL!");
						break;
					}
					if (model.isRobotCrashed()) {
						stop();
						view.setLabel2("ROBOT CRASHED !!!!");
						break;
					}

					// get robot behavior
					long startTime = System.currentTimeMillis();
					behaviour.controlSpeed();
					movingObstacleBehavior();
					long elapsed = System.currentTimeMillis() - startTime;
					// move speed
					try {
						long delta = (long) (1000.0 / NUMBER_OF_MOVES_PER_SECOND);
						if (elapsed < delta) {
							Thread.sleep(delta - elapsed);
						}
					} catch (InterruptedException e) {
					}

					// path painting
					LineSegment l = model.getRobot().nextStep(TIME_STEP);
					if (count == 0)
						view.addToPath(l);
					count++;
					count = count % PATH_STROKE_RATE;
				}
			};
			robotThread = new Thread(r);
			robotThread.start();

		} else {
			stop();
			view.setLabel2("ERROR: GOAL OR ROBOT NOT SET!");

		}
	}

	private void movingObstacleBehavior() {
		for (Robot r1 : model.getMovingObstacles()) {
			r1.setvLeft(Robot.V_MAX);
			r1.setvRight(Robot.V_MIN);
			r1.nextStep(TIME_STEP);
		}
	}
}
