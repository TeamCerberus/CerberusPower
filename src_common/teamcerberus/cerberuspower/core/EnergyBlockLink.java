package teamcerberus.cerberuspower.core;

import teamcerberus.cerberuspower.util.ElectricityDirection;

public class EnergyBlockLink {
	public ElectricityDirection	direction;
	public double				loss;

	public EnergyBlockLink(ElectricityDirection direction, double loss) {
		this.direction = direction;
		this.loss = loss;
	}
}
