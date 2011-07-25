package com.downloadfacebook;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    private ArrayList<Bitmap> FRIENDPICS = new ArrayList<Bitmap>();

    public ImageAdapter(Context c, ArrayList<Bitmap> fRIENDPICS_bitmaps) {
        mContext = c;
        FRIENDPICS = fRIENDPICS_bitmaps;
    }

    public int getCount() {
        return FRIENDPICS.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = new ImageView(mContext);
        if(position>=FRIENDPICS.size()) {
        	i.setImageResource(R.drawable.abc);
        }
        else {
	        i.setImageBitmap(FRIENDPICS.get(position));
	        i.setLayoutParams(new Gallery.LayoutParams(300, 300));
	        i.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        return i;
    }
}