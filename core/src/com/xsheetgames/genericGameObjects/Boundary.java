package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Boundary extends GameObject {

	protected short categoryBits = 1;
	protected short maskBits = 12;
	public String name;
	private float height;
	private float width;
	
	public Boundary(World world, float x, float y, float width, float height, String name) {
		
		super(world,null,0f,new Vector2(0f,0f),new Vector2(x+(width/2),y+(height/2)),false);
		this.name = name;
		this.loaderName = name;
		
		BodyDef bd = new BodyDef();
        bd.type = BodyType.StaticBody;
        bd.linearVelocity.set(this.startVelocity);
		bd.position.set(this.startPosition);
		
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);    
        
        FixtureDef fd = new FixtureDef();
        fd.density = 20f;
        fd.friction = 20f;
        fd.restitution = 0f;
        fd.shape = shape;
        fd.filter.categoryBits = this.categoryBits;
		fd.filter.maskBits = this.maskBits;
		
		
		this.body = world.createBody(bd);
		this.body.setUserData(this);
		
		this.body.createFixture(fd);
        shape.dispose();
        
        this.width = width;
        this.height = height;
	}
	
	@Override
	public void doMotionLogic(float delta) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Vector2 getLeftBottom() {
		return body.getPosition();
	}
	
	@Override
	public Vector2 getRightTop() {
		return body.getPosition().add(this.width, this.height);
	}
	
	public void dispose() {
		super.dispose();
	}
	

}
