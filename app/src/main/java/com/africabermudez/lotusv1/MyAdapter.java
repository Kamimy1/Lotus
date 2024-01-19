package com.africabermudez.lotusv1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<Article> {

    /**
     * Clase que se utiliza para adaptar una lista de objetos del tipo Article a una vista de lista.
     *
     * @author Africa Maria Bermudez Mejias
     * @version 1
     */

    private LayoutInflater inflater;
    private ArrayList<Article> articleList;

    public MyAdapter(Context context, ArrayList<Article> articleList) {
        super(context, 0, articleList);
        inflater = LayoutInflater.from(context);
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_layout_cardview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.imageView);
            viewHolder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Article currentItem = articleList.get(position);
        viewHolder.imageView.setImageResource(currentItem.getImage());
        viewHolder.textView.setText(currentItem.getTitulo());

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}