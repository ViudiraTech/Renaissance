package com.renaisce.particle;

import com.renaisce.Player;
import com.renaisce.Textures;
import com.renaisce.level.Level;
import com.renaisce.level.Tessellator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/* 粒子引擎类，用于在游戏中处理和渲染粒子效果 */
public class ParticleEngine {
	protected final Level level;								// 引用游戏的存档(Level)对象

	private final List<Particle> particles = new ArrayList<>();	// 存储所有粒子的列表

	public ParticleEngine(Level level) {
		this.level = level;
	}

	/* 向引擎中添加一个粒子 */
	/* particle 要添加的粒子 */
	public void add(Particle particle) {
		this.particles.add(particle);
	}

	/* 更新所有粒子的状态，并移除已经被标记为死亡的粒子 */
	public void onTick() {
		/* 更新所有粒子 */
		Iterator<Particle> iterator = this.particles.iterator();
		while (iterator.hasNext()) {
			Particle particle = iterator.next();

			/* 更新这个粒子 */
			particle.onTick();

			/* 如果粒子被标记为移除，则从列表中移除 */
			if (particle.removed) {
				iterator.remove();
			}
		}
	}

	/* 渲染所有粒子 */
	/* player 玩家对象 */
	/* tessellator 渲染使用的Tessellator对象 */
	/* partialTicks 用于插值的Ticks */
	/* layer 阴影层 */
	public void render(Player player, Tessellator tessellator, float partialTicks, int layer) {
		glEnable(GL_TEXTURE_2D); // 启用2D纹理

		/* 绑定地形纹理 */
		int id = Textures.loadTexture("/terrain.png", 9728);
		glBindTexture(GL_TEXTURE_2D, id);

		/* 获取相机角度 */
		double cameraX = -Math.cos(Math.toRadians(player.yRotation));
		double cameraY = Math.cos(Math.toRadians(player.xRotation));
		double cameraZ = -Math.sin(Math.toRadians(player.yRotation));

		/* 获取额外的相机旋转 */
		double cameraXWithY = -cameraZ * Math.sin(Math.toRadians(player.xRotation));
		double cameraZWithY = cameraX * Math.sin(Math.toRadians(player.xRotation));

		/* 开始渲染 */
		glColor4f(0.8F, 0.8F, 0.8F, 1.0F);	// 设置颜色
		tessellator.init();					// 初始化Tessellator

		/* 渲染所有在正确层的粒子 */
		for (Particle particle : this.particles) {
			if (particle.isLit() ^ layer == 1) { // 如果粒子是照亮的且层是1，或者粒子不是照亮的且层不是1
				particle.render(tessellator, partialTicks, (float) cameraX, (float) cameraY, (float) cameraZ, (float) cameraXWithY, (float) cameraZWithY);
			}
		}

		/* 完成渲染 */
		tessellator.flush();		// 清空Tessellator缓冲区
		glDisable(GL_TEXTURE_2D);	// 禁用2D纹理
	}
}
