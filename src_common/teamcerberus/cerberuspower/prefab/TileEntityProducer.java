package teamcerberus.cerberuspower.prefab;

import net.minecraft.tileentity.TileEntity;
import teamcerberus.cerberuspower.api.IElectricityProducer;
import teamcerberus.cerberuspower.core.ElectricityRegistry;
import teamcerberus.cerberuspower.util.ElectricityDirection;

public abstract class TileEntityProducer extends TileEntity implements
		IElectricityProducer {
	private boolean	addedToElectricityNetwork;

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

	public void emitElectricity(int amount) {
		ElectricityRegistry.getInstance().getInstanceForWorld(worldObj)
				.produceElectricity(this, amount);
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