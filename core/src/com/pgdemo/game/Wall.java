package com.pgdemo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Wall {
	Texture img;
	float positionX, positionY;
	float height;
	Rectangle rect;
	float speed;
	
	private final float DEFAULT_SPEED = 200.0f;	
	
	Wall(Texture image, float x, float y){
		img = image;
		positionX = x;
		positionY = y;
		height = 0;
		speed = DEFAULT_SPEED;
		rect = new Rectangle(positionX, positionY, img.getWidth(), height);
	}
	
	public void Grow(float deltaTime){
		height += deltaTime * speed;
		rect=new Rectangle(positionX, positionY, img.getWidth(), height);
	}

}
