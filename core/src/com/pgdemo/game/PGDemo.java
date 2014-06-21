package com.pgdemo.game;


import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PGDemo extends ApplicationAdapter {
	SpriteBatch batch;
	Character mainCharacter;
	List<Ball> balls;
	Wall wall;
	OrthographicCamera camera;
	Rectangle glViewport;
	BitmapFont font;
	
	int score = 0;
	int lifes = 3;
	
	int movementSpeed = 120;
	
	final int WIDTH = 800;
	final int HEIGHT = 480;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		mainCharacter=new Character(new Texture(Gdx.files.internal("character.png")));
		
		camera = new OrthographicCamera();
	    camera.setToOrtho(false, WIDTH, HEIGHT);
		
	    glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);
	    
	    balls = new ArrayList<Ball>();
	    balls.add(new Ball(new Texture(Gdx.files.internal("ball.png")), 200, 200, 15, 50, 1));
	    
	    font = new BitmapFont();
	    font.setColor(Color.BLUE);
	    font.setScale(2);
	    
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glViewport((int) glViewport.x, (int) glViewport.y,
                (int) glViewport.width, (int) glViewport.height);
		
		camera.update();
		
		keyboard();
		update();
		
		batch.begin();
		drawCharacter(mainCharacter);
		for(int i = 0; i < balls.size(); i++){
			drawBall(balls.get(i));
		}
		if(wall != null)
			drawWall(wall);
		font.draw(batch, Integer.toString(score), WIDTH - 50, HEIGHT - 30);
		
		for(int i = 0; i < lifes; i++){
			batch.draw(new Texture(Gdx.files.internal("life.png")), 10 + 20 * i, + HEIGHT - 30);
		}
		
		batch.end();
	}
	
	private void update(){
		
		for(int i = 0; i < balls.size(); i++){
			balls.get(i).BallMovement(Gdx.graphics.getDeltaTime(), WIDTH);
			
			if(balls.get(i).rect.overlaps(mainCharacter.rect)){
				System.out.println("Overlaped");
			}
			
			if(wall != null){
				if(balls.get(i).rect.overlaps(wall.rect)){
					Ball temp = balls.get(i);
					balls.remove(i);
					wall = null;
					
					score += (temp.level + 1) * 100;
					
					if(temp.level>0){
						balls.add(new Ball(new Texture(Gdx.files.internal("ball.png")), temp.positionX - 10, temp.positionY, temp.radius/2, -(temp.horizontalSpeed * 2 / 3), temp.level - 1));
						balls.add(new Ball(new Texture(Gdx.files.internal("ball.png")), temp.positionX + 10, temp.positionY, temp.radius/2, (temp.horizontalSpeed * 2 / 3), temp.level - 1));
					}
				}
			}		
		}
		
		if(wall != null){
			if(wall.height<HEIGHT)
				wall.Grow(Gdx.graphics.getDeltaTime());
			else{
				wall=null;
			}
		}
		
	}
	
	private void keyboard(){
		if(Gdx.input.isKeyPressed(Keys.LEFT)){
			mainCharacter.MoveHorizontal(-movementSpeed * Gdx.graphics.getDeltaTime(), WIDTH);
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)){
			mainCharacter.MoveHorizontal(movementSpeed * Gdx.graphics.getDeltaTime(), WIDTH);
		}
		if(Gdx.input.isKeyPressed(Keys.SPACE)){
			wall = new Wall(new Texture(Gdx.files.internal("wall.png")), mainCharacter.positionX, mainCharacter.positionY);
		}
	}
	
	private void drawCharacter(Character ch){
		batch.draw(ch.img, ch.positionX, ch.positionY);
	}
	
	private void drawBall(Ball b){
		batch.draw(b.img, b.positionX, b.positionY, b.radius * 2, b.radius * 2);
	}
	
	private void drawWall(Wall w){
		batch.draw(w.img, w.positionX, w.positionY, w.img.getWidth(), w.height);
	}
}
