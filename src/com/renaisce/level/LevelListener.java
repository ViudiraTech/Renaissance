package com.renaisce.level;

/* 存档监听类，用于接收关于存档变化的事件 */
public interface LevelListener {
	/* 当一个方块的光照级别发生变化时被调用 */
	/* x 光照变化的方块的X坐标 */
	/* z 光照变化的方块的Z坐标 */
	/* minY 光照变化区域的最小Y坐标（范围开始） */
	/* maxY 光照变化区域的最大Y坐标（范围结束） */
	void lightColumnChanged(int x, int z, int minY, int maxY);

	/* 当一个方块的类型发生变化时被调用 */
	/* x 变化方块的X坐标 */
	/* y 变化方块的Y坐标 */
	/* z 变化方块的Z坐标 */
	void tileChanged(int x, int y, int z);

	/* 当整个等级发生变化时被调用 */
	void allChanged();
}
