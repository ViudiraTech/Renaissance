package com.renaisce.level;

import com.renaisce.Player;

import java.util.Comparator;

/* 渲染排序类，用于排序Chunk渲染顺序的比较器 */
public class DirtyChunkSorter implements Comparator<Chunk> {
	private final long now = System.currentTimeMillis();	// 当前时间戳

	private final Player player;							// 玩家对象，用于距离优先级
	private final Frustum frustum;							// 视锥体，用于可见性优先级

	/* 构造一个DirtyChunkSorter对象 */
	/* player 玩家对象，用于距离优先级 */
	/* frustum 视锥体，用于可见性优先级 */
	public DirtyChunkSorter(Player player, Frustum frustum) {
		this.player = player;
		this.frustum = frustum;
	}

	/* 比较两个Chunk对象，确定它们的渲染顺序 */
	/* chunk1 第一个Chunk对象 */
	/* chunk2 第二个Chunk对象 */
	/* return 比较结果，-1表示chunk1优先级高，1表示chunk2优先级高，0表示优先级相同 */
	@Override
	public int compare(Chunk chunk1, Chunk chunk2) {
		/* 如果两个Chunk是同一个实例，返回0 */
		if (chunk1.equals(chunk2))
			return 0;

		boolean chunk1Visible = this.frustum.isVisible(chunk1.boundingBox); // chunk1是否在视锥体内
		boolean chunk2Visible = this.frustum.isVisible(chunk2.boundingBox); // chunk2是否在视锥体内

		/* 如果一个Chunk可见而另一个不可见，则返回优先级 */
		if (chunk1Visible && !chunk2Visible) {
			return -1;
		}
		if (chunk2Visible && !chunk1Visible) {
			return 1;
		}

		// 获取自上次Chunk更新以来的持续时间
		int dirtyDuration1 = (int) ((this.now - chunk1.dirtiedTime) / 2000L); // chunk1的脏持续时间
		int dirtyDuration2 = (int) ((this.now - chunk2.dirtiedTime) / 2000L); // chunk2的脏持续时间

		/* 如果一个Chunk的脏持续时间更长，则返回优先级 */
		if (dirtyDuration1 < dirtyDuration2) {
			return -1;
		}
		if (dirtyDuration1 > dirtyDuration2) {
			return 1;
		}

		/* 使用到玩家的距离来决定优先级 */
		return (chunk1.distanceToSqr(this.player) < chunk2.distanceToSqr(this.player)) ? -1 : 1;
	}
}
