package com.xsheetgames.genericGameObjects;

import java.util.Random;

import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.equations.Sine;

import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.genericElements.Level;
import com.xsheetgames.screens.GameScreen;

public abstract class EnemyCollection extends GameObjectCollection {
	
	protected float enemyTimer;
	protected Random rand;
	protected World world;
	protected TweenManager tweens;
	protected int enemyCounter;
	
	public EnemyCollection(World world, TweenManager t) {
		super();
		this.world = world;
		this.rand = new Random();
		this.tweens = t;
		this.enemyCounter = 0;
	}
	
	
	public abstract void spawnEnemy(String name, float y, float ySpeed, float xSpeed, float motionDuration, float motionPeculiarity, String motionEquation, boolean motionInfinite, GameScreen gs);
	
	
	public TweenEquation resolveEquation(String motionEquation) {		
		TweenEquation eq = null;
		if(motionEquation.equals("quad.inout")) {
			eq = Quad.INOUT;
		}
		else if(motionEquation.equals("quad.in")) {
			eq = Quad.IN;
		}
		else if(motionEquation.equals("quad.out")) {
			eq = Quad.IN;
		}
		else if(motionEquation.equals("elastic.in")) {
			eq = Elastic.IN;
		}
		else if(motionEquation.equals("elastic.out")) {
			eq = Elastic.OUT;
		}
		else if(motionEquation.equals("elastic.inout")) {
			eq = Elastic.INOUT;
		}
		else if(motionEquation.equals("cubic.in")) {
			eq = Cubic.IN;
		}
		else if(motionEquation.equals("cubic.out")) {
			eq = Cubic.OUT;
		}
		else if(motionEquation.equals("cubic.inout")) {
			eq = Cubic.INOUT;
		}
		else if(motionEquation.equals("sine.in")) {
			eq = Sine.IN;
		}
		else if(motionEquation.equals("sine.out")) {
			eq = Sine.OUT;
		}
		else if(motionEquation.equals("sine.inout")) {
			eq = Sine.INOUT;
		}
		else if(motionEquation.equals("circ.in")) {
			eq = Circ.IN;
		}
		else if(motionEquation.equals("circ.out")) {
			eq = Circ.OUT;
		}
		else if(motionEquation.equals("circ.inout")) {
			eq = Circ.INOUT;
		}
		else if(motionEquation.equals("back.in")) {
			eq = Back.IN;
		}
		else if(motionEquation.equals("back.out")) {
			eq = Back.OUT;
		}
		else if(motionEquation.equals("back.inout")) {
			eq = Back.INOUT;
		}
		else if(motionEquation.equals("expo.in")) {
			eq = Expo.IN;
		}
		else if(motionEquation.equals("expo.out")) {
			eq = Expo.OUT;
		}
		else if(motionEquation.equals("expo.inout")) {
			eq = Expo.INOUT;
		}
		else if(motionEquation.equals("quart.in")) {
			eq = Quart.IN;
		}
		else if(motionEquation.equals("quart.out")) {
			eq = Quart.OUT;
		}
		else if(motionEquation.equals("quart.inout")) {
			eq = Quart.INOUT;
		}
		else if(motionEquation.equals("quint.in")) {
			eq = Quint.IN;
		}
		else if(motionEquation.equals("quint.out")) {
			eq = Quint.OUT;
		}
		else if(motionEquation.equals("quint.inout")) {
			eq = Quint.INOUT;
		}
		else if(motionEquation.equals("linear.inout")) {
			eq = Linear.INOUT;
		}
		return eq;		
	}
	
	public void pause() {
		for(GameObject e : this.objects) {
			((Enemy) e).pause();
		}
	}
	
	public void resume() {
		for(GameObject e : this.objects) {
			((Enemy) e).resume();
		}
	}
	
	
	public int getEnemyCounter() {
		return this.enemyCounter;
	}
	
	public void resetEnemyCounter() {
		this.enemyCounter = 0;
	}
	
	public float getEnemyTimer() {
		return this.enemyTimer;
	}
	
	@Override
	public void invokeObjectLogic(float delta, Level actualLevel) {
		super.invokeObjectLogic(delta, actualLevel);
	}
	
	@Override
	public void clear() {
		this.resetEnemyCounter();
		super.clear();
	}
	
	
	
	@Override
	public void dispose() {
		this.rand = null;
		this.world = null;
		super.dispose();
	}

}
