package bots;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.util.AffineTransformation;

import model.Robot;
import model.SimModel;
import utils.SimMath;

/**
 * Obstacle ignoring behaviour which tries to reach the goal in a smooth or
 * greedy path. It does this by calculating the angle towards the goal and then
 * turns to reduce its angle. INVERSE KINEMATIC.
 * 
 * @author 150021237
 *
 */
public class A_FreeSpaceBotWithAngle extends _Behaviour {

	public A_FreeSpaceBotWithAngle(SimModel simModel) {
		super(simModel);
	}

	private static final boolean PATH_SMOOTHNESS = true;
	/**
	 * Eliminates wobbling when going straight due to imperfect convergence of wheel speed to a perfect angle.
	 */
	private static final double AIM_ANGLE_TOLERANCE = 3;
	private static final String BEHAVIOUR_NAME = "A: Free Space (Angle)";
	private Robot robot;

	public String toString() {
		return BEHAVIOUR_NAME;
	}

	public void controlSpeed() {
		robot = model.getRobot();

		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 8307);
		Geometry g = geometryFactory.createPoint(model.getGoal());

		AffineTransformation rot = new AffineTransformation();
		rot = rot.rotate(Angle.toRadians(-robot.getAngle()), robot.getCenter().x, robot.getCenter().y);
		g.apply(rot);

		double angle = -SimMath.getAngleDeg(robot.getCenter(), g.getCoordinate());

		adjustToAngle(angle);

	}

	public void adjustToAngle(double w) {
		double v = Robot.V_MID;
		double d = Robot.WHEEL_DISTANCE;
		
		if(Math.abs(w) < AIM_ANGLE_TOLERANCE) w = 0;
		
		if (PATH_SMOOTHNESS) {
			double goalDistance = robot.getCenter().distance(model.getGoal());
//			double goalDistance = distanceByMetric(robot.getCenter(), model.getGoal());
			w = (w / goalDistance) * SimMath.PHI;
		} 
		
		double vr = v + d/2 * w;
		double vl = v - d/2 * w;		
		robot.setvLeft(vl);
		robot.setvRight(vr);
	}

}
