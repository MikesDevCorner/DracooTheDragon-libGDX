package com.xsheetgames.genericElements;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.xsheetgames.Configuration;
import com.xsheetgames.genericGameObjects.Boundary;
import com.xsheetgames.genericGameObjects.Enemy;

public class ObjectContactListener implements ContactListener {
	
	private boolean setEnemyForces = true;
	

	@Override
	public void beginContact(Contact contact) {
		if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() != null) {
			Object a = contact.getFixtureA().getBody().getUserData();
			Object b = contact.getFixtureB().getBody().getUserData();		
			
			//if one is a boundary and the other is a enemy
			if(a instanceof com.xsheetgames.genericGameObjects.Boundary) {
				if(b instanceof com.xsheetgames.genericGameObjects.Enemy) {
					this.manageBoundaries((com.xsheetgames.genericGameObjects.Boundary)a, (com.xsheetgames.genericGameObjects.Enemy)b, contact);
				}
			}		
			if(b instanceof com.xsheetgames.genericGameObjects.Boundary) {
				if(a instanceof com.xsheetgames.genericGameObjects.Enemy) {
					this.manageBoundaries((com.xsheetgames.genericGameObjects.Boundary)b, (com.xsheetgames.genericGameObjects.Enemy)a, contact);
				}
			}	
			
			
			if(a instanceof com.xsheetgames.genericGameObjects.EnemyFire) {
				if(b instanceof com.xsheetgames.genericGameObjects.Obstacle) {
					((com.xsheetgames.genericGameObjects.EnemyFire) a).setDisposing = true;
				}
			}
			if(b instanceof com.xsheetgames.genericGameObjects.EnemyFire) {
				if(a instanceof com.xsheetgames.genericGameObjects.Obstacle) {
					((com.xsheetgames.genericGameObjects.EnemyFire) b).setDisposing = true;
				}
			}
			
			
			if(contact.isEnabled() && Configuration.debugLevel >= Application.LOG_INFO && Configuration.contactInfos)	{
				Gdx.app.log("Contact: ", a.getClass().getName() + " collides with "+b.getClass().getName() + " at X:" + contact.getWorldManifold().getPoints()[0].x+" Y:"+contact.getWorldManifold().getPoints()[0].y+" - first hash: "+a.hashCode()+", second hash: "+b.hashCode());
			}
			
			//one of the two is draco
			if(a instanceof com.xsheetgames.genericGameObjects.Draco) {
				//the other is the enemy
				if(b instanceof com.xsheetgames.genericGameObjects.Enemy) {
					if(((com.xsheetgames.genericGameObjects.Enemy) b).isDead() == false) {
						boolean reduced = false;
						if(Configuration.debugLevel >= Application.LOG_INFO  && Configuration.contactInfos) Gdx.app.log("Harm: ",b.getClass().getName()+" was harmed by "+a.getClass().getName()+" with the hash "+a.hashCode());
						if(((com.xsheetgames.genericGameObjects.Draco) a).invincible <= 0f) reduced = ((com.xsheetgames.genericGameObjects.Enemy) b).reduceEnergy();
						((com.xsheetgames.genericGameObjects.Draco) a).reduceEnergy();
						if(((com.xsheetgames.genericGameObjects.Enemy) b).getEnergy() == 0 && reduced == true) ((com.xsheetgames.genericGameObjects.Draco) a).incrementEnemiesKilled(((com.xsheetgames.genericGameObjects.Enemy) b).getEnemyPoints());						
					}
				}
				
				//the other is enemy fire
				if(b instanceof com.xsheetgames.genericGameObjects.EnemyFire) {
					((com.xsheetgames.genericGameObjects.EnemyFire) b).setDisposing = true;
					if(((com.xsheetgames.genericGameObjects.EnemyFire) b).ceaseCollision == false) {
						((com.xsheetgames.genericGameObjects.Draco) a).reduceEnergy();
						if(Configuration.debugLevel >= Application.LOG_INFO  && Configuration.contactInfos) Gdx.app.log("Harm: ",b.getClass().getName()+" was harmed by "+a.getClass().getName()+" with the hash "+a.hashCode());
						((com.xsheetgames.genericGameObjects.EnemyFire) b).ceaseCollision = true;
					}
				}
				
				//the other is a deadly obstacle
				if(b instanceof com.xsheetgames.genericGameObjects.DeadlyObstacle) {
					if(Configuration.debugLevel >= Application.LOG_INFO  && Configuration.contactInfos) Gdx.app.log("Harm: ",b.getClass().getName()+" was harmed by "+a.getClass().getName()+" with the hash "+a.hashCode());
					((com.xsheetgames.genericGameObjects.Draco) a).reduceEnergy();						
				}
				
				//the other is a powerup
				if(b instanceof com.xsheetgames.genericGameObjects.Chili) {
					if(((com.xsheetgames.genericGameObjects.Chili) b).ceaseCollision == false && ((com.xsheetgames.genericGameObjects.Chili) b).vanished == false) ((com.xsheetgames.genericGameObjects.Draco) a).enableFireMode(false);
					//((com.xsheetgames.genericGameObjects.Chili) b).ceaseCollision = true;
					((com.xsheetgames.genericGameObjects.Chili) b).vanish();
				}
			}
			if(b instanceof com.xsheetgames.genericGameObjects.Draco) { //same as above, only objects switched
				if(a instanceof com.xsheetgames.genericGameObjects.Enemy) {
					if(((com.xsheetgames.genericGameObjects.Enemy) a).isDead() == false) {
						boolean reduced = false;
						if(Configuration.debugLevel >= Application.LOG_INFO  && Configuration.contactInfos) Gdx.app.log("Harm: ",a.getClass().getName()+" was harmed by "+b.getClass().getName()+" with the hash "+b.hashCode());
						if(((com.xsheetgames.genericGameObjects.Draco) b).invincible <= 0f) reduced = ((com.xsheetgames.genericGameObjects.Enemy) a).reduceEnergy();
						((com.xsheetgames.genericGameObjects.Draco) b).reduceEnergy();
						if(((com.xsheetgames.genericGameObjects.Enemy) a).getEnergy() == 0 && reduced == true) ((com.xsheetgames.genericGameObjects.Draco) b).incrementEnemiesKilled(((com.xsheetgames.genericGameObjects.Enemy) a).getEnemyPoints());
					}
				}
				
				//the other is enemy fire
				if(a instanceof com.xsheetgames.genericGameObjects.EnemyFire) {
					((com.xsheetgames.genericGameObjects.EnemyFire) a).setDisposing = true;
					if(((com.xsheetgames.genericGameObjects.EnemyFire) a).ceaseCollision == false) {
						((com.xsheetgames.genericGameObjects.Draco) b).reduceEnergy();
						if(Configuration.debugLevel >= Application.LOG_INFO  && Configuration.contactInfos) Gdx.app.log("Harm: ",b.getClass().getName()+" was harmed by "+a.getClass().getName()+" with the hash "+a.hashCode());
						((com.xsheetgames.genericGameObjects.EnemyFire) a).ceaseCollision = true;
					}
				}
				
				
				if(a instanceof com.xsheetgames.genericGameObjects.DeadlyObstacle) {
					if(Configuration.debugLevel >= Application.LOG_INFO  && Configuration.contactInfos) Gdx.app.log("Harm: ",b.getClass().getName()+" was harmed by "+a.getClass().getName()+" with the hash "+a.hashCode());
					((com.xsheetgames.genericGameObjects.Draco) b).reduceEnergy();						
				}
				
				if(a instanceof com.xsheetgames.genericGameObjects.Chili) {
					if(((com.xsheetgames.genericGameObjects.Chili) a).ceaseCollision == false && ((com.xsheetgames.genericGameObjects.Chili) a).vanished == false) ((com.xsheetgames.genericGameObjects.Draco) b).enableFireMode(false);
					//((com.xsheetgames.genericGameObjects.Chili) a).ceaseCollision = true;
					((com.xsheetgames.genericGameObjects.Chili) a).vanish();
				}
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	
	//PreSolved Contacts knnen deaktiviert werden, damit der Contakt nicht die Physik beeinflusst!!
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
		if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() != null) {
		
			Object a = contact.getFixtureA().getBody().getUserData();
			Object b = contact.getFixtureB().getBody().getUserData();
			
			
			//one of the two is a fireball
			if(a instanceof com.xsheetgames.genericGameObjects.Fireball) {
				
				if(b instanceof com.xsheetgames.genericGameObjects.Hurtable) {
					//animate Flare
					((com.xsheetgames.genericGameObjects.Fireball) a).sparkles.add(contact.getWorldManifold().getPoints()[0].x,contact.getWorldManifold().getPoints()[0].y, ((com.xsheetgames.genericGameObjects.Hurtable) b).getBody().getLinearVelocity().x, ((com.xsheetgames.genericGameObjects.Hurtable) b).getBody().getLinearVelocity().y, 78f, 50f);
					if(((com.xsheetgames.genericGameObjects.Fireball) a).ceaseCollision == false) {
						boolean reduced = ((com.xsheetgames.genericGameObjects.Hurtable) b).reduceEnergy();
						((com.xsheetgames.genericGameObjects.Fireball) a).ceaseCollision = true;
						if(((com.xsheetgames.genericGameObjects.Hurtable) b).getEnergy() == 0 && reduced == true) ((com.xsheetgames.genericGameObjects.Fireball) a).draco.incrementEnemiesKilled(((com.xsheetgames.genericGameObjects.Hurtable) b).getEnemyPoints());
						contact.setEnabled(this.setEnemyForces);
					} else contact.setEnabled(false);	
				}
				
				if(b instanceof com.xsheetgames.genericGameObjects.Obstacle) {
					//animate Flare
					((com.xsheetgames.genericGameObjects.Fireball) a).sparkles.add(contact.getWorldManifold().getPoints()[0].x,contact.getWorldManifold().getPoints()[0].y, ((com.xsheetgames.genericGameObjects.Obstacle) b).getBody().getLinearVelocity().x, ((com.xsheetgames.genericGameObjects.Obstacle) b).getBody().getLinearVelocity().y, 78f, 50f);
					if(((com.xsheetgames.genericGameObjects.Obstacle) b).willDieOnFireball == true) {
						((com.xsheetgames.genericGameObjects.Obstacle) b).reduceEnergy();
					}
				}
				((com.xsheetgames.genericGameObjects.Fireball) a).vanish();
				
			}
			
			if(b instanceof com.xsheetgames.genericGameObjects.Fireball) {
				
				if(a instanceof com.xsheetgames.genericGameObjects.Hurtable) {
					//animate Flare
					((com.xsheetgames.genericGameObjects.Fireball) b).sparkles.add(contact.getWorldManifold().getPoints()[contact.getWorldManifold().getNumberOfContactPoints()-1].x,contact.getWorldManifold().getPoints()[contact.getWorldManifold().getNumberOfContactPoints()-1].y, ((com.xsheetgames.genericGameObjects.Hurtable) a).getBody().getLinearVelocity().x, ((com.xsheetgames.genericGameObjects.Hurtable) a).getBody().getLinearVelocity().y, 78f, 50f);
					if(((com.xsheetgames.genericGameObjects.Fireball) b).ceaseCollision == false) {
						boolean reduced = ((com.xsheetgames.genericGameObjects.Hurtable) a).reduceEnergy();
						((com.xsheetgames.genericGameObjects.Fireball) b).ceaseCollision = true;
						if(((com.xsheetgames.genericGameObjects.Hurtable) a).getEnergy() == 0 && reduced == true) ((com.xsheetgames.genericGameObjects.Fireball) b).draco.incrementEnemiesKilled(((com.xsheetgames.genericGameObjects.Hurtable) a).getEnemyPoints());
						contact.setEnabled(this.setEnemyForces);
					} else contact.setEnabled(false);								
				}
				
				if(a instanceof com.xsheetgames.genericGameObjects.Obstacle) {
					//animate Flare
					((com.xsheetgames.genericGameObjects.Fireball) b).sparkles.add(contact.getWorldManifold().getPoints()[contact.getWorldManifold().getNumberOfContactPoints()-1].x,contact.getWorldManifold().getPoints()[contact.getWorldManifold().getNumberOfContactPoints()-1].y, ((com.xsheetgames.genericGameObjects.Obstacle) a).getBody().getLinearVelocity().x, ((com.xsheetgames.genericGameObjects.Obstacle) a).getBody().getLinearVelocity().y, 78f, 50f);
					if(((com.xsheetgames.genericGameObjects.Obstacle) a).willDieOnFireball == true) {
						((com.xsheetgames.genericGameObjects.Obstacle) a).reduceEnergy();
					}
				}
				((com.xsheetgames.genericGameObjects.Fireball) b).vanish();
			}		
			
			
			
			
			//if one is a boundary and the other is an enemy
			if(a instanceof com.xsheetgames.genericGameObjects.Boundary) {
				if(b instanceof com.xsheetgames.genericGameObjects.Enemy) {
					this.manageBoundaries((com.xsheetgames.genericGameObjects.Boundary)a, (com.xsheetgames.genericGameObjects.Enemy)b, contact);
				}
			}		
			if(b instanceof com.xsheetgames.genericGameObjects.Boundary) {
				if(a instanceof com.xsheetgames.genericGameObjects.Enemy) {
					this.manageBoundaries((com.xsheetgames.genericGameObjects.Boundary)b, (com.xsheetgames.genericGameObjects.Enemy)a, contact);
				}
			}	
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub		
	}
	
	private void manageBoundaries(Boundary a, Enemy b, Contact contact) {		
		
		if(a.name == "top") {
			Vector2 vel = b.getBody().getLinearVelocity();
			if(b.isSensor() == false) {
				if(vel.y < 0) {
					contact.setEnabled(false);
				} else {
					b.getBody().setLinearVelocity(vel.x, vel.y-3f);
				}
			}
			/*BoundaryCheckQueryCallback c = new BoundaryCheckQueryCallback();
			b.getBody().getWorld().QueryAABB(c,0f,12.55f,20f,12.65f);
			for(Body foundBody : c.foundBodys) {
				if(b.getBody() == foundBody) {
					contact.setEnabled(false);
					break;
				}
			}*/
		}
		if(a.name == "bottom") {
			Vector2 vel = b.getBody().getLinearVelocity(); 
			if(vel.y > 0) {
				contact.setEnabled(false);
			} else {
				b.getBody().setLinearVelocity(vel.x, vel.y+3f);
			}
			/*BoundaryCheckQueryCallback c = new BoundaryCheckQueryCallback();
			b.getBody().getWorld().QueryAABB(c,0f,-0.15f,20f,-0.05f);
			for(Body foundBody : c.foundBodys) {
				if(b.getBody() == foundBody) {
					contact.setEnabled(false);
					break;
				}
			}*/
		}
		if(a.name == "right") {
			Vector2 vel = b.getBody().getLinearVelocity(); 
			if(vel.x < 0) {
				contact.setEnabled(false);
			} else {
				b.getBody().setLinearVelocity(vel.x-3f, vel.y);
			}
			/*BoundaryCheckQueryCallback c = new BoundaryCheckQueryCallback();
			b.getBody().getWorld().QueryAABB(c, 20.05f, 0f, 20.15f, 12.5f);
			for(Body foundBody : c.foundBodys) {
				if(b.getBody() == foundBody) {
					contact.setEnabled(false);
					break;
				}
			}*/
		}
	}
	
}
