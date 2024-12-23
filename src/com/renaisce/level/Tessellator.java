package com.renaisce.level;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

/* 镶嵌器类，用于处理3D图形的顶点数据，支持纹理和颜色 */
public class Tessellator {
	private static final int MAX_VERTICES = 100000;															// 最大顶点数

	private final FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(MAX_VERTICES * 3);				// 顶点缓冲区
	private final FloatBuffer textureCoordinateBuffer = BufferUtils.createFloatBuffer(MAX_VERTICES * 2);	// 纹理坐标缓冲区
	private final FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(MAX_VERTICES * 3);				// 颜色缓冲区

	private int vertices = 0;				// 当前顶点数

	/* 纹理相关 */
	private boolean hasTexture = false;		// 是否有纹理
	private float textureU;					// 纹理U坐标
	private float textureV;					// 纹理V坐标

	/* 颜色相关 */
	private boolean hasColor;				// 是否有颜色
	private float red;						// 红色分量
	private float green;					// 绿色分量
	private float blue;						// 蓝色分量

	/* 重置缓冲区 */
	public void init() {
		clear();
	}

	/* 向缓冲区添加一个顶点点 */
	/* x 顶点的x坐标 */
	/* y 顶点的y坐标 */
	/* z 顶点的z坐标 */
	public void vertex(float x, float y, float z) {
		/* 顶点 */
		this.vertexBuffer.put(this.vertices * 3, x);
		this.vertexBuffer.put(this.vertices * 3 + 1, y);
		this.vertexBuffer.put(this.vertices * 3 + 2, z);

		/* 纹理坐标 */
		if (this.hasTexture) {
			this.textureCoordinateBuffer.put(this.vertices * 2, this.textureU);
			this.textureCoordinateBuffer.put(this.vertices * 2 + 1, this.textureV);
		}

		/* 颜色坐标 */
		if (this.hasColor) {
			this.colorBuffer.put(this.vertices * 3, this.red);
			this.colorBuffer.put(this.vertices * 3 + 1, this.green);
			this.colorBuffer.put(this.vertices * 3 + 2, this.blue);
		}

		this.vertices++;

		/* 如果缓冲区中的顶点数太多，则清空缓冲区 */
		if (this.vertices == MAX_VERTICES) {
			flush();
		}
	}

	/* 添加一个顶点并设置纹理UV映射 */
	/* x 顶点的x坐标 */
	/* y 顶点的y坐标 */
	/* z 顶点的z坐标 */
	/* textureU 纹理的U坐标 */
	/* textureV 纹理的V坐标 */
	public void vertexUV(float x, float y, float z, float textureU, float textureV) {
		texture(textureU, textureV);
		vertex(x, y, z);
	}

	/* 设置纹理UV映射 */
	/* textureU 纹理的U坐标 */
	/* textureV 纹理的V坐标 */
	public void texture(float textureU, float textureV) {
		this.hasTexture = true;
		this.textureU = textureU;
		this.textureV = textureV;
	}

	/* 设置RGB颜色 */
	/* red 红色分量（0.0 - 1.0） */
	/* green 绿色分量（0.0 - 1.0） */
	/* blue 蓝色分量（0.0 - 1.0） */
	public void color(float red, float green, float blue) {
		this.hasColor = true;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/* 渲染缓冲区 */
	public void flush() {
		this.vertexBuffer.flip();
		this.textureCoordinateBuffer.flip();

		/* 设置顶点 */
		glVertexPointer(3, GL_POINTS, this.vertexBuffer);
		if (this.hasTexture) {
			glTexCoordPointer(2, GL_POINTS, this.textureCoordinateBuffer);
		}
		if (this.hasColor) {
			glColorPointer(3, GL_POINTS, this.colorBuffer);
		}

		/* 启用客户端状态 */
		glEnableClientState(GL_VERTEX_ARRAY);
		if (this.hasTexture) {
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		}
		if (this.hasColor) {
			glEnableClientState(GL_COLOR_ARRAY);
		}

		/* 绘制四边形 */
		glDrawArrays(GL_QUADS, GL_POINTS, this.vertices);

		/* 渲染后重置 */
		glDisableClientState(GL_VERTEX_ARRAY);
		if (this.hasTexture) {
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		}
		if (this.hasColor) {
			glDisableClientState(GL_COLOR_ARRAY);
		}
		clear();
	}

	/* 重置顶点缓冲区 */
	private void clear() {
		this.vertexBuffer.clear();
		this.textureCoordinateBuffer.clear();
		this.vertices = 0;

		this.hasTexture = false;
		this.hasColor = false;
	}
}
