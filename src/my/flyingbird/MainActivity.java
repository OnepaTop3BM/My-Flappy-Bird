package my.flyingbird;

import java.util.ArrayList;

import my.flyingbird.models.GameLogic;
import my.flyingbird.models.GameScreen;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private SurfaceView mainGameView;//all game logic on one canvas: menu, game, settings, game over, etc
	static Bitmap bitmap;
    static Canvas canvas;
    private GameLogic gameLogic;
    private ArrayList<String> wordsDictionary;
    private Context context;
    private MyTask mt;
    private boolean dictionaryLoaded;
    private ImageView image;
    private Activity activity;

    Intent playbackServiceIntent; 

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
			StrictMode.setThreadPolicy(policy); 
		}
		
		activity = this;
		
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SoundManager.getInstance();
        SoundManager.initSounds(this);
        SoundManager.loadSounds();

        SoundManager.playSound(1, 1);

        //playbackServiceIntent = new Intent(this, BackgroundAudioService.class);
        
        if(true)
            //используем экран на максимум
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            //оставляем сверху служебную информацию - время, сеть и пр. Чтобы было видно входящие сообщения
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        
        context = this;
		gameLogic = new GameLogic(context, getResources());

		dictionaryLoaded = false;
		
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
	    String data1 = sPref.getString("data1", "");

	    mainGameView = new MainGameView(this, gameLogic);
        //mainGameView = (MainGameView) findViewById(R.id -or- layout.game);

		final RelativeLayout layout = new RelativeLayout(context);
		layout.addView(mainGameView);
		
		//ставим рекламу на игровое поле (и в меню)
/*	    
		AdView admobView = new AdView(activity, AdSize.BANNER, "ca-app-pub-3194728467757569/6659505732");
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
		    RelativeLayout.LayoutParams.WRAP_CONTENT, 
		    RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		admobView.setLayoutParams(lp);

		layout.addView(admobView);
		admobView.loadAd(new AdRequest());
*/		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				//RegisterDevice();
			}
		};
		new Thread(runnable).start();
		
		//true/false - показываем процесс загрузки словаря или статичную фоновую картинку
	    if(true)
	    {
	        //show canvas game screen
	        setContentView(layout);
	    }
	    else
	    {
	        //show home screen with logo
			setContentView(R.layout.main_menu);
	
			mt = new MyTask();
		    mt.execute();
	
	        //find logo image for click -> game
	        image = (ImageView)findViewById(R.id.mainMenuImage);
	        
	        //Рисуем на канве (взято из RayTracer)
	        Bitmap workingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flappybird_big);
	        bitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
	        canvas = new Canvas(bitmap);  
	
	        int w = canvas.getWidth();
	        int h = canvas.getHeight();
	        Log.i(TAG, w + "x" + h);
	
	        final Paint p = new Paint();   
	        p.setAntiAlias(true);  
	        p.setStyle(Paint.Style.FILL_AND_STROKE);  
	        p.setColor(Color.WHITE);
	        p.setTextSize(w/27);
	        String str = "Пожалуйста, подождите. Загружается словарь...";
			int xw = (int)p.measureText(str);
	        canvas.drawText(str, w/2-xw/2, 3*h/5, p);
	        image.setImageBitmap(bitmap);
	
			image.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					if(dictionaryLoaded)
					{
						setContentView(layout);
					}
				}
	        });
	    }

        Log.i(TAG, "onCreate.done");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        //saving game state
	    	Log.w(TAG, "KEY BACK PRESSED!");
	    	
	    	if(gameLogic.ActiveScreen() instanceof GameScreen)
	    	{
	    		gameLogic.SetActiveScreen("Menu");
	    		return super.onKeyDown(0, event);
	    	}
	    }
	    if (keyCode == KeyEvent.KEYCODE_HOME) {
	        //что-то делаем: завершаем Activity, открываем другую и т.д.
	    	Log.w(TAG, "KEY HOME PRESSED!");
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
	
	
	public void OnLoadDictionary()
	{
		Log.i(TAG, "Dictionary loaded");
		//gameLogic.SetDictionary(wordsDictionary);
		
		//повторяем фон, чтобы затереть предыдущую надпись
		Bitmap workingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flappybird_big);
        bitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(bitmap);  
        
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        final Paint p = new Paint();   
        p.setAntiAlias(true);  
        p.setStyle(Paint.Style.FILL_AND_STROKE);  
        p.setColor(Color.WHITE);
        p.setTextSize(w/20);
        String str = "Словарь загружен!";
		int xw = (int)p.measureText(str);
        canvas.drawText(str, w/2-xw/2, 3*h/5, p);
        image.setImageBitmap(bitmap);
		
		dictionaryLoaded = true;
	}
	
	class MyTask extends AsyncTask<Void, Void, Void> {

	    @Override
	    protected void onPreExecute() {
	      super.onPreExecute();
			Log.i(TAG, "onPreExecute");
	    }

	    @Override
	    protected Void doInBackground(Void... params) {
			Log.i(TAG, "Dictionary loading...");
			
	      return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	      super.onPostExecute(result);
	      OnLoadDictionary();			
	      Log.i(TAG, "Dictionary loaded!");
	    }
	  }
}
