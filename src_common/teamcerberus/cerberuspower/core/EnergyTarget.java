package teamcerberus.cerberuspower.core;

import teamcerberus.cerberuspower.util.ElectricityDirection;
import net.minecraft.tileentity.TileEntity;

public class EnergyTarget {
	public TileEntity			tileEntity;
	public ElectricityDirection	direction;

	EnergyTarget(TileEntity tileEntity, ElectricityDirection direction) {
		this.tileEntity = tileEntity;
		this.direction = direction;
	}
}
