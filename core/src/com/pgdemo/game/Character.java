package com.pgdemo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Character {
	Texture img;	
	float positionX, positionY;
	Rectangle rect;
	
	
	Character(){
		positionX = positionY = 0;
	}
	
	Character(Texture image){
		img = image;
		positionX = positionY = 0;
		rect=new Rectangle(positionX, positionY, img.getWidth(), img.getHeight());
	}
	
	Character(Texture image, float x, float y){
		img=image;
		positionX=x;
		positionY=y;
		rect=new Rectangle(positionX, positionY, img.getWidth(), img.getHeight());
	}

	public void SetPositions(float x, float y){
		positionX = x;
		positionY = y;
		rect=new Rectangle(positionX, positionY, img.getWidth(), img.getHeight());
	}
	
	public void MoveHorizontal(float distance, float width){
		positionX += distance;
		if(positionX < 0)
			positionX = 0;
		
		if(positionX > width - img.getWidth())
			positionX = width - img.getWidth();
		
		rect=new Rectangle(positionX, positionY, img.getWidth(), img.getHeight());		
	}


}
