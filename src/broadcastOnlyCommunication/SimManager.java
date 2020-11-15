package broadcastOnlyCommunication;

import repast.simphony.context.Context;
import java.util.Random;
import java.security.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class SimManager {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	public final static Map<String, KeyPair> keyRing = new TreeMap<>();
	/*
	 * probability that the simulation manager destroy or creates a new relay,
	 * respectively Value should be betwenn 1 and 100
	 */
	protected int probCrash;
	protected int probNew;
	// minimum and maximum number of relays
	protected int minNum;
	protected int maxNum;
	// the id for the next created relay
	protected int nextRelayId;

	public SimManager(ContinuousSpace<Object> space, Grid<Object> grid) {
		this.space = space;
		this.grid = grid;

		Parameters params = RunEnvironment.getInstance().getParameters();
		this.probCrash = params.getInteger("probCrash");
		this.probNew = params.getInteger("probNew");
		this.minNum = params.getInteger("minNumRelays");
		this.maxNum = params.getInteger("maxNumRelays");

		this.nextRelayId = params.getInteger("numRelays");
	}

	// for each station and relay, create a keypair
	public void initializeCrypto() {
		List<Relay> relays = Utils.getAllRelaysInGrid(grid, this);
		List<Station> stations = Utils.getAllStationsInGrid(grid, this);

		for (int i = 0; i < relays.size(); i++) {
			try {
				KeyPair keys = KeyPairGenerator.getInstance("RSA").generateKeyPair();
				keyRing.put(relays.get(i).getRelayId(), keys);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < stations.size(); i++) {
			try {
				KeyPair keys = KeyPairGenerator.getInstance("RSA").generateKeyPair();
				keyRing.put(stations.get(i).getStationId(), keys);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}

	// make a random relay crash indefinitely with probability p
	@ScheduledMethod(start = 1, interval = 100)
	public void makeCrash() {
		Random r = new Random();
		int hasCrashed = r.nextInt(100) + 1;

		if (hasCrashed <= probCrash) {
			// get all relays
			List<Relay> relays = Utils.getAllRelaysInGrid(grid, this);
			if (relays.size() > 0 && relays.size() > minNum) {
				Relay relayToCrash = relays.remove(r.nextInt(relays.size()));
				System.out.println("Crashed a relay");
				Context<Object> context = ContextUtils.getContext(relayToCrash);
				context.remove(relayToCrash);
			}
		}
	}

	// make a new relay appear with probability p
	@ScheduledMethod(start = 1, interval = 100)
	public void createNew() {
		Random r = new Random();
		int hasNew = r.nextInt(100) + 1;

		if (hasNew <= probNew) {
			// get all relays
			List<Relay> relays = Utils.getAllRelaysInGrid(grid, this);
			if (relays.size() < maxNum) {
				var id = "relay" + nextRelayId;
				nextRelayId++;
				Relay relay = Utils.instantiateCorrectRelayVersion(space, grid, id);
				Context<Object> context = ContextUtils.getContext(this);
				context.add(relay);
				System.out.println("Created new " + id);

				NdPoint pt = space.getLocation(relay);
				grid.moveTo(relay, (int) pt.getX(), (int) pt.getY());
			}
		}
	}
}
