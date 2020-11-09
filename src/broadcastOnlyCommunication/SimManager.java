package broadcastOnlyCommunication;

import repast.simphony.context.Context;
import java.util.Random;
import java.util.List;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class SimManager {
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	/*probability that the simulation manager destroy or
	  creates a new relay, respectively
	  Value should be betwenn 1 and 100
	 */
	protected int probCrash;
	protected int probNew;
	//minimum and maximum number of relays
	protected int minNum;
	protected int maxNum;
	
	public SimManager(ContinuousSpace<Object> space, Grid<Object> grid, int probCrash, int probNew, int minNum, int maxNum){
		this.space = space;
		this.grid = grid;
		this.probCrash=probCrash;
		this.probNew=probNew;
		this.minNum=minNum;
		this.maxNum=maxNum;
	}
	
	//make a random relay crash indefinitely with probability p
	@ScheduledMethod(start = 1, interval = 100)
	public void makeCrash() {
		Random r = new Random();
		int hasCrashed = r.nextInt(100) + 1;
		
		if(hasCrashed >= probCrash) {
			//get all relays
			List<Relay> relays = Utils.getAllRelaysInGrid(grid, grid.getLocation(this));
			if(relays.size()>0 && relays.size()>minNum) {
				Relay relayToCrash = relays.remove(r.nextInt(relays.size()));
				System.out.println("Crashed a relay");
				Context<Object> context = ContextUtils.getContext(relayToCrash);
				context.remove(relayToCrash);
			}
		}
	}
	
	//make a new relay appear with probability p
	@ScheduledMethod(start = 1, interval = 100)
	public void createNew() {
		Random r = new Random();
		int hasNew = r.nextInt(100) + 1;
		
		if(hasNew >= probNew) {
			//get all relays
			List<Relay> relays = Utils.getAllRelaysInGrid(grid, grid.getLocation(this));
			if(relays.size()<maxNum) {
				var id = "relay" + relays.size();
				Relay relay = new RelayII(space, grid, id);
				Context<Object> context = ContextUtils.getContext(this);
				context.add(relay);
				System.out.println("Created new " + id);
				
				NdPoint pt = space.getLocation(relay);
				grid.moveTo(relay, (int) pt.getX(), (int) pt.getY());
			}
		}
	}
}
