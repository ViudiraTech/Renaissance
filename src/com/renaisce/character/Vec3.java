package com.renaisce.character;

/* 三维向量类，包含三个浮点数值 */
public class Vec3 {
	public float x; // X轴分量
	public float y; // Y轴分量
	public float z; // Z轴分量

	/* 构造一个包含三个浮点数值的向量对象 */
	/* x X轴的值 */
	/* y Y轴的值 */
	/* z Z轴的值 */
	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/* 创建一个从当前向量位置到给定向量的插值向量 */
	/* vector 目标向量 */
	/* partialTicks 插值进度 */
	/* return 两个位置之间的插值向量 */
	public Vec3 interpolateTo(Vec3 vector, float partialTicks) {
		float interpolatedX = this.x + (vector.x - this.x) * partialTicks;
		float interpolatedY = this.y + (vector.y - this.y) * partialTicks;
		float interpolatedZ = this.z + (vector.z - this.z) * partialTicks;
		return new Vec3(interpolatedX, interpolatedY, interpolatedZ);
	}

	/* 设置向量的X、Y和Z值 */
	/* x X轴的值 */
	/* y Y轴的值 */
	/* z Z轴的值 */
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
