package com.example.news_app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProgrammingAdapter extends RecyclerView.Adapter<ProgrammingAdapter.ProgrammingViewHolder> {

    private String[] data;

    private List<HeadLineStructure> dataArray;

    private Context context;
    private String testString;
//    public ProgrammingAdapter(String[] data){
//        this.data = data;
//    }




    public void reloadHelper(String position1){
        notifyItemChanged(Integer.parseInt(position1));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ProgrammingAdapter(String response){
        testString = response;
        dataArray = new ArrayList<HeadLineStructure>();

        JsonObject gson = new JsonParser().parse(response).getAsJsonObject();
          try {
              JSONObject jsonObject = new JSONObject(gson.toString());
              JSONArray arrayObject = jsonObject.getJSONArray("data");
              for(int i = 0;i<arrayObject.length();i++){
                  JSONObject curr = arrayObject.getJSONObject(i);
                  HeadLineStructure headline = new HeadLineStructure(curr.getString("image"), curr.getString("title"),curr.getString("description"));
                  headline.setId(curr.getString("id_for_details_page"));
                  headline.setUrl(curr.getString("sharing_modal_url"));
                  headline.setSection(curr.getString("section"));
                  String date = curr.getString("date");
                  String periodDiffStr = "";

                  try{
                      LocalDateTime ldt = LocalDateTime.now();
                      ZoneId zoneId = ZoneId.of("America/Los_Angeles");
                      ZonedDateTime zonedCurrent = ldt.atZone(zoneId);
                      //ZonedDateTime zonedArticle = LocalDateTime.parse(date.substring(0,date.length()-1)).atZone(zoneId);

                      Instant timestamp = Instant.parse(date);
                      ZonedDateTime zonedArticle = timestamp.atZone(zoneId);

                      Duration d = Duration.between(zonedArticle,zonedCurrent);
//                card.setPeriodDiff(String.valueOf(d.getSeconds()));
                      Long diff = d.getSeconds();

//                      if(diff < 0)
//                      {
//                          diff = diff * -1;
//                      }
                      if(diff > 3600 * 24)
                      {
                          Long numdays = diff/(3600 * 24);
                          periodDiffStr = numdays + "d ago";
                      }
                      else if(diff > 3600)
                      {
                          Long numHours = diff/3600;
                          periodDiffStr = numHours + " h ago";

                      }
                      else if(diff > 60)
                      {
                          Long numMin = diff/60;
                          periodDiffStr = numMin + " m ago";
                      }
                      else if(diff >= 0){
                          periodDiffStr = diff + " s ago";
                      }
                      else{
                          //periodDiffStr = "1 h ago";
                          periodDiffStr = "1 h ago";
                          //periodDiffStr = "Zoned article date = " + zonedArticle + "\n Zoned Current = " + zonedCurrent + "\n diff = NEGATIVE" ;
                      }
//                      periodDiffStr = "Zoned article date = " + zonedArticle + "\n Zoned Current = " + zonedCurrent + "\n diff = " + periodDiffStr;
                  }
                  catch (Exception e){
                      //Do nothing
                        //periodDiffStr = e.toString() + "\n " + date;
                      periodDiffStr = "1 h ago";
                  }

                  headline.setPeriodDiff(periodDiffStr);
                  headline.setDate(date);
                  dataArray.add(headline);
              }


          } catch (JSONException e) {
              e.printStackTrace();
              testString = e.toString();
          }
    }



    @NonNull
    @Override
    public ProgrammingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.horizontal_card_view,parent,false);
        return new ProgrammingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProgrammingViewHolder holder, final int position) {

//        holder.titleView.setText(testString);
        final HeadLineStructure headline = dataArray.get(position);
        holder.titleView.setText(headline.getTitle());
        // holder.descriptionView.setText(headline.getDescription());
        holder.timeDiff.setText(headline.getPeriodDiff()+" | " + headline.getSection());
        try {
            // Picasso.get().load(headline.getImage()).resize(60,60).into(holder.imageView);
            Picasso.get().load(headline.getImage()).resize(120,100).into(holder.imageView);
        }catch (Exception e){
            e.printStackTrace();
        }
//
        //1 is for not bookmarked and 0 is for bookmarked
        if(isBookmarked(context,headline)){
            holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
            holder.bookmark.setTag("0");
        }
        else{
            holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
            holder.bookmark.setTag("1");
        }

        //Setting onclick method for each of the headline cards
        holder.clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailsPageActivity.class);
                intent.putExtra("id",headline.getId());

                // Add the position of the element
                intent.putExtra("itemPosition",String.valueOf(position));

                // Add perdiodDiff as extra information
                intent.putExtra("periodDifference",headline.getPeriodDiff());
                intent.putExtra("title",headline.getTitle());
                intent.putExtra("share_url",headline.getSharingModalUrl());

                SharedPreferences pref = context.getSharedPreferences("Mugdha_hw9",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("changed","false");
                editor.commit();

                context.startActivity(intent);
            }
        });

        holder.bookmark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(context, headline.getTitle(),Toast.LENGTH_LONG).show();
                String tag = String.valueOf(v.getTag());
                switch(tag){
                    case "1" :
                        String str1 = "\"" + headline.getTitle() + "\" was added to Bookmarks";
                        Toast.makeText(context,str1,Toast.LENGTH_SHORT).show();
                        holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                        v.setTag("0");
                        addToBookmarks(context,headline);
                        break;
                    default:
                        String str2 = "\"" + headline.getTitle() + "\" was removed from Bookmarks";
                        Toast.makeText(context,str2,Toast.LENGTH_SHORT).show();
                        holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                        v.setTag("1");
                        removeFromBookmarks(context,headline);
                        break;
                }

            }

        });


        holder.clickable.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view){

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.modal_layout);

                ImageView dialogImage = (ImageView)dialog.findViewById(R.id.modal_main_image);
                TextView dialogTitle = (TextView)dialog.findViewById(R.id.modal_title);
                ImageView twitterButton = (ImageView)dialog.findViewById(R.id.modal_twitter);
                final ImageView dialogBookmark = (ImageView)dialog.findViewById(R.id.modal_bookmark_icon);


                Picasso.get().load(headline.getImage()).resize(150,150).into(dialogImage);
                dialogTitle.setText(headline.getTitle());

                twitterButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){

                        String URL = "https://twitter.com/intent/tweet";
                        Uri.Builder builder = Uri.parse(URL).buildUpon();
                        String tweet = "Check out this Link:" + "\n" + headline.getSharingModalUrl();
                        builder.appendQueryParameter("text",tweet).appendQueryParameter("hashtags","CSCI571NewsSearch");
                        String url = builder.toString();
                        Intent twitterIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                        context.startActivity(Intent.createChooser(twitterIntent,"Browse with"));
                    }
                });


                if(isBookmarked(context,headline)){
                    dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                    dialogBookmark.setTag("0");
                }
                else{
                    dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                    dialogBookmark.setTag("1");
                }

                dialogBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(context, headline.getTitle(),Toast.LENGTH_LONG).show();

                        String tag = String.valueOf(v.getTag());
                        switch(tag){

                            case "1" :
                                String str3 = "\"" + headline.getTitle() + "\" was added to Bookmarks";
                                Toast.makeText(context,str3,Toast.LENGTH_SHORT).show();
                                holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                                dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                                holder.bookmark.setTag("0");
                                dialogBookmark.setTag("0");
                                addToBookmarks(context,headline);
                                break;
                            default:
                                String str4 = "\"" + headline.getTitle() + "\" was removed from Bookmarks";
                                Toast.makeText(context,str4,Toast.LENGTH_SHORT).show();
                                holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                                dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                                holder.bookmark.setTag("1");
                                dialogBookmark.setTag("1");
                                removeFromBookmarks(context,headline);

                        }


                    }
                });



                dialog.show();
                return true;
            }

        });




