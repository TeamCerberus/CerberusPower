package teamcerberus.cerberuspower.prefab;

import teamcerberus.cerberuspower.api.IElectricityConductor;
import teamcerberus.cerberuspower.core.ElectricityRegistry;
import teamcerberus.cerberuspower.util.ElectricityDirection;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityConductor extends TileEntity implements
		IElectricityConductor {
	private boolean	addedToElectricityNetwork;

	@Override
	public boolean acceptsEnergyFrom(TileEntity paramTileEntity,
			ElectricityDirection paramDirection) {
		return true;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity paramTileEntity,
			ElectricityDirection paramDirection) {
		return true;
	}

	@Override
	public boolean isAddedToElectricityNetwork() {
		return addedToElectricityNetwork;
	}

	public void addToElectricityNetwork() {
		addedToElectricityNetwork = true;
		ElectricityRegistry.getInstance().getInstanceForWorld(worldObj)
				.addTileEntity(this);
	}

	public void removeFromElectricityNetwork() {
		addedToElectricityNetwork = false;
		ElectricityRegistry.getInstance().getInstanceForWorld(worldObj)
				.removeTileEntity(this);
	}

	@Override
	public void validate() {
		super.validate();
		addToElectricityNetwork();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		removeFromElectricityNetwork();
	}
}
