package utils;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.util.AffineTransformation;

/**
 * Little math lib for mathematical implementations.
 * 
 * @author 150021237
 *
 */
public class SimMath {

	/**
	 * The golden ratio.
	 */
	public static final double PHI = 1.6180339;

	/**
	 * Get angle of b relative to a's positive x axis.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double getAngleDeg(Coordinate a, Coordinate b) {
		double angle = Angle.angle(a, b);
		return Angle.toDegrees(angle);
	}

	/**
	 * Get coordinate with distance and angle relative to coordinates positive x
	 * axis.
	 * 
	 * @param coord
	 * @param angle
	 *            in degrees
	 * @param length
	 * @return
	 */
	public static Coordinate newCoordFromRotation(Coordinate coord, double angle, double length) {

		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 8307);

		Coordinate rotCoord = new Coordinate(coord.x + length, coord.y);
		Geometry point = geometryFactory.createPoint(rotCoord);

		AffineTransformation rot = new AffineTransformation();
		rot = rot.rotate(Angle.toRadians(angle), coord.x, coord.y);

		point.apply(rot);

		return point.getCoordinate();
	}
}
