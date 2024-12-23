package com.renaisce;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluBuild2DMipmaps;

/* 纹理类，在OpenGL中加载和绑定纹理 */
public class Textures {
	/* 存储纹理ID和资源名称的映射 */
	private static final Map<String, Integer> idMap = new HashMap<>();

	/* 存储上一个使用的纹理ID */
	private static int lastId = Integer.MIN_VALUE;

	/* 加载纹理到OpenGL */
	/* resourceName 图片资源的路径 */
	/* mode 纹理过滤模式（GL_NEAREST, GL_LINEAR) */
	/* return OpenGL的纹理ID */
	public static int loadTexture(String resourceName, int mode) {
		if (idMap.containsKey(resourceName)) {
			return idMap.get(resourceName);
		}

		/* 生成一个新的纹理ID */
		int id = glGenTextures();

		/* 将ID存储到映射中 */
		idMap.put(resourceName, id);

		/* 绑定这个纹理ID */
		bind(id);

		/* 设置纹理过滤模式 */
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, mode);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mode);

		/* 从资源中读取 */
		InputStream inputStream = Textures.class.getResourceAsStream(resourceName);

		try {
			/* 读取到缓冲图像 */
			BufferedImage bufferedImage = ImageIO.read(inputStream);

			/* 获取图像大小 */
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();

			/* 将图像像素写入数组 */
			int[] pixels = new int[width * height];
			bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);

			/* 翻转整数的RGB顺序 */
			for (int i = 0; i < pixels.length; i++) {
				int alpha = pixels[i] >> 24 & 0xFF;
				int red = pixels[i] >> 16 & 0xFF;
				int green = pixels[i] >> 8 & 0xFF;
				int blue = pixels[i] & 0xFF;

				// ARGB 到 ABGR
				pixels[i] = alpha << 24 | blue << 16 | green << 8 | red;
			}

			/* 从像素数组创建字节缓冲区 */
			ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * 4);
			byteBuffer.asIntBuffer().put(pixels);

			/* 将纹理写入OpenGL */
			gluBuild2DMipmaps(GL_TEXTURE_2D, GL_RGBA, width, height, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
		} catch (IOException exception) {
			throw new RuntimeException("Could not load texture " + resourceName, exception);
		}
		return id;
	}

	/* 使用 loadTexture 中的ID将纹理绑定到OpenGL */
	/* id 纹理ID */
	public static void bind(int id) {
		/* 如果传入的ID与上一个ID不同，则绑定纹理 */
		if (id != lastId) {
			glBindTexture(GL_TEXTURE_2D, id);
			lastId = id;
		}
	}
}
