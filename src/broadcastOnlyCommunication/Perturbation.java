package broadcastOnlyCommunication;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Perturbation {
	private final ContinuousSpace<Object> space;
	private final Grid<Object> grid;
	public final String senderId;
	public final int ref;
	public final String val;
	private double radius;
	public String receiverId;
	
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
	
	public Perturbation(ContinuousSpace<Object> space, Grid<Object> grid, String src, int ref, String val, String receiverId) {
		this.space = space;
		this.grid = grid;
		this.senderId = src;
		this.ref = ref;
		this.val = val;
		this.receiverId = receiverId;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void onTick() {
		increaseRadius();
		deliverToRelaysInsideReachingRing();
		ticks++;
		removeItselfWhenBiggerThanGrid();
	}

	private void deliverToRelaysInsideReachingRing() {
		// get the grid location of the Perturbation
		var pt = grid.getLocation(this);

		Utils
			.getAllRelaysInGrid(grid, this)
			.stream()
			.filter(relay -> {
				var dist = Utils.distanceBetweenPoints(pt, grid.getLocation(relay));
				var ringRadiusStart = radius - getRadiusIncrease();
				var ringRadiusEnd = radius;
				return dist >= ringRadiusStart && dist <= ringRadiusEnd;
			})
			.peek(relayInRange -> System.out.println("Deliverying perturbation to " + relayInRange))
			.forEach(relayInRange -> relayInRange.onSense(Perturbation.this));
	}
	
	private void increaseRadius() {
		if (ticks <= 0) {
			radius = 0.5;
		} else {
			radius += getRadiusIncrease();
		}
	}
	
	private double getRadiusIncrease() {
		return 0.5 - sigmoid(ticks);
	}
	
	private static double sigmoid(double x) {
		return (1 / (1 + Math.pow(Math.E, -(x / 10)))) - 1;
	}
	
	private void removeItselfWhenBiggerThanGrid() {
		if (radius > Utils.getBiggestSizeOfGrid(grid)) {
			Utils.removePerturbation(this);
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
		return "Perturbation [senderId=" + senderId + ", ref=" + ref + ", val=" + val + "]";
	}
	
}
