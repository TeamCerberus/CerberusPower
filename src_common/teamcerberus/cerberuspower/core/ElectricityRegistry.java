package teamcerberus.cerberuspower.core;

import java.util.HashMap;

import net.minecraft.world.World;

public class ElectricityRegistry {
	private static ElectricityRegistry										instance;
	private HashMap<World, ElectricityNetwork>								electricityNetworks;
	private HashMap<Class<? extends IElectricityTile>, ElectricityEntity>	electricityEntities;

	public ElectricityRegistry() {
		electricityNetworks = new HashMap<World, ElectricityNetwork>();
	}

	public ElectricityNetwork getInstanceForWorld(World world) {
		if (!electricityNetworks.containsKey(world)) electricityNetworks.put(
				world, new ElectricityNetwork());
		return electricityNetworks.get(world);
	}
	
	public void registerElectricityTile(Class<? extends IElectricityTile> class_){
		
	}

	public ElectricityRegistry getInstance() {
		if (instance == null) instance = new ElectricityRegistry();
		return instance;
	}
}
