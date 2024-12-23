# Renaissance

## 简介

这是一个使用Java语言和LWJGL2库实现的简单3D沙盒类建造游戏。游戏允许玩家在一个三维世界中自由移动、破坏和建造方块，类似于经典的沙盒游戏Minecraft。玩家可以使用键盘和鼠标与游戏世界互动，探索和创造属于自己的世界。

## 系统要求

- Java Development Kit (JDK) 8 或更高版本
- 任意Linux发行版（有能力者可以修改Makefile以在非Linux发行版编译）

## 功能特点

- 第一人称视角的3D游戏世界
- 方块的破坏和建造
- 基本的物理移动（前进、后退、左右移动、跳跃）
- 简单的光照和雾化效果
- 基础的用户界面（HUD）
- 键盘和鼠标控制

## 快速开始

1. **环境设置**：
   - 确保你的开发环境中已经安装了JDK。

2. **项目结构**：
   - 'src/com/':游戏的Java源码
   - 'libs/':运行游戏时必要的库
   - 确保所有的资源文件（如纹理、模型等）位于正确的路径下。

3. **编译代码**:
   - 在项目根目录执行make即可开始编译
   - 数秒后根目录中就会出现一个jar

3. **运行游戏**：
   - 通过make run来运行，或者输入下面的命令去启动：
   ```bash
	java -Djava.library.path=libs/ -cp Renaissance.jar com.renaisce.Renaissance \
	-XX:+UnlockExperimentalVMOptions \
	-XX:+UseG1GC \
	-XX:G1NewSizePercent=20 \
	-XX:G1ReservePercent=20 \
	-XX:MaxGCPauseMillis=50 \
	-XX:G1HeapRegionSize=32m \
	-XX:-UseAdaptiveSizePolicy \
	-XX:-OmitStackTraceInFastThrow \
	-XX:-DontCompileHugeMethods \
	```

4. **游戏控制**：
   - **W/A/S/D**：角色移动。
   - **空格键**：跳跃。
   - **鼠标左键**：破坏选中的方块。
   - **鼠标右键**：在选中的位置放置方块。
   - **数字键1-6**：选择不同的方块类型。

## 注意事项

- 游戏目前处于非常基础的阶段，许多功能（如更复杂的用户界面、更高级的光照和纹理处理、多人游戏等）尚未实现。
- 游戏性能和稳定性可能会根据你的硬件配置和Java环境的不同而有所差异。
- 代码中的注释和文档可能不完整，如果你在开发过程中遇到问题，可以参考LWJGL2的官方文档和社区资源。

## 贡献

如果你对这个游戏感兴趣，并希望为其贡献代码或提出改进建议，欢迎通过GitHub或其他方式联系我们。你可以添加新功能、修复bug或改进游戏的性能和用户体验。
