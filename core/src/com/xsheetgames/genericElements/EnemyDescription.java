package com.xsheetgames.genericElements;

public class EnemyDescription implements Comparable<EnemyDescription> {
	public float ctime, y, xSpeed, ySpeed, motionDuration, motionPeculiarity;
	public boolean motionInfinite;
	public String motionEquation, name;
	
	
	@Override
	public int compareTo(EnemyDescription b) {
		if(this.ctime < b.ctime) return 1;
		else if(this.ctime > b.ctime) return -1;
		else return 0;
	}
}
