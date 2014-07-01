package com.pgdemo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Wall {
	public Texture img;								
	public float positionX, positionY;					
	private float height;								
	public Rectangle rect;								//Çarpýþma durumlarý için dikdörtgensel bölge
	private float speed;								//Duvarýn yükselme hýzý
	
	private final float DEFAULT_SPEED = 200.0f;	
	
	//Constructors
	Wall(Texture image, float x, float y){
		img = image;
		positionX = x;
		positionY = y;
		height = 0;
		speed = DEFAULT_SPEED;
		rect = new Rectangle(positionX, positionY, img.getWidth(), height);
	}
	
	//Duvarýn zamana baðlý yükselmesi
	public void Grow(float deltaTime){
		height += deltaTime * speed;
		rect=new Rectangle(positionX, positionY, img.getWidth(), height);
	}
	
	public float getHeight(){
		return height;
	}

}
