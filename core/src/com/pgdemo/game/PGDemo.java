package com.pgdemo.game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.pgdemo.game.Quest.Type;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QEncoderStream;

public class PGDemo extends ApplicationAdapter implements InputProcessor{
	
	private Random rand = new Random();
	
	private boolean gameStarted = false;
	
	private SpriteBatch batch;
	private Character mainCharacter;							
	private List<Ball> balls;									
	private Wall wall;											
	private OrthographicCamera camera;							
	private Rectangle glViewport;			
	private BitmapFont font;
	private Texture ballTexture;								
	private Texture characterTexture;							
	private Texture wallTexture;								
	private Texture lifeTexture;								
	private Texture leftAnimTexture;							//Sol tarafa y�r�me animasyonu i�in doku atlas�
	private Texture menuBackground;
	
	private Animation charLeftAnim;								//Sol tarafa y�r�me animasyonu
	
	private boolean pause = false;								
	private String finishStr;									
	
	private int score = 0;										
	private int lifes = 3;										
	
	private int movementSpeed = 120;							//Ana karakterin y�r�me h�z�
	
	private boolean leftMoving = false;							//Ana karakterin sol ve sa� tarafa y�r�me bilgileri
	private boolean rightMoving = false;
	
	private int WIDTH = 800;									
	private int HEIGHT = 480;
	
	private float lastPressed = 0.0f;							//Ate� etme tu�unun art arda olmamas� i�in zaman tutma
	
	class TouchInfo{											//Mobil i�in dokunma s�n�f�
		public float touchX = 0.0f;
		public float touchY = 0.0f;
		public boolean touched = false;
	}
	
	private Map<Integer, TouchInfo> touches = new HashMap<Integer, TouchInfo>();									//Dokunma bilgilerini tutacak harita
	private int[] mousePosition = new int[2];
	private boolean mousePressed = false;
	
	private List<Quest> quests = new ArrayList<Quest>();
	private Quest currentQuest;
	
	private List<Integer> highscores = new ArrayList<Integer>();
	
