package com.android.skripsi.keluarga.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.skripsi.keluarga.Models.Gallery;
import com.android.skripsi.keluarga.R;
import com.koushikdutta.ion.Ion;

import java.util.List;

/**
 * Created by Karen on 10/22/2017.
 */

public class GalleryAdapter extends ArrayAdapter<Gallery> {
    //private final ColorMatrixColorFilter grayscaleFilter;
    private Context mContext;
    private int layoutResourceId;
    private List<Gallery> listItems;

    public GalleryAdapter(Context mContext, int layoutResourceId, List<Gallery> listItems) {
        super(mContext, layoutResourceId, listItems);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.listItems = listItems;
    }


    /**
     * Updates grid data and refresh grid items.
     *
     * @param listItems
     */
    public void setGridData(List<Gallery> listItems) {
        this.listItems = listItems;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Gallery item = listItems.get(position);

        if(item.image.equals("")){
            holder.imageView.setImageResource(R.drawable.noimage);
        }else{
            Ion.with(mContext)
                    .load(item.image)
                    .withBitmap()
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)
                    .intoImageView(holder.imageView);
        }
        return row;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}