package com.xsheetgames.genericGameObjects;


public abstract class GameObjectFactory {

	protected Class<? extends GameObject> myClass;
	
	public GameObjectFactory(Class<? extends GameObject> c) {
		this.myClass = c;
	}
	
	public abstract GameObject createObject();
	
}
