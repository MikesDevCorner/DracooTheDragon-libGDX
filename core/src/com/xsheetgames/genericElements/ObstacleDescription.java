package com.xsheetgames.genericElements;

public class ObstacleDescription implements Comparable<ObstacleDescription>{
	public float ctime, y;
	public String name;
	
	
	@Override
	public int compareTo(ObstacleDescription b) {
		if(this.ctime < b.ctime) return 1;
		else if(this.ctime > b.ctime) return -1;
		else return 0;
	}
}
