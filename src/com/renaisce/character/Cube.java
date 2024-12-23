package com.renaisce.character;

import static org.lwjgl.opengl.GL11.*;

/* 立方体类，管理立方体相关 */
public class Cube {
	private Polygon[] polygons; // 立方体的六个面

	private int textureOffsetX; // 纹理在X轴上的偏移量
	private int textureOffsetY; // 纹理在Y轴上的偏移量

	public float x; // 立方体的X坐标
	public float y; // 立方体的Y坐标
	public float z; // 立方体的Z坐标

	public float xRotation; // 立方体绕X轴的旋转角度
	public float yRotation; // 立方体绕Y轴的旋转角度
	public float zRotation; // 立方体绕Z轴的旋转角度

	/* 创建立方体对象 */
	/* textureOffsetX 纹理在X轴上的偏移量 */
	/* textureOffsetY 纹理在Y轴上的偏移量 */
	public Cube(int textureOffsetX, int textureOffsetY) {
		/* 初始化纹理偏移量 */
		this.textureOffsetX = textureOffsetX;
		this.textureOffsetY = textureOffsetY;
	}

	/* 设置立方体的纹理偏移量 */
	/* textureOffsetX 纹理在X轴上的偏移量 */
	/* textureOffsetY 纹理在Y轴上的偏移量 */
	public void setTextureOffset(int textureOffsetX, int textureOffsetY) {
		/* 初始化纹理偏移量 */
		this.textureOffsetX = textureOffsetX;
		this.textureOffsetY = textureOffsetY;
	}

	/* 使用偏移位置和尺寸创建立方体 */
	/* offsetX 渲染位置的X偏移 */
	/* offsetY 渲染位置的Y偏移 */
	/* offsetZ 渲染位置的Z偏移 */
	/* width 立方体的宽度 */
	/* height 立方体的高度 */
	/* depth 立方体的深度 */
	/* return 返回立方体对象自身 */
	public Cube addBox(float offsetX, float offsetY, float offsetZ, int width, int height, int depth) {
		this.polygons = new Polygon[6];

		float x = offsetX + width;
		float y = offsetY + height;
		float z = offsetZ + depth;

		/* 初始化立方体的六个面 */
		Vertex vertexBottom1 = new Vertex(offsetX, offsetY, offsetZ, 0.0F, 0.0F);
		Vertex vertexBottom2 = new Vertex(x, offsetY, offsetZ, 0.0F, 8.0F);
		Vertex vertexBottom3 = new Vertex(offsetX, offsetY, z, 0.0F, 0.0F);
		Vertex vertexBottom4 = new Vertex(x, offsetY, z, 0.0F, 8.0F);

		/* 创建立方体底部的顶点 */
		Vertex vertexTop1 = new Vertex(x, y, z, 8.0F, 8.0F);
		Vertex vertexTop2 = new Vertex(offsetX, y, z, 8.0F, 0.0F);
		Vertex vertexTop3 = new Vertex(x, y, offsetZ, 8.0F, 8.0F);
		Vertex vertexTop4 = new Vertex(offsetX, y, offsetZ, 8.0F, 0.0F);

		/* 创建立方体每个面的多边形 */
		this.polygons[0] = new Polygon(
				new Vertex[]{
						vertexBottom4, vertexBottom2, vertexTop3, vertexTop1
				},
				this.textureOffsetX + depth + width,
				this.textureOffsetY + depth,
				this.textureOffsetX + depth + width + depth,
				this.textureOffsetY + depth + height
		);

		this.polygons[1] = new Polygon(
				new Vertex[]{
						vertexBottom1, vertexBottom3, vertexTop2, vertexTop4
				},
				this.textureOffsetX,
				this.textureOffsetY + depth,
				this.textureOffsetX + depth,
				this.textureOffsetY + depth + height
		);

		this.polygons[2] = new Polygon(
				new Vertex[]{
						vertexBottom4, vertexBottom3, vertexBottom1, vertexBottom2
				},
				this.textureOffsetX + depth,
				this.textureOffsetY,
				this.textureOffsetX + depth + width,
				this.textureOffsetY + depth
		);

		this.polygons[3] = new Polygon(
				new Vertex[]{
						vertexTop3, vertexTop4, vertexTop2, vertexTop1
				},
				this.textureOffsetX + depth + width,
				this.textureOffsetY,
				this.textureOffsetX + depth + width + width,
				this.textureOffsetY + depth
		);

		this.polygons[4] = new Polygon(
				new Vertex[]{
						vertexBottom2, vertexBottom1, vertexTop4, vertexTop3
				},
				this.textureOffsetX + depth,
				this.textureOffsetY + depth,
				this.textureOffsetX + depth + width,
				this.textureOffsetY + depth + height
		);

		this.polygons[5] = new Polygon(
				new Vertex[]{
						vertexBottom3, vertexBottom4, vertexTop1, vertexTop2
				},
				this.textureOffsetX + depth + width + depth,
				this.textureOffsetY + depth,
				this.textureOffsetX + depth + width + depth + width,
				this.textureOffsetY + depth + height
		);
		/* 返回立方体对象自身 */
		return this;
	}

	/* 设置立方体的绝对位置 */
	/* x 立方体的绝对X位置 */
	/* y 立方体的绝对Y位置 */
	/* z 立方体的绝对Z位置 */
	public void setPosition(float x, float y, float z) {
		/* 设置立方体的位置 */
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/* 渲染立方体 */
	public void render() {
		glPushMatrix();

		/* 立方体的位置 */
		glTranslatef(this.x, this.y, this.z);

		/* 立方体的旋转 */
		glRotated(Math.toDegrees(this.zRotation), 0.0F, 0.0F, 1.0F);
		glRotated(Math.toDegrees(this.yRotation), 0.0F, 1.0F, 0.0F);
		glRotated(Math.toDegrees(this.xRotation), 1.0F, 0.0F, 0.0F);

		/* 开始渲染 */
		glBegin(GL_QUADS);

		/* 渲染多边形 */
		for (Polygon polygon : this.polygons) {
			polygon.render();
		}

		/* 结束渲染 */
		glEnd();

		glPopMatrix();
	}
}
