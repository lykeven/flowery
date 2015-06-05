package com.project.identification;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class GetInfo extends Activity{
	
	TextView ans;
	TextView detailans;
	ImageView image;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		
		Intent intent =getIntent();
		Bundle bundle =intent.getExtras();
		String str=bundle.getString("ans");
		
		int index=str.indexOf("&");
		if(index!=-1)
		{
			String name=str.substring(0, index);
			String detail=str.substring(index+1, str.length());
			int index2=detail.indexOf("|");
			if(index2!=-1)
			{
				String flowerdetail=detail.substring(0, index2);
				String imguri=detail.substring(index2+1, detail.length());
				Log.i("out","ans:"+imguri);
				Uri uri=Uri.parse(imguri);
				ans=(TextView)findViewById(R.id.showans);
				detailans=(TextView)findViewById(R.id.showdetatilans);
				image=(ImageView)findViewById(R.id.imageView1);
				ans.setText(name);
				detailans.setText(flowerdetail);
				image.setImageURI(uri);
			}
		
			
		}
		else
		{
			ans=(TextView)findViewById(R.id.showans);
			ans.setText(str);
		}
	
	}
}
