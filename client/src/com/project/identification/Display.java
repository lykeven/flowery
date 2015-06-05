package com.project.identification;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.Gallery.LayoutParams;

public class Display  extends Activity implements
AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
	private TextView Name;
    private ImageSwitcher mySwitcher;
	private Integer[] myThmIds = {
            R.drawable.meihua_sw, R.drawable.orchid_sw, R.drawable.juhua_sw,
            R.drawable.taohua_sw, R.drawable.rose_sw,R.drawable.yueji_sw,
            R.drawable.sunflower_sw,R.drawable.lihua_sw,R.drawable.baihe_sw,
            R.drawable.tulip_sw,R.drawable.shuixian_sw,R.drawable.moli_sw,
            R.drawable.haitang_sw,R.drawable.yinghua_sw};
    private Integer[] myImgIds = {
            R.drawable.meihua, R.drawable.orchid, R.drawable.juhua,
            R.drawable.taohua, R.drawable.rose,R.drawable.yueji,
            R.drawable.sunflower,R.drawable.lihua,R.drawable.baihe,
            R.drawable.tulip,R.drawable.shuixian,R.drawable.moli,
            R.drawable.haitang,R.drawable.yinghua};
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //设置这个Activity没有标题栏
        setContentView(R.layout.display);
        
        Name=(TextView)findViewById(R.id.name);
       
        mySwitcher = (ImageSwitcher) findViewById(R.id.switcher);
        mySwitcher.setFactory(this);
        mySwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        mySwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));

        Gallery g = (Gallery) findViewById(R.id.gallery);
        g.setAdapter(new ImageAdapter(this));
        g.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
    	CharSequence content[]={"梅花","兰花","菊花","桃花","玫瑰","月季","向日葵","梨花","百合","郁金香","水仙花","茉莉","海棠","樱花"};
    	Name.setText(content[(int)id]);
    	mySwitcher.setImageResource(myImgIds[position]);  //为mSwitcher设选中的图片资源
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public View makeView() { 
        ImageView iv = new ImageView(this);
       // iv.setBackgroundColor(0xFF000000);   //设置背景颜色
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);  //
        iv.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        return iv;
    }

    //自定义ImageAdapter适配器
    public class ImageAdapter extends BaseAdapter {
        private Context myContext;
        public ImageAdapter(Context c) { myContext = c; }
        public int getCount() { return myThmIds.length; }
        public Object getItem(int position) { return position; }
        public long getItemId(int position) { return position; }
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(myContext);
            i.setImageResource(myThmIds[position]);
            i.setAdjustViewBounds(true);
            i.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            return i;
        }
    }
 
}