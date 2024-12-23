package com.renaisce.level.tile;

import com.renaisce.level.Level;

import java.util.Random;

/* 特殊方块草地类 */
public class GrassTile extends Tile {
	/* 创建一个带有ID的草地方块 */
	/* id 草地方块的ID */
	protected GrassTile(int id) {
		super(id);
		this.textureId = 3;
	}

	/* 获取指定面的纹理ID */
	/* face 方块的面（0表示底部，1表示顶部） */
	/* return 对应面的纹理ID */
	@Override
	protected int getTexture(int face) {
		/* 草地方块的纹理映射 */
		return face == 1 ? 0 : face == 0 ? 2 : 3;
	}

	/* 每个游戏刻调用，用于方块的更新逻辑 */
	/* level 当前的游戏等级对象 */
	/* x 方块的X坐标 */
	/* y 方块的Y坐标 */
	/* z 方块的Z坐标 */
	/* random 用于生成随机数的Random对象 */
	@Override
	public void onTick(Level level, int x, int y, int z, Random random) {
		if (level.isLit(x, y, z)) {
			/* 如果方块被阳光照射 */
			for (int i = 0; i < 4; ++i) {
				int targetX = x + random.nextInt(3) - 1;
				int targetY = y + random.nextInt(5) - 3;
				int targetZ = z + random.nextInt(3) - 1;

				/* 如果目标是泥土方块并且有阳光照射 */
				if (level.getTile(targetX, targetY, targetZ) == Tile.dirt.id && level.isLit(targetX, targetY, targetZ)) {

					/* 将目标方块设置为草地 */
					level.setTile(targetX, targetY, targetZ, Tile.grass.id);
				}
			}
		} else {
			/* 如果方块没有被阳光照射，将其设置为泥土 */
			level.setTile(x, y, z, Tile.dirt.id);
		}
	}
}
