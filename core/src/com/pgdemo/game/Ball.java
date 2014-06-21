package com.pgdemo.game;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Ball {
	Texture img;
	float positionX, positionY;
	float horizontalSpeed;
	float verticalSpeed;
	Rectangle rect;
	int level;
	float radius;
	
	private final float DEFAULT_RADIUS = 15.0f;
	private final float DEFAULT_HORIZONTAL_SPEED = 50.0f;
	private final float GRAVITY = 40.0f;
	
	Ball(){
		positionX = positionY = 0;
		horizontalSpeed = DEFAULT_HORIZONTAL_SPEED;
		verticalSpeed = 0.0f;
		level = 0;
		radius = DEFAULT_RADIUS;
	}
	
	Ball(Texture image){
		img = image;
		positionX = positionY = 0;
		horizontalSpeed = DEFAULT_HORIZONTAL_SPEED;
		verticalSpeed = 0.0f;
		level = 0;
		radius = DEFAULT_RADIUS;
		rect=new Rectangle(positionX, positionY, 2 * radius, 2 * radius);
	}
	
	Ball(Texture image, float x, float y){
		img=image;
		positionX=x;
		positionY=y;
		horizontalSpeed = DEFAULT_HORIZONTAL_SPEED;
		verticalSpeed = 0.0f;
		level = 0;
		radius = DEFAULT_RADIUS;
		rect=new Rectangle(positionX, positionY, 2 * radius, 2 * radius);
	}
	
	Ball(Texture image, float x, float y, float r, float speed, int lv){
		img=image;
		positionX=x;
		positionY=y;
		horizontalSpeed = speed;
		verticalSpeed = 0.0f;
		level = lv;
		radius = r;
		rect=new Rectangle(positionX, positionY, 2 * radius, 2 * radius);
	}

	public void SetPositions(float x, float y){
		positionX = x;
		positionY = y;
		rect=new Rectangle(positionX, positionY, 2 * radius, 2 * radius);
	}
	
	public void BallMovement(float deltaTime, int width){
		positionX += deltaTime * horizontalSpeed;
		positionY += deltaTime * verticalSpeed;
		verticalSpeed -= GRAVITY * deltaTime;
		
		if(positionX > width - img.getWidth()){
			positionX = width - img.getWidth();
			horizontalSpeed *= -1;
		}
		if(positionX < 0){
			positionX = 0.0f;
			horizontalSpeed *= -1;
		}
		
		if(positionY < 0){
			positionY = 0.0f;
			verticalSpeed *= -1;
		}
		
		rect=new Rectangle(positionX, positionY, 2 * radius, 2 * radius);
	}

}
