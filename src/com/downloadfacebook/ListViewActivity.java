package com.downloadfacebook;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class ListViewActivity extends Activity {

	//Your member variable declaration here
    private ArrayList<String> FRIENDSorALBUMS = new ArrayList<String>();
    private ArrayList<String> IDS = new ArrayList<String>();
    boolean isMe;
    boolean isFriendView;
	boolean isAlbumView;
	String FriendID, myID;
	Facebook fb = Session.restore(ListViewActivity.this).getFb();
    
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
	  FriendID = getIntent().getExtras().getString("FriendID");
		
	  this.setContentView(R.layout.coverview);

	  final ArrayAdapter<String> adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FRIENDSorALBUMS);
	  
	  ListView lv = (ListView) findViewById(R.id.list);
	  
	  ((ListView) lv).setAdapter(adap);
	  lv.setTextFilterEnabled(true);
	  
	  if(isMe){
		  String str[] = getMyInfo().split(",");	
		  TextView tv1 = (TextView) findViewById(R.id.left_wrap);
		  tv1.setText(str[0]);
		  TextView tv2 = (TextView) findViewById(R.id.right_fill);
		  tv2.setText(str[1]);
		  TextView tv3 = (TextView) findViewById(R.id.right_fill2);
		  tv3.setText(str[2]);			
		  String pictureURL = getProfilePicture();
		  Log.d("test", pictureURL);
		  ImageView iv = (ImageView) findViewById(R.id.icon);
		  Bitmap b1 = ImageUtilities.load(pictureURL).bitmap;
		  b1 = ImageHelper.getRoundedCornerBitmap(b1, 10);
		  iv.setImageBitmap(b1);
		  FRIENDSorALBUMS.add("My Albums");
		  FRIENDSorALBUMS.add("My Friends");
	  }

	  else if(isFriendView){
		  Log.d("friend", FriendID);
		  Log.d("friends data", getFriendInfo(FriendID));
		  String str[] = getFriendInfo(FriendID).split(",");	
		  if (str.length!=3) {
			  TextView tv1 = (TextView) findViewById(R.id.left_wrap);
			  tv1.setText(str[0]);
			  TextView tv2 = (TextView) findViewById(R.id.right_fill);
			  tv2.setText("");
			  TextView tv3 = (TextView) findViewById(R.id.right_fill2);
			  tv3.setText("");	
		  }
		  else{
			  TextView tv1 = (TextView) findViewById(R.id.left_wrap);
			  tv1.setText(str[0]);
			  TextView tv2 = (TextView) findViewById(R.id.right_fill);
			  tv2.setText(str[1]);
			  TextView tv3 = (TextView) findViewById(R.id.right_fill2);
			  tv3.setText(str[2]);	
		  }
		  String pictureURL = getFriendsProfilePicture(FriendID);
		  ImageView iv = (ImageView) findViewById(R.id.icon);
		  Bitmap b1 = ImageUtilities.load(pictureURL).bitmap;
		  b1 = ImageHelper.getRoundedCornerBitmap(b1, 10);
		  iv.setImageBitmap(b1);
		  FRIENDSorALBUMS.add("My Albums");
	  }
	  else if(isAlbumView){
		  setAlbums(adap, FriendID);
	  }
	  
      lv.setOnItemClickListener(new OnItemClickListener() {
	  public void onItemClick(AdapterView<?> parent, View view,
	        int position, long id) {
		  
  		Intent myIntent = null;
		Bundle bundle = new Bundle();
		if(isMe){
			if(position == 0){
				myIntent = new Intent(ListViewActivity.this, FriendViewActivity.class);
				myID = getMyId();
				bundle.putString("FriendID", myID);
				bundle.putBoolean("isMe", false);
	    		bundle.putBoolean("isFriendView", false);
	    		bundle.putBoolean("isAlbumView", true);
			}
			else if (position == 1){
				myIntent = new Intent(ListViewActivity.this, FriendViewActivity.class);
				bundle.putBoolean("isMe", false);
	    		bundle.putBoolean("isFriendView", true);
	    		bundle.putBoolean("isAlbumView", false);
			}
		}
		else if(isFriendView){
			myIntent = new Intent(ListViewActivity.this, FriendViewActivity.class);
			bundle.putString("FriendID", FriendID);
			bundle.putBoolean("isMe", false);
    		bundle.putBoolean("isFriendView", false);
    		bundle.putBoolean("isAlbumView", true);
		}
		else if(isAlbumView){
			myIntent = new Intent(ListViewActivity.this, GridViewActivity.class);
			bundle.putString("AlbumID", IDS.get(position));
		}

		//Add this bundle to the intent
		myIntent.putExtras(bundle);
		ListViewActivity.this.startActivity(myIntent);
	    }
	  });
	
}


	private String getProfilePicture(){
		Bundle bl = new Bundle();
		bl.putString("fields", "picture");
		bl.putString("type", "large");
		String response = null;
		JSONObject obj = null;
		try {
			response = fb.request("me", bl); 
			try {
				obj = Util.parseJson(response);
				 response = obj.getString("picture");
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (FacebookError e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private String getFriendsProfilePicture(String ID){
		Bundle bl = new Bundle();
		bl.putString("fields", "picture");
		bl.putString("type", "large");
		String response = null;
		JSONObject obj = null;
		try {
			response = fb.request(ID, bl); 
			try {
				obj = Util.parseJson(response);
				 response = obj.getString("picture");
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (FacebookError e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private String getMyInfo(){
		
		Bundle bu = new Bundle();
		bu.putString("fields", "name,location");
		String response = null;
		JSONObject obj = null;
		  try {
			response = fb.request("me", bu);
			Log.d("response", response);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  try {
			obj = Util.parseJson(response);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FacebookError e) {
			e.printStackTrace();
		}
		String name = "";
		String city = "";
		String country = "";
			try {
				JSONObject arr = (obj.getJSONObject("location"));
				String location = arr.getString("name");
				String[] str = location.split(",");
				city = str[0];
				country = str[1];
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				name = obj.getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return name+","+ city +","+country;

	}

	private String getMyId(){
		
		Bundle bu = new Bundle();
		bu.putString("fields", "id");
		String response = null;
		JSONObject obj = null;
		  try {
			response = fb.request("me", bu);
			Log.d("response", response);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  try {
			obj = Util.parseJson(response);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FacebookError e) {
			e.printStackTrace();
		}
		String id = "";
			try {
				id = obj.getString("id");
				} catch (JSONException e) {
				e.printStackTrace();
			}
			return id;

	}

	private String getFriendInfo(String ID) {

		Bundle bu = new Bundle();
		bu.putString("fields", "name,location");
		String response = null;
		JSONObject obj = null;
		  try {
			response = fb.request(ID, bu);
			Log.d("response", response);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  try {
			obj = Util.parseJson(response);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FacebookError e) {
			e.printStackTrace();
		}
		String name = "";
		String city = "";
		String country = "";
		try {
			name = obj.getString("name");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
			try {
				JSONObject arr = (obj.getJSONObject("location"));
				String location = arr.getString("name");
				String[] str = location.split(",");
				city = str[0];
				country = str[1];
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return name+","+ city +","+country;
		
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
	            ListViewActivity.this.runOnUiThread(new Runnable() {
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
	            ListViewActivity.this.runOnUiThread(new Runnable() {
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