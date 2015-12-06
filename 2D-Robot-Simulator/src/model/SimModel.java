package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

import model.Robot.Sensor;

/**
 * This model saves all the non-robot objects which can be painted on the render
 * component.
 * 
 * @author 150021237
 *
 */
public class SimModel {

	private static final int WIN_DISTANCE = 10;
	private static final double MIN_CRASH_AREA = 70; // for pellets
	
	public static final int GOAL_SIZE = 10;
	

	private Robot robot;
	private Coordinate goal;
	private Coordinate start;
	private double startAngle;
	private List<Polygon> obstacles;
	private List<Robot> movingObstacles;
	
	public SimModel() {
		obstacles = Collections.synchronizedList(new ArrayList<Polygon>());
		movingObstacles = Collections.synchronizedList(new ArrayList<Robot>());
	}

	public Robot getRobot() {
		return robot;
	}

	public void setRobot(Robot r) {
		robot = r;
		start = r.getCenter();
		startAngle = robot.getAngle();
	}

	public Coordinate getGoal() {
		if (goal != null)
			return new Coordinate(goal);
		else
			return null;
	}

	public boolean isGoalSet() {
		return goal != null;
	}

	public boolean isRobotSet() {
		return robot != null;
	}

	public void setGoal(Coordinate goal) {
		this.goal = goal;
	}

	public boolean isRobotAtGoal() {
		return robot.getCenter().distance(goal) < WIN_DISTANCE;
	}

	public void addObstacle(Polygon obstacle) {
		obstacles.add(obstacle);
	}

	public void addAllObstacles(List<Polygon> obstacle) {
		obstacles.addAll(obstacle);
	}
	
	public void addMovingObstacle(Robot movingObstacle) {
		movingObstacles.add(movingObstacle);
	}

	public void addAllMovingObstacles(List<Robot> movingObstacle) {
		movingObstacles.addAll(movingObstacle);
	}

	public Coordinate getStart() {
		return start;
	}

	public void removeAllObstacles() {
		obstacles.clear();
		movingObstacles.clear();
	}

	public List<Polygon> getObstacles() {
		return new ArrayList<Polygon>(obstacles);
	}
	
	public List<Robot> getMovingObstacles() {
		return movingObstacles;
	}

	public boolean checkIfRobotCollided() {
		return false;
	}

	public double getStartAngle() {
		return startAngle;
	}

	public boolean isRobotCrashed() {
		Polygon r = robot.getRobotGeometry();
		for (Polygon o : obstacles) {
			if (o.isWithinDistance(o, Robot.SENSOR_LENGTH))
				if(o.getArea()>MIN_CRASH_AREA)
					if (o.intersects(r))
						return true;
		}
		for (Robot r1 : movingObstacles) {
			Polygon o = r1.getRobotGeometry();
			if (o.isWithinDistance(o, Robot.SENSOR_LENGTH))
				if (o.intersects(r))
					return true;
		}
		return false;
	}

	public Sensor adjustSensor(Sensor s) {
		for (Polygon p : obstacles) {
			Coordinate[] coords = s.getLine().intersection(p).getCoordinates();
			if (coords.length > 0 && robot != null) {
				s = new Sensor(robot.getCenter(), coords[0], true, s.angle);
				return s;
			}
		}
		for (Robot r1 : movingObstacles) {
			Polygon p = r1.getRobotGeometry();
			Coordinate[] coords = s.getLine().intersection(p).getCoordinates();
			if (coords.length > 0) {
				s = new Sensor(robot.getCenter(), coords[0], true, s.angle);
				return s;
			}
		}
		return s;
	}

	public void clearAll() {
		obstacles.clear();
		movingObstacles.clear();
		goal = null;
		robot = null;
	}

	public void removePellets() {
		LinkedList<Polygon> toRemove = new LinkedList<Polygon>();
		for(Polygon p : obstacles) {
			if(p.getArea()<MIN_CRASH_AREA) { 
				toRemove.add(p);
			}
		}
		obstacles.removeAll(toRemove);
	}

}
