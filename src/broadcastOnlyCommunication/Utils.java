package broadcastOnlyCommunication;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Utils {
	public static double distanceBetweenPoints(GridPoint pt1, GridPoint pt2) {
		double xDiff = pt1.getX() - pt2.getX();
		double yDiff = pt1.getY() - pt2.getY();
		return Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
	}
	
	public static List<Relay> getAllRelaysInGrid(Grid<Object> grid, GridPoint pt) {
		var extentX = grid.getDimensions().getWidth() / 2;
		var extentY = grid.getDimensions().getHeight() / 2;
		var nghCreator = new GridCellNgh<Relay>(grid, pt, Relay.class, extentX, extentY);
		return nghCreator
				.getNeighborhood(true)
				.stream()
				.flatMap(cell -> StreamSupport.stream(cell.items().spliterator(), false))
				.collect(Collectors.toList());
	}
}
