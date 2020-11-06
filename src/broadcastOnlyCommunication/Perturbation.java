package broadcastOnlyCommunication;

public class Perturbation {
	public String src; // ID of the sender
	public Integer ref;
	public String val;

	public Perturbation(String src, Integer ref, String val) {
		this.src = src;
		this.ref = ref;
		this.val = val;
	}
}
