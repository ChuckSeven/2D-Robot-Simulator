package bots;

import java.util.Arrays;
import java.util.Comparator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.math.Vector2D;

import model.Robot;
import model.SimModel;

/**
 * This bot uses the sample points in front of it for free space travel. Works
 * with different metrics from _Behaviour'
 * 
 * @author 150021237
 *
 */
public class C_FreeSpaceBotWithSamplePoints extends _Behaviour {

	public C_FreeSpaceBotWithSamplePoints(SimModel simModel) {
		super(simModel);
	}

	/**
	 * Eliminates wobbling when going straight due to imperfect convergence of
	 * wheel speed to a perfect angle.
	 */
	private static final double AIM_ANGLE_TOLERANCE = 3;
	private static final String BEHAVIOUR_NAME = "C:Free Space (Samples)";
	private Robot robot;

	final Comparator<Coordinate> comp = (a, b) -> {
		double aDist = distanceByMetric(a, model.getGoal());
		double bDist = distanceByMetric(b, model.getGoal());
		if (aDist < bDist)
			return -1;
		else if (aDist > bDist)
			return 1;
		else
			return 0;
	};

	public String toString() {
		return BEHAVIOUR_NAME;
	}

	public void controlSpeed() {
		robot = model.getRobot();

		Coordinate[] samplePoints = robot.getHeadings();
		Coordinate best = Arrays.stream(samplePoints).min(comp).get();

		moveToSamplePoint(best);
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
