package com.pgdemo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Wall {
	Texture img;								//Duvarýn resmi
	float positionX, positionY;					//2 boyutlu düzlemde konumu
	float height;								//Yükseklik
	Rectangle rect;								//Çarpýþma durumlarý için dikdörtgensel bölge
	float speed;								//Duvarýn yükselme hýzý
	
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

}
