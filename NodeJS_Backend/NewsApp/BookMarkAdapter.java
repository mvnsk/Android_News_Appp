package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Activities.BookMarkActivity;
import com.example.myapplication.Data.Mydata;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.BookMarkView> {
    private ArrayList<Mydata> newsArrayList;
    private static Context m_context;

    public BookMarkAdapter(Context context, ArrayList<Mydata> newsArrayList) {
        this.newsArrayList = newsArrayList;
        m_context=context;
    }

    @NonNull
    @Override
    public BookMarkView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.test3,viewGroup,false);

        return new BookMarkView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookMarkView bookMarkView, final int position) {
         final Mydata article = newsArrayList.get(position);
        bookMarkView.news_title_text_view.setText(article.getTitle());
        Picasso.get().load(article.thumbnail).into(bookMarkView.news_image_view);
        String mystring = "|" + article.section();
        bookMarkView.sectionView.setText(mystring);
        String originalDateTime = article.publish()();
      /*  String time_woz =originalDateTime.substring(0, originalDateTime.length() - 1);
        LocalDateTime ldt=LocalDateTime.parse(time_woz);
        ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );

        ZonedDateTime LA_time = ldt.atZone( zoneId );
        System.out.println("los angeles time is " + LA_time);
        LocalDateTime current_time = LocalDateTime.now();
        ZonedDateTime current_LA_time = current_time.atZone( zoneId );

        long diffInSeconds = java.time.Duration.between(current_LA_time, LA_time).getSeconds();
        long diffInMinutes = java.time.Duration.between(current_LA_time,LA_time).toMinutes();
        long diffInHours = java.time.Duration.between(current_LA_time,LA_time).toHours();
        diffInMinutes=diffInMinutes%60;
        diffInSeconds=diffInSeconds%60;
        String publish_time;
        if(diffInHours >0){
            publish_time=String.valueOf(diffInHours)+"h ago";
        }
        else if(diffInMinutes >0){
            publish_time= String.valueOf(diffInMinutes)+"m ago";
        }
        else{
            publish_time=String.valueOf(diffInSeconds)+"s ago";
        }
        bookMarkView.timeView.setText(publish_time);*/

        String time_woz =originalDateTime.substring(0, originalDateTime.length() - 1);
        LocalDateTime ldt=LocalDateTime.parse(time_woz);
        ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );
        //web date convereted to la time
        ZonedDateTime LA_time = ldt.atZone( zoneId );
        String month=LA_time.getMonth().toString().substring(0,3).toLowerCase();
        String cap = month.substring(0, 1).toUpperCase() + month.substring(1);
        String day=originalDateTime.substring(5,7);
        String year=originalDateTime.substring(0,4);
        // System.out.println("business time " + cap + " " + date +" "+ year);
       // TextView timeView = (TextView) findViewById(R.id.detail_time);
        String modified_Time=day+ " "+cap;
        bookMarkView.timeView.setText(modified_Time);
        //timeView.setText(modified_Time);
        String bookmarked=article.issaved();
        if(bookmarked.equals("yes")){
            bookMarkView.btn.setImageResource(R.drawable.bookmark_fill);
            article.savedstate("yes");
        }
        else{
            bookMarkView.btn.setImageResource(R.drawable.bookmark_empty);
            article.savedstate("no");
        }

        View.OnClickListener imgButtonHandler = new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("within adapter onclick Main Called");
                String title=article.getTitle();
                String weburl=article.getUrlOfStory();
                String toast_Add_msg='"'+title+'"'+ " was added to Bookmarks";
                String toast_Remove_msg='"'+title+'"'+ " was removed from Bookmarks";
                if(article.issaved().equals("no")){

                    bookMarkView.btn.setImageResource(R.drawable.bookmark_fill);
                    article.savedstate("yes");
                    Toast.makeText(m_context,toast_Add_msg,Toast.LENGTH_SHORT).show();
                 /*   String json = gson.toJson(article);
                    prefsEditor.putString(weburl, json);
                    prefsEditor.commit();*/


                }
                else{
                    bookMarkView.btn.setImageResource(R.drawable.bookmark_empty);

                    article.savedstate("no");
                    Toast.makeText(m_context,toast_Remove_msg,Toast.LENGTH_SHORT).show();
                   /* if(appSharedPrefs.contains(weburl)){
                        prefsEditor.remove(weburl);
                    }*/
                    SharedPreferences pref = m_context.getSharedPreferences("Bookmarked_articles", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    if(pref.contains(weburl)){
                        System.out.println("inside adapter if");
                        editor.remove(weburl).commit();
                    }
                    newsArrayList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,newsArrayList.size());
                  /*  if(newsArrayList.size()==0){
                        try {
                            BookMarkActivity dummy = new BookMarkActivity();
                            dummy.actuallyEmpty();
                        }
                        catch(Exception e){
                            Log.d("dummy exception is ", e.toString());
                        }
                    }*/
                }

            }
        };
        bookMarkView.btn.setOnClickListener(imgButtonHandler);


    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    public void removeItem(int position){
        newsArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,newsArrayList.size());
      /*  if(newsArrayList.size()==0){
            try {
                BookMarkActivity dummy = new BookMarkActivity();
                dummy.actuallyEmpty();
            }
            catch(Exception e){
                Log.d("dummy exception is ", e.toString());
            }
            }*/
      //  notifyDataSetChanged();
    }

    public class BookMarkView extends RecyclerView.ViewHolder{
        TextView news_title_text_view;
        ImageView news_image_view;
        TextView sectionView;
        TextView timeView;
        ImageButton btn;
        public BookMarkView(@NonNull View itemView) {
            super(itemView);
             news_title_text_view = (TextView) itemView.findViewById(R.id.bookmark_Card_title);
             news_image_view = (ImageView) itemView.findViewById(R.id.bookmark_card_image);
             sectionView = (TextView) itemView.findViewById(R.id.bookmark_Card_section);
             timeView = (TextView) itemView.findViewById(R.id.bookmark_Card_date);
             btn = (ImageButton) itemView.findViewById(R.id.bookmark_card_bookmark_image);

        }

}}
