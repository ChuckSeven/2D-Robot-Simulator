package bots;

import com.vividsolutions.jts.math.Vector2D;

import model.Robot;
import model.Robot.Sensor;
import model.SimModel;
import utils.SimMath;

/**
 * Robot behaviour which panics when an obstacle is encountered. It forgets the
 * goal and tries to avoid the obstacle. It uses its own heading vector as well
 * as the vector to the goal to minimise angle deviation. INVERSE KINEMATICS.
 * 
 * @author 150021237
 *
 */
public class B_ObstaclePanicBotWithAngle extends _Behaviour {

	public B_ObstaclePanicBotWithAngle(SimModel simModel) {
		super(simModel);
	}

	private static final boolean PATH_SMOOTHNESS = false;
	/**
	 * Eliminates wobbling when going straight due to imperfect convergence of
	 * wheel speed to a perfect angle.
	 */
	private static final double AIM_ANGLE_TOLERANCE = 3;
	private static final String BEHAVIOUR_NAME = "B: Dodger (Angle)";
	private Robot robot;

	public String toString() {
		return BEHAVIOUR_NAME;
	}

	public void controlSpeed() {
		robot = model.getRobot();

		Vector2D goalVector = new Vector2D(robot.getCenter(), model.getGoal());
		Vector2D robotVector = new Vector2D(robot.getCenter(), robot.getFront());

		goalVector = goalVector.normalize();
		robotVector = robotVector.normalize();

		Sensor[] sensors = robot.getSensors();
		for (int i = 0; i < Robot.NUMBER_OF_SENSORS; i++) {
			// TODO rounded for visualisation
			double value = Math.round(sensors[i].getValue() * 100.0) / 100.0;

			if (Math.abs(sensors[i].angle) < 50 && value != 1.0) {
				Vector2D repel = new Vector2D(sensors[i].to, robot.getCenter());
				goalVector = repel.normalize();
			}
		}

		double angle = Math.toDegrees(goalVector.angleTo(robotVector));
		adjustToAngle(angle);

	}

	public void adjustToAngle(double w) {
		double v = Robot.V_MID;
		double d = Robot.WHEEL_DISTANCE;

		if (Math.abs(w) < AIM_ANGLE_TOLERANCE)
			w = 0;

		if (PATH_SMOOTHNESS) {
			double goalDistance = robot.getCenter().distance(model.getGoal());
			w = (w / goalDistance) * SimMath.PHI;
		}

		double vr = v + d / 2 * w;
		double vl = v - d / 2 * w;
		robot.setvLeft(vl);
		robot.setvRight(vr);
	}

}
