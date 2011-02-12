package edu.cmu.ri.createlab.brainlink.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class RouteActivity extends Activity{
	
	String[] vData = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.act_route);
		vData = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i"};
		 
		ArrayAdapter aas = new ArrayAdapter( this
                , android.R.layout.simple_list_item_1
                , vData
                );
		
		GridView gv = (GridView)findViewById(R.id.GridView);
		
		gv.setNumColumns(4);
		gv.setAdapter(aas);
		
		gv.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
	}

}
