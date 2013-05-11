package teamcerberus.cerberuspower.core;

import teamcerberus.cerberuspower.util.ElectricityDirection;

public class EnergyBlockLink {
	public ElectricityDirection	direction;
	public float				loss;

	public EnergyBlockLink(ElectricityDirection direction, float loss) {
		this.direction = direction;
		this.loss = loss;
	}
}
