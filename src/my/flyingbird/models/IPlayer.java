package my.flyingbird.models;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public interface IPlayer {
	
	String GetName();
	int Score();
	void SetScore(int value);
	void SetX(int value);
	void SetY(int value);
	int GetX();
	int GetY();
	void draw(Canvas canvas);
	Bitmap Icon();
}
