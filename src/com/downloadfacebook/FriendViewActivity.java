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
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ListView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class FriendViewActivity extends Activity {

	//Your member variable declaration here
    private ArrayList<String> FRIENDSorALBUMS = new ArrayList<String>();
    private ArrayList<String> IDS = new ArrayList<String>();
    private ArrayList<String> FRIENDPICS = new ArrayList<String>();
    private ArrayList<Bitmap> FRIENDPICS_bitmaps = new ArrayList<Bitmap>();
    private ArrayList<String> setpics = new ArrayList<String>();
    boolean isFriendView;
	boolean isAlbumView;
	String FriendID;
	int lengthList = 0;
	Facebook fb = Session.restore(FriendViewActivity.this).getFb();
    
	// Called when the activity is first created.
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	  super.onCreate(savedInstanceState); 
      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	  isFriendView = getIntent().getExtras().getBoolean("isFriendView");
	  isAlbumView = getIntent().getExtras().getBoolean("isAlbumView");
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
              WindowManager.LayoutParams.FLAG_FULLSCREEN);
	  FriendID = getIntent().getExtras().getString("FriendID");
		
	  this.setContentView(R.layout.friendview);

	  final ArrayAdapter<String> adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FRIENDSorALBUMS);
	  final ListView lv = (ListView) findViewById(R.id.list_friends);
	  ((ListView) lv).setAdapter(adap);
	  lv.setTextFilterEnabled(true);


	  final Gallery g = (Gallery) findViewById(R.id.gallery);
	  
	  final ImageAdapter ia = new ImageAdapter(this, FRIENDPICS_bitmaps);
	  ((Gallery) g).setAdapter(ia);

	  g.setFadingEdgeLength(0);
	  g.setUnselectedAlpha((float) 0.65); 
		
	  if(isFriendView){
		  setFriends(adap);
		  getProfilePictures( ia );
	  }
	  else if(isAlbumView){
		  getAlbumPictures( ia , FriendID);
		  setAlbumNames( adap , FriendID);
	  }
	  
	  g.setOnItemSelectedListener ( new OnItemSelectedListener(){
		    @Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

		    	for(int c = arg2-1; c <= arg2+1 ; c++){
		        	if(c>=FRIENDPICS.size() || c < 0) continue;
			        String imageurl = FRIENDPICS.get(c);
			        if(!setpics.contains(""+c)){
					Bitmap b1 = ImageUtilities.load(imageurl).bitmap;
					b1 = ImageHelper.getRoundedCornerBitmap(b1, 10);
					FRIENDPICS_bitmaps.set(c, b1);
			        ia.notifyDataSetChanged();
					setpics.add(c+"");
			        }
		        lv.setSelection(arg2);
				
			}
		    }
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
	  }
	);
	  
	lv.setOnScrollListener(new OnScrollListener(){
	    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	    }
	    public void onScrollStateChanged(AbsListView view, int scrollState) {
	      if(scrollState == 0) {
	    	  g.setSelection(lv.getFirstVisiblePosition());
	      }
	    }
	  });
	
    lv.setOnItemClickListener(new OnItemClickListener() {
  	  public void onItemClick(AdapterView<?> parent, View view,
  	        int position, long id) {
  		  
    		Intent myIntent = null;
  		Bundle bundle = new Bundle();
  		
  		if(isFriendView){
  			myIntent = new Intent(FriendViewActivity.this, ListViewActivity.class);
  			bundle.putString("FriendID", IDS.get(position));
  			bundle.putBoolean("isFriendView", true);
      		bundle.putBoolean("isAlbumView", false);
  		}
  		else if(isAlbumView){
  			myIntent = new Intent(FriendViewActivity.this, GridViewActivity.class);
  			bundle.putString("AlbumID", IDS.get(position));
  		}

  		//Add this bundle to the intent
  		myIntent.putExtras(bundle);
  		FriendViewActivity.this.startActivity(myIntent);
  	    }
  	  });

    g.setOnItemClickListener(new OnItemClickListener() {
    	  public void onItemClick(AdapterView<?> parent, View view,
    	        int position, long id) {
    		  
      		Intent myIntent = null;
    		Bundle bundle = new Bundle();
    		
    		if(isFriendView){
    			myIntent = new Intent(FriendViewActivity.this, ListViewActivity.class);
    			bundle.putString("FriendID", IDS.get(position));
    			bundle.putBoolean("isFriendView", true);
        		bundle.putBoolean("isAlbumView", false);
    		}
    		else if(isAlbumView){
    			myIntent = new Intent(FriendViewActivity.this, GridViewActivity.class);
    			bundle.putString("AlbumID", IDS.get(position));
    		}

    		//Add this bundle to the intent
    		myIntent.putExtras(bundle);
    		FriendViewActivity.this.startActivity(myIntent);
    	    }
    	  });
  }	
	
	private void getProfilePictures(final ImageAdapter ia){
		Bundle bu = new Bundle();
		bu.putString("fields", "picture");
		bu.putString("limit", "2500");
	    new AsyncFacebookRunner(fb).request("/me/friends", bu, new AsyncRequestListener() {
	        public void onComplete(JSONObject obj, final Object state) {
				JSONArray arr = null;
				try {
					arr = obj.getJSONArray("data");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Bitmap bitmapfile = BitmapFactory.decodeResource(FriendViewActivity.this.getResources(),
                        R.drawable.abc);
				for (int i = 0; i < arr.length(); i++){
					try {
						String imageurl = arr.getJSONObject(i).getString("picture").replace("_q.jpg", "_n.jpg");
						FRIENDPICS.add(imageurl);
						FRIENDPICS_bitmaps.add(bitmapfile);	
					} catch (JSONException e) {
						e.printStackTrace();
					}
			        FriendViewActivity.this.runOnUiThread(new Runnable() {
			                public void run() {
			                	ia.notifyDataSetChanged();
			                }
			        	}
			        );
				}
				
			}
	    });
		
	}
	
	private void getAlbumPictures(final ImageAdapter ia, String ID){
		Bundle bu = new Bundle();
		bu.putString("fields", "name");
		bu.putString("limit", "500");
		new AsyncFacebookRunner(fb).request(ID+"/albums", bu, new AsyncRequestListener() {
	        public void onComplete(JSONObject obj, final Object state) {
				JSONArray arr = null;
				try {
					arr = obj.getJSONArray("data");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Bitmap bitmapfile = BitmapFactory.decodeResource(FriendViewActivity.this.getResources(),
                        R.drawable.abc);
				for (int i = 0; i < arr.length(); i++){
					try {
						Bundle bu = new Bundle();
						bu.putString("fields", "picture");
						String picture = null;
						try {
							String str = fb.request(arr.getJSONObject(i).getString("id"), bu);
							JSONObject obj1 = null;
							try {
								obj1 = Util.parseJson(str);
							} catch (FacebookError e) {
								e.printStackTrace();
							}
							picture = obj1.getString("picture");
							FRIENDPICS.add(picture);
							FRIENDPICS_bitmaps.add(bitmapfile);	
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
			        FriendViewActivity.this.runOnUiThread(new Runnable() {
			                public void run() {
			                	ia.notifyDataSetChanged();
			                }
			        	}
			        );
				}
				
			}
	    });
		
	}
	

	public void setFriends(final ArrayAdapter<String> adap){
		Bundle bu = new Bundle();
		bu.putString("limit", "2500");
	    new AsyncFacebookRunner(fb).request("/me/friends",  bu, 
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
		            FriendViewActivity.this.runOnUiThread(new Runnable() {
		                public void run() {
		                	adap.notifyDataSetChanged();
		                }
		            });
		            
			      }
			        
	        }
			    
	    }, null);
			
	}

	public void setAlbumNames(final ArrayAdapter<String> adap, String ID){
		Bundle bu = new Bundle();
		bu.putString("fields", "name,count");
		bu.putString("limit", "500");
	    new AsyncFacebookRunner(fb).request(ID+"/albums", bu,  
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
			      		FRIENDSorALBUMS.add(ob.getString("name")+" ("+ob.getInt("count")+")");
			      		
			      	} catch (JSONException e) {
			      		e.printStackTrace();
			      	}
		            FriendViewActivity.this.runOnUiThread(new Runnable() {
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