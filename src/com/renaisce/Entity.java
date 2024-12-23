package com.renaisce;

import com.renaisce.level.Level;
import com.renaisce.phys.AABB;

import java.util.List;

/* 实体类，表示游戏中的实体对象 */
public abstract class Entity {
	private final Level level;					// 实体的当前位置坐标

	public double x, y, z;						// 实体上一帧的位置坐标
	public double prevX, prevY, prevZ;
	public double motionX, motionY, motionZ;	// 实体的运动向量
	public float xRotation, yRotation;			// 实体的旋转角度

	public AABB boundingBox;					// 实体的边界框
	protected float boundingBoxWidth = 0.6F;	// 边界框的宽度
	protected float boundingBoxHeight = 1.8F;	// 边界框的高度

	protected boolean onGround;					// 实体是否在地面上
	protected float heightOffset;				// 实体的高度偏移量

	public boolean removed;						// 实体是否被移除

	/* 构造函数，用于创建具有物理特性的实体 */
	/* level 实体所在的存档 */
	public Entity(Level level) {
		this.level = level;
		resetPosition();
	}

	/* 设置实体到一个特定的位置 */
	/* x 位置的x坐标 */
	/* y 位置的y坐标 */
	/* z 位置的z坐标 */
	public void setPosition(double x, double y, double z) {
		/* 设置实体的位置 */
		this.x = x;
		this.y = y;
		this.z = z;

		/* 实体大小 */
		float width = this.boundingBoxWidth / 2.0F;
		float height = this.boundingBoxHeight / 2.0F;

		/* 设置边界框的位置 */
		this.boundingBox = new AABB(x - width, y - height,
				z - width, x + width,
				y + height, z + width);
	}

	/* 将实体的位置重置为存档中的一个随机位置 */
	protected void resetPosition() {
		float x = (float) Math.random() * this.level.width;
		float y = (float) (this.level.depth + 3);
		float z = (float) Math.random() * this.level.height;
		setPosition(x, y, z);
	}

	/* 在下一帧中移除实体 */
	public void remove() {
		this.removed = true;
	}

	/* 设置边界框的大小 */
	/* width  边界框的宽度 */
	/* height 边界框的高度 */
	protected void setSize(float width, float height) {
		this.boundingBoxWidth = width;
		this.boundingBoxHeight = height;
	}

	/* 使用运动偏航和俯仰来转动头部 */
	/* x 使用偏航旋转头部 */
	/* y 使用俯仰旋转头部 */
	public void turn(float x, float y) {
		this.yRotation += x * 0.15F;
		this.xRotation -= y * 0.15F;

		/* 俯仰限制 */
		this.xRotation = Math.max(-90.0F, this.xRotation);
		this.xRotation = Math.min(90.0F, this.xRotation);
	}

	/* 更新实体 */
	public void onTick() {
		/* 存储上一个位置 */
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
	}

	/* 在层级中相对移动实体，并检查碰撞 */
	/* x 相对x坐标 */
	/* y 相对y坐标 */
	/* z 相对z坐标 */
	public void move(double x, double y, double z) {
		double prevX = x;
		double prevY = y;
		double prevZ = z;

		/* 获取周围的块 */
		List<AABB> aABBs = this.level.getCubes(this.boundingBox.expand(x, y, z));

		/* 检查Y轴碰撞 */
		for (AABB abb : aABBs) {
			y = abb.clipYCollide(this.boundingBox, y);
		}
		this.boundingBox.move(0.0F, y, 0.0F);

		/* 检查X轴碰撞 */
		for (AABB aABB : aABBs) {
			x = aABB.clipXCollide(this.boundingBox, x);
		}
		this.boundingBox.move(x, 0.0F, 0.0F);

		/* 检查Z轴碰撞 */
		for (AABB aABB : aABBs) {
			z = aABB.clipZCollide(this.boundingBox, z);
		}
		this.boundingBox.move(0.0F, 0.0F, z);

		/* 更新在地面状态 */
		this.onGround = prevY != y && prevY < 0.0F;

		/* 碰撞后停止运动 */
		if (prevX != x) this.motionX = 0.0D;
		if (prevY != y) this.motionY = 0.0D;
		if (prevZ != z) this.motionZ = 0.0D;

		/* 移动实体的实际位置 */
		this.x = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
		this.y = this.boundingBox.minY + this.heightOffset;
		this.z = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
	}

	/* 以给定速度向实体面向的方向添加运动 */
	/* x X轴上的运动 */
	/* z Z轴上的运动 */
	/* speed 添加运动的强度 */
	protected void moveRelative(float x, float z, float speed) {
		float distance = x * x + z * z;

		/* 如果速度太慢则停止移动 */
		if (distance < 0.01F)
			return;

		/* 将速度应用于相对运动 */
		distance = speed / (float) Math.sqrt(distance);
		x *= distance;
		z *= distance;

		/* 计算实体旋转的正弦和余弦 */
		double sin = Math.sin(Math.toRadians(this.yRotation));
		double cos = Math.cos(Math.toRadians(this.yRotation));

		/* 向实体面向的方向移动实体 */
		this.motionX += x * cos - z * sin;
		this.motionZ += z * cos + x * sin;
	}

	/* 判断实体是否在阳光下 */
	/* retrun 实体是否在阳光下 */
	public boolean isLit() {
		return this.level.isLit((int) this.x, (int) this.y, (int) this.z);
	}
}
