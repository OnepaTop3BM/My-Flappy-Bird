package my.flyingbird.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import my.flyingbird.R;
import my.flyingbird.SoundManager;

import android.R.bool;
//import my.flappy.bird.R;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;

//TODO: Известные баги:
//1. если комп долго думает и сделать ход, то происходит сбой в очерёдности ходов игроков
//2. кнопки "отмена" и "отправить" работают всегда: и когда невидимы, и когда фишки двигаются

//TODO: сделать анимацию при убирании фишек
//сделать возможность убирать произвольную фишку (букву) из слова

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Game implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8326065952389292265L;
	private static final String TAG = Game.class.getSimpleName();
	private String name;
	private ColorTheme theme;
	private int distance = 0;
	private int score = 0;
	boolean boom = false;

	private GameLogic gameLogic;
	private int t = 0;
	private float a = 0.0f;
	private float g = 5.8f;
	private int riseCounter = 0;
	private boolean flapped = false;

	private List<Integer> pipeValues;
	
	private Bitmap bitmapBird;
	private Bitmap bitmapPipe;
	private Bitmap bitmapBg01;
	private Bitmap bitmapBg02;
	private Bitmap bitmapCloud01;
	private Bitmap bitmapCloud02;
	private Bitmap bitmapCloud03;
	
	private Resources resources;
	private int width;
	private int height;
	
	public static final int FIRST_MOVE_IS_MINE  = 0;
	public static final int FIRST_MOVE_OPPONENT = 1;
	public static final int GAME_PLAY = 0;
	public static final int GAME_OVER = 1;
	
	private BirdPlayer bird;
	
	private int GameState = GAME_PLAY;
	
	private boolean NeedShake;
	private boolean ShakeHot;
	private int shakes1 = 0;
	private int shakes2 = 0;
	private int ticks;
	
	private boolean visible = false;
	
	private String helpWord = "";
	
	public Game(GameLogic gameLogic, String name)
	{
		this.gameLogic = gameLogic;
		this.name = name;
		visible = false;

		Bitmap workingBitmap = BitmapFactory.decodeResource(gameLogic.Resources(), R.drawable.flappybird);
        bitmapBird = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Bitmap workingBitmapPipes = BitmapFactory.decodeResource(gameLogic.Resources(), R.drawable.spawn_cactus);
        bitmapPipe = workingBitmapPipes.copy(Bitmap.Config.ARGB_8888, true);

        Bitmap workingBitmapBg01 = BitmapFactory.decodeResource(gameLogic.Resources(), R.drawable.mountains2);
        bitmapBg01 = workingBitmapBg01.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap workingBitmapBg02 = BitmapFactory.decodeResource(gameLogic.Resources(), R.drawable.landscape02);
        bitmapBg02 = workingBitmapBg02.copy(Bitmap.Config.ARGB_8888, true);

        Bitmap workingBitmapCloud01 = BitmapFactory.decodeResource(gameLogic.Resources(), R.drawable.cloud01);
        bitmapCloud01 = workingBitmapCloud01.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap workingBitmapCloud02 = BitmapFactory.decodeResource(gameLogic.Resources(), R.drawable.cloud02);
        bitmapCloud02 = workingBitmapCloud02.copy(Bitmap.Config.ARGB_8888, true);

        pipeValues = new ArrayList<Integer>();
        
        bird = new BirdPlayer("bird", new ColorTheme(), 111, 111);
		visible = false;
		resources = gameLogic.Resources();
		width = gameLogic.width();

		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, resources.getDisplayMetrics());
		height = (int)(gameLogic.height() - px);//для AdMob
		
		//mField = new LettersField(this, resources, width, height);
		//mField.Generate(gameLogic.WordsDictionary(), letters);

		long seed = System.nanoTime();
		Random rnd = new Random(seed);
		double k = rnd.nextDouble();
		
		//theme = ColorTheme.Pop();
		theme = ColorTheme.Light();
		//theme = ColorTheme.YellowGreen();

		//CheckVictory(0);
	}
	
	public ColorTheme Theme()
	{
		return theme;
	}
	/*
	public LettersField Field()
	{
		return this.mField;
	}
	*/
	public String Name()
	{
		return name;
	}
	
	public void Init(int firstPlayer){
		visible = false;
		
		this.resources = gameLogic.Resources();
		
		NeedShake = false;
	}
	
	public void Render(Canvas canvas)
	{
		if(!visible) return;
	
		try{
			ticks++;
			
			if(bird.GetState() == 1)
				distance++;
	
			if(ticks%100 == 0)
			{
				//Log.i(TAG, "Render() " + Name());
			}
	
			canvas.drawColor(theme.BackgroundColor);
	
			//TODO:Animation effects, elements -> IAnimation
			if(NeedShake) shakes1++;
			if(shakes1>50) NeedShake = false;
				
			if(ShakeHot) shakes2++;
			if(shakes2>100) ShakeHot = false;
				
			//background
			Paint p1 = new Paint();
			p1.setAntiAlias(true);
			p1.setColor(theme.BackgroundColor);
	
			//draw buttons
			//if(mField.currentWord.GetLength()>0)
			{
				Paint btp = new Paint();
				btp.setColor(theme.TextColor);
				btp.setAntiAlias(true);
		
				Paint pb = new Paint();
				pb.setAntiAlias(true);
				pb.setColor(theme.ItemColor);
				/*
				for (GameButton btn : mField.GetButtons()) {
					canvas.drawRect(btn.Rectangle(), pb);
					btp.setTextSize(btn.Height()/2);
					canvas.drawText(btn.Text(), 
							(int)(btn.Rectangle().left+btn.Height()*0.3), 
							(int)(btn.Rectangle().top+btn.Height()*0.65), btp);
				}*/
			}
			
			double r = Math.cos(ticks*3.14/20)*0.05 + 0.6;
	
			Paint pu0 = new Paint();
			pu0.setAntiAlias(true);
			//pu0.setColor(Color.argb(255, 0, 100, 0));
			
			int w = bitmapBg01.getWidth(); 
	
			canvas.drawBitmap(bitmapBg01, -((distance/2)%w), height - (int)(bitmapBg01.getScaledHeight(resources.getDisplayMetrics())*0.75), pu0);
			//canvas.drawBitmap(bitmapBg01, -((distance/2)%w) + bitmapBg01.getWidth(), height - bitmapBg01.getScaledHeight(resources.getDisplayMetrics()), pu0);
	
			canvas.drawBitmap(bitmapBg02, -((distance*2)%w), height - (int)(bitmapBg02.getScaledHeight(resources.getDisplayMetrics())*0.85), pu0);
			//canvas.drawBitmap(bitmapBg02, -((distance*2)%w) + bitmapBg01.getWidth(), height - bitmapBg02.getScaledHeight(resources.getDisplayMetrics())*2/4, pu0);
	
			canvas.drawBitmap(bitmapCloud01, width - ((distance) % (width + bitmapCloud01.getScaledWidth(resources.getDisplayMetrics()))), height/2, pu0);
			canvas.drawBitmap(bitmapCloud02, width - (((distance+width/2)) % (width  + bitmapCloud02.getScaledWidth(resources.getDisplayMetrics()))), height/5, pu0);
	
			canvas.drawBitmap(bitmapBird, bird.GetX(), bird.GetY(), pu0);
	
			int pipeDist = 50;
			
			
			//Если bitmap лежат в 
			//ldpi, то 240x362
			//mdpi => XXXx400
			
			Random rand = new Random((long)(System.currentTimeMillis() + bird.GetY() + score));
	
			int bH = bitmapBird.getScaledHeight(resources.getDisplayMetrics());
			int bW = bitmapBird.getScaledWidth(resources.getDisplayMetrics());
			int pH = bitmapPipe.getScaledHeight(resources.getDisplayMetrics());
			int pW = bitmapPipe.getScaledWidth(resources.getDisplayMetrics());
			
			int nextPipeId = (int)((distance + 0)/pipeDist) + 1;
			if(nextPipeId > pipeValues.size())
			{
				Log.i(TAG, "newxPipeId=" + nextPipeId);
				int pipeX = (int)(( nextPipeId*pipeDist - distance)*5*width/240);
				
				int y = rand.nextInt((int)height/2) - height/4;
				int pipeY = -(int)(pH*750/1600)+height/2 + y;
				
				pipeValues.add(pipeY);
				Log.i(TAG, pipeX + ", " + pipeY);
			}
	
			
			for(int i=0; i<pipeValues.size(); i++)
			{
				int pipeX = (int)(((i+1)*pipeDist - distance)*5*width/240);
				canvas.drawBitmap(bitmapPipe, pipeX, pipeValues.get(i), pu0);
				
				int px1 = pipeX;
				int px2 = pipeX + pW;
				
				int minY = (pipeValues.get(i) + (pH*750/1600));
				int maxY = (pipeValues.get(i) + (pH*850/1600));
				
				int birdY1 = bird.GetY();
				int birdY2 = bird.GetY() + bH;
	
				Paint pl = new Paint();
				pl.setAntiAlias(true);
				pl.setColor(Color.argb(255, 255, 0, 0));
				//canvas.drawLine(px1, minY, px2, minY, pl);
				//canvas.drawLine(px1, maxY, px2, maxY, pl);
	
				pl.setColor(Color.argb(255, 0, 100, 0));
				//canvas.drawLine(bird.GetX(), birdY1, bird.GetX()+bW, birdY1, pl);
				//canvas.drawLine(bird.GetX(), birdY2, bird.GetX()+bW, birdY2, pl);
	
				if(bird.GetX()+bW > px1 && bird.GetX() < px2)
				{
					if(birdY1>=minY && birdY2<=maxY)
					{
						//msg = new Message("Yahooo!!! " + bird.GetY() + " : " + minY + "-" + maxY + " ; " + (height) + "/" + bitmapPipe.getHeight(), (px1+px2)/2, bird.GetY(), Color.BLUE, 50);
						//SoundManager.playSound(7, 1);
					}
					else
					{
						if(!boom)
							SoundManager.playSound(5, 1);

						boom = true;
						//msg = new Message("BOOM!!! " + bird.GetY() + " : " + minY + "-" + maxY + " " + (maxY-minY), (px1+px2)/2, bird.GetY(), Color.RED, 50);
						bird.SetState(0);
					}
					score = (i+1);
				}
				
			}
	
			pu0.setColor(theme.ItemPlayer1Color);
			//canvas.drawCircle(bird.GetX(), bird.GetY(), (float) (mField.getCardSize()*r), pu0);
	
			bird.SetX(width*1/4);
	
			if(bird.GetY()>=height)
				bird.SetState(0);
			
			switch (bird.GetState()) {
			case 0:
				bird.SetY(height/2);
				t = 0;
				break;
			case 1:
				t++;
				int newy = (int)(bird.GetY() + (g * t*t/2) * 0.01);
				
				if(flapped)
				{
					if(riseCounter<6)
					{
						newy = (int)(newy - 7 * height/400);
						riseCounter ++;
					}
					else
					{
						flapped = false;
						riseCounter = 0;
					}
				}
				
				bird.SetY(newy);
				break;
	
			default:
				break;
			}
			
			bird.draw(canvas);
			
			//SCORE
			Typeface tf = Typeface.create("Arial", Typeface.BOLD);
			Paint ps = new Paint();
			ps.setAntiAlias(true);
			ps.setColor(Color.rgb(255, 153, 0));
			ps.setTextSize(height/10);
			ps.setTypeface(tf);
			
			//float dx1 = mField.Score(1)<10 ? 0 : 0.14f;
			//float dx2 = mField.Score(2)<10 ? 0 : -0.14f;
			canvas.drawText( ""+score, (float)(width/2 - width/20 ), height/10, ps);
			
			Paint tp = new Paint();
			tp.setColor(theme.TextColor);
			tp.setAntiAlias(true);
	
			Paint paint = new Paint();
			paint.setColor(theme.TextColor);
			//paint.setTextSize(mField.getCardSize()/4);
			paint.setAntiAlias(true);
			//canvas.drawText("<<<", mField.getCardSize()/4, mField.getCardSize()/3, paint);
			//paint.setAlpha(100);
	
			//paddle.draw(canvas);
			//ball.draw(canvas);
		} catch(Exception e){
			//msg  = new Message(e.getMessage(), 10, height/2, Color.RED, 100);
		}
	}
	
	public boolean nextMove() {
		return false;
	}

	public void Show()
	{
		//int bH = mField.getCardSize()/2;
		//int bW = width/4;
/*		
		mField.AddButton(new GameButton(5, mField.cards[0].getCardRealFieldPosition(0, 0).top-bH-5, 
				resources.getString(R.string.ru_cancel), bW, bH));
		mField.AddButton(new GameButton(width-bW-5, mField.cards[0].getCardRealFieldPosition(0, 0).top-bH-5, 
				resources.getString(R.string.ru_send), bW, bH));
		*/
		visible = true;
	}
	
	public void Touched(float x, float y)
	{
		boom = false;

		try{
			switch (bird.GetState()) {
			case 0://готово к полёту
				distance = 0;
				bird.SetState(1);//играть
				flapped = true;
				riseCounter = 0;
				pipeValues.clear();

				SoundManager.playSound(2, 1);
				
				break;
			case 1://игра
			{
				riseCounter = 0;
				flapped = true;
				t = 0;
				SoundManager.playSound(2, 1);
				//bird.SetY(bird.GetY() - (int)(height*50/400));
			}
			break;
			case 2://умирает
			{
				riseCounter = 0;
				flapped = true;
				t = 0;
				//bird.SetY(bird.GetY() - (int)(height*50/400));
			}
			break;
			default:
				break;
			}
		} catch(Exception e){}
	}
	
	public boolean GameOver()
	{
		return GameState == GAME_OVER;
	}

}
