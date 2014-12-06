package my.flyingbird;

import java.lang.Thread.State;

import my.flyingbird.models.BaseScreen;
import my.flyingbird.models.GameLogic;
import my.flyingbird.models.IScreen;
import my.flyingbird.models.LoadingScreen;
import my.flyingbird.models.MenuScreen;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGameView extends SurfaceView implements 
		SurfaceHolder.Callback {

	private static final String TAG = MainGameView.class.getSimpleName();

	private MainThread thread;
	
	//private Game game;//no! we can have many games
	private GameLogic gameLogic;//GameLogic + MainGameView ?
	
	public MainGameView(Context context, GameLogic gameLogic) {
		super(context);
		getHolder().addCallback(this);

		this.gameLogic = gameLogic;
		// create the game loop thread
		thread = new MainThread(getHolder(), this);

		IScreen loadingScreen = new LoadingScreen("Loading", gameLogic);
		IScreen menuScreen = new MenuScreen("Menu", gameLogic);

		gameLogic.AddScreen(((LoadingScreen)loadingScreen).Name(), loadingScreen);
		gameLogic.AddScreen(((MenuScreen)menuScreen).Name(), menuScreen);

		gameLogic.SetActiveScreen(loadingScreen);
		
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(TAG, "surfaceChanged");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		Log.i(TAG, "surfaceCreated: " + getWidth() + "x" + getHeight());

		gameLogic.CreateSurface(getWidth(), getHeight());

		//solve problem:
		////java.lang.IllegalThreadStateException: Thread already started.
		State state = Thread.currentThread().getState();
		Log.i("Thread.State", "" + state);
		try
		{
			if(state != State.NEW)
			{
				//thread.join();
			}
			//else
			{
				thread.setRunning(true);
				thread.start();
			}
				
		}
		catch(Exception e)
		{
			Log.i(TAG, "surfaceCreated, thread error:" + e.getMessage());
		}

		gameLogic.ActiveScreen().OnCreate();

		//TODO:убрать, когда поставлю загрузчик словаря на загрузочную страницу
		if(gameLogic.ActiveScreen().getClass().equals(LoadingScreen.class))
			((LoadingScreen)gameLogic.ActiveScreen()).LoadDictionary(getContext());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		thread.setRunning(false);
		boolean retry = true;
		while (retry) 
		{
			try {
				Log.d(TAG, "surfaceDestroyed(): joining...");
				thread.join();
				Log.d(TAG, "surfaceDestroyed(): joined");
				retry = false;
			} catch (InterruptedException e) {
				Log.d(TAG, "surfaceDestroyed(): try again shutting down the thread, error:" + e.getMessage());
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			//check if in the left upper part of the screen we exit
			/*if (event.getY() < game.CardSize() && event.getX() < game.CardSize()) {
				thread.setRunning(false);
				((Activity)getContext()).finish();
			}*/
		} 

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// the gestures
		} if (event.getAction() == MotionEvent.ACTION_UP) {
			IScreen activeScreen = 	gameLogic.ActiveScreen();
			activeScreen.OnTouch(event.getX(), event.getY());
		}
		return true;
	}

	public void render(Canvas canvas) {
		if (canvas == null) return;

		//Log.i("Render", ((BaseScreen)gameLogic.ActiveScreen()).Name());
		//Log.i(TAG, "render()");
		gameLogic.ActiveScreen().Render(canvas);
	}

	/**
	 * This is the game update method. It iterates through all the objects
	 * and calls their update method if they have one or calls specific
	 * engine's update method.
	 */
	public void update() {
		gameLogic.ActiveScreen().Update();
	}
	
}
