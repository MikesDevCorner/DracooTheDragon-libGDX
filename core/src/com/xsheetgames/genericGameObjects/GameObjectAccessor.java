package com.xsheetgames.genericGameObjects;



import aurelienribon.tweenengine.TweenAccessor;

public class GameObjectAccessor implements TweenAccessor<GameObject> {

	 public static final int POSITION_X = 1;
	 public static final int POSITION_Y = 2;
	 public static final int POSITION_XY = 3;
	 public static final int ANGLE = 4;
	 public static final int VELOCITY_X = 5;
	 public static final int VELOCITY_Y = 6;
	 public static final int VELOCITY_XY = 7;
	 
	
	@Override
	 public int getValues(GameObject target, int tweenType, float[] returnValues) {
	     switch (tweenType) {
	         case POSITION_X:
	        	 if(target.getBody() != null) returnValues[0] = target.getBody().getPosition().x;
	        	 else returnValues[0] = 0f;
	        	 return 1;
	         case POSITION_Y:
	        	 if(target.getBody() != null) returnValues[0] = target.getBody().getPosition().y;
	        	 else returnValues[0] = 0f;
	        	 return 1;
	         case POSITION_XY:
	        	 if(target.getBody() != null) {
	        		 returnValues[0] = target.getBody().getPosition().x;
		             returnValues[1] = target.getBody().getPosition().y;
	        	 }
	        	 else {
	        		 returnValues[0] = 0f;
	        		 returnValues[1] = 0f;
	        	 }
	             return 2;
	         case ANGLE: 
	        	 if(target.getBody() != null) returnValues[0] = target.getBody().getAngle();
	        	 else returnValues[0] = 0f;
	        	 return 1;
	         case VELOCITY_X:
	        	 if(target.getBody() != null) returnValues[0] = target.getBody().getLinearVelocity().x;
	        	 else returnValues[0] = 0f;
	        	 return 1;
	         case VELOCITY_Y:
	        	 if(target.getBody() != null) returnValues[0] = target.getBody().getLinearVelocity().y;
	        	 else returnValues[0] = 0f;
	        	 return 1;
	         case VELOCITY_XY:
	        	 if(target.getBody() != null) {
	        		 returnValues[0] = target.getBody().getLinearVelocity().x;
		             returnValues[1] = target.getBody().getLinearVelocity().y;
	        	 }
	        	 else {
	        		 returnValues[0] = 0f;
	        		 returnValues[1] = 0f;
	        	 }
	             return 2;
	         default: assert false; return -1;
	     }
	 }
	
	
	@Override
	 public void setValues(GameObject target, int tweenType, float[] newValues) {
	     switch (tweenType) {
	         case POSITION_X: 
	        	 if(target.getBody() != null) target.getBody().setTransform(newValues[0],target.getBody().getPosition().y, target.getBody().getAngle());
	        	 break;
	         case POSITION_Y: 
	        	 if(target.getBody() != null) target.getBody().setTransform(target.getBody().getPosition().x, newValues[0], target.getBody().getAngle()); 
	        	 break;
	         case POSITION_XY:
	        	 if(target.getBody() != null) target.getBody().setTransform(newValues[0], newValues[1], target.getBody().getAngle()); 
	        	 break;
	         case ANGLE:
	        	 if(target.getBody() != null) {
		        	 target.getBody().setAngularVelocity(0.0f);
		        	 target.getBody().setTransform(target.getBody().getPosition(), newValues[0]);
	        	 }
	        	 break;
	         case VELOCITY_X: 
	        	 if(target.getBody() != null) target.getBody().setLinearVelocity(newValues[0],target.getBody().getLinearVelocity().y); 
	        	 break;
	         case VELOCITY_Y: 
	        	 if(target.getBody() != null) target.getBody().setLinearVelocity(target.getBody().getLinearVelocity().x,newValues[0]);
	        	 break;	 
	         case VELOCITY_XY: 
	        	 if(target.getBody() != null) target.getBody().setLinearVelocity(newValues[0],newValues[1]); 
	        	 break;
	         default: assert false; break;
	     }
	 }
	
}