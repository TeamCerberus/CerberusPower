package teamcerberus.cerberuspower.api;

import teamcerberus.cerberuspower.core.IElectricityEmitter;

public interface IElectricityProducer extends IElectricityEmitter {
	public int getElectricityOutputLimit();
}
