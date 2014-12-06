package my.flyingbird.models;

import java.io.Serializable;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BirdPlayer extends BasePlayer {

	private int state; 
	
	public BirdPlayer(String name, ColorTheme theme, int x, int y)
	{
		super(name, theme, x, y);
		state = 0;
	}

	@Override
	public void draw(Canvas canvas) {
		/*
		Paint pu1 = new Paint();
		pu1.setAntiAlias(true);
		pu1.setColor(theme.ItemPlayer1Color);
		pu1.setColor(theme.TextColor);
		pu1.setColor(theme.BackgroundColor);*/
	}
	
	public void SetState(int x)
	{
		state = x;
	}

	public int GetState()
	{
		return state;
	}
}
