package com.example.sotiria.sightsar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sot on 3/2/2018.
 */

public class Customadapter extends ArrayAdapter<Product> {
    private String[] urls;
    private Bitmap[] bitmaps;
    ArrayList<Product> products;
    Context context;
    int resource;

    public Customadapter(@NonNull Context context, int resource, @NonNull ArrayList<Product> products) {
        super(context, resource, products);
        this.context=context;
        this.products=products;
        this.resource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
if (convertView==null) {
    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    convertView = inflater.inflate(R.layout.gallery_list, null, true);
}
    Product product= getItem(position);
    ImageView image = (ImageView) convertView.findViewById(R.id.imgvw);
        Picasso.with(context).load(product.getImage()).into(image);
        return  convertView;
    }
}
