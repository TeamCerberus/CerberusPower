package teamcerberus.cerberuspower.core;

import teamcerberus.cerberuspower.util.ElectricityDirection;
import net.minecraft.tileentity.TileEntity;

public interface IElectricityEmitter extends IElectricityTile {
	public boolean emitsEnergyTo(TileEntity paramTileEntity,
			ElectricityDirection paramDirection);
}
