package broadcastOnlyCommunication;

import java.util.List;
import java.util.stream.Collectors;

import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Perturbation {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	public String src; // ID of the sender
	public int ref;
	public String val;
	private int ticks = 0; // number of ticks passed from its creation. Used for velocity and distance
							// calculations

	public Perturbation(ContinuousSpace<Object> space, Grid<Object> grid, String src, int ref, String val) {
		this.space = space;
		this.grid = grid;
		this.src = src;
		this.ref = ref;
		this.val = val;
		
		Schedule schedule = new Schedule();
		ScheduleParameters params = ScheduleParameters.createRepeating(1, 1);
		schedule.schedule(params, this, "deliverToRelays");
	}

	@ScheduledMethod(interval = 1)
	public void deliverToRelays() {
		// get the grid location of the Perturbation
		var pt = grid.getLocation(this);

		// the velocity decays so the radius depends on the passed ticks
		var radius = (ticks > 0) ? 1 / Math.pow(ticks, 2) : 0;

		List<Relay> allRelays = Utils.getAllRelaysInGrid(grid, pt);
		List<Relay> relays = allRelays.parallelStream().filter(k -> {
			var dist = Utils.distanceBetweenPoints(pt, grid.getLocation(k));
			return (radius - 1 <= dist) && (dist <= radius);
		}).collect(Collectors.toList());

		for (var relay : relays) {
			relay.onSense(this);
		}

		ticks++;
	}
}
