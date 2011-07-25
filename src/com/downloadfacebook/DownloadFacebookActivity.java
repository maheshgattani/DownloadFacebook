package com.downloadfacebook;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class DownloadFacebookActivity extends Activity {
    /** Called when the activity is first created. */
	
	public static final String FB_APP_ID = "219138891459416";
    // The permissions that the app should request from the user
    // when the user authorizes the app.
    private static String[] PERMISSIONS = 
        new String[] { "offline_access", "read_stream", "friends_location", "publish_stream", "user_photos", "friends_photos", "user_photo_video_tags", "friends_photo_video_tags" };

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        if (FB_APP_ID == null) {
            Builder alertBuilder = new Builder(this);
            alertBuilder.setTitle("Warning");
            alertBuilder.setMessage("A Facebook Applicaton ID must be " +
                    "specified before running this example: see App.java");
            alertBuilder.create().show();
        }
        
        this.setContentView(R.layout.login);
        
        Button button = (Button) findViewById(R.id.id_button);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                final Facebook fb = new Facebook(DownloadFacebookActivity.FB_APP_ID);
                Session.waitForAuthCallback(fb);
                fb.authorize(DownloadFacebookActivity.this, PERMISSIONS,
                             new AppLoginListener(fb));
            }
        });
        
    }
    
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
    	Facebook fb = Session.wakeupForAuthCallback();
    	fb.authorizeCallback(requestCode, resultCode, data);
    }

    private class AppLoginListener implements DialogListener {

        private Facebook fb;

        public AppLoginListener(Facebook fb) {
            this.fb = fb;
        }

        public void onCancel() {
            Log.d("app", "login canceled");
        }

        public void onComplete(Bundle values) {
            /**
             * We request the user's info so we can cache it locally and
             * use it to render the new html snippets
             * when the user updates her status or comments on a post. 
             */
            new AsyncFacebookRunner(fb).request("/me", 
                    new AsyncRequestListener() {
                public void onComplete(JSONObject obj, final Object state) {
                    // save the session data
                    String uid = obj.optString("id");
                    String name = obj.optString("name");
                    new Session(fb, uid, name).save(DownloadFacebookActivity.this);
                	
                    DownloadFacebookActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                        	Intent myIntent = new Intent(DownloadFacebookActivity.this, ListViewActivity.class);
                    		Bundle bundle = new Bundle();
                    		bundle.putBoolean("isFriendView", false);
                    		bundle.putBoolean("isAlbumView", false);
                    		bundle.putBoolean("isMe", true);
                    		bundle.putString("FriendID","");
                    		myIntent.putExtras(bundle);
                    		DownloadFacebookActivity.this.startActivity(myIntent);                            	
                        }
                    });
                }
            }, null);
        }

        public void onError(DialogError e) {
            Log.d("app", "dialog error: " + e);               
        }

        public void onFacebookError(FacebookError e) {
            Log.d("app", "facebook error: " + e);
        }
    }

}