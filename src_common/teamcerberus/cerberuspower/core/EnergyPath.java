package teamcerberus.cerberuspower.core;

import java.util.HashSet;

import teamcerberus.cerberuspower.api.IElectricityConductor;
import teamcerberus.cerberuspower.util.ElectricityDirection;

import net.minecraft.tileentity.TileEntity;

public class EnergyPath {
	public TileEntity						target					= null;
	public ElectricityDirection				targetDirection;
	public HashSet<IElectricityConductor>	conductors				= new HashSet<IElectricityConductor>();

	public int								minX					= 2147483647;
	public int								minY					= 2147483647;
	public int								minZ					= 2147483647;
	public int								maxX					= -2147483648;
	public int								maxY					= -2147483648;
	public int								maxZ					= -2147483648;

	public double							loss					= 0.0D;
	public long								totalEnergyConducted	= 0L;
}
