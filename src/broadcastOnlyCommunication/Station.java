package broadcastOnlyCommunication;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Station {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	public final String id;
	private int ref = 0;
	protected Boolean canUnicast;

	private Perturbation lastPerturbation; // used for logging purposes

	public Station(ContinuousSpace<Object> space, Grid<Object> grid, String id, Boolean canUnicast) {
		this.space = space;
		this.grid = grid;
		this.id = id;
		this.canUnicast = canUnicast;
	}

	@ScheduledMethod(start = 1, interval = 100)
	public void sendPerturbation() {
		//in p2p comm, all messages are unicast
		if(canUnicast) {
			Random r = new Random();
			
			var value = UUID.randomUUID().toString();
			List<Relay> relays = Utils.getAllRelaysInGrid(grid, this);
			Relay receiver = relays.remove(r.nextInt(relays.size()));
			
			this.lastPerturbation = Utils.createNewPerturbation(space, grid, id, ref, value, this, receiver.getId());
			this.ref++;
		} else {
			var value = UUID.randomUUID().toString();
			this.lastPerturbation = Utils.createNewPerturbation(space, grid, id, ref, value, this);
			
			this.ref++;
		}
	}

	public String getNewPerturbationValue() {
		if (this.lastPerturbation != null) {
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
