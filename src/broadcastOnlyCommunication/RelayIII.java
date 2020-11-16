package broadcastOnlyCommunication;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class RelayIII extends Relay {

	public static final String ARQ_VAL = "!ARQ";
	protected boolean useCrypto;

	private final Map<String, List<Perturbation>> history = new HashMap<>();

	public RelayIII(ContinuousSpace<Object> space, Grid<Object> grid, String id, boolean useCrypto) {
		super(space, grid, id);

		this.useCrypto = useCrypto;
	}

	@ScheduledMethod(start = 1, interval = 5)
	public void askForMissingPerturbations() {
		Utils.getAllStationsInGrid(grid, this).forEach(this::askForMissingPerturbationsFrom);
	}

	private void askForMissingPerturbationsFrom(Station src) {
		int nextRef = expectedNextRefOf(src);
		var arq = new Perturbation(space, grid, src.id, nextRef, ARQ_VAL);
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
		getHistoryOfStation(arq.senderId).stream().filter(p -> p.ref == arq.ref).findFirst()
				.ifPresent(this::forwardPerturbation);
	}

	private void processDataPerturbation(Perturbation p) {
		var historyOfStation = getHistoryOfStation(p.senderId);
		if (p.ref == expectedNextRefOf(historyOfStation)) {
			forwardPerturbation(p);
			getHistoryOfStation(p.senderId).add(p);

			// if the message is unicast and is for me
			if (p.receiverId != null && p.receiverId.equals(getId())) {
				var value = p.val;
				if (useCrypto) {
					try {
						var privKey = SimManager.keyRing.get(getRelayId()).getPrivate();
						byte[] decode = Base64.getDecoder().decode(value);
						Cipher rsa = Cipher.getInstance("RSA");
				        rsa.init(Cipher.DECRYPT_MODE, privKey);
				        byte[] utf8 = rsa.doFinal(decode);
				        value = new String(utf8, "UTF8");
					}
					catch(Exception e) {
						System.err.println("Error decrypting the message!");
						e.printStackTrace();
					}
					System.out.println("Message decrypted correctly!");
				}
				System.out.println("Received unicast msg from " + p.getSenderId() + " to " + getId() + " with value: " + value);
			}
		}
	}

	private List<Perturbation> getHistoryOfStation(String src) {
		return history.computeIfAbsent(src, k -> new ArrayList<>());
	}

	private int expectedNextRefOf(Station s) {
		var history = getHistoryOfStation(s.id);
		return expectedNextRefOf(history);
	}

	private static int expectedNextRefOf(List<Perturbation> history) {
		if (history.isEmpty()) {
			return 0;
		}
		var lastPerturbation = history.get(history.size() - 1);
		return nextRef(lastPerturbation);
	}
}
