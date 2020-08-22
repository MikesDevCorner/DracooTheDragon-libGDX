package com.xsheetgames.screens;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AbstractLevelpack;
import com.xsheetgames.genericElements.AtlasAnimation;
import com.xsheetgames.genericElements.AtlasAnimationCollection;
import com.xsheetgames.genericElements.BossEnergyMeter;
import com.xsheetgames.genericElements.InputButtonCollection;
import com.xsheetgames.genericElements.ObjectContactListener;
import com.xsheetgames.genericElements.ParallaxLayer;
import com.xsheetgames.genericGameObjects.AbstractObstacleCollection;
import com.xsheetgames.genericGameObjects.BoundaryCollection;
import com.xsheetgames.genericGameObjects.Chili;
import com.xsheetgames.genericGameObjects.Draco;
import com.xsheetgames.genericGameObjects.EnemyCollection;
import com.xsheetgames.genericGameObjects.GameObject;
import com.xsheetgames.genericGameObjects.GameObjectCollection;
import com.xsheetgames.genericGameObjects.GameObjectPool;

public class GameScreen extends AbstractScreen implements iAdAble {
	
	private short chilisCount = 1;
	
	//Important stuff
	private OrthographicCamera camera;
	private World world;
	private TweenManager tweenManager;
	private SpriteBatch batch;
	private boolean solved = false;
	
	//Misc	
	private Box2DDebugRenderer debugRenderer;
	private boolean assetsLoaded = false;
	private boolean keepScreen = false;
	private boolean disposed = false;
	private Sprite pauseLayer, menu, more, rate;
	public static boolean paused = false;
	private String pauseMessageOriginal = "pause";
	private String[] pauseMessage = pauseMessageOriginal.split("\n");
	private boolean doDisposing = false;
	private float debugCounter = 0f;
	private float showCounter = 0f;
	private AtlasRegion hud_track;
	private AtlasRegion hud_sign;
	private Sprite pauseResumeBtn;
	private boolean resumeEnemies;
	private boolean startLevelMusic;
	public static BossEnergyMeter bossEnergyMeter;
	private float musicLevel = 0.6f;
	private boolean adShowed;
	private boolean lastConnectedState;
	
	//Game Objects
	private Draco draco;
	private AbstractLevelpack actualPack;
	private GameObjectCollection fireballs;
	private GameObjectCollection powerups;
	private EnemyCollection enemies;
	private AbstractObstacleCollection obstacles;
	private BoundaryCollection boundaries;
	private ParallaxLayer middleLayer;
	private ParallaxLayer fogLayer;
	private ParallaxLayer frontLayer;
	private ParallaxLayer backLayer;
	private AtlasAnimationCollection sparkles;
	
	//INPUT RELATED THINGS:	
	private InputButtonCollection buttons;
	private Array<Vector2> touchInputs;
	private Array<Vector2> actualInputs;
	private Array<Vector2> absolutInputStarts;
	
	
	//Allan Bishop's Fixing Time Step Stuff (syncronize render-framerate with box2d-steprate)
	private final float FIXED_TIMESTEP = 1.0f/60.0f;
	private float fixedTimestepAccumulator = 0f;
	private float fixedTimestepAccumulatorRatio = 0f;
	private int velocityIterations = 8;
	private int positionIterations = 8;
	//private InputMultiplexer multiplexer;
	
	public GameScreen(Game game) {
		this.game = game;
		this.adShowed = false;
		
		if(Configuration.debugLevel >= Application.LOG_INFO) Gdx.app.log("GameScreen", "Konstructor aufgerufen");
	}
	
	
	
