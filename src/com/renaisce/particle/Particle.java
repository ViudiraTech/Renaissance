package com.renaisce.particle;

import com.renaisce.Entity;
import com.renaisce.level.Level;
import com.renaisce.level.Tessellator;

/* 粒子实体类，用于创建和管理粒子 */
public class Particle extends Entity {
	/* 粒子的纹理ID */
	public int textureId;

	/* 粒子的纹理U偏移和V偏移 */
	private final float textureUOffset;
	private final float textureVOffset;

	/* 粒子的大小 */
	private final float size;

	/* 粒子的生命周期 */
	private final int lifetime;

	/* 粒子的年龄 */
	private int age = 0;

	/* 构造一个粒子实体 */
	/* level 所在的层级 */
	/* x 粒子位置x */
	/* y 粒子位置y */
	/* z 粒子位置z */
	/* motionX 粒子运动x */
	/* motionY 粒子运动y */
	/* motionZ 粒子运动z */
	/* textureId 粒子的纹理槽ID */
	public Particle(Level level, double x, double y, double z, double motionX, double motionY, double motionZ, int textureId) {
		super(level);

		/* 设置纹理 */
		this.textureId = textureId;

		/* 设置粒子大小 */
		setSize(0.2F, 0.2F);
		this.heightOffset = this.boundingBoxHeight / 2.0F;

		/* 设置位置 */
		setPosition(x, y, z);

		/* 设置运动并添加随机值 */
		this.motionX = motionX + (Math.random() * 2.0D - 1.0D) * 0.4D;
		this.motionY = motionY + (Math.random() * 2.0D - 1.0D) * 0.4D;
		this.motionZ = motionZ + (Math.random() * 2.0D - 1.0D) * 0.4D;

		/* 创建随机速度 */
		double speed = (Math.random() + Math.random() + 1.0D) * 0.15D;

		/* 应用速度 */
		double distance = Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
		this.motionX = this.motionX / distance * speed * 0.7D;
		this.motionY = this.motionY / distance * speed;
		this.motionZ = this.motionZ / distance * speed * 0.7D;

		/* 创建随机纹理偏移 */
		this.textureUOffset = (float) Math.random() * 3.0F;
		this.textureVOffset = (float) Math.random() * 3.0F;

		this.size = (float) (Math.random() * 0.5D + 0.5D);
		this.lifetime = (int) (4.0D / (Math.random() * 0.9D + 0.1D));
	}

	@Override
	public void onTick() {
		super.onTick();

		/* 随机销毁 */
		if (this.age++ >= this.lifetime) {
			remove();
		}

		/* 应用重力 */
		this.motionY -= 0.06D;

		/* 使用运动移动粒子 */
		this.move(this.motionX, this.motionY, this.motionZ);

		/* 减少运动速度 */
		this.motionX *= 0.98D;
		this.motionY *= 0.98D;
		this.motionZ *= 0.98D;

		/* 在地面上减少运动速度 */
		if (this.onGround) {
			this.motionX *= 0.7D;
			this.motionZ *= 0.7D;
		}
	}

	/* 渲染粒子 */
	/* tessellator 用于渲染的Tessellator */
	/* partialTicks 用于插值的刻度数 */
	/* cameraX 相机旋转X */
	/* cameraY 相机旋转Y */
	/* cameraZ 相机旋转Z */
	/* cameraXWithY 包含Y旋转的额外相机旋转X */
	/* cameraZWithY 包含Y旋转的额外相机旋转Z */
	public void render(Tessellator tessellator, float partialTicks, float cameraX, float cameraY, float cameraZ, float cameraXWithY, float cameraZWithY) {
		/* UV映射点 */
		float minU = (this.textureId % 16 + this.textureUOffset / 4.0F) / 16.0F;
		float maxU = minU + 999.0F / 64000.0F;
		float minV = ((float) (this.textureId / 16) + this.textureVOffset / 4.0F) / 16.0F;
		float maxV = minV + 999.0F / 64000.0F;

		/* 插值位置 */
		float x = (float) (this.prevX + (this.x - this.prevX) * partialTicks);
		float y = (float) (this.prevY + (this.y - this.prevY) * partialTicks);
		float z = (float) (this.prevZ + (this.z - this.prevZ) * partialTicks);

		/* 粒子大小 */
		float size = this.size * 0.1F;

		/* 渲染顶点 */
		tessellator.vertexUV(x - cameraX * size - cameraXWithY * size, y - cameraY * size, z - cameraZ * size - cameraZWithY * size, minU, maxV);
		tessellator.vertexUV(x - cameraX * size + cameraXWithY * size, y + cameraY * size, z - cameraZ * size + cameraZWithY * size, minU, minV);
		tessellator.vertexUV(x + cameraX * size + cameraXWithY * size, y + cameraY * size, z + cameraZ * size + cameraZWithY * size, maxU, minV);
		tessellator.vertexUV(x + cameraX * size - cameraXWithY * size, y - cameraY * size, z + cameraZ * size - cameraZWithY * size, maxU, maxV);
	}
}
