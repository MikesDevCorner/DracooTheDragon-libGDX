package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AbstractLevelpack;
import com.xsheetgames.genericElements.CoverLayout;

public class ChooseLevelScreen extends AbstractScreen {

	private SpriteBatch batch;
	private Sprite screenBackground;
	private Sprite blackLayer, backBtn, tmpSprite;
	private Array<Array<Object>> allLevels;
	private boolean disposed = false;
	private boolean assetsLoaded;
	private String levelPackHeading;
	private AbstractLevelpack levelPack;
	private boolean adShowed;
	private boolean resetToMainScreen;
	private int countEggs;
	private boolean lastConnectedState;
	private float axisCount = 0f;
	private int levelPackIndex;
	private int highlighted = 0;
	private Sprite highlight;
	
	public ChooseLevelScreen(Game game, int levelPackIndex) {
		this.game = game;
		this.resetToMainScreen = false;
		this.adShowed = false;
		this.levelPackIndex = levelPackIndex;
	}
	
	public void setLevelPack(AbstractLevelpack pack) {
		this.levelPack = pack;
	}
	
	@Override
	public void render(float delta) {
		if(GameAssets.assetsLoaded(batch) && this.adShowed == true) {
			if(this.disposed == false) {
				if(GameAssets.assetsLoaded(batch)) {

					if(assetsLoaded == false) this.doAssetProcessing();

					this.beginScreenPass(batch);
					CoverLayout.apply(screenBackground, screenBackground.getRegionWidth(), screenBackground.getRegionHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					this.blackLayer.setSize(Gdx.graphics.getWidth()+10f, Gdx.graphics.getHeight()+10f);
					this.blackLayer.setPosition(-5f, -5f);
					batch.begin();
					screenBackground.draw(batch);
					blackLayer.draw(batch);
					batch.end();

					this.beginUiPass(batch);
					batch.begin();
					if(adShowed == true) {
						backBtn.draw(batch);
					}
					
					Sprite tmpSprite = null;
					
					
					
					if(adShowed == true) {
						
						if(highlighted > 0) {
							tmpSprite = ((Sprite)(this.allLevels.get(highlighted - 1).get(0)));
							this.highlight.setPosition(tmpSprite.getX(), tmpSprite.getY());
							this.highlight.draw(batch);
							tmpSprite = null;
						}
						
						for(int i = 0; i < this.allLevels.size; i++) {
							tmpSprite = ((Sprite)(this.allLevels.get(i).get(0)));
							
							tmpSprite.draw(batch);
							if(((String)this.allLevels.get(i).get(1)).equals("locked") == false && i != 14) {
								GameAssets.fetchFont("fonts/memory.fnt").draw(batch,Integer.toString(i+1),tmpSprite.getX()+((i>=9)?55f:65f),tmpSprite.getY()+155f);
							}
						}
					}
					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), levelPackHeading);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,levelPackHeading, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 780f);
					batch.end();
					this.endScreenRender();


					if(this.resetToMainScreen == true) {
						this.dispose();
						this.game.setScreen(new MenuScreen(this.game));
					}
					
					//Emulate Events
					if(GameAssets.buttonTimer > 0f) GameAssets.buttonTimer-=delta;
					if(this.axisCount > 0f) this.axisCount-=delta;
}
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		this.updateUiViewport(width, height);
	}

