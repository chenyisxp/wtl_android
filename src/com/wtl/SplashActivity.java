package com.wtl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SplashActivity extends Activity{
	 private static final int sleepTime = 3000;
	  public void onCreate(Bundle arg0) {
		  final View view = View.inflate(this, R.layout.activity_splash, null);  
         setContentView(view);  
         Log.v("btnbtnididi", "11111111");
         super.onCreate(arg0);  
	  }
	  protected void onStart(){
		  super.onStart();
		  new Thread(new Runnable() { 
		      public void run() {    
		        long start = System.currentTimeMillis(); 
		        long costTime = System.currentTimeMillis() - start; 
		          if (sleepTime - costTime > 0) { 
		            try { 
		              Thread.sleep(sleepTime - costTime); 
		            } catch (InterruptedException e) { 
		              e.printStackTrace(); 
		            } 
		          } 
		          startActivity(new Intent(SplashActivity.this, MainActivity.class)); 
		          finish(); 
		      } 
		    }).start(); 
	  }
}
