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
	private String id = "station";
	private int ref = 0;
	
	private Perturbation lastPerturbation; // used for logging purposes

	public Station(ContinuousSpace<Object> space, Grid<Object> grid, String id) {
		this.space = space;
		this.grid = grid;
		this.id = id;
	}

	@ScheduledMethod(start = 1, interval = 100)
	public void sendPerturbation() {
		// get the grid location of the Station
		var pt = grid.getLocation(this);

		List<Relay> relays = Utils.getAllRelaysInGrid(grid, pt);

		var value = UUID.randomUUID().toString();
		this.lastPerturbation = new Perturbation(this.id, this.ref, value);
		this.ref++;

		for (var relay : relays) {
			double distance = Utils.distanceBetweenPoints(pt, grid.getLocation(relay));
			int missingTicks = (int) Math.ceil(distance);
			relay.onSense(this.lastPerturbation, missingTicks);
		}
	}

	public String getNewPerturbationValue() {
		if(this.lastPerturbation != null) {
			var val = this.lastPerturbation.val;			
			this.lastPerturbation = null;
			
			return val;
		}
		return "";
	}

	public int getCurrentRef() {
		return this.ref;
	}

	public String getStationId() {
		return this.id;
	}
}
