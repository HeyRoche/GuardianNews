package com.example.rojo.guardiannews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GuardianAdapter extends ArrayAdapter<Guardian>{

    private static final String LOCATION_SEPARATOR = "T";


    public GuardianAdapter(Context context, List<Guardian> guardians){
        super(context, 0, guardians);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        View listItemView = convertView;
        if (listItemView == null){
            listItemView= LayoutInflater.from(getContext()).inflate(
                    R.layout.guardian_list_item, parent,false);
        }

        //Find the story at the given position of hte list of guardian, stories
        Guardian currentGuardian = getItem(position);

        TextView articleView = listItemView.findViewById(R.id.article);
        articleView.setText(currentGuardian.getArticle());

        TextView sectionView = listItemView.findViewById(R.id.section);
        sectionView.setText(currentGuardian.getSection());

        //TextView authorView = listItemView.findViewById(R.id.author);
        //authorView.setText(currentGuardian.getAuthor());


        String primaryDate =currentGuardian.getDate();

        String date;

        if(primaryDate.contains(LOCATION_SEPARATOR )){
            String[] parts = primaryDate.split(LOCATION_SEPARATOR);
            date = parts[0];

     }else {
          date = null;

        }

        TextView dateView = listItemView.findViewById(R.id.date);
        dateView.setText(date);

        return listItemView;
    }
}
