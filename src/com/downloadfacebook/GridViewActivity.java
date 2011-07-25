package com.downloadfacebook;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class GridViewActivity extends Activity {

    private ArrayList<String> ImageIDs = new ArrayList<String>();
    private ArrayList<Drawable> ImageURL_s = new ArrayList<Drawable>();
	Facebook fb = Session.restore(GridViewActivity.this).getFb();

    boolean nextIsImage;
    String AlbumID;
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
	super.onCreate(savedInstanceState);

    getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	AlbumID = getIntent().getExtras().getString("AlbumID");
	  
	this.setContentView(R.layout.grid);
    GridView gridview = (GridView) this.findViewById(R.id.gridview);
    gridview.setBackgroundResource(R.drawable.screenshotlarge);
    AlbumGrid ag = new AlbumGrid(this, ImageURL_s);
    gridview.setAdapter(ag);

    fillImageDrawables(ag, AlbumID);

    gridview.setOnItemClickListener(new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        		Intent myIntent = new Intent(GridViewActivity.this, ImageViewerActivity.class);
        		Bundle bundle = new Bundle();
        		bundle.putStringArrayList("images", ImageIDs);
        		bundle.putInt("position", position);
        		//Add this bundle to the intent
        		myIntent.putExtras(bundle);
        		GridViewActivity.this.startActivity(myIntent);       	
        	}
    });
    }

    private void fillImageDrawables(final AlbumGrid ag, String albumID) {
		Bundle bu = new Bundle();
		bu.putString("limit", "500");
        new AsyncFacebookRunner(fb).request("/"+albumID+"/photos", bu, 
                new AsyncRequestListener() {
            public void onComplete(JSONObject obj, final Object state) {
            	JSONArray arr = null;
		        try {
		        	arr = obj.getJSONArray("data");
		        } catch (JSONException e) {
		        	e.printStackTrace();
		        }
		        Log.d("arr size", ""+arr.length());
		        for (int i = 0; i < arr.length(); i++){
		         	JSONObject ob = null;
		        	try {
		        		ob = arr.getJSONObject(i);		        		
		        	} catch (JSONException e) {
		        		e.printStackTrace();
		        	}
		        	try {
		        		ImageURL_s.add(ImageOperations(ob.getString("picture")));
		        		ImageIDs.add(ob.getString("id"));
		        	} catch (JSONException e) {
		        		e.printStackTrace();
		        	}
		        	
		            GridViewActivity.this.runOnUiThread(new Runnable() {
		                public void run() {
		    	        	ag.notifyDataSetChanged();
		                }
		            });
		        }
  	
            }
        }, null);
	}

	private Drawable ImageOperations(String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    
    public Object fetch(String address) throws MalformedURLException,IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }
    
}