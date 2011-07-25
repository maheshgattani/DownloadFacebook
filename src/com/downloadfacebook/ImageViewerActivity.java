package com.downloadfacebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.AdapterView.OnItemSelectedListener;

import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class ImageViewerActivity extends Activity {

//Your member variable declaration here
	Facebook fb = Session.restore(ImageViewerActivity.this).getFb();
	String ImageURL = "";
	JSONObject obj = null;
	ArrayList<String> ImageIDs = new ArrayList<String>();
    private ArrayList<Bitmap> FRIENDPICS_bitmaps = new ArrayList<Bitmap>();
    private ArrayList<String> setpics = new ArrayList<String>();
// Called when the activity is first created.
@Override
public void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState);
	
    getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	ImageIDs = getIntent().getExtras().getStringArrayList("images");
	int position = getIntent().getExtras().getInt("position");
	this.setContentView(R.layout.image);
	Gallery image = (Gallery) this.findViewById(R.id.finalimage);
	
	final FinalImageAdapter ia = new FinalImageAdapter(this, FRIENDPICS_bitmaps);
	((Gallery) image).setAdapter(ia);
 
	image.setFadingEdgeLength(0);
	image.setSpacing(1);
	image.setUnselectedAlpha(0); 
	setpics(ia);
	image.setSelection(position);
	
	image.setOnItemSelectedListener ( new OnItemSelectedListener(){
		    @Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

		    	for(int c = arg2; c <= arg2 ; c++){
		        	if(c>=ImageIDs.size() || c < 0) continue;
			        String imageurl = ImageIDs.get(c);
			        
			        if(!setpics.contains(""+c)){
				    	try {
				    		String response = fb.request("/"+imageurl);
				    		JSONObject obj = Util.parseJson(response);
				    		ImageURL = obj.getString("source");
				    	} catch (MalformedURLException e) {
				    		e.printStackTrace();
				    	} catch (IOException e) {
				    		e.printStackTrace();
				    	} catch (JSONException e) {
				    		e.printStackTrace();
				    	} catch (FacebookError e) {
				    		e.printStackTrace();
				    	}
				    	
					Bitmap b1 = ImageUtilities.load(ImageURL).bitmap;
					FRIENDPICS_bitmaps.set(c, b1);
					ia.notifyDataSetChanged();
					setpics.add(c+"");
			        }
		     }
		    }
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
	  }
	);
	  	
}


private void setpics(final FinalImageAdapter ia){
			Bitmap bitmapfile = BitmapFactory.decodeResource(ImageViewerActivity.this.getResources(),
                    R.drawable.abc);
			for (int i = 0; i < ImageIDs.size(); i++){
					FRIENDPICS_bitmaps.add(bitmapfile);
					ia.notifyDataSetChanged();
			}
}

public Object fetch(String address) throws MalformedURLException,IOException {
    URL url = new URL(address);
    Object content = url.getContent();
    return content;
}


}