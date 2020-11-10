package broadcastOnlyCommunication;

import java.util.List;
import java.util.stream.Collectors;

import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Perturbation {
	private final ContinuousSpace<Object> space;
	private final Grid<Object> grid;
	public final String senderId;
	public final int ref;
	public final String val;
	private double radius = 1;
	
	/**
	 * number of ticks passed from its creation. Used for velocity and distance calculations
	 */
	private int ticks = 0;

	public Perturbation(ContinuousSpace<Object> space, Grid<Object> grid, String src, int ref, String val) {
		this.space = space;
		this.grid = grid;
		this.senderId = src;
		this.ref = ref;
		this.val = val;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void deliverToRelays() {
		increaseRadius();
		
		// get the grid location of the Perturbation
		var pt = grid.getLocation(this);

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
	
	private void increaseRadius() {
		if (ticks > 0) {
			radius += (1 / Math.pow(radius, 2));
		}
	}
	
	/**
	 * Even though the radius is public, we need a getter method to use this field
	 * inside a Dataset
	 * @return
	 */
	public double getRadius() {
		return radius;
	}
	
	/**
	 * Even though the senderId is public, we need a getter method to use this field
	 * inside a Dataset
	 * @return
	 */
	public String getSenderId() {
		return senderId;
	}

	@Override
	public String toString() {
		return "Perturbation [senderId=" + senderId + ", ref=" + ref + "]";
	}
	
}