	@Override
	public void create () {
		
		ballTexture = new Texture(Gdx.files.internal("ball.png"));													
		characterTexture = new Texture(Gdx.files.internal("character.png"));
		wallTexture = new Texture(Gdx.files.internal("wall.png"));
		lifeTexture = new Texture(Gdx.files.internal("life.png"));
		leftAnimTexture = new Texture(Gdx.files.internal("leftAnim.png"));
		menuBackground = new Texture(Gdx.files.internal("backgroundmenu.png"));
		
		TextureRegion[] leftRegions = new TextureRegion[18]; 														//Y�r�me animasyonu i�in 
		for(int i = 0; i < 3; i++){																					//doku atlas�ndan dokular�n �ekilmesi
			for(int j = 0; j < 6; j++){
				leftRegions[i * 6 + j] = new TextureRegion(leftAnimTexture, j * 35, i * 55, 35, 55);
			}
		}
		
		charLeftAnim = new Animation(0.1f, leftRegions);															//Y�r�me animasyonunun olu�turulmas�		
		
		batch = new SpriteBatch();
		//mainCharacter=new Character(characterTexture);																
		
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		
		//camera = new OrthographicCamera();
		//camera.setToOrtho(false, WIDTH, HEIGHT);
		
	    glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
	    
	    balls = new ArrayList<Ball>();
	    //balls.add(new Ball(ballTexture, 200, 200, 15, 50, 1));														
	    
	    font = new BitmapFont();
	    font.setColor(Color.BLUE);
	    font.setScale(2);
	    
	    Gdx.input.setInputProcessor(this);																		
	    for(int i = 0; i < 2; i++){																					
	    	touches.put(i, new TouchInfo());	
	    }
	    
	    quests.add(new Quest("Destroy 2 ball in 30 secs.", Quest.Type.BALL_DESTROY, 2, 30));
	    quests.add(new Quest("Do not die in 10 secs.", Quest.Type.DONT_DIE, 10));
	    quests.add(new Quest("Score 200 points in 7 secs.", Quest.Type.SCORE, 200, 7));
	    quests.add(new Quest("Destroy 3 balls with 3 walls.", Quest.Type.BALL_DESTROY_EFFICIENT, 3, 3));
	    quests.add(new Quest("Make highscore.", Quest.Type.HIGHSCORE));
	    
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport((int) glViewport.x, (int) glViewport.y,
                (int) glViewport.width, (int) glViewport.height);
		
		//camera.update();
		
		if(!pause && gameStarted){																					//Oyun durarken �al��mas�n� istemedi�imiz fonksiyonlar
			keyboard();																								//Klavyeden gelen bilgilerin i�lenece�i fonksiyon
			touch();																								//Mobil i�in dokunma i�lemlerinin yap�laca�� fonksiyon
			update();																								//Her frame �al��mas� istenen fonksiyonlar
			checkWin();																								//Oyunun kazanma bilgisinin tutulaca�� fonksiyon
		}
		
		batch.begin();
		
		drawBackground();
		
		if(!gameStarted){																							//Oyunun menudeki �al��acak fonksiyonlar�
			font.draw(batch, "PLAY", WIDTH / 6, HEIGHT / 2);
			font.draw(batch, "EXIT", WIDTH / 6, HEIGHT / 2 - 30);
			for(int i=0; i<highscores.size(); i++){
				font.draw(batch, (i+1)+". "+highscores.get(i), WIDTH * 5 / 6 , HEIGHT - 200 - i * 30);
				if(i==5)
					break;
			}
			//menuMouse();
			menuTouch();
		}
		
		if(gameStarted){
			drawCharacter(mainCharacter);																				
			for(int i = 0; i < balls.size(); i++){																		
				drawBall(balls.get(i));
			}
			if(wall != null)
				drawWall(wall);																							
			font.draw(batch, Integer.toString(score), WIDTH - 100, HEIGHT - 30);											
			
			for(int i = 0; i < lifes; i++){																				
				batch.draw(lifeTexture, 10 + 20 * i, + HEIGHT - 30);
			}
			
			showQuest();
			
			if(pause)																									
				font.draw(batch, finishStr, WIDTH / 2 - 60, HEIGHT / 2);
		}
		
		
		batch.end();
	}
	
