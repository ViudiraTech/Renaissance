package com.renaisce;

/* 点击结果类，鼠标点击交互结果 */
public class HitResult {
	public int type;	// 结果的类型

	public int x;		// 方块的位置x坐标
	public int y;		// 方块的位置y坐标
	public int z;		// 方块的位置z坐标

	public int face;	// 方块的面标识

	/* 鼠标上方的目标方块 */
	/* type 结果的类型 */
	/* x 瓷砖的位置x */
	/* y 瓷砖的位置y */
	/* z 瓷砖的位置z */
	/* face 瓷砖的面id */
	public HitResult(int type, int x, int y, int z, int face) {
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.face = face;
	}
}
