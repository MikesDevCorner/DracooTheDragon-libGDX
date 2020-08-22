package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AtlasAnimationCollection;

public class Fireball extends GameObject {

	protected short categoryBits = 16;
	protected short maskBits = 70;
	public boolean ceaseCollision = false;
	public AtlasAnimationCollection sparkles;
	public Draco draco;
	private boolean vanished;
	
	public Fireball(World world, float x, float y, AtlasAnimationCollection sparkles) {
		super(world,GameAssets.fetchTextureAtlas("game/images/game_objects.pack"),0f,new Vector2(27f,0f),new Vector2(x,y),true);

		this.sparkles = sparkles;
		this.vanished = false;
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.bullet = true;
		this.bodyType = BodyType.DynamicBody;
		bd.fixedRotation = true;
		bd.gravityScale = 0f;
		
		FixtureDef fd = new FixtureDef();
		fd.density = 1f;
		fd.friction = 0.5f;
		fd.restitution = 0.4f;
		fd.filter.categoryBits = this.categoryBits;
		fd.filter.maskBits = this.maskBits;

		super.init(bd, fd, GameAssets.getObjectLoader(), "Fireball","fireball", Animation.PlayMode.NORMAL);
	}
	
	@Override
	public void reset() {
		this.vanished = false;
		this.ceaseCollision = false;
		super.reset();
	}
	
	
	public void setDraco(Draco d) {
		this.draco = d;
	}
	
	public void vanish() {
		this.vanished = true;
	}
	
	
	@Override
	public void dispose() {
		this.sparkles = null;
		this.draco = null;
		super.dispose();
	}


	@Override
	public void doMotionLogic(float delta) {
		if(this.vanished == true) {
			if(this.world.isLocked() == false) {
				if(!this.free()) this.dispose();
			}
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, float delta) {
			
		super.draw(batch, delta);
	}
	
}
