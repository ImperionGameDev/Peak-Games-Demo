package com.pgdemo.game;

class Quest {
	
	private String info;											
	private int ball, currentBall;
	private float time, currentTime;
	private int score, currentScore;
	private int wall, currentWall;
	
	
	//Görev tipleri:
	//BALL_DESTROY: Belli bir zaman icinde belli bir miktarda toplarýn yok edilmesi
	//DONT_DIE: Belli bir zaman icinde karakterin can kaybýnýn olmamasý
	//SCORE: Belli bir zaman icinde belli bir skor kazanma
	//BALL_DESTROY_EFFICIENT: Belli bir sayýda duvar kullanarak belli bir sayýda toplarýn yok edilmesi
	//HIGHSCORE: En yuksek skorun yapilmasi	
	public enum Type{BALL_DESTROY, DONT_DIE, SCORE, BALL_DESTROY_EFFICIENT, HIGHSCORE};
	
	private Type questType;
	
	//Constructors
	public Quest(String i, int b, float t, int s, int w, Type type){
		info=i;
		ball=b;
		currentBall=0;
		time=t;
		currentTime=0;
		score=s;
		currentScore=0;
		wall=w;
		currentWall=0;
		questType=type;
	}
	
	//Type 1, 3 and 4: Constructor
	public Quest(String i, Type type, int b, int t){
		info=i;
		questType=type;
		if(type == Type.BALL_DESTROY){
			ball=b;
			currentBall=0;
			time=t;
			currentTime=0;
		}
		else if(type == Type.SCORE) {
			score=b;
			currentScore=0;
			time=t;
			currentTime=0;
		}
		else{
			ball=b;
			currentBall=0;
			wall=t;
			currentWall=0;
		}
	}
	
	//Type 2: Constructor
	public Quest(String i, Type type, float t){
		info=i;
		questType=type;
		time=t;
		currentTime=0;
	}
	
	//Type 5: Constructor
	public Quest(String i, Type type){
		info = i;
		questType=type;
	}
	
	public void refresh(){
		currentTime=0;
		currentBall=0;
		currentScore=0;
		currentWall=0;
	}
	
	public String getInfo(){
		return info;
	}
	
	public float getCurrentTime(){
		return currentTime;
	}
	
	public float getCurrentScore(){
		return currentScore;
	}
	
	public int getCurrentWall(){
		return currentWall;
	}
	
	public int getCurrentBall(){
		return currentBall;
	}
	
	public Type getType(){
		return questType;
	}
	
	public void ballDestroyed(){
		currentBall++;
	}
	
	public void addScore(int amount){
		currentScore += amount;
	}
	
	public void wallUsed(){
		currentWall++;
	}
	
	//Görev zamanýnýn geçip geçmemesinin kontrolu
	public boolean enoughTime(){
		if(questType == Quest.Type.HIGHSCORE || questType == Quest.Type.BALL_DESTROY_EFFICIENT){
			return true;
		}
		else{
			if(currentTime>time)
				return false;
			else 
				return true;
		}
	}
	
	//Belirtilen sayidan fazla duvarin kullanilmamasi
	public boolean enoughWall(){
		if(questType == Quest.Type.BALL_DESTROY_EFFICIENT){
			if(currentWall>wall){
				System.out.println("ses");
				return false;
			}
		}
		return true;
	}
	
	//Görevlerin her karede isleyisi
	public boolean progress(float deltaTime){
		boolean result = false;
		
		currentTime += deltaTime;
		
		switch(questType){
		case BALL_DESTROY:
			result = questBallDestroy();
			break;
		case DONT_DIE:
			result = questDontDie();
			break;
		case SCORE:
			result = questScore();
			break;
		case BALL_DESTROY_EFFICIENT:
			result = questBallDestroyEfficient();
			break;
			
		}
		
		
		return result;
	}
	
	//Gorev fonksiyonlari
	private boolean questBallDestroy(){
		if(currentTime>time){
			return false;
		}
		else{
			if(currentBall == ball){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean questDontDie(){
		if(currentTime>time){
			return true;
		}
		else
			return false;
	}

	private boolean questScore(){
		if(currentTime>time){
			return false;
		}
		else{
			if(currentScore>score)
				return true;
		}
		
		return false;
	}
	
	private boolean questBallDestroyEfficient(){
		if(currentWall > wall){
			return false;
		}
		else{
			if(currentBall == ball)
				return true;
		}
		return false;
	}
}
