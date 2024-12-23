package com.renaisce.phys;

/* 边界框类，用于表示三维空间中的一个矩形区域 */
public class AABB {
	private final double epsilon = 0.0F; // 用于处理浮点数精度问题的一个小值

	/* 边界框的六个边界值 */
	public double minX; // 最小X坐标
	public double minY; // 最小Y坐标
	public double minZ; // 最小Z坐标
	public double maxX; // 最大X坐标
	public double maxY; // 最大Y坐标
	public double maxZ; // 最大Z坐标

	/* 构造函数 */
	/* minX 最小x边 */
	/* minY 最小y边 */
	/* minZ 最小z边 */
	/* maxX 最大x边 */
	/* maxY 最大y边 */
	/* maxZ 最大z边 */
	public AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	/* 复制当前边界框对象 */
	/* return 边界框的克隆 */
	public AABB clone() {
		return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	/* 扩展边界框，正数和负数控制边界框的哪一边应该扩展 */
	/* x 扩展minX或maxX的量 */
	/* y 扩展minY或maxY的量 */
	/* z 扩展minZ或maxZ的量 */
	/* return 扩展后的边界框 */
	public AABB expand(double x, double y, double z) {
		double minX = this.minX;
		double minY = this.minY;
		double minZ = this.minZ;
		double maxX = this.maxX;
		double maxY = this.maxY;
		double maxZ = this.maxZ;

		/* 处理min/max x的扩展 */
		if (x < 0.0F) {
			minX += x;
		} else {
			maxX += x;
		}

		/* 处理min/max y的扩展 */
		if (y < 0.0F) {
			minY += y;
		} else {
			maxY += y;
		}

		/* 处理min/max z的扩展 */
		if (z < 0.0F) {
			minZ += z;
		} else {
			maxZ += z;
		}

		/* 创建新的边界框 */
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/* 在两侧扩展边界框，使用grow时，中心始终保持固定 */
	/* x x轴的扩展量 */
	/* y y轴的扩展量 */
	/* z z轴的扩展量 */
	/* return 扩展后的边界框 */
	public AABB grow(double x, double y, double z) {
		return new AABB(this.minX - x, this.minY - y,
				this.minZ - z, this.maxX + x,
				this.maxY + y, this.maxZ + z);
	}

	/* 检查X轴上的碰撞 */
	/* otherBoundingBox 与当前边界框发生碰撞的另一个边界框 */
	/* x 发生碰撞的X轴位置 */
	/* return 返回发生碰撞的修正后的x位置 */
	public double clipXCollide(AABB otherBoundingBox, double x) {
		/* 检查两个盒子是否在Y轴上发生碰撞 */
		if (otherBoundingBox.maxY <= this.minY || otherBoundingBox.minY >= this.maxY) {
			return x;
		}

		/* 检查两个盒子是否在Z轴上发生碰撞 */
		if (otherBoundingBox.maxZ <= this.minZ || otherBoundingBox.minZ >= this.maxZ) {
			return x;
		}

		/* 如果当前框的X轴更大，检查碰撞 */
		if (x > 0.0F && otherBoundingBox.maxX <= this.minX) {
			double max = this.minX - otherBoundingBox.maxX - this.epsilon;
			if (max < x) {
				x = max;
			}
		}

		/* 如果当前框的X轴更小，检查碰撞 */
		if (x < 0.0F && otherBoundingBox.minX >= this.maxX) {
			double max = this.maxX - otherBoundingBox.minX + this.epsilon;
			if (max > x) {
				x = max;
			}
		}
		return x;
	}

	/* 检查Y轴上的碰撞 */
	/* otherBoundingBox 与当前边界框发生碰撞的另一个边界框 */
	/* y 发生碰撞的Y轴位置 */
	/* return 返回发生碰撞的修正后的y位置 */
	public double clipYCollide(AABB otherBoundingBox, double y) {
		/* 检查两个盒子是否在X轴上发生碰撞 */
		if (otherBoundingBox.maxX <= this.minX || otherBoundingBox.minX >= this.maxX) {
			return y;
		}

		/* 检查两个盒子是否在Z轴上发生碰撞 */
		if (otherBoundingBox.maxZ <= this.minZ || otherBoundingBox.minZ >= this.maxZ) {
			return y;
		}

		/* 如果当前框的Y轴更大，检查碰撞 */
		if (y > 0.0F && otherBoundingBox.maxY <= this.minY) {
			double max = this.minY - otherBoundingBox.maxY - this.epsilon;
			if (max < y) {
				y = max;
			}
		}

		/* 如果当前框的Y轴更大，检查碰撞 */
		if (y < 0.0F && otherBoundingBox.minY >= this.maxY) {
			double max = this.maxY - otherBoundingBox.minY + this.epsilon;
			if (max > y) {
				y = max;
			}
		}
		return y;
	}

	/* 检查Z轴上的碰撞 */
	/* otherBoundingBox 与当前边界框发生碰撞的另一个边界框 */
	/* z 发生碰撞的Z轴位置 */
	/* return 返回发生碰撞的修正后的z位置 */
	public double clipZCollide(AABB otherBoundingBox, double z) {
		/* 检查两个盒子是否在X轴上发生碰撞 */
		if (otherBoundingBox.maxX <= this.minX || otherBoundingBox.minX >= this.maxX) {
			return z;
		}

		/* 检查两个盒子是否在Y轴上发生碰撞 */
		if (otherBoundingBox.maxY <= this.minY || otherBoundingBox.minY >= this.maxY) {
			return z;
		}

		/* 如果当前框的Z轴更大，检查碰撞 */
		if (z > 0.0F && otherBoundingBox.maxZ <= this.minZ) {
			double max = this.minZ - otherBoundingBox.maxZ - this.epsilon;
			if (max < z) {
				z = max;
			}
		}

		/* 如果当前框的Z轴更大，检查碰撞 */
		if (z < 0.0F && otherBoundingBox.minZ >= this.maxZ) {
			double max = this.maxZ - otherBoundingBox.minZ + this.epsilon;
			if (max > z) {
				z = max;
			}
		}
		return z;
	}

	/* 检查两个盒子是否相交/重叠 */
	/* otherBoundingBox 可能与当前边界框相交的另一个边界框 */
	/* return 两个盒子是否重叠 */
	public boolean intersects(AABB otherBoundingBox) {
		/* 在X轴上检查 */
		if (otherBoundingBox.maxX <= this.minX || otherBoundingBox.minX >= this.maxX) {
			return false;
		}

		/* 在Y轴上检查 */
		if (otherBoundingBox.maxY <= this.minY || otherBoundingBox.minY >= this.maxY) {
			return false;
		}
		/* 在Z轴上检查 */
		return (!(otherBoundingBox.maxZ <= this.minZ)) && (!(otherBoundingBox.minZ >= this.maxZ));
	}

	/* 相对移动边界框 */
	/* x 相对偏移x */
	/* y 相对偏移y */
	/* z 相对偏移z */
	public void move(double x, double y, double z) {
		this.minX += x;
		this.minY += y;
		this.minZ += z;
		this.maxX += x;
		this.maxY += y;
		this.maxZ += z;
	}

	/* 创建一个新的边界框，并给定偏移量 */
	/* x 相对偏移x */
	/* y 相对偏移y */
	/* z 相对偏移z */
	/* return 相对于当前边界框的新边界框 */
	public AABB offset(double x, double y, double z) {
		return new AABB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}
}