	@Override
	public void show() {
		
		this.adShowed = true;

		lastConnectedState = GameAssets.input.isControllerConnected();
		highlighted = GameAssets.input.isControllerConnected() ? 1 : 0;
			
		if(this.adShowed == true) {
			
			this.countEggs = 0;
			
			this.levelPackHeading = this.levelPack.packName + "-Levels";
			
			this.assetsLoaded = false;
			this.batch = new SpriteBatch();
			this.setupUiViewport();

			this.screenBackground = new Sprite(GameAssets.fetchTexture("menu/images/credits_back.jpg"));
			this.blackLayer = new Sprite(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("blackLayer"));
		}
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
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
			screenBackground = null;
			blackLayer = null;
			backBtn = null;
			//levelPack = null;			
			allLevels.clear();
			allLevels = null;
			this.disposed = true;
		}
	}
	
	private void doAssetProcessing() {
		TextureAtlas atlas = GameAssets.fetchTextureAtlas("menu/images/menu_items.pack");		
		this.backBtn = new Sprite(atlas.findRegion("backbtn"));
		this.backBtn.setSize(atlas.findRegion("backbtn").getRegionWidth(), atlas.findRegion("backbtn").getRegionHeight());
		this.backBtn.setPosition(Configuration.TARGET_WIDTH-backBtn.getWidth()-50f,50f);
		
		
		
		this.allLevels = new Array<Array<Object>>();
		
		int spacerBetweenX = 43;
		int spacerBetweenY = 20;
		int spacerBorderLeft = 135;
		int spacerBorderTop = 290;
		int elementsInRow = 5;
		
		float positionX;
		float positionY;
		AtlasRegion tmpRegion;
		String tmpString;
		
		for(int i = 0; i<15; i++) {
			
			tmpRegion = atlas.findRegion("level_buttons_noegg");
			tmpString = "noeggs";
			
			if(GameAssets.gameProgress != null) {
				for(XmlReader.Element pack : GameAssets.gameProgress.getChildrenByName("pack")) {
					if(pack.getAttribute("name").equals(this.levelPack.packName)) {
						for(XmlReader.Element levelItem : pack.getChildrenByName("level")) {
							if(levelItem.getAttribute("number").equals((i+1)+"")) {								
								
								if(i<this.levelPack.getLevelCount()) {
									if(i != 14) {
										
										if(levelItem.getAttribute("status").equals("noeggs")) {
											tmpRegion = atlas.findRegion("level_buttons_noegg");
											tmpString = "noeggs";
										}
										if(levelItem.getAttribute("status").equals("oneegg")) {
											tmpRegion = atlas.findRegion("level_buttons_one");
											tmpString = "oneegg";
											this.countEggs += 1;
										}
										if(levelItem.getAttribute("status").equals("twoeggs")) {
											tmpRegion = atlas.findRegion("level_buttons_two");
											tmpString = "twoeggs";
											this.countEggs += 2;
										}
										if(levelItem.getAttribute("status").equals("threeeggs")) {
											tmpRegion = atlas.findRegion("level_buttons_all");
											tmpString = "threeeggs";
											this.countEggs += 3;
										}
										
										if(levelItem.getAttribute("status").equals("locked")) {
											tmpRegion = atlas.findRegion("level_buttons_locked");
											tmpString = "locked";
										}
									}									
									else {
										
										if(levelItem.getAttribute("status").equals("locked")) {
											tmpRegion = atlas.findRegion("level_buttons_boss_lock");
											tmpString = "locked";
										}
										
										if(levelItem.getAttribute("status").equals("noeggs")) {
											tmpRegion = atlas.findRegion("level_buttons_boss_noegg");
											tmpString = "noeggs";
										}
										
										if(levelItem.getAttribute("status").equals("threeeggs")) {
											tmpRegion = atlas.findRegion("level_buttons_boss_all");
											tmpString = "threeeggs";
										}
										
										if(this.countEggs < 24) {
											tmpRegion = atlas.findRegion("level_buttons_boss_2lock");
											tmpString = "locked";
										}
									}
									
								} else {
									tmpRegion = atlas.findRegion("level_buttons_not_available");
									tmpString = "locked";
								}
							}
						}
					}
				}
			}
			
			this.tmpSprite = new Sprite(tmpRegion);
			this.tmpSprite.setSize(tmpRegion.getRegionWidth(), tmpRegion.getRegionHeight());
			
			positionX = spacerBorderLeft + i%elementsInRow * tmpRegion.getRegionWidth() + i%elementsInRow * spacerBetweenX;
			positionY = Configuration.TARGET_HEIGHT - (spacerBorderTop + ((int) i/elementsInRow) * tmpRegion.getRegionHeight() + ((int) i/elementsInRow) * spacerBetweenY);
			this.tmpSprite.setPosition(positionX,positionY);
			
			Array<Object> tmpArray = new Array<Object>();
			tmpArray.add(this.tmpSprite);
			tmpArray.add(tmpString);			
			this.allLevels.add(tmpArray);
		}
		this.highlight = new Sprite(atlas.findRegion("highlight"));
		this.highlight.setSize(atlas.findRegion("highlight").getRegionWidth(), atlas.findRegion("highlight").getRegionHeight());
		this.highlight.setPosition(-400, -400);
		
		this.assetsLoaded = true;
	}
	
	
	public void commandToMainScreen() {
		this.resetToMainScreen = true;
	}

	@Override
	public void stepBack(String source) {
		if(GameAssets.buttonTimer <= 0f) {
			this.dispose();
			this.game.setScreen(new ChooseLevelpackScreen(this.game, this.levelPackIndex));
			GameAssets.buttonTimer = 0.35f;
		}
	}

	@Override
	public void startPress() {
		if(this.highlighted > 0) {
			if(GameAssets.buttonTimer <= 0f) {
				activateLevelButton(this.highlighted-1);
				GameAssets.buttonTimer = 0.35f;
			}
		}
	}

	@Override
	public void primaryPress() {
		if(this.highlighted > 0) {
			if(GameAssets.buttonTimer <= 0f) {
				activateLevelButton(this.highlighted-1);
				GameAssets.buttonTimer = 0.35f;
			}			
		}		
	}

	@Override
	public void steerXAxis(float peculiarity) {
		if(this.highlighted > 0) {			
			if(this.axisCount <= 0f) {
				if(peculiarity > 0f) {
					if(this.highlighted % 5 == 0) this.highlighted -= 5;
					this.highlighted++; 
				} else if (peculiarity < 0f) {
					if((this.highlighted - 1) % 5 == 0) this.highlighted += 5;
					this.highlighted--;
				}
				this.axisCount = 0.35f;
			}
		}
	}

	@Override
	public void steerYAxis(float peculiarity) {
		if(this.highlighted > 0) {			
			if(this.axisCount <= 0f) {
				if(peculiarity > 0f) {
					this.highlighted+=5;
					if(this.highlighted > 15) this.highlighted -= 15;
				} else if (peculiarity < 0f) {
					this.highlighted-=5;
					if(this.highlighted < 1) this.highlighted += 15;
				}
				this.axisCount = 0.35f;
			}
		}
	}

	@Override
	public boolean screenTouched(int x, int y, int pointer) {
		try {
			Vector2 touchPoint = this.unprojectUi(x, y);
			if(backBtn != null && backBtn.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				this.dispose();
				this.game.setScreen(new ChooseLevelpackScreen(this.game, this.levelPackIndex));
				return true;
			}
			
			
			for(int i = 0; i<this.levelPack.getLevelCount(); i++) {
				if(((Sprite)this.allLevels.get(i).get(0)) != null && ((Sprite)this.allLevels.get(i).get(0)).getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
					this.activateLevelButton(i);
					return true;
				}
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
	
	
	private void activateLevelButton(int i) {
		if(((String)this.allLevels.get(i).get(1)).equals("locked") == false) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			GameAssets.fetchMusic("menu/music/tribute.mp3").stop();
			this.dispose();
			
			GameAssets.unloadMenuAssets();
			GameAssets.manager.clear();
			GameAssets.manager.finishLoading();
			GameAssets.loadGameAssets();
			this.levelPack.loadAssets();
			
			GameScreen gameScreen = new GameScreen(this.game);
			this.levelPack.setStartLevel(i+1);
		    
			gameScreen.setLevelPack(this.levelPack);
			if(i==0 && Configuration.useIntroScreen) {
				StartPackScreen sps = new StartPackScreen(this.game);
				sps.setComic(this.levelPack.getStartComic());						
				
				sps.setGameScreen(gameScreen);
				this.game.setScreen(sps);
			}
			else this.game.setScreen(gameScreen);
		} else {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click2.mp3"));
		}
	}

}