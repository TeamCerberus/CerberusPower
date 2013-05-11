package teamcerberus.cerberuspower.api;

import teamcerberus.cerberuspower.core.IElectricityAcceptor;
import teamcerberus.cerberuspower.core.IElectricityEmitter;

public interface IElectricityConductor extends IElectricityAcceptor,
		IElectricityEmitter {
	public float getElectricityLoss();
}
