package broadcastOnlyCommunication.Styles;

import java.awt.Color;
import java.awt.Font;

import broadcastOnlyCommunication.Perturbation;
import broadcastOnlyCommunication.RelayIII;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class PerturbationStyle extends DefaultStyleOGL2D {
	@Override
	public Color getColor(Object o) {
		Perturbation p = (Perturbation) o;

		if (p.val.equals(RelayIII.ARQ_VAL)) {
			return Color.LIGHT_GRAY;
		} else {
			return Color.ORANGE;
		}
	}

	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		
		Perturbation p = (Perturbation) agent;
		return shapeFactory.createCircle(p.getRadius(), 30);
	}
	
	@Override
	public String getLabel(Object o) {
		Perturbation p = (Perturbation) o;
		return p.getSenderId();
	}
	
	@Override
	public Font getLabelFont(Object o) {
		return new Font("TimesRoman", Font.PLAIN, 9);
	}
	
	@Override
	public float getScale(Object o) {
		return 15f;
	}
}