package my.flyingbird.models;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class BasePlayer implements IPlayer{

	protected String name;
	protected int score;
	protected int x;
	protected int y;
	protected int textX;
	protected int textY;
	protected ColorTheme theme;
	
	public BasePlayer(String name, ColorTheme theme, int x, int y)
	{
		this.name = name;
		this.score = 0;
		this.x = x;
		this.y = y;
		this.theme = theme;
	}
	
	public void SetX(int x)
	{
		this.x = x;
	}
	
	public void SetY(int y)
	{
		this.y = y;
	}

	public int GetX() {
		return this.x;
	}

	public int GetY() {
		return this.y;
	}
	
	public String GetName()
	{
		return this.name;
	}
	
	public Bitmap Icon()
	{
		return null;
	}
	
	public int Score()
	{
		return this.score;
	}
	
	public void SetScore(int value)
	{
		this.score = value;
	}
	
	public void draw(Canvas canvas) {
		Paint pu1 = new Paint();
		pu1.setAntiAlias(true);
		pu1.setColor(theme.ItemPlayer1Color);
		
		pu1.setColor(theme.TextColor);
		
		canvas.drawText(this.name, textX, textY, pu1);

		//players inner circle
		pu1.setColor(theme.BackgroundColor);
		//canvas.drawCircle(x, y, gameField.getCardSize()/3, pu1);

	}
}
