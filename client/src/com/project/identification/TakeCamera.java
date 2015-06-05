package com.project.identification;

import java.io.*;
import java.util.*;

import com.project.identification.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.net.Uri;
import android.widget.*;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;



@SuppressLint("NewApi")
public class  TakeCamera extends Activity {

	private Button btn_take, btn_select;
	private LinearLayout ll_show;

	// 请求
	private static final int CAMERA_TAKE = 1;
	private static final int CAMERA_SELECT = 2;

	// 图片名
	public String name;
	public String img_path;
	// 存储路径
	private static  String PATH = Environment.getExternalStorageDirectory() + "/DCIM";
	Uri imgUri=null;
	private boolean isBig = false;
	
	private boolean isSelect = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo);

		btn_take = (Button) findViewById(R.id.btn_take);
		btn_select = (Button) findViewById(R.id.btn_select);
		ll_show = (LinearLayout) findViewById(R.id.ll_show);

		btn_take.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				takePhoto();
			}
		});

		btn_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, CAMERA_SELECT);
			}
		});

	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			Bitmap bitmap =null;
			
			if (requestCode==CAMERA_TAKE) 
			{
				isSelect=false;
				bitmap = BitmapFactory.decodeFile(PATH + "/" + name);
				Toast.makeText(this, name, Toast.LENGTH_LONG).show();
				//System.out.println(bitmap.getHeight() + "======"+ bitmap.getWidth());
			}
			else
			{
				ContentResolver resolver = getContentResolver();
				// 照片的原始资源地址
				imgUri = data.getData();
				isSelect=true;
				img_path=getImagePath(imgUri);
				
				//System.out.println("imgUri"+imgUri.toString());
				//System.out.println("img_path:"+img_path);
				 try {
					bitmap = MediaStore.Images.Media.getBitmap(resolver,imgUri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

				// 获取屏幕分辨率
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);

				// 图片分辨率与屏幕分辨率
				float scale = bitmap.getWidth() / (float) dm.widthPixels;

				Bitmap newBitMap = null;
				if (scale > 1) {
					newBitMap = zoomBitmap(bitmap, bitmap.getWidth() / scale,
							bitmap.getHeight() / scale);
					bitmap.recycle();
					isBig = true;
				}
				//添加relative布局放置img，btn
				final RelativeLayout rl_show_2 = new RelativeLayout(this);
				rl_show_2.setLayoutParams(new LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT));

				ImageButton imgBtn_del_2 = new ImageButton(this);
				imgBtn_del_2.setBackgroundResource(android.R.drawable.ic_delete);
				
				//上传按钮的布局规则	
				Button imgUpLoad=new Button(this);
				imgUpLoad.setText("上传");
				imgUpLoad.setId(3);
				RelativeLayout.LayoutParams rl_1 = new RelativeLayout.LayoutParams(
						new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
								RelativeLayout.LayoutParams.WRAP_CONTENT));
				rl_1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				rl_1.addRule(RelativeLayout.ALIGN_LEFT, 2);
				
				
				//设置按钮的布局规则 
				RelativeLayout.LayoutParams rl_2 = new RelativeLayout.LayoutParams(
						new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
								RelativeLayout.LayoutParams.WRAP_CONTENT));
				rl_2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				rl_2.addRule(RelativeLayout.ALIGN_RIGHT, 2);
				
				
				// 将图片显示到界面
				ImageView img = new ImageView(this);
				img.setId(2);
				img.setLayoutParams(new LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
				img.setScaleType(ImageView.ScaleType.CENTER_CROP);
				img.setPadding(2, 0, 0, 5);
				if (isBig) {
					img.setImageBitmap(newBitMap);
					isBig = false;
				} else
					img.setImageBitmap(bitmap);
				
				rl_show_2.addView(img);
				rl_show_2.addView(imgBtn_del_2,rl_2);
				rl_show_2.addView(imgUpLoad,rl_1);
				
				
				ll_show.addView(rl_show_2);
				
				imgBtn_del_2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ll_show.removeView(rl_show_2);
					}
				});
				
				imgUpLoad.setOnClickListener(new OnClickListener(){
					public void onClick(View v)
					{
			
						new AlertDialog.Builder(TakeCamera.this).setTitle("uploading....")
						.setMessage("正在上传") .setPositiveButton("确定", null) .show();  
						MyThread ms=new MyThread("2");
						 ms.start();
						
					}
				});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//网络通信线程
	class MyThread extends Thread {  
			  
	        public String txt1;  
	        public MyThread(String str) {  
	            txt1 = str;  
	        }  
	  
	        @Override  
	        public void run() {  
	        	try
	    		{
	        		Log.i("out","upload begin");
	        		HttpAssist ht=new HttpAssist();
	        		File myfile;
	        		if(isSelect)
	        			myfile=new File(img_path);
	        		else
	        		   myfile=new File(PATH,name);
	        		String ans=ht.uploadFile(myfile);
	        		Log.i("out","ans:"+ans);
	        		ans=ans+"|"+imgUri.toString();
	        		startResult(ans);
	    		}
	    		catch(Exception e)
	    		{
	    			e.printStackTrace(); 
	    		}
	        }  
	    }  
	
	/**
	 * 调用系统相机
	 */
	public void takePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机
		new DateFormat();
		name = DateFormat.format("yyyyMMdd_hhmmss",
				Calendar.getInstance(Locale.CHINA))	+ ".jpg";
		 imgUri = Uri.fromFile(new File(PATH, name));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);

		startActivityForResult(intent, CAMERA_TAKE);
	}

	
	public void startResult(String ans) {  
		
       Intent intent = new Intent(TakeCamera.this,GetInfo.class);
       intent.putExtra("ans",ans);
      	startActivity(intent);
		//this.finish();
      }	
	
	
	public String getImagePath(Uri uri){
		
		   Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		   cursor.moveToFirst();
		   String document_id = cursor.getString(0);
		   document_id = document_id.substring(document_id.lastIndexOf(":")+1);
		   cursor.close();

		   cursor = getContentResolver().query( 
		   android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		   null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
		   cursor.moveToFirst();
		   String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
		   cursor.close();

		   return path;
		}
	// 对分辨率较大的图片进行缩放
	public Bitmap zoomBitmap(Bitmap bitmap, float width, float height) {

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);

		matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

		return newbmp;

	}


}