//        holder.bookmark.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Headlines Bookmark clicked",Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return dataArray.size();
    }

    public class ProgrammingViewHolder extends  RecyclerView.ViewHolder{
//        TextView titleView;
            TextView titleView;
            // TextView descriptionView;
            ImageView imageView;
            ImageView bookmark;
            View clickable;
            TextView timeDiff;

        public ProgrammingViewHolder(View itemView){
            super(itemView);
//            titleView = itemView.findViewById(R.id.horizontal_card_title);
            titleView = itemView.findViewById(R.id.horizontal_card_title);
            // descriptionView = itemView.findViewById(R.id.horizontal_card_description);
            imageView = itemView.findViewById(R.id.horizontal_card_image);
            bookmark = itemView.findViewById(R.id.horizontal_card_bookmarkicon);
            clickable = itemView.findViewById(R.id.horizontal_card_clickable);
            timeDiff = itemView.findViewById(R.id.horizontal_card_timediff);
            context = itemView.getContext();

        }
    }

    public void addToBookmarks(Context context, HeadLineStructure cardObj){


        BookmarkObjectStructure currObj = new BookmarkObjectStructure();
        currObj.setImage(cardObj.getImage());
        currObj.setTitle(cardObj.getTitle());
        currObj.setDate(cardObj.getDate());
        currObj.setSection(cardObj.getSection());
        currObj.setId(cardObj.getId());
        currObj.setUrl(cardObj.getSharingModalUrl());
        currObj.setPeriodDiff(cardObj.getPeriodDiff());
        new SharedPreferenceHelper().addFavorite(context,currObj);

    }

    public void removeFromBookmarks(Context context, HeadLineStructure cardObj){

        BookmarkObjectStructure currObj = new BookmarkObjectStructure();
        currObj.setImage(cardObj.getImage());
        currObj.setTitle(cardObj.getTitle());
        currObj.setDate(cardObj.getDate());
        currObj.setSection(cardObj.getSection());
        currObj.setId(cardObj.getId());
        currObj.setUrl(cardObj.getSharingModalUrl());
        currObj.setPeriodDiff(cardObj.getPeriodDiff());

        new SharedPreferenceHelper().removeFavorite(context,currObj);
    }


    public boolean isBookmarked(Context context, HeadLineStructure cardObj){

        BookmarkObjectStructure currObj = new BookmarkObjectStructure();
        currObj.setImage(cardObj.getImage());
        currObj.setTitle(cardObj.getTitle());
        currObj.setDate(cardObj.getDate());
        currObj.setSection(cardObj.getSection());
        currObj.setId(cardObj.getId());
        currObj.setUrl(cardObj.getSharingModalUrl());
        currObj.setPeriodDiff(cardObj.getPeriodDiff());

        return new SharedPreferenceHelper().checkIfBookmarked(context,currObj);

    }

}