	@SuppressWarnings("unused")
	@Override
	public void render(float delta) {
	    if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {		   
				if(assetsLoaded == false) this.doAssetProcessing();
			   
				/***************** LOGIC OPERATIONS AND SIMULATIONS *********************/
				
				if(this.showCounter <= 2f) this.showCounter += delta;
				
				if(GameScreen.paused == false) {
					
					draco.processFireKey(this.fireballs, delta);
					
					
					final int MAX_STEPS = 5;
					fixedTimestepAccumulator += delta;
					final int nSteps = (int) Math.floor(fixedTimestepAccumulator / FIXED_TIMESTEP);
					
					if(nSteps > 0) {
						fixedTimestepAccumulator -= nSteps * FIXED_TIMESTEP;
					}
					fixedTimestepAccumulatorRatio = fixedTimestepAccumulator / FIXED_TIMESTEP;
					final int nStepsClamped = Math.min(nSteps, MAX_STEPS);
					
					for(int i = 0; i < nStepsClamped; ++i) {
						this.resetSmoothStates();
						this.singleStep(FIXED_TIMESTEP);
					}
					this.world.clearForces();
					this.smoothStates();
					
					
					/* not depending on bxo2d world.step */
					if(actualPack.actualLevelDone() == false) actualPack.advanceActualLevel(delta, this);
					middleLayer.doLogic(delta);
					if(actualPack.getActualLevel().getLevelSpeed() >= 1.5f) frontLayer.doLogic(delta);
					if(Configuration.FogActive)	{
						fogLayer.doLogic(delta);						
					}
					backLayer.doLogic(delta);
					
					//DEBUG LOGIC:
					if(this.debugCounter > 0f) this.debugCounter -= delta;
				}
				
				/***************** DRAWING *********************/	
				
				camera.update();
				
			    //DRAW THINGS DEPENDING ON METRIC SIZES
			
				batch.setProjectionMatrix(camera.combined);
				batch.begin();
				batch.disableBlending();
				backLayer.draw(batch);
				batch.enableBlending();
				middleLayer.draw(batch);
				if(Configuration.FogActive)	{
					fogLayer.draw(batch);
				}
				powerups.drawObjects(batch, delta);
				fireballs.drawObjects(batch,delta);
				draco.draw(batch, delta);
				sparkles.draw(batch, delta);
				enemies.drawObjects(batch,delta);
				obstacles.drawObjects(batch,delta);
				if(actualPack.getActualLevel().getLevelSpeed() >= 1.5f) {
					frontLayer.draw(batch);
				}
				if(buttons != null) {
					if(!GameAssets.nativ.isControllerConnected() && Gdx.app.getType() != ApplicationType.Desktop && Gdx.app.getType() != ApplicationType.WebGL) buttons.drawAllButtons(batch);
				}
							
				batch.end();
				if(Configuration.debugLevel >= Application.LOG_DEBUG) debugRenderer.render(this.world, camera.combined);
				
			    //DRAW THINGS DEPENDING ON TARGET_WIDTH SIZES IN PIXEL (HUD)
				batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
				batch.begin();

				GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), "World "+this.actualPack.getActualLevelNumber());
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch, "World "+this.actualPack.getActualLevelNumber(), Configuration.TARGET_WIDTH/2-GameAssets.glyphLayout.width/2, Configuration.TARGET_HEIGHT+27f);
				
				if(actualPack.getRemainingLevelSeconds() <= 400f) {
					batch.draw(this.hud_track,920f,756f,this.hud_track.getRegionWidth(), this.hud_track.getRegionHeight());
					batch.draw(this.hud_sign,918f + 272f*actualPack.getActualLevel().getPercentage() - 12f,747f,this.hud_sign.getRegionWidth(), this.hud_sign.getRegionHeight());
				} else {
					if(GameScreen.bossEnergyMeter != null) GameScreen.bossEnergyMeter.draw(batch);
				}
				
				if(Configuration.debugLevel >= Application.LOG_INFO) {
					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), Configuration.VERSION + " - " + Configuration.VERSION_DATE + "  -  FPS: "+ Gdx.graphics.getFramesPerSecond());
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch, Configuration.VERSION + " - " + Configuration.VERSION_DATE + "  -  FPS: "+ Gdx.graphics.getFramesPerSecond(), Configuration.TARGET_WIDTH/2-GameAssets.glyphLayout.width/2, GameAssets.fetchFont("fonts/memory.fnt").getLineHeight()*1.5f);
				}
				
				batch.draw(draco.getLiveTexture(), 75f,Configuration.TARGET_HEIGHT-draco.getLiveTexture().getRegionHeight() - 10);
				
				if(GameScreen.paused == true) {
					this.pauseLayer.draw(batch);
					if(this.pauseMessage[0].equals("pause")) {
						this.menu.draw(batch);
						this.rate.draw(batch);
						if(this.more != null && false) this.more.draw(batch);
					}
					float linePadding = 18f;


					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.pauseMessage[0]);
					float lineHeight = GameAssets.glyphLayout.height;
					float allHeight = this.pauseMessage.length * (lineHeight + linePadding);
					float startHeight = 800f/2f + allHeight / 2f;
					if(allHeight < 780f) {
						for(int i = 0; i < this.pauseMessage.length; i++) {
							GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.pauseMessage[i]);
							GameAssets.fetchFont("fonts/memory.fnt").draw(batch, this.pauseMessage[i], Configuration.TARGET_WIDTH/2-GameAssets.glyphLayout.width/2, startHeight+lineHeight+10f);
							startHeight -= (linePadding + lineHeight);
						}						
					} else {
						GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.pauseMessage[0]);
						GameAssets.fetchFont("fonts/memory.fnt").draw(batch, "Paused - message hast too much lines...", Configuration.TARGET_WIDTH/2-GameAssets.glyphLayout.width/2, startHeight+lineHeight+10f);
					}
					this.pauseResumeBtn.draw(batch);
				}
				
				batch.end();

				
				//Emulate Events
				if(GameAssets.buttonTimer > 0f) GameAssets.buttonTimer-=delta;
				if(GameAssets.nativ.getInputDevice().toLowerCase().contains("moga")) {
					if(GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_START) == true) {
						this.startPress();
					}
					if(GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_PRIMARY) == true) {
						this.primaryPress();
					}
					if(GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_BACK) == true) {
						this.stepBack("moga");
					}
					if(GameAssets.nativ.isControllerConnected() == true && this.lastConnectedState == false) {
						GameScreen.paused = true;
						GameAssets.nativ.showMessage("Controller", "Moga Controller connected");
					}
					if(GameAssets.nativ.isControllerConnected() == false && this.lastConnectedState == true) {
						GameScreen.paused = true;
						GameAssets.nativ.showMessage("Controller", "Moga Controller disconnected");
					}
					lastConnectedState = GameAssets.nativ.isControllerConnected();
				}
				
				
				/***************** LEAVING *********************/
				
				//check if leaving szenario is given:
				if(this.doDisposing == true) {
					if(this.solved && Configuration.useOutroScreen) {
						this.keepScreen = true;
						EndPackScreen eps = new EndPackScreen(game, this);
						eps.setComic(this.actualPack.getEndComic());
						game.setScreen(eps);
					}
					else {
						this.unloadAssetsAndFinalize();
						game.setScreen(new MenuScreen(game));
					}
					return;
				}
				
				if(draco.getEnergy() == 0) {
					this.chilisCount = 1;
					draco.dispose();
					this.enemies.pause();
					this.keepScreen = true;
					if(Configuration.musicEnabled == true) this.actualPack.getActualLevel().getLevelMusic().stop();
					game.setScreen(new DeadScreen(game, this));
					return;
				}
				if(actualPack.actualLevelDone()) {
					
					this.chilisCount = draco.getEnergy();
					
					draco.dispose();
					this.enemies.pause();
					this.keepScreen = true;
					if(Configuration.musicEnabled == true) this.actualPack.getActualLevel().getLevelMusic().stop();
					game.setScreen(new LevelDoneScreen(this.game,this, actualPack.getActualLevelNumber(),actualPack.packName));
					return;
				}
			}
	    }
		
	}
	
	
	
	private void singleStep(float dt) {
		
		//APPLY PHYSICS, FORCES, INPUTS, ETC.
		tweenManager.update(dt);
		
		//everything which is not in the clipping pane anymore will be purged and disposed
		fireballs.removeOutlaws();
		powerups.removeOutlaws();
		enemies.removeOutlaws();
		obstacles.removeOutlaws();
		draco.checkIfOutlaw();
		
		//invoke object logic
		fireballs.invokeObjectLogic(dt, actualPack.getActualLevel());				
		powerups.invokeObjectLogic(dt, actualPack.getActualLevel());
		draco.doMotionLogic(dt);
		
		//invoke Object logik (including potentional investigating Spawn Point position) 
		obstacles.invokeObjectLogic(dt, actualPack.getActualLevel());
		enemies.invokeObjectLogic(dt, actualPack.getActualLevel());		
		
		//SIMULATE A SINGLE TIMESTEP IN THIS WORLD
		world.step(dt, this.velocityIterations, this.positionIterations);
		this.world.clearForces();
	}
	
	private void smoothStates() {		
		obstacles.smoothStates(fixedTimestepAccumulatorRatio);
		fireballs.smoothStates(fixedTimestepAccumulatorRatio);
		powerups.smoothStates(fixedTimestepAccumulatorRatio);
		enemies.smoothStates(fixedTimestepAccumulatorRatio);
		draco.smoothStates(fixedTimestepAccumulatorRatio);
	}
	
	private void resetSmoothStates() {
		obstacles.resetSmoothStates();
		fireballs.resetSmoothStates();
		powerups.resetSmoothStates();
		enemies.resetSmoothStates();
		draco.resetSmoothStates();
	}	
	
	
	

	@Override
	public void resize(int width, int height) {
		if(Configuration.debugLevel >= Application.LOG_INFO) Gdx.app.log("GameScreen", "Resize auf width " + width + " and height "+height);
	}

	@Override
	public void show() {
		if(this.adShowed == false) {
			this.resumeEnemies = false;
			this.startLevelMusic = false;
			
			lastConnectedState = GameAssets.nativ.isControllerConnected();
			
			GameAssets.nativ.trackPageView("/GameScreen");
			
			if(this.actualPack.getActualLevelNumber() == 15 && this.actualPack.bossAssetsLoaded == false) {
				this.actualPack.loadBossAssets();
				this.assetsLoaded = false;
			}
			
			this.showCounter = 0f;
			
			this.touchInputs = new Array<Vector2>();
			this.actualInputs = new Array<Vector2>();
			this.absolutInputStarts = new Array<Vector2>();
			
			for(int ii = 0; ii<5; ii++) {
				this.touchInputs.add(new Vector2());
				this.actualInputs.add(new Vector2());
				this.absolutInputStarts.add(new Vector2());
			}
			
			if(Configuration.debugLevel >= Application.LOG_INFO) Gdx.app.log("GameScreen", "Show aufgerufen");
			this.camera = new OrthographicCamera(Configuration.VIEWPORT_WIDTH, Configuration.VIEWPORT_HEIGHT);
		    this.camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
		    this.camera.update();
		    
		    
		    this.actualPack.readLevelFromFile();
		    
		    if(this.middleLayer != null) this.middleLayer.setSpeed(this.actualPack.getMiddleLayerSpeed());
		    if(this.backLayer != null) this.backLayer.setSpeed(this.actualPack.getBackLayerSpeed());
		    if(this.frontLayer != null) this.frontLayer.setSpeed(this.actualPack.getFrontLayerSpeed());
		    if(this.fogLayer != null) this.fogLayer.setSpeed(this.actualPack.getFogLayerSpeed());
		    
		    this.pause("press play when ready...");
		    
		    if(this.world == null) {
		    	this.world = new World(new Vector2(0f,-10f), true);
		    	this.world.setContactListener(new ObjectContactListener());
		    }
		    
		    this.batch = new SpriteBatch();
			
		    //Gdx.input.setInputProcessor(this.multiplexer);
		    //Gdx.input.setCatchBackKey(true);
		    
		    if(this.boundaries != null) this.boundaries.dispose();
		    this.boundaries = new BoundaryCollection(world, this.actualPack.getActualLevel().getOpenBack());
		    
		    this.keepScreen = false;
		} else this.adShowed = false;
	}

	@Override
	public void hide() {
		if(Configuration.debugLevel >= Application.LOG_INFO) Gdx.app.log("GameScreen", "Hide aufgerufen");
	    if(this.enemies != null) this.enemies.pause();
	    if(this.keepScreen == false) this.dispose();
	    GameAssets.nativ.TriggerStandingInterstitials();
	}

	@Override
	public void pause() {
		if(Configuration.debugLevel >= Application.LOG_INFO) Gdx.app.log("GameScreen", "Pause aufgerufen");
		GameScreen.paused = true;
		GameAssets.nativ.showBannerAd();
		if(this.actualPack != null) {
			if(this.actualPack.getActualLevel().getLevelMusic() != null) {
				GameAssets.pauseMusic(this.actualPack.getActualLevel().getLevelMusic());
			}
		}
		if(this.enemies != null) this.enemies.pause();		
	}
	
	public void pause(String message) {		
		this.pauseMessage = message.split("#");
		if(pauseMessage.length == 1) {
			this.pauseMessage = this.pauseMessage[0].split("\n");
			this.pause();
		} else {
			if(Configuration.inputType == 1) { //Buttons
				if(this.pauseMessage[1].equals("blank") == false) {
					this.pauseMessage = this.pauseMessage[1].split("\n");
					this.pause();
				}
			}
		}
	}
	
	public void endPause() {
		if(Configuration.debugLevel >= Application.LOG_INFO) Gdx.app.log("GameScreen", "End Pause aufgerufen");
		GameAssets.nativ.closeBannerAd();
		GameScreen.paused = false;
		
		this.pauseMessage = this.pauseMessageOriginal.split("\n");
		if(this.actualPack != null) {
			if(this.actualPack.getActualLevel().getLevelMusic() != null && this.assetsLoaded == true) {
				GameAssets.playMusic(this.actualPack.getActualLevel().getLevelMusic(), true, musicLevel);
			} else if(this.assetsLoaded == false) this.startLevelMusic = true;
		}
		
		if(this.assetsLoaded == true) {
			if(this.enemies != null) this.enemies.resume();
		} else this.resumeEnemies = true;
	}

	@Override
	public void resume() {
		if(Configuration.debugLevel >= Application.LOG_INFO) Gdx.app.log("GameScreen", "Resume aufgerufen");
		if(this.actualPack != null) {
			if(GameAssets.manager.isLoaded("game/images/game_objects.pack") == false || this.actualPack.areAssetsLoaded() == false) {
				this.assetsLoaded = false;
				GameAssets.unloadMenuAssets();
				GameAssets.manager.clear();
				GameAssets.manager.finishLoading();
				GameAssets.loadGameAssets();
				this.actualPack.loadAssets();
				
				//FOR NOW BECAUSE OF MEDIA PLAYER BUG IN MUSIC AND SOUND CAUSED BY RELOADING THE SOUND- AND MUSICFILES:
				Configuration.musicEnabled = false;
				Configuration.soundEnabled = false;
			}
		}
	}

	
	public void unloadAssetsAndFinalize() {
		AbstractLevelpack tmpPack = this.actualPack;
		this.dispose();
		GameAssets.unloadGameAssets();
		tmpPack.unloadAssets();
		tmpPack = null;
		GameAssets.manager.clear();
		GameAssets.manager.finishLoading();
		GameAssets.loadMenuAssets();
	}
	
	
	@Override
	public void dispose() {
		if(disposed == false) {
		   if(GameScreen.bossEnergyMeter != null) {
			   GameScreen.bossEnergyMeter.dispose();
		   }
		   if(this.buttons != null)  {
			   this.buttons.dispose();
			   this.buttons = null;
		   }
		   
		   if(this.draco != null) {
			   this.draco.dispose();
			   this.draco = null;
		   }
		   if(this.boundaries != null) {
			   this.boundaries.dispose();
			   this.boundaries = null;
		   }
		   this.camera = null;
		   if(this.fireballs != null) {
			   this.fireballs.dispose();
			   this.fireballs = null;
		   }
		   if(this.powerups != null) {
			   this.powerups.dispose();
			   this.powerups = null;
		   }
		   if(this.obstacles != null) {
			   this.obstacles.dispose();
			   this.obstacles = null;
		   }
		   if(this.sparkles != null) {
			   this.sparkles.dispose();
			   this.sparkles = null;
		   } 
		   if(this.enemies != null) {
			   this.enemies.dispose();
			   this.enemies = null;
		   }
		   if(this.world != null) {
			   this.world.dispose();
			   this.world = null;
		   } 	
		   if(this.batch != null) {
			   this.batch.dispose();
			   this.batch = null;
		   }
		   this.tweenManager = null;
		   
		   if(this.backLayer != null) {
			   this.backLayer.dispose();
			   if(this.fogLayer != null) this.fogLayer.dispose();
			   this.frontLayer.dispose();
			   this.middleLayer.dispose();
			   this.fogLayer = null;
			   this.frontLayer = null;
			   this.middleLayer = null;
			   this.backLayer = null;
		   }
		   
		   
		   if(this.actualPack != null) {
			   this.actualPack.dispose();
			   this.actualPack = null;
		   }
		   this.absolutInputStarts = null;
		   this.actualInputs = null;
		   this.touchInputs = null;
		   
		   this.pauseLayer = null;
		   this.menu = null;
		   this.rate = null;
		   this.more = null;
		   
		   this.disposed = true;
		}
	}
	
	
	private void doAssetProcessing() {
		if(Configuration.debugLevel >= Application.LOG_INFO) Gdx.app.log("GameScreen", "Assetprocessing aufgerufen");
		this.assetsLoaded = true;
		
		this.actualPack.setAtlas();
	   	this.actualPack.createObstacleCollection(this.world);
	   	
	   	GameScreen.bossEnergyMeter = new BossEnergyMeter();
	   	
	   	if(this.obstacles == null) obstacles = this.actualPack.getObstacles();
	   	else {
	   		for(GameObject go : obstacles.objects) {
	   			go.resetGraphics(this.actualPack.getAtlas());
	   		}
	   	}
	   	this.obstacles.resetGraphics(this.actualPack.getAtlas());
	   	
		this.actualPack.setActualLevel(this.actualPack.getActualLevelNumber());	    
	   	
	   	if(buttons == null && Configuration.inputType == 1) {
	   		buttons = new InputButtonCollection(world);
	   	} else {
	   		if(buttons != null) buttons.resetGraphics(GameAssets.fetchTextureAtlas("game/images/game_objects.pack"));
	   	}
	   	
	   	if(fireballs == null) fireballs = new GameObjectCollection();
	   	else {
	   		for(GameObject go : fireballs.objects) {
	   			go.resetGraphics(GameAssets.fetchTextureAtlas("game/images/game_objects.pack"));
	   		}
	   		
	   	}
	   	if(powerups == null) {
	   		powerups = new GameObjectCollection();
	   		Chili.createChiliPool(this.world);
	   		powerups.pools.add(Chili.chiliPool);
	   		powerups.preFillPools();
	   	}
	   	else {
	   		for(GameObject go : powerups.objects) {
	   			go.resetGraphics(GameAssets.fetchTextureAtlas("game/images/game_objects.pack"));
	   		}
	   		for(GameObjectPool p : powerups.pools) {
	   			p.resetGraphics(GameAssets.fetchTextureAtlas("game/images/game_objects.pack"));
	   		}
	   	}
	   	if(tweenManager == null) this.tweenManager = new TweenManager();
	   	if(enemies == null) enemies = this.actualPack.getEnemyCollection(this.world, this.tweenManager);
	   	else {
	   		for(GameObject go : enemies.objects) {
	   			go.resetGraphics(this.actualPack.getAtlas());
	   		}
	   	}
	   	this.enemies.resetGraphics(this.actualPack.getAtlas());
	   	
	   	if(sparkles == null) sparkles = new AtlasAnimationCollection(GameAssets.fetchTextureAtlas("game/images/game_objects.pack"), 5, "explosion_sparkles");
	   	else {
	   		sparkles.setAtlas(GameAssets.fetchTextureAtlas("game/images/game_objects.pack"));
	   		for(AtlasAnimation go : sparkles.animations) {
	   			go.resetGraphics(GameAssets.fetchTextureAtlas("game/images/game_objects.pack"));
	   		}
	   	}
	   	
	   	if(draco == null) this.draco = new Draco(world,8.0f,6.0f, buttons, sparkles);
	   	else {
	   		this.draco.resetGraphics(GameAssets.fetchTextureAtlas("game/images/game_objects.pack"));
	   		this.draco.resetGraphics(GameAssets.fetchTextureAtlas("game/images/game_objects.pack"));
	   		draco.resetGraphicsFireballPool(GameAssets.fetchTextureAtlas("game/images/game_objects.pack"));
	   	}
	   	
	   	this.middleLayer = new ParallaxLayer(this.actualPack.getMiddleLayerSpeed(),this.actualPack.getMiddleLayer());
	   	if(Configuration.FogActive)	{
	   		fogLayer = new ParallaxLayer(this.actualPack.getFogLayerSpeed(),this.actualPack.getFogLayer());
	   	}
	   	this.frontLayer = new ParallaxLayer(this.actualPack.getFrontLayerSpeed(),this.actualPack.getFrontLayer());
	   	this.backLayer = new ParallaxLayer(this.actualPack.getBackLayerSpeed(),this.actualPack.getBackLayer());
		
	   	
   		this.pauseLayer = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("blackLayer"));
   		this.pauseLayer.setSize(Configuration.TARGET_WIDTH+30f, Configuration.TARGET_HEIGHT+30f);
   		this.pauseLayer.setPosition(-15f, -15f);
   		
   		this.menu = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_menu"));
   		this.menu.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_rate").getRegionWidth(),+GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_rate").getRegionHeight()+16);
   		this.menu.setPosition(354f, 312);
   		
   		this.rate = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_rate"));
   		this.rate.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_rate").getRegionWidth(),GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_rate").getRegionHeight());
   		this.rate.setPosition(716f, 320f);
   		
   		this.more = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_more"));
   		this.more.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_rate").getRegionWidth(),GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_rate").getRegionHeight());
   		this.more.setPosition(535, 320);
	   	
	   	this.hud_sign = GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("distance_sign");
	   	this.hud_track = GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("distance_track");
	   	
	   	this.pauseResumeBtn = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_right"));
	   	this.pauseResumeBtn.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_right").getRegionWidth(),GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("btn_right").getRegionHeight());
	   	this.pauseResumeBtn.setPosition(1090f,15f);
	   	
	   	if(this.resumeEnemies) {
	   		this.enemies.resume();
	   		this.resumeEnemies = false;
	   	}
	   	if(this.startLevelMusic) {
	   		this.startLevelMusic = false;
	   		this.actualPack.getActualLevel().setLevelMusic(GameAssets.fetchMusic(this.actualPack.getActualLevel().getLevelMusicString()));
	   		GameAssets.playMusic(this.actualPack.getActualLevel().getLevelMusic(), true, musicLevel);
	   	}
	   	
	   	if(Configuration.debugLevel >= Application.LOG_DEBUG) this.debugRenderer = new Box2DDebugRenderer();
	}
	
	
	public void setLevelPack(AbstractLevelpack pack) {
		this.actualPack = pack;
		if(this.world == null) {
			this.world = new World(new Vector2(0f,-10f), true);
			this.world.setContactListener(new ObjectContactListener());
		}
	}
	
	
	public AbstractLevelpack getLevelpack() {
		return this.actualPack;
	}
	
	
	public void nextLevel() {
		if(this.actualPack.nextLevel()) {
			this.tweenManager.killAll();
			this.middleLayer.dispose();
			this.middleLayer = new ParallaxLayer(this.actualPack.getMiddleLayerSpeed(),this.actualPack.getMiddleLayer());
			if(Configuration.FogActive)	{
				this.fogLayer.dispose();
				this.fogLayer = new ParallaxLayer(this.actualPack.getFogLayerSpeed(),this.actualPack.getFogLayer());
			}
			this.frontLayer.dispose();
		   	this.frontLayer = new ParallaxLayer(this.actualPack.getFrontLayerSpeed(),this.actualPack.getFrontLayer());
			
		   	this.backLayer.dispose();
		   	this.backLayer = new ParallaxLayer(this.actualPack.getBackLayerSpeed(),this.actualPack.getBackLayer());
			this.obstacles.clear();
			this.sparkles.clear();
			this.fireballs.clear();
			this.powerups.clear();
			this.enemies.clear();
			if(this.draco.isDisposed() == false) this.draco.dispose();
			this.draco = new Draco(world,8.0f,6.0f, this.buttons, this.sparkles);
			this.draco.setEnergy(this.chilisCount);
			if(this.chilisCount > 1) draco.enableFireMode(true);
			
		} else {
			this.keepScreen = false;
			this.solved = true;
			GameScreen.paused = true;
			this.doDisposing = true;
		}
	}
	
	
	public void retryLevel() {
		this.obstacles.clear();
		this.sparkles.clear();
		this.fireballs.clear();
		this.powerups.clear();
		this.tweenManager.killAll();
		this.enemies.clear();
		this.actualPack.getActualLevel().resetLevel();
		if(this.draco.isDisposed() == false) this.draco.dispose();
		this.draco = new Draco(world,8.0f,6.0f, this.buttons, this.sparkles);
	}


	public Draco getActualDraco() {
		return this.draco;
	}
	
	public EnemyCollection getActualEnemies() {
		return this.enemies;
	}
	
	public AbstractObstacleCollection getActualObstacles() {
		return this.obstacles;
	}
	
	public GameObjectCollection getActualPowerups() {
		return this.powerups;
	}
	
	public GameObjectCollection getActualFireballs() {
		return this.fireballs;
	}
	
	public World getActualWorld() {
		return this.world;
	}



	@Override
	public void setAdShowed(boolean adShowed) {
		this.adShowed = adShowed;
		
	}



	@Override
	public void stepBack(String source) {
		if(GameAssets.buttonTimer <= 0f) {
			try {
				if(GameScreen.paused == true) {
					this.doDisposing = true;
				} else {
					this.pauseMessage = this.pauseMessageOriginal.split("\n");
					this.pause();
				}			
			} catch(Exception e) {
				
			}
			GameAssets.buttonTimer = 0.35f;
		}
	}



	@Override
	public void startPress() {
		if(GameAssets.buttonTimer <= 0f) {
			try {
				if(GameScreen.paused == false) {
					this.pauseMessage = this.pauseMessageOriginal.split("\n");
					this.pause();
				} else {
					this.endPause();
				}
			} catch(Exception e) {
				
			}
			GameAssets.buttonTimer = 0.35f;
		}
	}



	@Override
	public void primaryPress() {
		if(GameAssets.buttonTimer <= 0f) {
			try {
				if(GameScreen.paused == true) {
					this.endPause();
				}
			} catch(Exception e) {
				
			}
			GameAssets.buttonTimer = 0.35f;
		}
	}



	@Override
	public void steerXAxis(float peculiarity) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void steerYAxis(float peculiarity) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public boolean screenTouched(int x, int y, int pointer) {
		try {
			Vector2 touchPoint = new Vector2((float)x/Gdx.graphics.getWidth()*Configuration.VIEWPORT_WIDTH, Configuration.VIEWPORT_HEIGHT-((float)y/Gdx.graphics.getHeight()*Configuration.VIEWPORT_HEIGHT));
			if(this.disposed == false) {
				if(GameScreen.paused == true && this.showCounter >= 0.5f) {
					Vector2 touchPoint2 = new Vector2((float)x*Configuration.TARGET_WIDTH/Gdx.graphics.getWidth(), Configuration.TARGET_HEIGHT - ((float)y*Configuration.TARGET_HEIGHT/Gdx.graphics.getHeight()));
					if(this.pauseResumeBtn != null && this.pauseResumeBtn.getBoundingRectangle().contains(touchPoint2.x, touchPoint2.y)) {
						this.draco.upPressed = false;
						this.draco.downPressed = false;
						this.draco.leftPressed = false;
						this.draco.rightPressed = false;
						this.endPause();
					}
					if(this.more != null && this.more.getBoundingRectangle().contains(touchPoint2.x, touchPoint2.y)) {
						GameAssets.nativ.sendEvent("SocialAction", "More Button", "pressed", 1);
						GameAssets.nativ.more();
					}
					if(this.menu != null && this.menu.getBoundingRectangle().contains(touchPoint2.x, touchPoint2.y)) {
						this.doDisposing = true;
					}
					if(this.rate != null && this.rate.getBoundingRectangle().contains(touchPoint2.x, touchPoint2.y)) {
						GameAssets.nativ.sendEvent("SocialAction", "Rate Button", "pressed", 1);
						GameAssets.nativ.rate();
					}
					
				}
				
				if(Configuration.inputType == 1 && GameScreen.paused == false && this.showCounter >= 0.5f) {
					if(draco.up.getRectangle().contains(touchPoint.x,touchPoint.y) == true) {
						draco.upPressed = true;
						draco.downPressed = false;
					}
					if(draco.down.getRectangle().contains(touchPoint.x,touchPoint.y) == true) {
						draco.downPressed = true;
						draco.upPressed = false;
					}
					if(draco.left.getRectangle().contains(touchPoint.x,touchPoint.y) == true) {
						draco.leftPressed = true;
						draco.rightPressed = false;
						draco.firePressed = false;
					}
					if(draco.right.getRectangle().contains(touchPoint.x,touchPoint.y) == true) {
						draco.rightPressed = true;
						draco.leftPressed = false;
						draco.firePressed = false;
					}
					if(draco.fire.getRectangle().contains(touchPoint.x,touchPoint.y) == true) {
						draco.firePressed = true;
						draco.rightPressed = false;
						draco.leftPressed = false;
					}
				}
			}
		} catch(Exception e) {
			
		}
		return false;
	}



	@Override
	public boolean screenAfterTouched(int x, int y, int pointer) {
		try {
			Vector2 touchPoint = new Vector2((float)x/Gdx.graphics.getWidth()*Configuration.VIEWPORT_WIDTH, Configuration.VIEWPORT_HEIGHT-((float)y/Gdx.graphics.getHeight()*Configuration.VIEWPORT_HEIGHT));			
			if(GameScreen.paused == false && this.disposed == false) {
				
				if(Configuration.inputType == 1 && this.draco != null && this.showCounter >= 0.5f) {
					if(touchPoint.x < Configuration.VIEWPORT_WIDTH/2) {
						if(draco.up.getRectangle().contains(touchPoint.x,touchPoint.y) == true) draco.upPressed = false;
						if(draco.down.getRectangle().contains(touchPoint.x,touchPoint.y) == true) draco.downPressed = false;
					} else {
						if(draco.left.getRectangle().contains(touchPoint.x,touchPoint.y) == true) draco.leftPressed = false;
						if(draco.right.getRectangle().contains(touchPoint.x,touchPoint.y) == true) draco.rightPressed = false;
						if(draco.fire.getRectangle().contains(touchPoint.x,touchPoint.y) == true) draco.firePressed = false;
					}
				}
			}
		} catch(Exception e) {
			
		}
		return false;
	}



	@Override
	public boolean screenWhileTouch(int x, int y, int pointer) {
		try {
			Vector2 touchPoint = new Vector2((float)x/Gdx.graphics.getWidth()*Configuration.VIEWPORT_WIDTH, Configuration.VIEWPORT_HEIGHT-((float)y/Gdx.graphics.getHeight()*Configuration.VIEWPORT_HEIGHT));
			if(GameScreen.paused == false && Configuration.inputType == 1 && this.draco != null && this.disposed == false && this.showCounter >= 0.5f) {
				if(draco.up.getRectangle().contains(touchPoint.x,touchPoint.y) == true) draco.upPressed = true;
				else {
					if(touchPoint.x < Configuration.VIEWPORT_WIDTH/2) draco.upPressed = false;
				}
				
				if(draco.down.getRectangle().contains(touchPoint.x,touchPoint.y) == true) draco.downPressed = true;
				else {
					if(touchPoint.x < Configuration.VIEWPORT_WIDTH/2) draco.downPressed = false;
				}
				
				if(draco.left.getRectangle().contains(touchPoint.x,touchPoint.y) == true) draco.leftPressed = true;
				else  {
					if(touchPoint.x >= Configuration.VIEWPORT_WIDTH/2) draco.leftPressed = false;
				}
				
				if(draco.right.getRectangle().contains(touchPoint.x,touchPoint.y) == true) draco.rightPressed = true;
				else  {
					if(touchPoint.x >= Configuration.VIEWPORT_WIDTH/2) draco.rightPressed = false;
				}
				
				if(Configuration.autoFire == false) {
					if(draco.fire.getRectangle().contains(touchPoint.x,touchPoint.y) == true) {
						draco.firePressed = true;
					}
					else  {
						if(touchPoint.x >= Configuration.VIEWPORT_WIDTH/2) draco.firePressed = false;
					}
				}
			}
		} catch(Exception e) {
			
		}
		return false;
	}


}
