package com.xsheetgames.genericGameObjects;


/**
 * Knows how to build one concrete {@link GameObject} subtype for a
 * {@link GameObjectPool} - {@link #createObject()} is called by the pool the
 * first time it needs an instance it can't reuse from its free list.
 * {@link #myClass} is only used for tagging/lookup (e.g. {@code EnemyFactory}'s
 * class-based dispatch); it does not drive {@link #createObject()} via
 * reflection - see the note on {@code EnemyFactory.createObject()} for why
 * (historical GWT/HTML-backend constraint: no {@code java.lang.reflect}).
 */
public abstract class GameObjectFactory {

	protected Class<? extends GameObject> myClass;

	public GameObjectFactory(Class<? extends GameObject> c) {
		this.myClass = c;
	}

	public abstract GameObject createObject();

}
