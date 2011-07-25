package com.downloadfacebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AlbumViewActivity extends Activity {
	
	//Your member variable declaration here
    private ArrayList<String> FRIENDSorALBUMS = new ArrayList<String>();
    private ArrayList<String> IDS = new ArrayList<String>();
    boolean isMe;
    boolean isFriendView;
	boolean isAlbumView;
	String FriendID;
	Facebook fb = Session.restore(AlbumViewActivity.this).getFb();
    
	// Called when the activity is first created.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	  super.onCreate(savedInstanceState); 
      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
      isMe = getIntent().getExtras().getBoolean("isMe");
	  isFriendView = getIntent().getExtras().getBoolean("isFriendView");
	  isAlbumView = getIntent().getExtras().getBoolean("isAlbumView");
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
              WindowManager.LayoutParams.FLAG_FULLSCREEN);

	  if(isAlbumView)
		  FriendID = getIntent().getExtras().getString("FriendID");
		
	  this.setContentView(R.layout.friendview);

	  final ArrayAdapter<String> adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FRIENDSorALBUMS);
	  
	  ListView lv = (ListView) findViewById(R.id.list);
	  
	  ((ListView) lv).setAdapter(adap);
	  lv.setTextFilterEnabled(true);
	
	  if(isMe){
		  
	  }
	  else if(isFriendView){
		  setFriends(adap);
	  }
	  else if(isAlbumView){
		  setAlbums(adap, FriendID);
	  }

	}
	
	private void setAlbums(final ArrayAdapter<String> adap, String ID) {
	    new AsyncFacebookRunner(fb).request("/"+ID+"/albums", 
	            new AsyncRequestListener() {
	        public void onComplete(JSONObject obj, final Object state) {
				JSONArray arr = null;
			    try {
			    	arr = obj.getJSONArray("data");
			    } catch (JSONException e) {
			    	e.printStackTrace();
			    }
			    for (int i = 0; i < arr.length(); i++){
			     	JSONObject ob = null;
			    	try {
			    		ob = arr.getJSONObject(i);
			    	} catch (JSONException e) {
			    		e.printStackTrace();
			    	}
			    	try {
			    		IDS.add(ob.getString("id"));
			    	} catch (JSONException e) {
			    		e.printStackTrace();
			    	}
			    	try {
			    		FRIENDSorALBUMS.add(ob.getString("name")+" ("+ob.getString("count")+")");
			    	} catch (JSONException e) {
			    		e.printStackTrace();
			    	}
		            AlbumViewActivity.this.runOnUiThread(new Runnable() {
		                public void run() {
		                	adap.notifyDataSetChanged();
		                }
		            });
			    	
			    }
	        }
	    }, null);
			
	}

	public void setFriends(final ArrayAdapter<String> adap){
	    new AsyncFacebookRunner(fb).request("/me/friends", 
	            new AsyncRequestListener() {
	        public void onComplete(JSONObject obj, final Object state) {
			      JSONArray arr = null;
			      try {
			      	arr = obj.getJSONArray("data");
			      } catch (JSONException e) {
			      	e.printStackTrace();
			      }
			      for (int i = 0; i < arr.length(); i++){
			       	JSONObject ob = null;
			      	try {
			      		ob = arr.getJSONObject(i);
			      	} catch (JSONException e) {
			      		e.printStackTrace();
			      	}
			      	try {
			      		IDS.add(ob.getString("id"));
			      	} catch (JSONException e) {
			      		e.printStackTrace();
			      	}
			      	try {
			      		FRIENDSorALBUMS.add(ob.getString("name"));
			      	} catch (JSONException e) {
			      		e.printStackTrace();
			      	}
		            AlbumViewActivity.this.runOnUiThread(new Runnable() {
		                public void run() {
		                	adap.notifyDataSetChanged();
		                }
		            });
		            
			      }
			        
	        }
			    
	    }, null);
			
	}

	public Object fetch(String address) throws MalformedURLException,IOException {
	    URL url = new URL(address);
	    Object content = url.getContent();
	    return content;
	}

}
