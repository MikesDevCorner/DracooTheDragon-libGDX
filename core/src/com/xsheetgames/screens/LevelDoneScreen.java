	package com.xsheetgames.screens;

	import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AbstractLevelpack;

	public class LevelDoneScreen extends AbstractScreen {

		private boolean keepGameScreen = false;
		private GameScreen gameScreen;
		private SpriteBatch batch;
		private Sprite screenBackground;
		private Sprite noeggs, oneegg, twoeggs, threeeggs;
		private Sprite next, menu, retry;
		private boolean disposed = false;
		private boolean soundPlayed = false;
		private int level;
		private Sprite blackLayer;
		private String pack;
		private float actualTimeDelay = 1.2f;
		private float reachedPoints = 0f;
		private float reachedPointsSingleCounting = 0f;
		private boolean quater = false;
		private boolean half = false;
		private boolean seventyfife = false;
		private Sound counting;
		private boolean countingPlaying = false;
		private int howMuchEggsReached;
		private boolean keepScreen = false;
		private boolean lastConnectedState;
		
		
		public LevelDoneScreen(Game game, GameScreen gameScreen, int level, String pack) {
			this.game = game;
			this.level = level;
			this.pack = pack;
			this.gameScreen = gameScreen;
			this.keepScreen = false;
			this.counting = GameAssets.fetchSound("game/sounds/counting.mp3");
		}
	
		
		@Override
		public void render(float delta) {
			if(this.disposed == false) {
				if(GameAssets.assetsLoaded(batch)) {
					if(Configuration.soundEnabled == true && soundPlayed == false) {
						GameAssets.playSound(GameAssets.fetchSound("game/sounds/cheers.mp3"),0.35f);
						if(howMuchEggsReached == 0) GameAssets.playSound(GameAssets.fetchSound("game/sounds/win0.mp3"),2f);
						if(howMuchEggsReached == 1) GameAssets.playSound(GameAssets.fetchSound("game/sounds/win1.mp3"),2f);
						if(howMuchEggsReached == 2) GameAssets.playSound(GameAssets.fetchSound("game/sounds/win2.mp3"),2f);
						if(howMuchEggsReached == 3) GameAssets.playSound(GameAssets.fetchSound("game/sounds/win3.mp3"),2f);
						soundPlayed = true;
					}
					this.batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
					batch.begin();
					batch.disableBlending();
					screenBackground.draw(batch);
					batch.enableBlending();
					//blackLayer.draw(batch);
					this.next.draw(batch);
					this.menu.draw(batch);
					this.retry.draw(batch);

					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.pack + " Level " + this.level +" cleared");
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch, this.pack + " Level " + this.level +" cleared", Configuration.TARGET_WIDTH/2-GameAssets.glyphLayout.width/2 , Configuration.TARGET_HEIGHT/100*92 - 60f);
					
					if(this.actualTimeDelay >= 0f) {
						this.actualTimeDelay -= delta; 
						if(Configuration.soundEnabled == true) {
							this.counting.stop();
							this.countingPlaying = false;
						}
					}
					else {
						if(this.reachedPoints < this.gameScreen.getActualDraco().getEnemiesKilled()) {
							this.reachedPoints += ((this.gameScreen.getActualDraco().getEnemiesKilled()/3f) * delta); //Dauert 3 Sekunden fr alle 4 Zustnde
							this.reachedPointsSingleCounting += ((this.gameScreen.getActualDraco().getEnemiesKilled()*150f/3f) * delta); //zum langsamen mitzhlen, ist aber frs Endergebnis falsch
							
							if(this.countingPlaying == false && Configuration.soundEnabled == true) {
								this.counting.loop();
								this.countingPlaying = true;
							}
						}
						else {
							if(Configuration.soundEnabled == true) {
								this.counting.stop();
								this.countingPlaying = false;
							}
						}
					}
					float quotient = 100;
					if(this.gameScreen.getActualEnemies().getEnemyCounter() != 0) {
						quotient = (this.reachedPoints * 100f) / this.gameScreen.getActualEnemies().getEnemyCounter();
					}
					
					if(quotient < 25f) {
						this.noeggs.draw(batch);
					}
					if(quotient >= 25f && quotient < 50f) {
						this.oneegg.draw(batch);
						if(quater == false) {
							this.actualTimeDelay = 0.5f;
							GameAssets.playSound(GameAssets.fetchSound("game/sounds/egg.mp3"));
							quater = true;
						}
					}
					if(quotient >= 50f && quotient < 75f) {
						this.twoeggs.draw(batch);
						if(half == false) {
							this.actualTimeDelay = 0.5f;
							GameAssets.playSound(GameAssets.fetchSound("game/sounds/egg.mp3"));
							half = true;
						}
					}
					if(quotient >= 75f) {
						this.threeeggs.draw(batch);
						if(seventyfife == false) {
							this.actualTimeDelay = 0.5f;
							GameAssets.playSound(GameAssets.fetchSound("game/sounds/egg.mp3"));
							seventyfife = true;
						}
					}
					
					if(this.reachedPoints < this.gameScreen.getActualDraco().getEnemiesKilled()) {
						GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), (int)this.reachedPointsSingleCounting + " of "+(int)(this.gameScreen.getActualEnemies().getEnemyCounter() * 150f)+" Pts");
						GameAssets.fetchFont("fonts/memory.fnt").draw(batch, (int)this.reachedPointsSingleCounting + " of "+(int)(this.gameScreen.getActualEnemies().getEnemyCounter() * 150f)+" Pts", Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 280);
					}
					else {
						GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), (((int)this.reachedPoints) * 150) + " of "+(int)(this.gameScreen.getActualEnemies().getEnemyCounter() * 150f)+" Pts");
						GameAssets.fetchFont("fonts/memory.fnt").draw(batch, (((int)this.reachedPoints) * 150) + " of "+(int)(this.gameScreen.getActualEnemies().getEnemyCounter() * 150f)+" Pts", Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 280);
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
							GameAssets.nativ.showMessage("Controller", "Moga Controller connected");
						}
						if(GameAssets.nativ.isControllerConnected() == false && this.lastConnectedState == true) {
							GameAssets.nativ.showMessage("Controller", "Moga Controller disconnected");
						}
					}
					lastConnectedState = GameAssets.nativ.isControllerConnected();
				}
			}
		}

		@Override
		public void resize(int width, int height) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void show() {			
				
			lastConnectedState = GameAssets.nativ.isControllerConnected();
			
			howMuchEggsReached = 0;
			this.keepScreen = false;
			GameAssets.nativ.trackPageView("/LevelDone");
			GameAssets.nativ.sendEvent("Player-State", "LevelDone", this.gameScreen.getLevelpack().packName+ " Level "+this.level, this.gameScreen.getActualDraco().getEnemiesKilled());
			
			this.batch = new SpriteBatch();
			this.screenBackground = new Sprite(GameAssets.fetchTexture("game/images/background.jpg"));
			screenBackground.setSize(GameAssets.fetchTexture("game/images/background.jpg").getWidth(), GameAssets.fetchTexture("game/images/background.jpg").getHeight());
			
			this.next = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("advance"));
			this.next.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("advance").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("advance").getRegionHeight());
			this.next.setPosition(Configuration.TARGET_WIDTH/2 + next.getWidth()*0.7f, 55f);
			
			this.menu = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("menu"));
			this.menu.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("menu").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("menu").getRegionHeight());
			this.menu.setPosition(Configuration.TARGET_WIDTH/2 - (menu.getWidth() + menu.getWidth() * 0.7f), 55f);
			
			this.retry = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("retry"));
			this.retry.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("retry").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("retry").getRegionHeight());
			this.retry.setPosition(Configuration.TARGET_WIDTH/2 - retry.getWidth()/2, 55f);
			
			this.noeggs = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_no"));
			this.noeggs.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_no").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_no").getRegionHeight());
			this.noeggs.setPosition(Configuration.TARGET_WIDTH/2 - noeggs.getWidth()/2, 210);
			
			this.oneegg = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_one"));
			this.oneegg.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_one").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_one").getRegionHeight());
			this.oneegg.setPosition(Configuration.TARGET_WIDTH/2 - oneegg.getWidth()/2, 210);
			
			this.twoeggs = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_two"));
			this.twoeggs.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_two").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_two").getRegionHeight());
			this.twoeggs.setPosition(Configuration.TARGET_WIDTH/2 - twoeggs.getWidth()/2, 210);
			
			this.threeeggs = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_three"));
			this.threeeggs.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_three").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("eggs_three").getRegionHeight());
			this.threeeggs.setPosition(Configuration.TARGET_WIDTH/2 - threeeggs.getWidth()/2, 210);
			
			this.blackLayer = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("blackLayer"));
			this.blackLayer.setSize(1280f,800f);
			this.blackLayer.setPosition(-5f, -5f);
			
			float quotient = 100;
			if(this.gameScreen.getActualEnemies().getEnemyCounter() != 0) {
				quotient = this.gameScreen.getActualDraco().getEnemiesKilled() * 100 / this.gameScreen.getActualEnemies().getEnemyCounter();
				if(quotient < 25f) howMuchEggsReached = 0;
				if(quotient >= 25f && quotient < 50f) howMuchEggsReached = 1;
				if(quotient >= 50f && quotient < 75f) howMuchEggsReached = 2;
				if(quotient >= 75f) howMuchEggsReached = 3;
			}
			
			
			if(GameAssets.gameProgress != null && Gdx.files.isLocalStorageAvailable()) {
				for(XmlReader.Element pack : GameAssets.gameProgress.getChildrenByName("pack")) {
					if(pack.getAttribute("name").equals(this.gameScreen.getLevelpack().packName)) {
						for(XmlReader.Element levelItem : pack.getChildrenByName("level")) {
							if(levelItem.getAttribute("number").equals(this.gameScreen.getLevelpack().getActualLevel().getNumber()+"")) {
								if(howMuchEggsReached == 0) {
									if(levelItem.getAttribute("status").equals("locked")) {
										levelItem.setAttribute("status", "noeggs");
									}
								}
								if(howMuchEggsReached == 1) {
									if(levelItem.getAttribute("status").equals("locked") || levelItem.getAttribute("status").equals("noeggs")) {
										levelItem.setAttribute("status", "oneegg");
									}
								}
								if(howMuchEggsReached == 2) {
									if(levelItem.getAttribute("status").equals("threeeggs") == false && levelItem.getAttribute("status").equals("twoeggs") == false) {
										levelItem.setAttribute("status", "twoeggs");
									}
								}
								if(howMuchEggsReached == 3) {
									if(levelItem.getAttribute("status").equals("threeeggs") == false) {
										levelItem.setAttribute("status", "threeeggs");
									}
								}
							}
							if(this.gameScreen.getLevelpack().getActualLevel().getNumber() < 15) {
								if(levelItem.getAttribute("number").equals((this.gameScreen.getLevelpack().getActualLevel().getNumber()+1)+"")) {
									if(levelItem.getAttribute("status").equals("locked")) {
										levelItem.setAttribute("status", "noeggs");
									}
								}
							}
						}
					}
				}
				//rewrite xml-file
				GameAssets.ProgressFileHandle.writeString(GameAssets.gameProgress.toString(), false);
			}
		}

		@Override
		public void hide() {
			if(Configuration.soundEnabled == true) {
				try {
					this.counting.stop();
					this.countingPlaying = false;
				} catch(Exception exc) {}
				
			}
			if(this.keepScreen == false) this.dispose();			
		}

		@Override
		public void pause() {
			if(Configuration.soundEnabled == true) {
				try {
					this.counting.stop();
					this.countingPlaying = false;
				} catch(Exception exc) {}
			}			
		}

		@Override
		public void resume() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			if(this.disposed == false) {
				batch.dispose();
				batch = null;
				if(Configuration.soundEnabled) this.counting.stop();
				this.counting = null;
				screenBackground = null;
				noeggs=null;
				oneegg=null;
				twoeggs=null;
				threeeggs=null;
				next=null;
				menu=null;
				retry=null;
				counting=null;
				if(keepGameScreen == false) this.gameScreen.dispose();
				this.disposed = true;
			}
		}


				
		
		public void returnToMainMenu() {
			AbstractLevelpack tmpPack = this.gameScreen.getLevelpack();
			this.dispose();
			this.gameScreen.dispose();
			GameAssets.unloadGameAssets();
			tmpPack.unloadAssets();
			tmpPack = null;
			GameAssets.manager.clear();
			GameAssets.manager.finishLoading();
			GameAssets.loadMenuAssets();
			this.game.setScreen(new MenuScreen(this.game));
		}



		@Override
		public void stepBack(String source) {
			if(GameAssets.buttonTimer <= 0f) {
				this.returnToMainMenu();
				GameAssets.buttonTimer = 0.35f;
			}
		}


		@Override
		public void startPress() {
			if(GameAssets.buttonTimer <= 0f) {
				this.keepGameScreen = true;
				this.gameScreen.nextLevel();
				this.game.setScreen(this.gameScreen);
				GameAssets.buttonTimer = 0.35f;
			}
		}


		@Override
		public void primaryPress() {
			if(GameAssets.buttonTimer <= 0f) {
				this.keepGameScreen = true;
				this.gameScreen.nextLevel();
				this.game.setScreen(this.gameScreen);
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
				Vector2 touchPoint = new Vector2((float)x*Configuration.TARGET_WIDTH/Gdx.graphics.getWidth(), Configuration.TARGET_HEIGHT - ((float)y*Configuration.TARGET_HEIGHT/Gdx.graphics.getHeight()));
				if(next.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
					this.keepGameScreen = true;
					this.dispose();
					this.gameScreen.nextLevel();
					if(this.gameScreen.getLevelpack().getActualLevelNumber() == 15) {
						this.game.setScreen(new PreBossScreen(this.game,this.gameScreen));
					}
					else {
						this.game.setScreen(this.gameScreen);
					}
					return true;
				}
				
				if(menu.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
					this.returnToMainMenu();
					return true;
				}
				
				if(retry.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
					this.keepGameScreen = true;
					this.dispose();
					this.gameScreen.retryLevel();
					this.game.setScreen(this.gameScreen);
					return true;
				}
			} catch(Exception e) {
				
			}
			return false;
		}


		@Override
		public boolean screenAfterTouched(int x, int y, int pointer) {
			// TODO Auto-generated method stub
			return false;
		}


		@Override
		public boolean screenWhileTouch(int x, int y, int pointer) {
			// TODO Auto-generated method stub
			return false;
		}

	}
