package com.xsheetgames.genericElements;

import java.util.Random;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.xsheetgames.Configuration;
import com.xsheetgames.genericGameObjects.Chili;
import com.xsheetgames.genericGameObjects.GameObject;
import com.xsheetgames.screens.GameScreen;

public class Level {
	
	protected float levelSpeed;  //anzahl m/s in dem die primren Obstacles vorbeiziehen. Geschwindigkeit ParallaxenLayer werden auf dieser Basis errechnet.
	
	protected float levelActualSeconds; //bei welcher Sekunde stehen wir im Level (wird im HUD gemeinsam mit dem LevelSpeed in Meter umgerechnet)
	protected float levelInitialSeconds; //wie lange dauert ein Level in Sekunden
	protected Music levelMusic; //welche Musik soll diesen Level begleiten
	protected String musicString;
	private int number; //welche Nummer hat dieser Level im Levelpack
	protected Array<EnemyDescription> enemies;
	protected Array<ObstacleDescription> obstacles;
	protected Array<BreakDescription> breaks;
	protected Array<PowerupDescription> powerups;
	protected boolean openBack;
	protected float chiliRandomtime;
	protected float chiliRandomtimeInit;
	private boolean prefilled = false;
	private Random rand;
	
	public Level(float levelSpeed, float seconds, int number, String musicString) {
		this.levelInitialSeconds = seconds;
		this.levelSpeed = levelSpeed;
		this.musicString = musicString;
		this.levelMusic = null;
		this.resetLevel();
		this.number = number;
		this.rand = new Random();
	}
	
	
	public void setLevelSpeed(float speed) {
		this.levelSpeed = speed;
	}
	
	public float getLevelSpeed() {
		return this.levelSpeed;
	}
	
	public boolean getOpenBack() {
		return this.openBack;
	}
	
	public void setOpenBack(boolean openBack) {
		this.openBack = openBack;
	}
	
	public void resetLevel() {
		this.levelActualSeconds = this.levelInitialSeconds;
	}
	
	public boolean levelDone() {
		return (levelActualSeconds <= 0f);
	}
	
	public boolean isPrefilled() {
		return this.prefilled;
	}

	//Spult die Level-Timeline beim Start einmalig nach vorne und setzt alle
	//Elemente, die bis dahin gespawnt waeren, direkt an ihre errechnete
	//Position. Damit ist der Schirm ab der ersten Sekunde ueber die volle
	//(aspect-abhaengige) Breite gefuellt statt erst nach und nach von rechts.
	public void prefillLevelStart(GameScreen gameScreen) {
		this.prefilled = true;

		//Bosslevel (Endlos-Timer) und bereits laufende Level nicht vorspulen
		if(this.levelSpeed <= 0f || this.levelInitialSeconds > 400f) return;
		if(this.levelActualSeconds != this.levelInitialSeconds) return;

		float spawnX = Configuration.GAME_WORLD_WIDTH + 5f;
		float frontX = 11f; //nichts direkt vor/auf Draco (x=8) absetzen
		float preroll = (spawnX - frontX) / this.levelSpeed;

		//nicht ueber einen Break (Pausen-Message) hinweg vorspulen
		if(this.breaks.size > 0) preroll = Math.min(preroll, Math.max(this.breaks.peek().ctime - 0.05f, 0f));
		if(preroll <= 0f) return;

		while(this.enemies.size > 0 && this.enemies.peek().ctime <= preroll) {
			EnemyDescription en = this.enemies.pop();
			float x = spawnX - (preroll - en.ctime) * en.xSpeed; //Enemies fliegen mit eigener Geschwindigkeit
			if(x < frontX) continue;
			Array<GameObject> spawned = gameScreen.getActualEnemies().objects;
			int before = spawned.size;
			gameScreen.getActualEnemies().spawnEnemy(en.name, en.y, en.ySpeed, en.xSpeed, en.motionDuration, en.motionPeculiarity, en.motionEquation, en.motionInfinite, gameScreen);
			if(spawned.size > before) spawned.peek().setPosition(new Vector2(x, en.y));
		}

		while(this.obstacles.size > 0 && this.obstacles.peek().ctime <= preroll) {
			ObstacleDescription ob = this.obstacles.pop();
			float x = spawnX - (preroll - ob.ctime) * this.levelSpeed;
			if(x < frontX) continue;
			Array<GameObject> spawned = gameScreen.getActualObstacles().objects;
			int before = spawned.size;
			gameScreen.getActualObstacles().spawnObstacle(ob.name, ob.y);
			if(spawned.size > before) spawned.peek().setPosition(new Vector2(x, ob.y));
		}

		while(this.powerups.size > 0 && this.powerups.peek().ctime <= preroll) {
			PowerupDescription pw = this.powerups.pop();
			float pwSpawnX = Configuration.GAME_WORLD_WIDTH + Math.max(pw.x - Configuration.VIEWPORT_WIDTH, 0.5f);
			float x = pwSpawnX - (preroll - pw.ctime) * this.levelSpeed;
			if(x < frontX) continue;
			Chili.spawnChili(x, pw.y, gameScreen.getActualPowerups(), gameScreen.getActualWorld(), this);
		}

		//Level-Uhr passend weiterstellen, damit die folgenden Spawns nahtlos
		//an die vorgesetzten Elemente anschliessen
		this.levelActualSeconds = this.levelInitialSeconds - preroll;
	}

