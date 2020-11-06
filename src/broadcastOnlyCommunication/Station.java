package broadcastOnlyCommunication;

import java.util.List;
import java.util.UUID;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Station {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private String ID = "station";
	private Integer ref = 0;

	public Station(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}

	@ScheduledMethod(start = 1, interval = 100)
	public void SendPerturbation() {
		// get the grid location of the Station
		GridPoint pt = grid.getLocation(this);

		// get all the Relays in the grid
		Integer extentX = grid.getDimensions().getWidth() / 2;
		Integer extentY = grid.getDimensions().getHeight() / 2;
		GridCellNgh<Relay> nghCreator = new GridCellNgh<Relay>(grid, pt, Relay.class, extentX, extentY);
		List<GridCell<Relay>> gridCells = nghCreator.getNeighborhood(true);

		String value = UUID.randomUUID().toString();
		Perturbation p = new Perturbation(this.ID, this.ref, value);
		this.ref++;

		Double tick = RepastEssentials.GetTickCount();
		System.out.println(tick + " -- Station: " + pt.getX() + " - " + pt.getY() + ": " + value);
		for (GridCell<Relay> cell : gridCells) {
			for (Relay relay : cell.items()) {
				Double distance = Utils.DistanceBetweenPoints(pt, grid.getLocation(relay));
				Integer missingTicks = (int) Math.ceil(distance);
				relay.OnSense(p, missingTicks);
			}
		}
	}
}
