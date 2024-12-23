package com.renaisce.character;

/* 顶点类，包含一个三维向量位置和UV坐标 */
public class Vertex {
	public Vec3 position;	// 顶点的位置向量

	public float u;			// U纹理坐标
	public float v;			// V纹理坐标

	/* 构造一个包含三维向量位置和UV坐标的顶点 */
	/* x X坐标 */
	/* y Y坐标 */
	/* z Z坐标 */
	/* u U映射 */
	/* v V映射 */
	public Vertex(float x, float y, float z, float u, float v) {
		this(new Vec3(x, y, z), u, v);
	}

	/* 构造一个包含三维向量位置和UV坐标的顶点 */
	/* vertex 顶点对象，用于获取位置 */
	/* u U映射 */
	/* v V映射 */
	public Vertex(Vertex vertex, float u, float v) {
		this.position = vertex.position;
		this.u = u;
		this.v = v;
	}

	/* 构造一个包含三维向量位置和UV坐标的顶点 */
	/* position 向量位置 */
	/* u U映射 */
	/* v V映射 */
	public Vertex(Vec3 position, float u, float v) {
		this.position = position;
		this.u = u;
		this.v = v;
	}

	/* 创建一个新的顶点，其位置与当前顶点相同，但具有不同的UV映射 */
	/* u 新的U映射 */
	/* v 新的V映射 */
	/* return 具有当前顶点位置的新顶点 */
	public Vertex remap(float u, float v) {
		return new Vertex(this, u, v);
	}
}
