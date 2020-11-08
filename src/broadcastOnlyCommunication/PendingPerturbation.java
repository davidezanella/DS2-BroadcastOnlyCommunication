package broadcastOnlyCommunication;

public class PendingPerturbation {
	private int remainingTicks;
	public final Perturbation perturbation;
	
	public PendingPerturbation(int ticks, Perturbation perturbation) {
		this.remainingTicks = ticks;
		this.perturbation = perturbation;
	}
	
	public void onTick() {
		if (this.remainingTicks < 0) {
			throw new IllegalStateException("Perturbation should have been already processed");
		}
		this.remainingTicks--;
	}
	
	public boolean isTimeToProcess() {
		return this.remainingTicks <= 0;
	}
}
