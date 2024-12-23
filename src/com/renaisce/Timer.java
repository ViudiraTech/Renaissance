package com.renaisce;

/* 定时器类，控制游戏中的更新频率 */
public class Timer {
	private static final long NS_PER_SECOND = 1000000000L;		// 每秒的纳秒数
	private static final long MAX_NS_PER_UPDATE = 1000000000L;	// 每次更新允许的最大纳秒数
	private static final int MAX_TICKS_PER_UPDATE = 100;		// 每次更新允许的最大刻度数

	/* 每秒的刻度数 */
	private final float ticksPerSecond;

	/* 上次更新的时间（纳秒） */
	private long lastTime = System.nanoTime();

	/* 缩放刻度速度的比例因子 */
	public float timeScale = 1.0F;

	/* 帧率，即每秒更新的次数 */
	public float fps = 0.0F;

	/* 自上次游戏更新以来经过的时间 */
	public float passedTime = 0.0F;

	/* 当前游戏更新的刻度数，它是经过时间作为整数的结果 */
	public int ticks;

	/* 当前刻度的溢出部分，由于将经过时间转换为整数而产生 */
	public float partialTicks;

	/* 构造函数，用于初始化定时器并设置每秒的刻度数 */
	/* ticksPerSecond 每秒的刻度数 */
	public Timer(float ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
	}

	/* 计算达到ticksPerSecond所需的刻度数 */
	/* 应在游戏的主渲染循环中调用此函数 */
	public void advanceTime() {
		long now = System.nanoTime();
		long passedNs = now - this.lastTime;

		/* 存储此次更新的纳秒时间 */
		this.lastTime = now;

		/* 最大值和最小值限制 */
		passedNs = Math.max(0, passedNs);
		passedNs = Math.min(MAX_NS_PER_UPDATE, passedNs);

		/* 计算帧率 */
		this.fps = (float) (NS_PER_SECOND / passedNs);

		/* 计算经过的时间和刻度数 */
		this.passedTime += passedNs * this.timeScale * this.ticksPerSecond / NS_PER_SECOND;
		this.ticks = (int) this.passedTime;

		/* 最大刻度数限制每次更新 */
		this.ticks = Math.min(MAX_TICKS_PER_UPDATE, this.ticks);

		/* 计算当前刻度的溢出部分 */
		this.passedTime -= this.ticks;
		this.partialTicks = this.passedTime;
	}
}
