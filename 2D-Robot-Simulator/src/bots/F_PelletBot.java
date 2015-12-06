package bots;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.math.Vector2D;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import model.Robot;
import model.Robot.Sensor;
import model.SimModel;

public class F_PelletBot extends _Behaviour {

	public F_PelletBot(SimModel simModel) {
		super(simModel);
	}

	/**
	 * Eliminates wobbling when going straight due to imperfect convergence of
	 * wheel speed to a perfect angle.
	 */
	private static final double AIM_ANGLE_TOLERANCE = 3;
	private static final double GOAL_EXPONENT = 5.8;
	private static final double OBSTACLE_EXPONENT = 2.5;
	private static final String BEHAVIOUR_NAME = "F:Pellet Bot";
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
			double futureCost = distanceByMetric(samplePoints[i], model.getGoal());
			double pastCost = robot.getCenter().distance(model.getStart());
			double minPath = model.getStart().distance(model.getGoal());
			double progress = pastCost / (pastCost + futureCost);
			double newDistToGoal = (1.0 - progress) * minPath;

			field[i] -= (Math.exp(GOAL_EXPONENT) / (newDistToGoal - 6)) + 10;
		}

		// substract obstacle potential
		for (int i = 0; i < samplePoints.length; i++) {
			for (int j = 0; j < sensors.length; j++) {
				Sensor s = sensors[j];
				if (s.cut) {
					// obstacle potential found
					double distance = samplePoints[i].distance(s.to);
					field[i] += Math.exp(OBSTACLE_EXPONENT) / (distance - 4);
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

		// crap strategy
		int pelletSize = 8;
		if(robot.getStepCount()%50 == 0) {

			Vector2D r = new Vector2D(robot.getCenter());
			Vector2D v = new Vector2D(robot.getCenter(),robot.getFront());
			Vector2D v2 = v.rotateByQuarterCircle(2).multiply(1.5);
			
			Coordinate spot = r.add(v2).toCoordinate(); 

			GeometricShapeFactory gsf = new GeometricShapeFactory();
			gsf.setSize(pelletSize);
			gsf.setNumPoints(100);
			gsf.setBase(new Coordinate(spot.x - (pelletSize/2), spot.y - (pelletSize/2)));
			model.addObstacle(gsf.createRectangle());
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
