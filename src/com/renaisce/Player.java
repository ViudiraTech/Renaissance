package com.renaisce;

import com.renaisce.level.Level;
import org.lwjgl.input.Keyboard;

/* 玩家类，游戏中的玩家 */
public class Player extends Entity {
	/* 控制游戏摄像机的玩家 */
	/* level 玩家所在的存档 */
	public Player(Level level) {
		super(level);
		this.heightOffset = 1.62f;
	}

	@Override
	public void onTick() {
		super.onTick();

		float forward = 0.0F;
		float vertical = 0.0F;
		float speedMultiplier = 1.0F;

		/* 重置玩家位置 */
		if (Keyboard.isKeyDown(19)) { // R
			resetPosition();
		}

		/* 玩家移动 */
		if (Keyboard.isKeyDown(200) || Keyboard.isKeyDown(17)) { // Up, W
			forward--;
		}
		if (Keyboard.isKeyDown(208) || Keyboard.isKeyDown(31)) { // Down, S
			forward++;
		}
		if (Keyboard.isKeyDown(203) || Keyboard.isKeyDown(30)) { // Left, A
			vertical--;
		}
		if (Keyboard.isKeyDown(205) || Keyboard.isKeyDown(32)) {  // Right, D
			vertical++;
		}
		if ((Keyboard.isKeyDown(57) || Keyboard.isKeyDown(219)) && this.onGround) { // Space, Windows Key
			this.motionY = 0.5F;
		}

		/* 检查是否按下左Ctrl键以加速 */
		if (Keyboard.isKeyDown(29)) { // Left Ctrl
			speedMultiplier = 1.8F;
		}

		/* 根据键盘输入给玩家添加移动 */
		moveRelative(vertical, forward, this.onGround ? 0.1F * speedMultiplier : 0.02F * speedMultiplier);

		/* 应用重力移动 */
		this.motionY -= 0.08D;

		/* 使用移动值移动玩家 */
		move(this.motionX, this.motionY, this.motionZ);

		/* 减少移动 */
		this.motionX *= 0.91F;
		this.motionY *= 0.98F;
		this.motionZ *= 0.91F;

		/* 在地面上减少移动 */
		if (this.onGround) {
			this.motionX *= 0.7F;
			this.motionZ *= 0.7F;
		}
	}
}
