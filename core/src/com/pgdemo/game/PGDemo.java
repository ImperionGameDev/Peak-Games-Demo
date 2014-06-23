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
	Wall wall;											//Ana karakterin ateþ ederek oluþturduðu duvar
	OrthographicCamera camera;							
	Rectangle glViewport;			
	BitmapFont font;
	Texture ballTexture;								//Oyundaki toplarýn dokusu
	Texture characterTexture;							//Ana karakterin dokusu
	Texture wallTexture;								//Duvar dokusu
	Texture lifeTexture;								//Can bilgisi için doku
	Texture leftAnimTexture;							//Sol tarafa yürüme animasyonu için doku atlasý
	
	Animation charLeftAnim;								//Sol tarafa yürüme animasyonu
	
	boolean pause = false;								//Oyunun durmasý
	String finishStr;									//Oyun durduðunda ekranda gözükecek bilgi
	
	int score = 0;										//Skor bilgisi
	int lifes = 3;										//Can sayýsý
	
	int movementSpeed = 120;							//Ana karakterin yürüme hýzý
	
	boolean leftMoving = false;							//Ana karakterin sol ve sað tarafa yüürme bilgileri
	boolean rightMoving = false;
	
	int WIDTH = 800;								//Ekranýn geniþliði ve yüksekliði
	int HEIGHT = 480;
	
	class TouchInfo{									//Mobil için dokunma sýnýfý
		public float touchX = 0.0f;
		public float touchY = 0.0f;
		public boolean touched = false;
	}
	
	private Map<Integer, TouchInfo> touches = new HashMap<Integer, TouchInfo>();									//Dokunma bilgilerini tutacak harita
	
	@Override
	public void create () {
		
		ballTexture = new Texture(Gdx.files.internal("ball.png"));													//Dokularýn alýndýðý bölüm
		characterTexture = new Texture(Gdx.files.internal("character.png"));
		wallTexture = new Texture(Gdx.files.internal("wall.png"));
		lifeTexture = new Texture(Gdx.files.internal("life.png"));
		leftAnimTexture = new Texture(Gdx.files.internal("leftAnim.png"));
		
		TextureRegion[] leftRegions = new TextureRegion[18]; 														//Yürüme animasyonu için 
		for(int i = 0; i < 3; i++){																					//doku atlasýndan dokularýn çekilmesi
			for(int j = 0; j < 6; j++){
				leftRegions[i * 6 + j] = new TextureRegion(leftAnimTexture, j * 35, i * 55, 35, 55);
			}
		}
		
		charLeftAnim = new Animation(0.1f, leftRegions);															//Yürüme animasyonunun oluþturulmasý		
		
		batch = new SpriteBatch();
		mainCharacter=new Character(characterTexture);																//Ana karakterin oluþturulmasý
		
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		
		//camera = new OrthographicCamera();
		//camera.setToOrtho(false, WIDTH, HEIGHT);
		
	    glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
	    
	    balls = new ArrayList<Ball>();
	    balls.add(new Ball(ballTexture, 200, 200, 15, 50, 1));														//Ýlk seviye topun oluþturulmasý
	    
	    font = new BitmapFont();
	    font.setColor(Color.BLUE);
	    font.setScale(2);
	    
	    Gdx.input.setInputProcessor(this);																		
	    for(int i = 0; i < 2; i++){																					//Ýki dokunmayý haritaya ata
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
		
		if(!pause){																									//Oyun durarken çalýþmasýný istemediðimiz fonksiyonlar
			keyboard();																								//Klavyeden gelen bilgilerin iþleneceði fonksiyon
			touch();																								//Mobil için dokunma iþlemlerinin yapýlacaðý fonksiyon
			update();																								//Her frame çalýþmasý istenen fonksiyonlar
			checkWin();																								//Oyunun kazanma bilgisinin tutulacaðý fonksiyon
		}
		
		batch.begin();
		drawCharacter(mainCharacter);																				//Ana karakterin çizilmesi
		for(int i = 0; i < balls.size(); i++){																		//Toplarýn çizilmesi
			drawBall(balls.get(i));
		}
		if(wall != null)
			drawWall(wall);																							//Duvar oluþturulmuþsa çizilmesi
		font.draw(batch, Integer.toString(score), WIDTH - 50, HEIGHT - 30);											//Skor bilgisinin gösterilmesi
		
		for(int i = 0; i < lifes; i++){																				//Can bilgisinin gösterilmesi
			batch.draw(lifeTexture, 10 + 20 * i, + HEIGHT - 30);
		}
		
		if(pause)																									//Oyun durduðunda gösterilmesi istenilen yazý
			font.draw(batch, finishStr, WIDTH / 2 - 60, HEIGHT / 2);
		
		batch.end();
	}
	
	private void update(){
		
		for(int i = 0; i < balls.size(); i++){
			balls.get(i).BallMovement(Gdx.graphics.getDeltaTime(), WIDTH);											//Her bir topun kare baþýna hareketinin saðlanmasý
			
			if(balls.get(i).rect.overlaps(mainCharacter.rect)){														//Toplarýn ana karakterlerle çarpýþma durumuna bakýlmasý
				if(lifes > 0){																						//Eðer canýmýz yeteri kadar var ise oyun yeniden baþlayacak
					restart();
				}
				else{
					finish();																						//Son canýmýzý kullandýysak oyunu bitirilecek
				}
			}
			
			if(wall != null){
				if(balls.get(i).rect.overlaps(wall.rect)){															//Eðer toplar duvara çarptýysa
					Ball temp = balls.get(i);
					balls.remove(i);																				//O top ve duvar yok olacak
					wall = null;
					
					score += (temp.level + 1) * 100;																//Skor topun seviyesine göre artacak
					
					if(temp.level>0){																				//Seviyeye göre yarýçapýn ve seviyenin yarýsý kadar 2 yeni top oluþturulacak
						balls.add(new Ball(ballTexture, temp.positionX - 10, temp.positionY, temp.radius/2, -(temp.horizontalSpeed * 2 / 3), temp.level - 1));
						balls.add(new Ball(ballTexture, temp.positionX + 10, temp.positionY, temp.radius/2, (temp.horizontalSpeed * 2 / 3), temp.level - 1));
					}
				}
			}		
		}
		
		if(wall != null){
			if(wall.height<HEIGHT)
				wall.Grow(Gdx.graphics.getDeltaTime());																//Duvarýn frame baþýna yükselmesi
			else{
				wall=null;
			}
		}
		
	}
	
	
	//Klavyeyle ilgili fonksiyonlar
	private void keyboard(){
		if(Gdx.input.isKeyPressed(Keys.LEFT)){																		//Sol tuþa basýlýrsa sol tarafa hareket saðlanacak
			mainCharacter.MoveHorizontal(-movementSpeed * Gdx.graphics.getDeltaTime(), WIDTH);				
			leftMoving = true;
		}
		else{
			leftMoving = false;
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)){																		//Sað tuþa basýlýrsa sað tarafa hareket saðlanacak
			mainCharacter.MoveHorizontal(movementSpeed * Gdx.graphics.getDeltaTime(), WIDTH);
			rightMoving = true;
		}
		else{
			rightMoving = false;
		}
		if(Gdx.input.isKeyPressed(Keys.SPACE)){																		//Space tuþuna basýlýrsa ateþ etme iþlemi yapýlacak
			wall = new Wall(wallTexture, mainCharacter.positionX, mainCharacter.positionY);							
		}
	}
	
	//Ana karakterin animasyon ve çiziminin yapýldýðý fonksiyon
	private void drawCharacter(Character ch){
		if(leftMoving){																								//Eðer sol tarafa ilerleme yapýyorsa
			TextureRegion keyFrame = charLeftAnim.getKeyFrame(mainCharacter.walkingTime, true);						//Animasyon nesnesinden o framedeki resim çekilir
			mainCharacter.walkingTime += Gdx.graphics.getDeltaTime();												//Framedeki zaman ana karakterdeki yürüme zamanýna eklenir
			batch.draw(keyFrame, ch.positionX, ch.positionY);														//O framedeki resim ana karakterde gösterilir
		}
		else if(rightMoving){																						
			TextureRegion keyFrame = charLeftAnim.getKeyFrame(mainCharacter.walkingTime, true);						//Sað tarafa gitmek için yeni bir animasyon ve doku atlasýna gerek olmayýp sadece 
			mainCharacter.walkingTime += Gdx.graphics.getDeltaTime();												//Dokuyu y eksenine göre simetrik bir þekilde gösterilir
			batch.draw(keyFrame, ch.positionX + keyFrame.getRegionWidth(), ch.positionY, -(keyFrame.getRegionWidth()), keyFrame.getRegionHeight());
		}
		else{																										//Yürüme iþlemleri yapýlmadýðý zaman çalýþacak yer
			batch.draw(ch.img, ch.positionX, ch.positionY);
			mainCharacter.walkingTime = 0.0f;
		}
	}
	
	//Topun çiziminin yapýlacaðý fonksiyon
	private void drawBall(Ball b){
		batch.draw(b.img, b.positionX, b.positionY, b.radius * 2, b.radius * 2);									//Topun yarýçapýna baðlý olarak deðiþen çizim
	}
	
	//Duvarýn çiziminin yapýlacaðý fonksiyon
	private void drawWall(Wall w){
		batch.draw(w.img, w.positionX, w.positionY, w.img.getWidth(), w.height);									//Duvarýn yüksekliðine göre deðiþen çizim
	}
	
	//Topun ana karaktere çarpmasý sonucunda çalýþacak fonksiyon
	private void restart(){	
		lifes--;																									//Can sayýsý bir aza iner
		
		mainCharacter = new Character(characterTexture);															//Ana karakter yeniden oluþturulur
		
		balls = new ArrayList<Ball>();																				//Top dizisi sýfýrlanýr ve yeni top oluþturulur
	    balls.add(new Ball(ballTexture, 200, 200, 15, 50, 1));		
	}
	
	//Oyunun bitiþini için oluþturulan fonksiyon
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
	
	
	//Mobil olarak çalýþan dokunma iþlemlerini yapan fonksiyon
	private void touch(){
		for(int i = 0; i < 2; i++){
			if(touches.get(i).touched){
				if(touches.get(i).touchX < WIDTH / 3){																					//Sol pencerede olma durumu - Sola yürüme
					mainCharacter.MoveHorizontal(-movementSpeed * Gdx.graphics.getDeltaTime(), WIDTH);				
					leftMoving = true;
				}
				else{
					leftMoving = false;
				}
				if(touches.get(i).touchX < WIDTH * 2 / 3 && touches.get(i).touchX >= WIDTH / 3){										//Orta pencerede olma durumu - Ateþ etme
					wall = new Wall(wallTexture, mainCharacter.positionX, mainCharacter.positionY);	
				}
				if(touches.get(i).touchX < WIDTH && touches.get(i).touchX >= WIDTH * 2 / 3){											//Sað pencerede olma durumu - Saða yürüme
					mainCharacter.MoveHorizontal(movementSpeed * Gdx.graphics.getDeltaTime(), WIDTH);
					rightMoving = true;
				}
				else{
					rightMoving = false;
				}
			}
		}
	}
	
	//InputProcessor'un override edilmesi gereken dokunma olduðu zaman çalýþan kod
	 @Override
	 public boolean touchDown(int screenX, int screenY, int pointer, int button){ 
		 if(pointer < 2){
			 touches.get(pointer).touchX = screenX;							//Ýki dokunmaya kadar konumu sýnýfa ata
			 touches.get(pointer).touchY = screenY;
			 touches.get(pointer).touched = true;
		 }
		 return true;
	 }
	 
	//InputProcessor'un override edilmesi gereken dokunma býrakýldýðý zaman çalýþan kod
	 @Override
	 public boolean touchUp(int screenX, int screenY, int pointer, int button){
		 if(pointer < 2){
			 touches.get(pointer).touchX = 0;								//Ýki dokunmaya kadar bütün sýnýfý sýfýrla
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
