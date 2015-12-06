package bots;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.math.Vector2D;

import model.Robot;
import model.Robot.Sensor;
import model.SimModel;

/**
 * Uses samples in front of the robot to create a potential field. Finds the
 * lowest point in the potential field and moves towards it.
 * 
 * @author 150021237
 *
 */
public class D_AvoidObstacleWithSamplePoints extends _Behaviour {

	public D_AvoidObstacleWithSamplePoints(SimModel simModel) {
		super(simModel);
	}

	/**
	 * Eliminates wobbling when going straight due to imperfect convergence of
	 * wheel speed to a perfect angle.
	 */
	private static final double AIM_ANGLE_TOLERANCE = 3;
	private static final String BEHAVIOUR_NAME = "D:Potential Fields";
	private static final double ga = 5.8;
	private static final double oa = 2.5;
	private Robot robot;

	public String toString() {
		return BEHAVIOUR_NAME;
	}

	public void controlSpeed() {
		robot = model.getRobot();

		Coordinate[] samplePoints = robot.getHeadings();
		Sensor[] sensors = robot.getSensors();

		// initialize potential field
		double[] field = new double[samplePoints.length];

		// add goal potential
		for (int i = 0; i < samplePoints.length; i++) {
			// double distance = samplePoints[i].distance(model.getGoal());
			double distance = distanceByMetric(samplePoints[i], model.getGoal());

			field[i] -= (Math.exp(ga) / (distance - 6)) + 10;
		}

		// substract obstacle potential
		for (int i = 0; i < samplePoints.length; i++) {
			for (int j = 0; j < sensors.length; j++) {
				Sensor s = sensors[j];
				if (s.cut) {
					// obstacle potential found
					double distance = samplePoints[i].distance(s.to);
					field[i] += Math.exp(oa) / (distance - 4);
				}
			}
		}

		// get index with lowest
		int minIdx = -1;
		double minVal = Double.MAX_VALUE;
		for (int i = 0; i < field.length; i++) {
			if (field[i] < minVal) {
				minVal = field[i];
				minIdx = i;
			}
		}

		moveToSamplePoint(samplePoints[minIdx]);
	}

	private void moveToSamplePoint(Coordinate samplePoint) {
		Vector2D headingVector = new Vector2D(robot.getCenter(), samplePoint);
		Vector2D robotVector = new Vector2D(robot.getCenter(), robot.getFront());
		double angle = -Math.toDegrees(robotVector.angleTo(headingVector));
		adjustToAngle(angle);
	}

	public void adjustToAngle(double w) {
		double v = Robot.V_MID;
		double d = Robot.WHEEL_DISTANCE;

		if (Math.abs(w) < AIM_ANGLE_TOLERANCE)
			w = 0;

		double vr = v + d / 2 * w;
		double vl = v - d / 2 * w;
		robot.setvLeft(vl);
		robot.setvRight(vr);
	}
}
