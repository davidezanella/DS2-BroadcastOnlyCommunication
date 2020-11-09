package broadcastOnlyCommunication;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Utils {
	public static double distanceBetweenPoints(GridPoint pt1, GridPoint pt2) {
		double xDiff = pt1.getX() - pt2.getX();
		double yDiff = pt1.getY() - pt2.getY();
		return Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
	}

	public static int getNeededTimeToDeliver(double distance) {
		return (int) Math.ceil(distance) ^ 2;
	}

	public static List<Relay> getAllRelaysInGrid(Grid<Object> grid, GridPoint pt) {
		return getAllActorsInGrid(grid, pt, Relay.class);
	}

	public static List<Station> getAllStationsInGrid(Grid<Object> grid, GridPoint pt) {
		return getAllActorsInGrid(grid, pt, Station.class);
	}

	public static <T> List<T> getAllActorsInGrid(Grid<Object> grid, GridPoint pt, Class<T> clazz) {
		var extentX = grid.getDimensions().getWidth() / 2;
		var extentY = grid.getDimensions().getHeight() / 2;
		var nghCreator = new GridCellNgh<T>(grid, pt, clazz, extentX, extentY);
		return nghCreator.getNeighborhood(true).stream()
				.flatMap(cell -> StreamSupport.stream(cell.items().spliterator(), false)).collect(Collectors.toList());
	}

	public static Relay instantiateCorrectRelayVersion(ContinuousSpace<Object> space, Grid<Object> grid, String id) {
		Parameters params = RunEnvironment.getInstance().getParameters();
		String protocolVersion = params.getString("protocolVersion");

		if (protocolVersion.equals("PerfectConditions")) {
			return new RelayI(space, grid, id);
		} else if (protocolVersion.equals("DynamicNetwork")) {
			return new RelayII(space, grid, id);
		} else if (protocolVersion.equals("RecoveringLoss")) {
			return new RelayIII(space, grid, id);
		} else {
			System.err.println("Unsupported or not implemented protocol version!");
			return null;
		}
	}
}
