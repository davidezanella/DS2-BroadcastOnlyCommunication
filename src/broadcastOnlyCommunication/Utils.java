package broadcastOnlyCommunication;

import repast.simphony.space.grid.GridPoint;

public class Utils {
	public static Double distanceBetweenPoints(GridPoint pt1, GridPoint pt2) {
		double xDiff = pt1.getX() - pt2.getX();
		double yDiff = pt1.getY() - pt2.getY();
		return Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
	}
}
