package com.pgdemo.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Character {
	Texture img;											//Ana karakterin resmi
	float positionX, positionY;								//2 boyutlu d�zlemde konumu
	Rectangle rect;											//�arp��ma durumu i�in kullan�lan dikd�rtgensel alan
	float walkingTime = 0.0f;								//Animasyondaki durum i�in kullan�lan y�r�me zaman�
	
	//Constructors
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

	//Pozisyonun ayarlanmas�
	public void SetPositions(float x, float y){
		positionX = x;
		positionY = y;
		rect=new Rectangle(positionX, positionY, img.getWidth(), img.getHeight());
	}
	
	//Y�r�me i�lemlerinin �al��t��� yer
	public void MoveHorizontal(float distance, float width){
		positionX += distance;
		if(positionX < 0)									//Pencereden ��kmamas�n�n sa�lanmas�
			positionX = 0;
		
		if(positionX > width - img.getWidth())
			positionX = width - img.getWidth();
		
		rect=new Rectangle(positionX, positionY, img.getWidth(), img.getHeight());		
	}


}
