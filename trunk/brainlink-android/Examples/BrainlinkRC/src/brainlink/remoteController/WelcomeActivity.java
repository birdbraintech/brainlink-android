package brainlink.remoteController;

import brainlink.remoteController.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity{
	
	private final int SPLASH_DISPLAY_LENGTH = 3000;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		requestWindowFeature(Window.FEATURE_NO_TITLE);

        
        setContentView(R.layout.act_welcome);
        
        new Handler().postDelayed(new Runnable(){  
        	   
            @Override  
            public void run() {  
                Intent mainIntent = new Intent(WelcomeActivity.this,MainActivity.class);  
                WelcomeActivity.this.startActivity(mainIntent);  
                    WelcomeActivity.this.finish();  
            }  
                
           }, SPLASH_DISPLAY_LENGTH);  
    }
}
