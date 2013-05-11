package teamcerberus.cerberuspower.core;

import teamcerberus.cerberuspower.util.ElectricityDirection;
import net.minecraft.tileentity.TileEntity;

public interface IElectricityAcceptor extends IElectricityTile {
	public boolean acceptsEnergyFrom(TileEntity paramTileEntity,
			ElectricityDirection paramDirection);

}
