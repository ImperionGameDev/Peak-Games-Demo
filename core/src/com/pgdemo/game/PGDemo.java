package com.pgdemo.game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class PGDemo extends ApplicationAdapter implements InputProcessor{
	
	SpriteBatch batch;
	Character mainCharacter;							//Ana karakter
	List<Ball> balls;									//Oyundaki toplar
	Wall wall;											//Ana karakterin ate� ederek olu�turdu�u duvar
	OrthographicCamera camera;							
	Rectangle glViewport;			
	BitmapFont font;
	Texture ballTexture;								//Oyundaki toplar�n dokusu
	Texture characterTexture;							//Ana karakterin dokusu
	Texture wallTexture;								//Duvar dokusu
	Texture lifeTexture;								//Can bilgisi i�in doku
	Texture leftAnimTexture;							//Sol tarafa y�r�me animasyonu i�in doku atlas�
	
	Animation charLeftAnim;								//Sol tarafa y�r�me animasyonu
	
	boolean pause = false;								//Oyunun durmas�
	String finishStr;									//Oyun durdu�unda ekranda g�z�kecek bilgi
	
	int score = 0;										//Skor bilgisi
	int lifes = 3;										//Can say�s�
	
	int movementSpeed = 120;							//Ana karakterin y�r�me h�z�
	
	boolean leftMoving = false;							//Ana karakterin sol ve sa� tarafa y��rme bilgileri
	boolean rightMoving = false;
	
	int WIDTH = 800;								//Ekran�n geni�li�i ve y�ksekli�i
	int HEIGHT = 480;
	
	class TouchInfo{									//Mobil i�in dokunma s�n�f�
		public float touchX = 0.0f;
		public float touchY = 0.0f;
		public boolean touched = false;
	}
	
	private Map<Integer, TouchInfo> touches = new HashMap<Integer, TouchInfo>();									//Dokunma bilgilerini tutacak harita
	
	@Override
	public void create () {
		
		ballTexture = new Texture(Gdx.files.internal("ball.png"));													//Dokular�n al�nd��� b�l�m
		characterTexture = new Texture(Gdx.files.internal("character.png"));
		wallTexture = new Texture(Gdx.files.internal("wall.png"));
		lifeTexture = new Texture(Gdx.files.internal("life.png"));
		leftAnimTexture = new Texture(Gdx.files.internal("leftAnim.png"));
		
		TextureRegion[] leftRegions = new TextureRegion[18]; 														//Y�r�me animasyonu i�in 
		for(int i = 0; i < 3; i++){																					//doku atlas�ndan dokular�n �ekilmesi
			for(int j = 0; j < 6; j++){
				leftRegions[i * 6 + j] = new TextureRegion(leftAnimTexture, j * 35, i * 55, 35, 55);
			}
		}
		
		charLeftAnim = new Animation(0.1f, leftRegions);															//Y�r�me animasyonunun olu�turulmas�		
		
		batch = new SpriteBatch();
		mainCharacter=new Character(characterTexture);																//Ana karakterin olu�turulmas�
		
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		
		//camera = new OrthographicCamera();
		//camera.setToOrtho(false, WIDTH, HEIGHT);
		
	    glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
	    
	    balls = new ArrayList<Ball>();
	    balls.add(new Ball(ballTexture, 200, 200, 15, 50, 1));														//�lk seviye topun olu�turulmas�
	    
	    font = new BitmapFont();
	    font.setColor(Color.BLUE);
	    font.setScale(2);
	    
	    Gdx.input.setInputProcessor(this);																		
	    for(int i = 0; i < 2; i++){																					//�ki dokunmay� haritaya ata
	    	touches.put(i, new TouchInfo());	
	    }
	    
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport((int) glViewport.x, (int) glViewport.y,
                (int) glViewport.width, (int) glViewport.height);
		
		//camera.update();
		
		if(!pause){																									//Oyun durarken �al��mas�n� istemedi�imiz fonksiyonlar
			keyboard();																								//Klavyeden gelen bilgilerin i�lenece�i fonksiyon
			touch();																								//Mobil i�in dokunma i�lemlerinin yap�laca�� fonksiyon
			update();																								//Her frame �al��mas� istenen fonksiyonlar
			checkWin();																								//Oyunun kazanma bilgisinin tutulaca�� fonksiyon
		}
		
		batch.begin();
		drawCharacter(mainCharacter);																				//Ana karakterin �izilmesi
		for(int i = 0; i < balls.size(); i++){																		//Toplar�n �izilmesi
			drawBall(balls.get(i));
		}
		if(wall != null)
			drawWall(wall);																							//Duvar olu�turulmu�sa �izilmesi
		font.draw(batch, Integer.toString(score), WIDTH - 50, HEIGHT - 30);											//Skor bilgisinin g�sterilmesi
		
		for(int i = 0; i < lifes; i++){																				//Can bilgisinin g�sterilmesi
			batch.draw(lifeTexture, 10 + 20 * i, + HEIGHT - 30);
		}
		
		if(pause)																									//Oyun durdu�unda g�sterilmesi istenilen yaz�
			font.draw(batch, finishStr, WIDTH / 2 - 60, HEIGHT / 2);
		
		batch.end();
	}
	
	private void update(){
		
		for(int i = 0; i < balls.size(); i++){
			balls.get(i).BallMovement(Gdx.graphics.getDeltaTime(), WIDTH);											//Her bir topun kare ba��na hareketinin sa�lanmas�
			
			if(balls.get(i).rect.overlaps(mainCharacter.rect)){														//Toplar�n ana karakterlerle �arp��ma durumuna bak�lmas�
				if(lifes > 0){																						//E�er can�m�z yeteri kadar var ise oyun yeniden ba�layacak
					restart();
				}
				else{
					finish();																						//Son can�m�z� kulland�ysak oyunu bitirilecek
				}
			}
			
			if(wall != null){
				if(balls.get(i).rect.overlaps(wall.rect)){															//E�er toplar duvara �arpt�ysa
					Ball temp = balls.get(i);
					balls.remove(i);																				//O top ve duvar yok olacak
					wall = null;
					
					score += (temp.level + 1) * 100;																//Skor topun seviyesine g�re artacak
					
					if(temp.level>0){																				//Seviyeye g�re yar��ap�n ve seviyenin yar�s� kadar 2 yeni top olu�turulacak
						balls.add(new Ball(ballTexture, temp.positionX - 10, temp.positionY, temp.radius/2, -(temp.horizontalSpeed * 2 / 3), temp.level - 1));
						balls.add(new Ball(ballTexture, temp.positionX + 10, temp.positionY, temp.radius/2, (temp.horizontalSpeed * 2 / 3), temp.level - 1));
					}
				}
			}		
		}
		
		if(wall != null){
			if(wall.height<HEIGHT)
				wall.Grow(Gdx.graphics.getDeltaTime());																//Duvar�n frame ba��na y�kselmesi
			else{
				wall=null;
			}
		}
		
	}
	
	
	//Klavyeyle ilgili fonksiyonlar
	private void keyboard(){
		if(Gdx.input.isKeyPressed(Keys.LEFT)){																		//Sol tu�a bas�l�rsa sol tarafa hareket sa�lanacak
			mainCharacter.MoveHorizontal(-movementSpeed * Gdx.graphics.getDeltaTime(), WIDTH);				
			leftMoving = true;
		}
		else{
			leftMoving = false;
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)){																		//Sa� tu�a bas�l�rsa sa� tarafa hareket sa�lanacak
			mainCharacter.MoveHorizontal(movementSpeed * Gdx.graphics.getDeltaTime(), WIDTH);
			rightMoving = true;
		}
		else{
			rightMoving = false;
		}
		if(Gdx.input.isKeyPressed(Keys.SPACE)){																		//Space tu�una bas�l�rsa ate� etme i�lemi yap�lacak
			wall = new Wall(wallTexture, mainCharacter.positionX, mainCharacter.positionY);							
		}
	}
	
	//Ana karakterin animasyon ve �iziminin yap�ld��� fonksiyon
	private void drawCharacter(Character ch){
		if(leftMoving){																								//E�er sol tarafa ilerleme yap�yorsa
			TextureRegion keyFrame = charLeftAnim.getKeyFrame(mainCharacter.walkingTime, true);						//Animasyon nesnesinden o framedeki resim �ekilir
			mainCharacter.walkingTime += Gdx.graphics.getDeltaTime();												//Framedeki zaman ana karakterdeki y�r�me zaman�na eklenir
			batch.draw(keyFrame, ch.positionX, ch.positionY);														//O framedeki resim ana karakterde g�sterilir
		}
		else if(rightMoving){																						
			TextureRegion keyFrame = charLeftAnim.getKeyFrame(mainCharacter.walkingTime, true);						//Sa� tarafa gitmek i�in yeni bir animasyon ve doku atlas�na gerek olmay�p sadece 
			mainCharacter.walkingTime += Gdx.graphics.getDeltaTime();												//Dokuyu y eksenine g�re simetrik bir �ekilde g�sterilir
			batch.draw(keyFrame, ch.positionX + keyFrame.getRegionWidth(), ch.positionY, -(keyFrame.getRegionWidth()), keyFrame.getRegionHeight());
		}
		else{																										//Y�r�me i�lemleri yap�lmad��� zaman �al��acak yer
			batch.draw(ch.img, ch.positionX, ch.positionY);
			mainCharacter.walkingTime = 0.0f;
		}
	}
	
	//Topun �iziminin yap�laca�� fonksiyon
	private void drawBall(Ball b){
		batch.draw(b.img, b.positionX, b.positionY, b.radius * 2, b.radius * 2);									//Topun yar��ap�na ba�l� olarak de�i�en �izim
	}
	
	//Duvar�n �iziminin yap�laca�� fonksiyon
	private void drawWall(Wall w){
		batch.draw(w.img, w.positionX, w.positionY, w.img.getWidth(), w.height);									//Duvar�n y�ksekli�ine g�re de�i�en �izim
	}
	
	//Topun ana karaktere �arpmas� sonucunda �al��acak fonksiyon
	private void restart(){	
		lifes--;																									//Can say�s� bir aza iner
		
		mainCharacter = new Character(characterTexture);															//Ana karakter yeniden olu�turulur
		
		balls = new ArrayList<Ball>();																				//Top dizisi s�f�rlan�r ve yeni top olu�turulur
	    balls.add(new Ball(ballTexture, 200, 200, 15, 50, 1));		
	}
	
	//Oyunun biti�ini i�in olu�turulan fonksiyon
	private void finish(){																			
		finishStr = "Game Over";
		pause = true;
	}
	
	//Oyunun kazanma durumuna bakan fonksiyon
	private void checkWin(){
		if(balls.size()==0){
			finishStr = "You Won";
			pause = true;
		}
	}
	
	
	//Mobil olarak �al��an dokunma i�lemlerini yapan fonksiyon
	private void touch(){
		for(int i = 0; i < 2; i++){
			if(touches.get(i).touched){
				if(touches.get(i).touchX < WIDTH / 3){																					//Sol pencerede olma durumu - Sola y�r�me
					mainCharacter.MoveHorizontal(-movementSpeed * Gdx.graphics.getDeltaTime(), WIDTH);				
					leftMoving = true;
				}
				else{
					leftMoving = false;
				}
				if(touches.get(i).touchX < WIDTH * 2 / 3 && touches.get(i).touchX >= WIDTH / 3){										//Orta pencerede olma durumu - Ate� etme
					wall = new Wall(wallTexture, mainCharacter.positionX, mainCharacter.positionY);	
				}
				if(touches.get(i).touchX < WIDTH && touches.get(i).touchX >= WIDTH * 2 / 3){											//Sa� pencerede olma durumu - Sa�a y�r�me
					mainCharacter.MoveHorizontal(movementSpeed * Gdx.graphics.getDeltaTime(), WIDTH);
					rightMoving = true;
				}
				else{
					rightMoving = false;
				}
			}
		}
	}
	
	//InputProcessor'un override edilmesi gereken dokunma oldu�u zaman �al��an kod
	 @Override
	 public boolean touchDown(int screenX, int screenY, int pointer, int button){ 
		 if(pointer < 2){
			 touches.get(pointer).touchX = screenX;							//�ki dokunmaya kadar konumu s�n�fa ata
			 touches.get(pointer).touchY = screenY;
			 touches.get(pointer).touched = true;
		 }
		 return true;
	 }
	 
	//InputProcessor'un override edilmesi gereken dokunma b�rak�ld��� zaman �al��an kod
	 @Override
	 public boolean touchUp(int screenX, int screenY, int pointer, int button){
		 if(pointer < 2){
			 touches.get(pointer).touchX = 0;								//�ki dokunmaya kadar b�t�n s�n�f� s�f�rla
			 touches.get(pointer).touchY = 0;
			 touches.get(pointer).touched = false;
		 }
		 return true;
	 }

	 //Interface'den gelen eklenmesi gereken kodlar
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}


}
