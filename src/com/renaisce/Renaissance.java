package com.renaisce;

import com.renaisce.level.*;
import com.renaisce.level.tile.Tile;
import com.renaisce.particle.ParticleEngine;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import static org.lwjgl.util.glu.GLU.gluPickMatrix;

/* Renaissance游戏主类 */
public class Renaissance implements Runnable {
	private static final boolean FULLSCREEN_MODE = true;	// 全屏模式开关
	private final Timer timer = new Timer(20);				// 计时器，用于控制游戏循环

	/* 存档、渲染器和玩家对象 */
	private Level level;
	private LevelRenderer levelRenderer;
	private Player player;

	/* 粒子引擎 */
	private ParticleEngine particleEngine;

	/* 雾化效果相关 */
	private final FloatBuffer fogColorDaylight = BufferUtils.createFloatBuffer(4);
	private final FloatBuffer fogColorShadow = BufferUtils.createFloatBuffer(4);
	private final FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(16);

	/* 非全屏模式下的窗口大小 */
	private int width = 1024;
	private int height = 768;

	/* 拾取方块相关 */
	private final IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16);
	private final IntBuffer selectBuffer = BufferUtils.createIntBuffer(2000);
	private HitResult hitResult;

	/* HUD渲染相关 */
	private final Tessellator tessellator = new Tessellator();

	/* 手持选中的方块ID */
	private int selectedTileId = 1;

	/* 初始化游戏 */
	/* 设置显示、键盘、鼠标、渲染和相机 */
	/* throws LWJGLException 游戏无法初始化 */
	public void init() throws LWJGLException {
		/* 设置日间雾的颜色 */
		this.fogColorDaylight.put(new float[]{
				254 / 255.0f,
				251 / 255.0f,
				250 / 255.0f,
				255 / 255.0F
		}).flip();

		/* 设置阴影雾的颜色 */
		this.fogColorShadow.put(new float[]{
				14 / 255.0F,
				11 / 255.0F,
				10 / 255.0F,
				255 / 255.0F
		}).flip();

		/* 设置屏幕尺寸 */
		Display.setFullscreen(FULLSCREEN_MODE);

		/* 设置定义的窗口大小 */
		if (!FULLSCREEN_MODE) {
			Display.setDisplayMode(new DisplayMode(this.width, this.height));
		}

		/* 设置I/O */
		Display.create();
		Display.setTitle("Renaissance v1.0 - Java Edition");
		Keyboard.create();
		Mouse.create();

		/* 在全屏模式下使用监视器大小 */
		if (FULLSCREEN_MODE) {
			this.width = Display.getDisplayMode().getWidth();
			this.height = Display.getDisplayMode().getHeight();
		}

		/* 设置纹理和颜色 */
		glEnable(GL_TEXTURE_2D);
		glShadeModel(GL_SMOOTH);
		glClearColor(0.5F, 0.8F, 1.0F, 0.0F);
		glClearDepth(1.0);

		/* 设置深度 */
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glDepthFunc(GL_LEQUAL);

		/* 设置Alpha */
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0.5F);

		/* 设置相机 */
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glMatrixMode(GL_MODELVIEW);

		/* 创建存档和玩家（必须在主线程中） */
		this.level = new Level(256, 256, 64);
		this.levelRenderer = new LevelRenderer(this.level);
		this.player = new Player(this.level);
		this.particleEngine = new ParticleEngine(this.level);

		/* 抓取鼠标光标 */
		Mouse.setGrabbed(true);
	}

	/* 保存存档，销毁鼠标、键盘和显示 */
	public void destroy() {
		this.level.save();
		Mouse.destroy();
		Keyboard.destroy();
		Display.destroy();
	}

	/* 主游戏线程 */
	@Override
	public void run() {
		try {
			/* 初始化游戏 */
			init();
		} catch (Exception e) {
			/* 显示错误消息对话框并停止游戏 */
			JOptionPane.showMessageDialog(null, e, "Failed to start Renaissance", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		/* 跟踪帧率 */
		int frames = 0;
		long lastTime = System.currentTimeMillis();

		try {
			/* 开始游戏循环 */
			while (!Display.isCloseRequested()) {
				/* 更新计时器 */
				this.timer.advanceTime();

				/* 调用tick以达到每秒20次更新 */
				for (int i = 0; i < this.timer.ticks; ++i) {
					onTick();
				}

				/* 渲染游戏 */
				render(this.timer.partialTicks);

				/* 增加渲染帧数 */
				frames++;

				/* 如果一秒过去了 */
				while (System.currentTimeMillis() >= lastTime + 1000L) {
					/* 打印帧数 */
					System.out.println(frames + " fps, " + Chunk.updates);

					/* 重置全局重建统计 */
					Chunk.updates = 0;

					/* 增加上次打印时间并重置帧计数器 */
					lastTime += 1000L;
					frames = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			/* 销毁I/O并保存游戏 */
			destroy();
		}
	}

	/* 游戏tick，每秒精确调用20次 */
	private void onTick() {
		/* 监听键盘输入 */
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				/* 暂停 */
				if (Keyboard.isKeyDown(1)) { // ESC
					Mouse.setGrabbed(false);
					if (JOptionPane.showConfirmDialog(null, "Exit Renaissance?", "Pause",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						System.exit(0);
					} else {
						Mouse.setGrabbed(true);
					}
				}

				/* 保存存档 */
				if (Keyboard.getEventKey() == 28) { // Enter
					this.level.save();
				}

				/* 选择方块 */
				if (Keyboard.getEventKey() == 2) { // 1
					this.selectedTileId = Tile.rock.id;
				}
				if (Keyboard.getEventKey() == 3) { // 2
					this.selectedTileId = Tile.grass.id;
				}
				if (Keyboard.getEventKey() == 4) { // 3
					this.selectedTileId = Tile.dirt.id;
				}
				if (Keyboard.getEventKey() == 5) { // 4
					this.selectedTileId = Tile.stoneBrick.id;
				}
				if (Keyboard.getEventKey() == 6) { // 5
					this.selectedTileId = Tile.wood.id;
				}
				if (Keyboard.getEventKey() == 7) { // 6
					this.selectedTileId = Tile.test.id;
				}
			}
		}

		/* 更新存档中的方块 */
		this.level.onTick();

		/* 更新粒子 */
		this.particleEngine.onTick();

		/* 更新玩家 */
		this.player.onTick();
	}

	/* 移动并旋转相机到玩家的位置和旋转 */
	/* partialTicks 溢出的tick用于插值 */
	private void moveCameraToPlayer(float partialTicks) {
		Entity player = this.player;

		/* 眼睛高度 */
		glTranslatef(0.0f, 0.0f, -0.3f);

		/* 旋转相机 */
		glRotatef(player.xRotation, 1.0f, 0.0f, 0.0f);
		glRotatef(player.yRotation, 0.0f, 1.0f, 0.0f);

		/* 平滑移动 */
		double x = this.player.prevX + (this.player.x - this.player.prevX) * partialTicks;
		double y = this.player.prevY + (this.player.y - this.player.prevY) * partialTicks;
		double z = this.player.prevZ + (this.player.z - this.player.prevZ) * partialTicks;

		/* 移动相机到玩家位置 */
		glTranslated(-x, -y, -z);
	}

	/* 设置普通玩家相机 */
	/* partialTicks 用于插值的溢出tick */
	private void setupCamera(float partialTicks) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		/* 设置相机视角 */
		gluPerspective(70, width / (float) height, 0.05F, 1000F);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		/* 移动相机到玩家位置 */
		moveCameraToPlayer(partialTicks);
	}

	/* 设置拾取（点击）相机 */
	/* partialTicks 用于计算平滑移动的溢出tick */
	/* 屏幕位置x */
	/* 屏幕位置y */
	private void setupPickCamera(float partialTicks, int x, int y) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		/* 重置缓冲区 */
		this.viewportBuffer.clear();

		/* 获取视口值 */
		glGetInteger(GL_VIEWPORT, this.viewportBuffer);

		/* 翻转 */
		this.viewportBuffer.flip();
		this.viewportBuffer.limit(16);

		/* 设置矩阵和相机视角 */
		gluPickMatrix(x, y, 5.0F, 5.0F, this.viewportBuffer);
		gluPerspective(70.0F, this.width / (float) this.height, 0.05F, 1000.0F);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		/* 移动相机到玩家位置 */
		moveCameraToPlayer(partialTicks);
	}

	/* 拾取（点击）处理 */
	/* partialTicks 用于插值的溢出tick */
	private void pick(float partialTicks) {
		/* 重置选择缓冲区 */
		this.selectBuffer.clear();

		glSelectBuffer(this.selectBuffer);
		glRenderMode(GL_SELECT);

		/* 设置拾取相机 */
		this.setupPickCamera(partialTicks, this.width / 2, this.height / 2);

		/* 渲染所有可能的拾取选择面到目标 */
		this.levelRenderer.pick(this.player, Frustum.getFrustum());

		/* 翻转缓冲区 */
		this.selectBuffer.flip();
		this.selectBuffer.limit(this.selectBuffer.capacity());

		long closest = 0L;
		int[] names = new int[10];
		int hitNameCount = 0;

		/* 获取命中数量 */
		int hits = glRenderMode(GL_RENDER);
		for (int hitIndex = 0; hitIndex < hits; hitIndex++) {

			/* 获取名称数量 */
			int nameCount = this.selectBuffer.get();
			long minZ = this.selectBuffer.get();
			this.selectBuffer.get();

			/* 检查命中是否更接近相机 */
			if (minZ < closest || hitIndex == 0) {
				closest = minZ;
				hitNameCount = nameCount;

				/* 填充名称 */
				for (int nameIndex = 0; nameIndex < nameCount; nameIndex++) {
					names[nameIndex] = this.selectBuffer.get();
				}
			} else {
				/* 跳过名称 */
				for (int nameIndex = 0; nameIndex < nameCount; ++nameIndex) {
					this.selectBuffer.get();
				}
			}
		}

		/* 更新命中结果 */
		if (hitNameCount > 0) {
			this.hitResult = new HitResult(names[0], names[1], names[2], names[3], names[4]);
		} else {
			this.hitResult = null;
		}
	}

	/* 渲染游戏 */
	/* partialTicks 用于插值的溢出tick */
	private void render(float partialTicks) {
		/* 获取鼠标移动 */
		float motionX = Mouse.getDX();
		float motionY = Mouse.getDY();

		/* 使用鼠标移动输入旋转相机 */
		this.player.turn(motionX, motionY);

		// 拾取（点击）方块
		pick(partialTicks);

		/* 监听鼠标输入 */
		while (Mouse.next()) {
			/* 左键点击 */
			if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.hitResult != null) {
				Tile previousTile = Tile.tiles[this.level.getTile(this.hitResult.x, this.hitResult.y, this.hitResult.z)];

				/* 摧毁方块 */
				boolean tileChanged = this.level.setTile(this.hitResult.x, this.hitResult.y, this.hitResult.z, 0);

				/* 为这个方块创建粒子 */
				if (previousTile != null && tileChanged) {
					previousTile.onDestroy(this.level, this.hitResult.x, this.hitResult.y, this.hitResult.z, this.particleEngine);
				}
			}

			/* 右键点击 */
			if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState() && this.hitResult != null) {
				/* 获取目标方块位置 */
				int x = this.hitResult.x;
				int y = this.hitResult.y;
				int z = this.hitResult.z;

				/* 使用面方向获取方块位置 */
				if (this.hitResult.face == 0) y--;
				if (this.hitResult.face == 1) y++;
				if (this.hitResult.face == 2) z--;
				if (this.hitResult.face == 3) z++;
				if (this.hitResult.face == 4) x--;
				if (this.hitResult.face == 5) x++;

				/* 设置方块 */
				this.level.setTile(x, y, z, this.selectedTileId);
			}
		}

		/* 清除颜色和深度缓冲区并重置相机 */
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		/* 设置普通玩家相机 */
		setupCamera(partialTicks);
		glEnable(GL_CULL_FACE);

		/* 获取当前视锥 */
		Frustum frustum = Frustum.getFrustum();

		/* 更新脏块 */
		this.levelRenderer.updateDirtyChunks(this.player);

		/* 设置日间雾 */
		setupFog(0);
		glEnable(GL_FOG);

		/* 渲染亮方块 */
		this.levelRenderer.render(0);

		/* 渲染阳光下的粒子 */
		this.particleEngine.render(this.player, this.tessellator, partialTicks, 0);

		/* 设置阴影雾 */
		setupFog(1);

		/* 渲染阴影中的暗方块 */
		this.levelRenderer.render(1);

		/* 渲染阴影中的粒子 */
		this.particleEngine.render(this.player, this.tessellator, partialTicks, 1);

		/* 完成渲染 */
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_FOG);

		/* 渲染实际命中 */
		if (this.hitResult != null) {
			glDisable(GL_ALPHA_TEST);
			this.levelRenderer.renderHit(this.hitResult);
			glEnable(GL_ALPHA_TEST);
		}

		/* 绘制玩家HUD */
		drawGui(partialTicks);

		/* 更新显示 */
		Display.update();
	}

	/* 绘制HUD */
	/* partialTicks 用于插值的溢出tick */
	private void drawGui(float partialTicks) {
		/* 清除深度 */
		glClear(GL_DEPTH_BUFFER_BIT);

		/* 设置HUD相机 */
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		int screenWidth = this.width * 240 / this.height;
		int screenHeight = this.height * 240 / this.height;

		/* 设置相机视角 */
		glOrtho(0.0, screenWidth, screenHeight, 0.0, 100.0F, 300.0F);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		/* 移动相机到Z级别-200 */
		glTranslatef(0.0f, 0.0f, -200.0f);

		/* 开始方块显示 */
		glPushMatrix();

		/* 将方块位置转换到左下角 */
		glTranslatef(25.0F, screenHeight - 25.0F, 0.0F);
		glScalef(25.0F, 25.0F, 25.0F);
		glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
		glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
		glTranslatef(-1.5F, 0.5F, -0.5F);
		glScalef(-1.0F, -1.0F, 1.0F);

		/* 设置方块渲染 */
		int id = Textures.loadTexture("/terrain.png", 9728);
		glBindTexture(GL_TEXTURE_2D, id);
		glEnable(GL_TEXTURE_2D);

		/* 在手中渲染选中的方块 */
		this.tessellator.init();
		Tile.tiles[this.selectedTileId].render(this.tessellator, this.level, 0, -2, 0, 0);
		this.tessellator.flush();

		/* 完成方块渲染 */
		glDisable(GL_TEXTURE_2D);
		glPopMatrix();

		/* 准心位置 */
		int x = screenWidth / 2;
		int y = screenHeight / 2;

		/* 准心颜色 */
		glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		/* 渲染准心 */
		this.tessellator.init();
		this.tessellator.vertex((float) (x + 1), (float) (y - 4), 0.0F);
		this.tessellator.vertex((float) (x - 0), (float) (y - 4), 0.0F);
		this.tessellator.vertex((float) (x - 0), (float) (y + 5), 0.0F);
		this.tessellator.vertex((float) (x + 1), (float) (y + 5), 0.0F);
		this.tessellator.vertex((float) (x + 5), (float) (y - 0), 0.0F);
		this.tessellator.vertex((float) (x - 4), (float) (y - 0), 0.0F);
		this.tessellator.vertex((float) (x - 4), (float) (y + 1), 0.0F);
		this.tessellator.vertex((float) (x + 5), (float) (y + 1), 0.0F);
		this.tessellator.flush();
	}

	/* 设置雾的类型 */
	/* fogType 雾的类型（0: 日间，1: 阴影） */
	private void setupFog(int fogType) {
		/* 日间雾 */
		if (fogType == 0) {
			/* 雾距离 */
			glFogi(GL_FOG_MODE, GL_VIEWPORT_BIT);
			glFogf(GL_FOG_DENSITY, 0.001F);

			/* 设置雾颜色 */
			glFog(GL_FOG_COLOR, this.fogColorDaylight);

			glDisable(GL_LIGHTING);
		}

		/* 阴影雾 */
		if (fogType == 1) {
			/* 雾距离 */
			glFogi(GL_FOG_MODE, GL_VIEWPORT_BIT);
			glFogf(GL_FOG_DENSITY, 0.06F);

			/* 设置雾颜色 */
			glFog(GL_FOG_COLOR, this.fogColorShadow);

			glEnable(GL_LIGHTING);
			glEnable(GL_COLOR_MATERIAL);

			float brightness = 0.6F;
			glLightModel(GL_LIGHT_MODEL_AMBIENT, this.getBuffer(brightness, brightness, brightness, 1.0F));
		}
	}

	/* 用颜色值填充浮点缓冲区并返回 */
	/* red 红色值 */
	/* green 绿色值 */
	/* blue 蓝色值 */
	/* alpha Alpha值 */
	/* return 按RGBA顺序填充的浮点缓冲区 */
	private FloatBuffer getBuffer(float red, float green, float blue, float alpha) {
		this.colorBuffer.clear();
		this.colorBuffer.put(red).put(green).put(blue).put(alpha);
		this.colorBuffer.flip();
		return this.colorBuffer;
	}

	/* 游戏的入口点 */
	/* args 程序参数（未使用） */
	public static void main(String[] args) {
		new Thread(new Renaissance()).start();
	}
}
