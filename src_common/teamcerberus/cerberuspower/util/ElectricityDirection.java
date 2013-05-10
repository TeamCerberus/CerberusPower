package teamcerberus.cerberuspower.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public enum ElectricityDirection {
	XN(0), XP(1), YN(2), YP(3), ZN(4), ZP(5);

	private int									dir;
	public static final ElectricityDirection[]	directions	= values();

	private ElectricityDirection(int dir) {
		this.dir = dir;
	}

	public TileEntity applyToTileEntity(TileEntity tileEntity) {
		int[] coords = { tileEntity.xCoord, tileEntity.yCoord,
				tileEntity.zCoord };

		coords[dir / 2] += getSign();

		if (tileEntity.worldObj != null
				&& tileEntity.worldObj.blockExists(coords[0], coords[1],
						coords[2])) { return tileEntity.worldObj
				.getBlockTileEntity(coords[0], coords[1], coords[2]); }
		return null;
	}

	public ElectricityDirection getInverse() {
		int inverseDir = dir - getSign();

		for (ElectricityDirection direction : directions) {
			if (direction.dir == inverseDir) {
				return direction;
			}
		}

		return this;
	}

	public int toSideValue() {
		return (dir + 4) % 6;
	}

	private int getSign() {
		return dir % 2 * 2 - 1;
	}

	public ForgeDirection toForgeDirection() {
		return ForgeDirection.getOrientation(toSideValue());
	}
}
