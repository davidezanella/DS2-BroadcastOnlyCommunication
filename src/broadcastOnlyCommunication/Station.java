package broadcastOnlyCommunication;

import java.util.List;
import java.util.UUID;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Station {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private String ID = "station";
	private int ref = 0;

	public Station(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}

	@ScheduledMethod(start = 1, interval = 100)
	public void sendPerturbation() {
		// get the grid location of the Station
		var pt = grid.getLocation(this);

		List<Relay> relays = Utils.getAllRelaysInGrid(grid, pt);

		var value = UUID.randomUUID().toString();
		var p = new Perturbation(this.ID, this.ref, value);
		this.ref++;

		Double tick = RepastEssentials.GetTickCount();
		System.out.println(tick + " -- Station: " + pt.getX() + " - " + pt.getY() + ": " + value);
		for (var relay : relays) {
			double distance = Utils.distanceBetweenPoints(pt, grid.getLocation(relay));
			int missingTicks = (int) Math.ceil(distance);
			relay.onSense(p, missingTicks);
		}
	}

}
