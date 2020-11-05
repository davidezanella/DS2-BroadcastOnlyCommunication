package broadcastOnlyCommunication;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Station {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private String ID = "station";

	public Station(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;
	}
}
