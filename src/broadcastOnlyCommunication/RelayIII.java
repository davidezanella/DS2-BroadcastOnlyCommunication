package broadcastOnlyCommunication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class RelayIII extends Relay {

	public static final String ARQ_VAL = "!ARQ";

	private final Map<String, List<Perturbation>> history = new HashMap<>();
	
	public RelayIII(ContinuousSpace<Object> space, Grid<Object> grid, String id) {
		super(space, grid, id);
	}

	@ScheduledMethod(start = 1, interval = 5)
	public void askForMissingPerturbations() {
		GridPoint pt = grid.getLocation(this);
		Utils.getAllStationsInGrid(grid, pt)
			.forEach(this::askForMissingPerturbationsFrom);
	}
	
	private void askForMissingPerturbationsFrom(Station src) {
		int nextRef = 1;
		var arq = new Perturbation(src.id, nextRef, ARQ_VAL);
		forwardPerturbation(arq);
	}
	
	@Override
	public void processPerturbation(Perturbation p) {
		if (p.val.equals(ARQ_VAL)) {
			processArqPerturbation(p);
		} else {
			processDataPerturbation(p);
		}
	}

	private void processArqPerturbation(Perturbation arq) {
		getHistoryOfStation(arq.src)
			.stream()
			.filter(p -> p.ref == arq.ref)
			.findFirst()
			.ifPresent(this::forwardPerturbation);
	}

	private void processDataPerturbation(Perturbation p) {
		var historyOfStation = getHistoryOfStation(p.src);
		if (p.ref == expectedNextRefOf(historyOfStation)) {
			forwardPerturbation(p);
			getHistoryOfStation(p.src).add(p);	
		}
	}
	
	private List<Perturbation> getHistoryOfStation(String src) {
		return history.computeIfAbsent(src, k -> new ArrayList<>());
	}
	
	private static int expectedNextRefOf(List<Perturbation> history) {
		return history.isEmpty() ? 0 : history.get(history.size() - 1).ref;
	}
}
