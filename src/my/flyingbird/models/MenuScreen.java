package my.flyingbird.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

public class MenuScreen extends BaseScreen {
	private static final String TAG = MenuScreen.class.getSimpleName();
	
	public MenuScreen(String name, GameLogic gameLogic) {
		super(name, gameLogic);
		Log.i(TAG, "Created");
	}
	
	@Override 
	public void OnShow()
	{
		int k = 0;
		int sz = GetGameLogic().width()/5;
		int x = GetGameLogic().width()/7;
	}

	private void StartNewGame()
	{
		Game game = new Game(GetGameLogic(), "game"+System.currentTimeMillis());
		//GetGameLogic().AddGame(game);
		game.Init(0);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GetGameLogic().Context());
	    String regId = prefs.getString("deviceId", "");

		IScreen gameScr = new GameScreen(game.Name(), GetGameLogic());
		((GameScreen)gameScr).RegisterGame(game);
		GetGameLogic().AddScreen(game.Name(), gameScr);
		//gameLogic.SetActiveScreen(loadingScreen);

		SwitchScreen(game.Name());		
	}
	
	@Override
	public void OnTouch(float x, float y) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Menu touched at " + x + ", " + y);
		
		StartNewGame();

/*
		for (MenuButton mb : menuButtons) {
			if(mb.Clicked(x, y))
			{
				Log.i(TAG, "mb.title: " + mb.title);
				
				if(mb.ScreenName().equals("NewGame"))
				{
					if(GetGameLogic().GetGames().size()<3)
					{
						StartNewGame();
						return;//итератор по кнопкам меняется при добавлении новой игры, поэтому выходим из цикла
					}
				}
				else
				if(mb.ScreenName().startsWith("game"))
				{
					Log.i(TAG, "Switch to game " + mb.ScreenName());
					Game game = GetGameLogic().GetGame(mb.ScreenName());

					//SwitchScreen(game.Name());
					super.GetGameLogic().SetActiveScreen(game.Name());
					//super.GetGameLogic().SetActiveScreen("Menu");
				}
			}
		}
		//find clicked item: new game, options or any player
		//SwitchScreen("NewGame");*/
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void Render(Canvas canvas) {
		canvas.drawColor(Color.argb(255, 240, 240, 240));

		ColorTheme dt = ColorTheme.Pop();
		
		canvas.drawColor(dt.BackgroundColor);

		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setAntiAlias(true);
		canvas.drawText("This is Menu", 10, 50, paint);
		canvas.drawText("Click for New Game", (float) (100 + Math.random()*20-10), (float) (100 + Math.random()*20-10), paint);
	}

	@Override
	public void Update() {
/*		for (Game game : GetGameLogic().GetGames()) {
			if(game.GameOver())
			{
				GetGameLogic().RemoveScreen(game.Name());
			}
		}*/
	}
	
	
}
