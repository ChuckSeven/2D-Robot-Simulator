package bots;

import com.vividsolutions.jts.geom.Coordinate;

import model.SimModel;

/**
 * Implements the different distance metrics for the bot behaviours to use.
 * 
 * @author 150021237
 *
 */
public abstract class _Behaviour {

	public SimModel model;
	private Metric currMetric = Metric.Linear;
	
	public _Behaviour(SimModel simModel) {
		model = simModel;
	}

	public void setMetric(Metric m) {
		currMetric = m;
	}
	
	public abstract void controlSpeed();
	public abstract String toString();

	protected double distanceByMetric(Coordinate a, Coordinate b) {
		switch(currMetric) {
		case Linear:
			return linear(a,b);
		case Quadratic:
			return quadratic(a,b);
		case Arc:
			return arcBased(a,b);
		default: 
			return linear(a,b);
		}
	}
	
	private double linear(Coordinate a, Coordinate b) {
		return a.distance(b);
	}
	
	private double quadratic(Coordinate a, Coordinate b) {
		double d = a.distance(b);
		return d*d;
	}
	
	private double arcBased(Coordinate a, Coordinate b) {
		double d1 = model.getStart().distance(a);
		double d2 = a.distance(b);
		double d3 = model.getStart().distance(b);
		return (1-(d1/(d1+d2))) * d3;
	}
	
	public enum Metric { Linear, Quadratic, Arc };
}
