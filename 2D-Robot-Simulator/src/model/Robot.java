package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import utils.SimMath;

/**
 * Model class which saves the robot configuration as well as all the robot
 * states.
 * 
 * @author 150021237
 *
 */
public class Robot {

	public static final double V_MIN = 70;
	public static final double V_MID = 100;
	public static final double V_MAX = 130;
	public static int robotSize = 20;
	public static final double WHEEL_DISTANCE = robotSize * 2;
	public static final int NUMBER_OF_SENSORS = 17;
	public static final int NUMBER_OF_HEADINGS = 17;
	public static final double SENSOR_LENGTH = 100;
	public static final double HEADING_LENGTH = 10;

	private static final Color ROBOT_COLOR = Color.BLUE;
	private static final Color SENSOR_COLOR = Color.GREEN;

	private SimModel model;
	private Coordinate center, front;
	private double angle;
	private double sensorAngle;
	private double vLeft = V_MID;
	private double vRight = V_MID;
	private boolean isObstacle;

	private int stepCount = 0;
	private double lengthCount = 0;

	private Sensor[] sensors = new Sensor[NUMBER_OF_SENSORS];
	private Coordinate[] headings = new Coordinate[NUMBER_OF_HEADINGS];

	public Robot(double x, double y, double angle, SimModel m, boolean obstacle) {
		model = m;
		this.angle = angle;
		sensorAngle = 180 / (NUMBER_OF_SENSORS - 1);
		setCenter(new Coordinate(x, y));
		isObstacle = obstacle;
	}

	public Sensor[] getSensors() {
		return sensors;
	}

	public Coordinate[] getHeadings() {
		return headings;
	}

	/**
	 * Calculates the position of the sensor end based on the current position
	 * of the robot.
	 */
	private void calcSensorPositions() {
		double currAngle = angle + 90;
		for (int i = 0; i < NUMBER_OF_SENSORS; i++) {
			Coordinate peak = SimMath.newCoordFromRotation(center, currAngle, SENSOR_LENGTH);
			sensors[i] = model.adjustSensor(new Sensor(center, peak, false, currAngle - angle));
			currAngle -= sensorAngle;
		}
	}

	/**
	 * Calculates the heading positions based on the current position of the
	 * robot.
	 */
	private void calcHeadingPositions() {
		double currAngle = angle + 90;
		for (int i = 0; i < NUMBER_OF_HEADINGS; i++) {
			Coordinate pos = SimMath.newCoordFromRotation(center, currAngle, HEADING_LENGTH);
			headings[i] = pos;
			currAngle -= sensorAngle;
		}
	}

	/**
	 * Draws the robot on the render component.
	 * 
	 * @param g
	 */
	public void draw(Graphics2D g) {
		if(!isObstacle) {
			// Sensors
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			for (int i = 0; i < NUMBER_OF_SENSORS; i++) {
				if (sensors[i].cut)
					g.setColor(Color.RED);
				else
					g.setColor(SENSOR_COLOR);
	
				LineString s = sensors[i].getLine();
				g.drawLine((int) s.getCoordinates()[0].x, (int) s.getCoordinates()[0].y, (int) s.getCoordinates()[1].x,
						(int) s.getCoordinates()[1].y);
			}
		}

		// Body
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ShapeWriter sw = new ShapeWriter();
		if(!isObstacle) {
			g.setColor(ROBOT_COLOR);
			g.draw(sw.toShape(getRobotGeometry()));
			g.drawLine((int) center.x, (int) center.y, (int) front.x, (int) front.y);
		} else {
			g.setColor(Color.LIGHT_GRAY);
			g.fill(sw.toShape(getRobotGeometry()));
			g.setColor(Color.DARK_GRAY);
			g.draw(sw.toShape(getRobotGeometry()));
			g.drawLine((int) center.x, (int) center.y, (int) front.x, (int) front.y);
		}

	}

	/**
	 * Calculates the next position of the robot based on its speed on the left
	 * and right wheel. It calculates the turning angle as well as the forward
	 * speed to calculate the position.
	 * 
	 * @param step
	 *            Step size (distance).
	 * @return Line for path rendering.
	 */
	public LineSegment nextStep(double step) {
		stepCount++;
		double vForward = getvForward();
		double angle2 = (vLeft - vRight) / WHEEL_DISTANCE;
		double length = vForward * step;
		lengthCount += length;
		angle2 += angle;
		Coordinate nextPos = SimMath.newCoordFromRotation(getCenter(), angle2, length);
		LineSegment l = new LineSegment(getCenter(), nextPos);
		setCenter(nextPos);
		angle = angle2;
		front = getFrontFromAngle(angle);
		return l;
	}

	private Coordinate getFrontFromAngle(double angle) {
		return SimMath.newCoordFromRotation(center, angle, robotSize / 2);
	}

	/**
	 * Sets the new position of the robot and updates all the essential elements
	 * of the robot.
	 * 
	 * @param coord
	 */
	public synchronized void setCenter(Coordinate coord) {
		center = new Coordinate(coord);
		front = getFrontFromAngle(angle);
		calcSensorPositions();
		calcHeadingPositions();
	}

	public synchronized Coordinate getCenter() {
		return new Coordinate(center);
	}

	public synchronized Coordinate getFront() {
		return new Coordinate(front);
	}

	public double getvLeft() {
		return vLeft;
	}

	public double getvForward() {
		return (vLeft + vRight) / 2;
	}

	public void setvLeft(double vLeft) {
		if (vLeft < V_MIN)
			this.vLeft = V_MIN;
		else if (vLeft > V_MAX)
			this.vLeft = V_MAX;
		else
			this.vLeft = vLeft;
	}

	public double getvRight() {
		return vRight;
	}

	public int getStepCount() {
		return stepCount;
	}

	public void resetStepCount() {
		stepCount = 0;
	}

	public double getLengthCount() {
		return lengthCount;
	}

	public void resetLengthCount() {
		lengthCount = 0;
	}

	public double getAngle() {
		return angle;
	}
	
	public void incRobotSize() {
		robotSize++;
	}
	
	public void decRobotSize() {
		robotSize--;
	}

	public void setAngle(double newAngle) {
		angle = newAngle;
	}

	public void setvRight(double vRight) {
		if (vRight < V_MIN)
			this.vRight = V_MIN;
		else if (vRight > V_MAX)
			this.vRight = V_MAX;
		else
			this.vRight = vRight;
	}

	public Polygon getRobotGeometry() {
		GeometricShapeFactory gsf = new GeometricShapeFactory();
		gsf.setBase(new Coordinate(center.x - robotSize / 2, center.y - robotSize / 2));
		gsf.setWidth(robotSize);
		gsf.setHeight(robotSize);
		return gsf.createCircle();
	}

	/**
	 * A data class for easier sensor handling.
	 * 
	 * @author 150021237
	 *
	 */
	public static class Sensor {
		public final Coordinate from;
		public final Coordinate to;
		public final boolean cut;
		final double length = SENSOR_LENGTH;
		public final double angle;

		public Sensor(Coordinate from, Coordinate to, boolean cut, double angle) {
			this.from = from;
			this.to = to;
			this.cut = cut;
			this.angle = angle;
		}

		public double getValue() {
			return from.distance(to) / SENSOR_LENGTH;
		}

		public LineString getLine() {
			return new GeometryFactory().createLineString(new Coordinate[] { from, to });
		}
	}
}
