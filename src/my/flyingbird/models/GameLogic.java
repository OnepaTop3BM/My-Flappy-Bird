package my.flyingbird.models;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

//singleton
public class GameLogic implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5610446110733214508L;
	private static final String TAG = GameLogic.class.getSimpleName();
	private Map<String, IScreen> screens = new HashMap<String, IScreen>();
	private IScreen activeScreen;
	private int ticks;
	private int width, height;
	private Resources resources;
	private Context context;

	private Game games;

	public GameLogic(Context context, Resources resources)
	{
		Log.i(TAG, "GameLogic()");
		this.resources = resources;
		this.context = context;
		screens = new HashMap<String, IScreen>();
		games = new Game(this, "game");
	}
	
	public Context Context()
	{
		return this.context;
	}
	
	public Resources Resources()
	{
		return this.resources;
	}
	
	//RegisterScreen
	public void AddScreen(String name, IScreen screen)
	{
		if(screens.get(name) == null)
		{
			screens.put(name, screen);
			Log.i(TAG, "new screen added: " + name);
		}
	}
	
	public void RemoveScreen(String name)
	{
		screens.remove(name);
	}
	
	public IScreen FindScreen(String name)
	{
		IScreen s = screens.get(name);
		Log.i(TAG, "screen found: " + s);
		return s;
	}
	
	public void SetActiveScreen(IScreen screen)
	{
		activeScreen = screen;
		activeScreen.OnCreate();
	}
	
	public void SetActiveScreen(String name)
	{
		Log.i(TAG, "SetActiveScreen to " + name);
		//current screen becomes old
		activeScreen.OnHide();

		//new screen
		activeScreen = FindScreen(name);
		Log.i(TAG, "ActiveScreen=" + activeScreen);
		Log.i(TAG, "ActiveScreen is " + name);
		activeScreen.OnShow();
		Log.i(TAG, "ActiveScreen " + name + " shown");
	}
	
	public IScreen ActiveScreen()
	{
		return activeScreen;
	}

	public void CreateSurface(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int width()
	{
		return width;
	}
	
	public int height()
	{
		return height;
	}
	
}

