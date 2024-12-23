package com.renaisce.character;

import static org.lwjgl.opengl.GL11.*;

/* 多边形类，用于表示一个由顶点组成的图形 */
public class Polygon {
	public Vertex[] vertices;	// 多边形的顶点数组
	public int vertexCount;		// 顶点的数量

	/* 创建一个没有UV映射的多边形 */
	/* vertices 顶点数组 */
	public Polygon(Vertex[] vertices) {
		this.vertices = vertices;
		this.vertexCount = vertices.length;
	}

	/* 创建一个绑定了UV映射的多边形 */
	/* vertices 顶点数组 */
	/* minU UV映射的最小U坐标 */
	/* minV UV映射的最小V坐标 */
	/* maxU UV映射的最大U坐标 */
	/* maxV UV映射的最大V坐标 */
	public Polygon(Vertex[] vertices, int minU, int minV, int maxU, int maxV) {
		this(vertices);

		/* 为顶点映射UV */
		vertices[0] = vertices[0].remap(maxU, minV);
		vertices[1] = vertices[1].remap(minU, minV);
		vertices[2] = vertices[2].remap(minU, maxV);
		vertices[3] = vertices[3].remap(maxU, maxV);
	}

	/* 渲染多边形 */
	public void render() {
		/* 设置多边形的颜色 */
		glColor3f(1.0F, 1.0F, 1.0F);

		/* 渲染所有顶点 */
		for (int i = 3; i >= 0; i--) {
			Vertex vertex = this.vertices[i];

			/* 绑定UV映射 */
			glTexCoord2f(vertex.u / 64.0F, vertex.v / 32.0F);

			/* 渲染顶点 */
			glVertex3f(vertex.position.x, vertex.position.y, vertex.position.z);
		}
	}
}
