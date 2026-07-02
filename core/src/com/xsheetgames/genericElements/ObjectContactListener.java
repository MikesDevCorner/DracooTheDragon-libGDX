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

/**
 * Box2D {@link ContactListener} for the whole game world - the single source of
 * truth for what happens when two bodies touch (damage, kills, powerups,
 * fireball hits, keeping enemies inside the play field).
 *
 * Every fixture's body carries the owning {@code GameObject} as Box2D user
 * data (set in {@code GameObject.init()}); this class dispatches purely on
 * the runtime type of that user data via {@code instanceof}. Box2D does not
 * guarantee which body is "A" and which is "B" for a given pair, so almost
 * every rule below is written twice - once for (a,b) and once for the
 * swapped (b,a) - instead of normalizing the pair first.
 */
public class ObjectContactListener implements ContactListener {

	private boolean setEnemyForces = true;


	/**
	 * Fired once when two fixtures start touching. Handles anything that should
	 * happen exactly once per hit: Draco taking damage from an enemy/enemy
	 * fire/obstacle, killing enemies, Draco picking up a {@code Chili} powerup,
	 * and enemies bouncing off the play-field {@link Boundary} walls.
	 */
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
					((com.xsheetgames.genericGameObjects.Chili) a).vanish();
				}
			}
		}
	}

	@Override
	public void endContact(Contact contact) {
		// unused: nothing needs to react to contacts ending, only starting/persisting
	}

	/**
	 * Fired every physics step for each persisting contact, before the solver
	 * runs. Used here for fireball hits (spawn a spark, apply damage once via
	 * {@code ceaseCollision}, then {@code contact.setEnabled(false)} so the
	 * fireball doesn't get physically deflected by what it just hit) and to
	 * keep re-applying the boundary nudge in {@link #manageBoundaries} for as
	 * long as an enemy stays pressed against a wall. Disabling a contact here
	 * only suppresses the physics response for this step, it doesn't stop
	 * {@code beginContact}/collision detection.
	 */
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
		// unused: no gameplay rule depends on the resolved collision impulse
	}

	private void manageBoundaries(Boundary a, Enemy b, Contact contact) {

		// Enemies are kept inside the play field by nudging their velocity back
		// on contact instead of a hard physical bounce: a wall hit while already
		// moving away from it just disables the contact (lets it separate on its
		// own), otherwise the enemy's velocity gets a flat 3f kick back inward.
		if(a.name == "top") {
			Vector2 vel = b.getBody().getLinearVelocity();
			if(b.isSensor() == false) {
				if(vel.y < 0) {
					contact.setEnabled(false);
				} else {
					b.getBody().setLinearVelocity(vel.x, vel.y-3f);
				}
			}
		}
		if(a.name == "bottom") {
			Vector2 vel = b.getBody().getLinearVelocity();
			if(vel.y > 0) {
				contact.setEnabled(false);
			} else {
				b.getBody().setLinearVelocity(vel.x, vel.y+3f);
			}
		}
		if(a.name == "right") {
			Vector2 vel = b.getBody().getLinearVelocity();
			if(vel.x < 0) {
				contact.setEnabled(false);
			} else {
				b.getBody().setLinearVelocity(vel.x-3f, vel.y);
			}
		}
	}
	
}
