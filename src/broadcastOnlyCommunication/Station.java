package broadcastOnlyCommunication;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Station {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	public final String id;
	private int ref = 0;
	protected Boolean canUnicast;
	protected Boolean useCrypto;

	private Perturbation lastPerturbation; // used for logging purposes

	public Station(ContinuousSpace<Object> space, Grid<Object> grid, String id, Boolean canUnicast, Boolean useCrypto) {
		this.space = space;
		this.grid = grid;
		this.id = id;
		this.canUnicast = canUnicast;
		this.useCrypto = useCrypto;
	}

	@ScheduledMethod(start = 1, interval = 100)
	public void sendPerturbation() {
		var value = UUID.randomUUID().toString();
		
		// in p2p comm, all messages are unicast
		if (canUnicast) {
			Random r = new Random();

			List<Relay> relays = Utils.getAllRelaysInGrid(grid, this);
			Relay receiver = relays.remove(r.nextInt(relays.size()));

			if (useCrypto) {
				try {
					var pubKey = SimManager.keyRing.get(receiver.getRelayId()).getPublic();
					value = CryptoUtils.encryptMessage(value, pubKey);
					System.out.println("Message encrypted correctly!");
				}
				catch (Exception e) {
					System.err.println("Error encrypting the message!");
					e.printStackTrace();
				}
			}
			this.lastPerturbation = Utils.createNewUnicastPerturbation(space, grid, id, ref, value, this, receiver.getId());
		} else {
			this.lastPerturbation = Utils.createNewPerturbation(space, grid, id, ref, value, this);
		}
		this.ref++;
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