	private void update(){
		int hs = 0;
		if(highscores.size()>0)
			hs=highscores.get(0);
		if(currentQuest.getType() == Quest.Type.HIGHSCORE){														//Y�ksek skor g�revi tamamlanm�� m�?
			if(score > hs){
				score += 250;
				currentQuest.refresh();
				currentQuest = quests.get(rand.nextInt(quests.size()));
			}
		}
		
		if(currentQuest.progress(Gdx.graphics.getDeltaTime())){													//G�rev tamamlanm�� m�?
			score += 250;
			currentQuest.refresh();
			currentQuest = quests.get(rand.nextInt(quests.size()));
		}
		else{																									
			if(!currentQuest.enoughTime()){																		//Zaman hakk� ge�mi� mi?
				currentQuest.refresh();
				currentQuest = quests.get(rand.nextInt(quests.size()));
			}
			if(!currentQuest.enoughWall()){																		//Duvar kullanma hakk� ge�mi� mi?
				currentQuest.refresh();
				currentQuest = quests.get(rand.nextInt(quests.size()));
			}
		}
		
		for(int i = 0; i < balls.size(); i++){
			balls.get(i).BallMovement(Gdx.graphics.getDeltaTime(), WIDTH);											//Her bir topun kare ba��na hareketinin sa�lanmas�
			
			if(balls.get(i).rect.overlaps(mainCharacter.rect)){														//Toplar�n ana karakterlerle �arp��ma durumuna bak�lmas�
				if(lifes > 0){																						//E�er can�m�z yeteri kadar var ise oyun yeniden ba�layacak
					restart();
				}
				else{
					finish();																						//Son can�m�z� kulland�ysak oyunu bitirilecek
				}
				break;
			}
			
			if(wall != null){
				if(balls.get(i) != null & balls.get(i).rect.overlaps(wall.rect)){											//E�er toplar duvara �arpt�ysa
					Ball temp = balls.get(i);																				//O top ve duvar yok olacak
					wall = null;
					balls.remove(i);
					System.out.println(balls.size());
					
					if(currentQuest.getType()==Quest.Type.BALL_DESTROY){
						currentQuest.ballDestroyed();
					}
					if(currentQuest.getType() == Quest.Type.SCORE){
						currentQuest.addScore((temp.getLevel() + 1) * 100);
					}
					if(currentQuest.getType() == Quest.Type.BALL_DESTROY_EFFICIENT){
						currentQuest.ballDestroyed();
					}					
					
					score += (temp.getLevel() + 1) * 100;																//Skor topun seviyesine g�re artacak
					
					if(temp.getLevel()>0){																				//Seviyeye g�re yar��ap�n ve seviyenin yar�s� kadar 2 yeni top olu�turulacak
						System.out.println("update - BALL CREATED");
						balls.add(new Ball(ballTexture, temp.positionX - 10, temp.positionY, temp.getRadius()/2, temp.horizontalSpeed<0 ? (temp.horizontalSpeed * 2 / 3) : -(temp.horizontalSpeed * 2 / 3), temp.getLevel() - 1));
						balls.add(new Ball(ballTexture, temp.positionX + 10, temp.positionY, temp.getRadius()/2, temp.horizontalSpeed>0 ? (temp.horizontalSpeed * 2 / 3) : -(temp.horizontalSpeed * 2 / 3), temp.getLevel() - 1));
					}
				}
			}		
		}
		
		
		if(wall != null){
			if(wall.getHeight()<HEIGHT)
				wall.Grow(Gdx.graphics.getDeltaTime());																//Duvar�n frame ba��na y�kselmesi
			else{
				wall=null;
			}
		}
		
		lastPressed += Gdx.graphics.getDeltaTime();
		
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
			if(lastPressed >= 0.2f){
				wall = new Wall(wallTexture, mainCharacter.positionX, mainCharacter.positionY);
				if(currentQuest.getType() == Quest.Type.BALL_DESTROY_EFFICIENT)
					currentQuest.wallUsed();
				lastPressed = 0.0f;
			}
		}
	}
	
	//Menudeki mouse hareketi
	private void menuMouse(){
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			mousePressed = true;
			mousePosition[0] = Gdx.input.getX();
			mousePosition[1] = Gdx.input.getY();
		}
		else{
			mousePressed = false;
		}
		if(mousePressed){
			if(mousePosition[0] > WIDTH / 6 && mousePosition[0] < WIDTH / 6 + 220 && mousePosition[1] > HEIGHT / 2 && mousePosition[1] < HEIGHT / 2 + 30 ){			//PLAY Butonu
				startGame();
			}
			if(mousePosition[0] > WIDTH / 6 && mousePosition[0] < WIDTH / 6 + 220 && mousePosition[1] > HEIGHT / 2 + 30 && mousePosition[1] < HEIGHT / 2 + 60 ){	//EXIT Butonu
				Gdx.app.exit();
			}
		}
		
	}
	
	//Menudeki dokunma fonksiyonu
	private void menuTouch(){
		//TODO: TOUCH
		for(int i=0;i<2;i++){
			if(touches.get(i).touched){
				if(touches.get(i).touchX > WIDTH / 6 && touches.get(i).touchX < WIDTH / 6 + 220 && touches.get(i).touchY > HEIGHT / 2 && touches.get(i).touchY < HEIGHT / 2 + 30){
					startGame();
				}
				
				if(touches.get(i).touchX > WIDTH / 6 && touches.get(i).touchX < WIDTH / 6 + 220 && touches.get(i).touchY > HEIGHT / 2 + 30 && touches.get(i).touchY < HEIGHT / 2 + 60){
					Gdx.app.exit();
				}
			}
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
		batch.draw(b.img, b.positionX, b.positionY, b.getRadius() * 2, b.getRadius() * 2);									
	}
	
	//Duvar�n �iziminin yap�laca�� fonksiyon
	private void drawWall(Wall w){
		batch.draw(w.img, w.positionX, w.positionY, w.img.getWidth(), w.getHeight());									
	}
	
	private void drawBackground(){
		if(gameStarted){
			//batch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
		}
		else{
			batch.draw(menuBackground, 0, 0, WIDTH, HEIGHT);
		}
	}
	
	//Menuden oyuna gecis
	private void startGame(){
		mainCharacter = new Character(characterTexture);
		balls.add(new Ball(ballTexture, 200, 200, 15, 50, 1));
		currentQuest = quests.get(rand.nextInt(quests.size()));
		gameStarted = true;
		score=0;
		lifes=3;
	}
	
	//Topun ana karaktere �arpmas� sonucunda �al��acak fonksiyon
	private void restart(){	
		lifes--;																									
		if(currentQuest.getType() == Quest.Type.DONT_DIE){
			currentQuest.refresh();
			currentQuest = quests.get(rand.nextInt(quests.size()));
		}
		mainCharacter = new Character(characterTexture);															
		
		balls = new ArrayList<Ball>();																				
	    //balls.add(new Ball(ballTexture, 200, 200, 15, 50, 1));		
	}
	
	//Oyunun biti�ini i�in olu�turulan fonksiyon
	private void finish(){																			
		//finishStr = "Game Over";
		//pause = true;
		gameStarted=false;
		mainCharacter=null;
		for(int i=highscores.size()-1; i>=0; i--){
			if(score < highscores.get(i)){
				highscores.add(i, score);
				break;
			}
			if(i==0){
				highscores.add(0, score);
			}
		}
		if(highscores.size()==0)
			highscores.add(score);
		balls.clear();
	}
	
	//Oyunun kazanma durumuna bakan fonksiyon
	private void checkWin(){
		System.out.println(balls.size());
		if(balls.size()==0){
			int l = rand.nextInt(4);
			System.out.println("checkWin BALL CREATED");
			balls.add(new Ball(ballTexture, rand.nextInt(WIDTH), rand.nextInt(HEIGHT / 3) + HEIGHT / 2, l * 15, rand.nextInt(150) - 75, l));
		}
	}
	
	//G�rev durumunun ekranda g�sterimi
	private void showQuest(){
		font.setScale(1);
		String progressString = "";
		
		if(currentQuest.getType() == Quest.Type.BALL_DESTROY){
			progressString = currentQuest.getCurrentBall()+" balls "+(int)currentQuest.getCurrentTime()+"secs.";
		}
		else if(currentQuest.getType() == Quest.Type.DONT_DIE){
			progressString = (int)currentQuest.getCurrentTime()+"secs";
		}
		else if(currentQuest.getType() == Quest.Type.SCORE){
			progressString = currentQuest.getCurrentScore()+" points "+(int)currentQuest.getCurrentTime()+"secs.";
		}
		else if(currentQuest.getType() == Quest.Type.BALL_DESTROY_EFFICIENT){
			progressString = currentQuest.getCurrentBall()+" balls "+currentQuest.getCurrentWall()+" walls.";
		}
		else{
			progressString = "Highscore is ";
			if(highscores.size()>0){
				progressString += highscores.get(0);
			}
			else
				progressString += "0";
		}
		
		font.draw(batch, currentQuest.getInfo() + " :: " + progressString, 0, HEIGHT - 100);
		font.setScale(2);
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
					if(lastPressed > 0.2f){
						wall = new Wall(wallTexture, mainCharacter.positionX, mainCharacter.positionY);
						if(currentQuest.getType() == Quest.Type.BALL_DESTROY_EFFICIENT)
							currentQuest.wallUsed();
						lastPressed = 0.0f;
					}
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
			 touches.get(pointer).touchX = screenX;							
			 touches.get(pointer).touchY = screenY;
			 touches.get(pointer).touched = true;
		 }
		 return true;
	 }
	 
	//InputProcessor'un override edilmesi gereken dokunma b�rak�ld��� zaman �al��an kod
	 @Override
	 public boolean touchUp(int screenX, int screenY, int pointer, int button){
		 if(pointer < 2){
			 touches.get(pointer).touchX = 0;								
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
