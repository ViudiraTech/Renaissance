JC			= javac
JA			= java
JR			= jar
MR			= mkdir
CP			= cp

GAME_NAME	= Renaissance
GS_NAME		= renaisce
LIB_PATH	= libs/
RES_PATH	= src/com/$(GS_NAME)/

JA_FLAGS	= -Djava.library.path=$(LIB_PATH)
JC_FLAGS	= -classpath src/
JR_FLAGS	= cvf

all:
	$(JC) $(JC_FLAGS) $(RES_PATH)$(GAME_NAME).java
	$(CP) -r src/com/ ./
	rm -rf com/$(GS_NAME)/*.java
	rm -rf com/$(GS_NAME)/level/*.java
	rm -rf com/$(GS_NAME)/level/tile/*.java
	rm -rf /com/$(GS_NAME)/phys/*.java
	rm -rf /com/$(GS_NAME)/character/*.java
	rm -rf /com/$(GS_NAME)/particle/*.java
	$(CP) -r src/org/ ./
	$(CP) src/terrain.png ./
	$(JR) $(JR_FLAGS) $(GAME_NAME).jar com/ org/ terrain.png
	rm -rf com/
	rm -rf org/
	rm -rf terrain.png

run:
	$(JA) $(JA_FLAGS) -cp $(GAME_NAME).jar com.renaisce.Renaissance \
	-XX:+UnlockExperimentalVMOptions \
	-XX:+UseG1GC \
	-XX:G1NewSizePercent=20 \
	-XX:G1ReservePercent=20 \
	-XX:MaxGCPauseMillis=50 \
	-XX:G1HeapRegionSize=32m \
	-XX:-UseAdaptiveSizePolicy \
	-XX:-OmitStackTraceInFastThrow \
	-XX:-DontCompileHugeMethods \

clean:
	rm -rf $(RES_PATH)*.class
	rm -rf $(RES_PATH)level/*.class
	rm -rf $(RES_PATH)level/tile/*.class
	rm -rf $(RES_PATH)phys/*.class
	rm -rf $(RES_PATH)character/*.class
	rm -rf $(RES_PATH)particle/*.class
	rm -rf $(GAME_NAME).jar
	rm -rf level.dat
