package teamcerberus.cerberuspower.api;

import teamcerberus.cerberuspower.core.IElectricityAcceptor;
import teamcerberus.cerberuspower.util.ElectricityDirection;

public interface IElectricityConsumer extends IElectricityAcceptor {
	public int electricityWanted();

	public int giveElectricity(ElectricityDirection paramDirection,
			int paramInt);
}
