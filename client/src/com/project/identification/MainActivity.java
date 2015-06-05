package com.project.identification;

import com.project.identification.MainActivity;
import com.project.identification.R;
import com.project.identification.TakeCamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.app.TabActivity;
public class MainActivity extends TabActivity implements OnTabChangeListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 TabHost tabHost =getTabHost();
		
		Intent photo = new Intent(this,TakeCamera.class);
		Intent itNormal = new Intent(this,NormalActivity.class);
		Intent itDisplay = new Intent(this,Display.class);
				
		tabHost.addTab(tabHost.newTabSpec("tab1")
				.setIndicator(getResources().getText(R.string.tab_home),
						getResources().getDrawable(R.drawable.home))
				.setContent(itNormal)
				);
		tabHost.addTab(tabHost.newTabSpec("tab2")
				.setIndicator(getResources().getText(R.string.take ),
						getResources().getDrawable(R.drawable.photo ))
				.setContent(photo)
				);
		tabHost.addTab(tabHost.newTabSpec("tab3")
				.setIndicator(getResources().getText(R.string.display),
						getResources().getDrawable(R.drawable.display ))
				.setContent(itDisplay)
				);
		
	}

	@Override
	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub
		 if(arg0=="tab3") 
             this.getTabHost().setCurrentTabByTag(arg0);  
     
	}

	
}
