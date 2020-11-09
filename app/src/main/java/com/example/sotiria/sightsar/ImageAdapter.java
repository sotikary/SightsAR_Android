package com.example.sotiria.sightsar;

/**
 * Created by sotiria on 26-Oct-17.
 */

       import android.content.Context;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.ViewGroup.LayoutParams;
        import android.widget.BaseAdapter;
        import android.widget.Gallery;
        import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] mImageIds;

    public ImageAdapter(Context c, Integer[] mImageIds) {
        this.mImageIds = mImageIds;
        mContext = c;
    }

    @Override
    public int getCount() {

        return mImageIds.length;
    }

    @Override
    public Object getItem(int pos) {

        return mImageIds[pos];
    }

    @Override
    public long getItemId(int pos) {

        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Adding dynamic image simillarly we had added to Image Switcher
        ImageView i = new ImageView(mContext);
        i.setImageResource(mImageIds[position]);
        i.setAdjustViewBounds(true);
        i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        // Setting background resource
        i.setBackgroundResource(mImageIds[0]);

        return i;
    }

}