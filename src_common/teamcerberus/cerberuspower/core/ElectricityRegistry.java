package teamcerberus.cerberuspower.core;

import java.util.HashMap;

import teamcerberus.cerberuspower.api.IElectricityConductor;
import teamcerberus.cerberuspower.api.IElectricityConsumer;
import teamcerberus.cerberuspower.api.IElectricityProducer;

import net.minecraft.world.World;

public class ElectricityRegistry {
	private static ElectricityRegistry										instance;
	private HashMap<World, ElectricityNetwork>								electricityNetworks;
	private HashMap<Class<? extends IElectricityTile>, ElectricityEntity>	electricityEntities;

	public ElectricityRegistry() {
		electricityNetworks = new HashMap<World, ElectricityNetwork>();
		electricityEntities = new HashMap<Class<? extends IElectricityTile>, ElectricityEntity>();
	}

	public ElectricityNetwork getInstanceForWorld(World world) {
		if (!electricityNetworks.containsKey(world)) {
			electricityNetworks.put(world, new ElectricityNetwork());
		}
		return electricityNetworks.get(world);
	}

	public void registerElectricityTile(Class<? extends IElectricityTile> class_) {
		ElectricityEntity entity = new ElectricityEntity();
		entity.electricityAcceptor = IElectricityAcceptor.class
				.isAssignableFrom(class_);
		entity.electricityConductor = IElectricityConductor.class
				.isAssignableFrom(class_);
		entity.electricityConsumer = IElectricityConsumer.class
				.isAssignableFrom(class_);
		entity.electricityEmitter = IElectricityEmitter.class
				.isAssignableFrom(class_);
		entity.electricityProducer = IElectricityProducer.class
				.isAssignableFrom(class_);
		entity.electricityTile = IElectricityTile.class
				.isAssignableFrom(class_);
		electricityEntities.put(class_, entity);
	}

	public ElectricityEntity getElectricityEntity(Class<?> class_) {
		return electricityEntities.get(class_);
	}

	public boolean isEntityRegistured(Class<?> class_) {
		return electricityEntities.containsKey(class_);
	}

	public static ElectricityRegistry getInstance() {
		if (instance == null) {
			instance = new ElectricityRegistry();
		}
		return instance;
	}
}