	public void advanceLevel(float delta, GameScreen gameScreen) {
		this.levelActualSeconds -= delta;
		float secondsForward = this.levelInitialSeconds - this.levelActualSeconds;
		
		//spawn Enemies
		while(this.enemies.size > 0 && secondsForward >= enemies.peek().ctime) {
			EnemyDescription en = this.enemies.pop();
			gameScreen.getActualEnemies().spawnEnemy(en.name, en.y, en.ySpeed, en.xSpeed, en.motionDuration, en.motionPeculiarity, en.motionEquation, en.motionInfinite, gameScreen);
		}	
		
		//spawn Obstacles
		while(this.obstacles.size > 0 && secondsForward >= this.obstacles.peek().ctime) {
			ObstacleDescription ob = this.obstacles.pop();
			gameScreen.getActualObstacles().spawnObstacle(ob.name, ob.y);
		}
		
		//spawn Powerups
		while(this.powerups.size > 0 && secondsForward >= this.powerups.peek().ctime) {
			PowerupDescription pw = this.powerups.pop();
			// JSON x is authored against the fixed 20m design width (24/25 = 4/5m past the
			// old right edge). Re-anchor to the actual, aspect-dependent world edge.
			float spawnX = Configuration.GAME_WORLD_WIDTH + Math.max(pw.x - Configuration.VIEWPORT_WIDTH, 0.5f);
			Chili.spawnChili(spawnX, pw.y, gameScreen.getActualPowerups(), gameScreen.getActualWorld(), this);
		}
		if(this.chiliRandomtimeInit > 0f && this.chiliRandomtime > 0f) {
			this.chiliRandomtime-=delta;
		}
		if(this.chiliRandomtimeInit > 0f && this.chiliRandomtime <= 0f) {
			Chili.spawnChili(Configuration.GAME_WORLD_WIDTH + 5f, rand.nextFloat()*11f+0.5f, gameScreen.getActualPowerups(), gameScreen.getActualWorld(), this);
			this.chiliRandomtime = this.chiliRandomtimeInit;
		}
		
		//pause game on breaks
		if(this.breaks.size > 0) {
			float timeNextBreak = this.breaks.peek().ctime;
			if(secondsForward >= timeNextBreak) {
				BreakDescription br = this.breaks.pop();
				gameScreen.pause(br.message);
			}
		}
	}
	
	public void setLevelMusic(Music m) {
		this.levelMusic = m;
	}
	
	public void setLevelElements(Array<EnemyDescription> enemies, Array<ObstacleDescription> obstacles, Array<PowerupDescription> powerups, Array<BreakDescription> breaks, float chiliRandomtime) {
		this.enemies = enemies;
		this.obstacles = obstacles;
		this.powerups = powerups;
		this.breaks = breaks;
		this.chiliRandomtime = chiliRandomtime;
		this.chiliRandomtimeInit = chiliRandomtime;
	}
	
	public Music getLevelMusic() {
		return this.levelMusic;
	}
	
	public String getLevelMusicString() {
		return this.musicString;
	}
	
	public float getRemainingSeconds() {
		return this.levelActualSeconds;
	}
	
	public float getPercentage() { //returns value from 0 to 1
		float progressedSeconds = this.levelInitialSeconds - this.levelActualSeconds;
		if(this.levelInitialSeconds == 0) return 0f;
		else return progressedSeconds / this.levelInitialSeconds;
	}
	
	public void setRemainingSeconds(float s) {
		this.levelActualSeconds = s;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public void dispose() {
		if(this.levelMusic != null) {
			this.levelMusic = null;
		}		
	}

}
