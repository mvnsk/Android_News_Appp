package com.example.news_app;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
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


public class ProgrammingAdapter2 extends RecyclerView.Adapter<ProgrammingAdapter2.ProgrammingViewHolder2> {

    private String[] data;
    private List<HomePageCardStructure> dataArray;
    private String testString;
    private  Context context;
    private Activity mContext;

    // For weather card
    String cityName = "";
    String stateName = "";
    String temperature = "";
    String summaryText = "";
    Boolean hideWeatherCard = false;


    public void setWeatherData(String cityName,String stateName, String temperature, String summaryText){
        this.cityName = cityName;
        this.stateName = stateName;
        this.temperature = temperature;
        this.summaryText = summaryText;
        Toast.makeText(context,"In this method",Toast.LENGTH_SHORT);
        notifyItemChanged(0);
    }


    public void reloadHelper(String position1){
//        Toast.makeText(context, "RELOAD METHOD CALLED + " + String.valueOf(position1),Toast.LENGTH_SHORT).show();
        notifyItemChanged(Integer.parseInt(position1));
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDataSource(String response, Context testContent){
        this.mContext = (Activity) testContent;
        testString = response;
        dataArray = new ArrayList<HomePageCardStructure>();

        JsonObject gson = new JsonParser().parse(response).getAsJsonObject();
        try {
            JSONObject jsonObject = new JSONObject(gson.toString());
            JSONArray arrayObject = jsonObject.getJSONArray("data");
            for(int i = 0;i<arrayObject.length();i++){
                JSONObject curr = arrayObject.getJSONObject(i);
                HomePageCardStructure card = new HomePageCardStructure(curr.getString("image"), curr.getString("title"));
                card.setId(curr.getString("id"));
                card.setWebUrl(curr.getString("webUrl"));
                card.setSection(curr.getString("section"));
                // card.setDate(curr.getString("date"));

                String date = curr.getString("date");
                card.setDate(date);

                String periodDiffStr = "";

                try{
                    LocalDateTime ldt = LocalDateTime.now();
                    ZoneId zoneId = ZoneId.of("America/Los_Angeles");
                    ZonedDateTime zonedCurrent = ldt.atZone(zoneId);
                    // ZonedDateTime zonedArticle = LocalDateTime.parse(date.substring(0,date.length()-1)).atZone(zoneId);
                    Instant timestamp = Instant.parse(date);
                    ZonedDateTime zonedArticle = timestamp.atZone(zoneId);
                    Duration d = Duration.between(zonedArticle,zonedCurrent);
//                card.setPeriodDiff(String.valueOf(d.getSeconds()));
                    Long diff = d.getSeconds();

                    if(diff > 3600*24)
                    {
                        Long numDays = diff/(3600 * 24);
                        periodDiffStr = numDays + " d ago";
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
                        periodDiffStr = "1 h ago";
                    }
                }
                catch (Exception e){
                    //Do nothing
                    periodDiffStr = "1 h ago";
                }

                card.setPeriodDiff(periodDiffStr);

                dataArray.add(card);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            testString = e.toString();
        }

        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
//    public ProgrammingAdapter2(String response, Context testContent){
//
//    }
    public ProgrammingAdapter2(Context testContent){
        this.mContext = (Activity) testContent;

    }

    @NonNull
    @Override
    public ProgrammingViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.hometab_card,parent,false);

        return new ProgrammingViewHolder2(view);
    }

    @Override
    public void onBindViewHolder (@NonNull final ProgrammingViewHolder2 holder, final int position) {


        if(position > 0){
            //Setting the divider line


            holder.weatherCard.setVisibility(View.GONE);

        final HomePageCardStructure current = dataArray.get(position-1);
        holder.titleView.setText(current.getTitle());
        holder.timeDiff.setText(current.getPeriodDiff() + " | " + current.getSection());
        try {
//            Picasso.get().load(current.getImage()).transform(new LeftRoundedCornersBitmap(10, Color.BLACK,3)).into(holder.imageView);
            Picasso.get().load(current.getImage()).resize(120,100).into(holder.imageView);
            // Picasso.get().load(current.getImage()).fit().into(holder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //1 is for not bookmarked and 0 is for bookmarked
        if (isBookmarked(context, current)) {
            //Set the image for bookmarked
            holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
            holder.bookmark.setTag("0");
        } else {
            //set the image for not bookmarked
            holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
            holder.bookmark.setTag("1");
        }

        //Setting the onclick method for each of the homepage cards
        holder.clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, DetailsPageActivity.class);
                intent.putExtra("id", current.getId());
                intent.putExtra("itemPosition", String.valueOf(position));

                // Add perdiodDiff as extra information
                intent.putExtra("periodDifference", current.getPeriodDiff());
                intent.putExtra("title", current.getTitle());
                intent.putExtra("sharing_url",current.getWebUrl());
                intent.putExtra("sharing_url",current.getWebUrl());


                SharedPreferences pref = context.getSharedPreferences("Mugdha_hw9", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("changed", "false");
                editor.commit();
                context.startActivity(intent);

//                mContext.startActivityForResult(intent,1);

//                Toast.makeText(context, "DONE",Toast.LENGTH_SHORT).show();


            }
        });

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, current.getTitle(),Toast.LENGTH_LONG).show();
                String tag = String.valueOf(v.getTag());
                switch (tag) {
                    case "1":
                        String str1 = "\"" + current.getTitle() + "\" was added to Bookmarks";
                        Toast.makeText(context,str1,Toast.LENGTH_SHORT).show();
//                        Toast.makeText(context, "1",Toast.LENGTH_SHORT).show();
                        holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                        v.setTag("0");
                        addToBookmarks(context, current);
                        break;
                    default:
                        String str2 = "\"" + current.getTitle() + "\" was removed from Bookmarks";
                        Toast.makeText(context,str2,Toast.LENGTH_SHORT).show();
//                        Toast.makeText(context, "default",Toast.LENGTH_SHORT).show();
                        holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                        v.setTag("1");
                        removeFromBookmarks(context, current);
                        break;
                }

            }
        });

        holder.clickable.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.modal_layout);

                ImageView dialogImage = (ImageView) dialog.findViewById(R.id.modal_main_image);
                TextView dialogTitle = (TextView) dialog.findViewById(R.id.modal_title);
                ImageView twitterButton = (ImageView) dialog.findViewById(R.id.modal_twitter);
                final ImageView dialogBookmark = (ImageView) dialog.findViewById(R.id.modal_bookmark_icon);

                // Picasso.get().load(current.getImage()).resize(150,150).into(dialogImage);
                Picasso.get().load(current.getImage()).into(dialogImage);
                dialogTitle.setText(current.getTitle());

                twitterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //DO nothing
                        String URL = "https://twitter.com/intent/tweet";
                        Uri.Builder builder = Uri.parse(URL).buildUpon();
                        String tweet = "Check out this Link:" + "\n" + current.getWebUrl();
                        builder.appendQueryParameter("text", tweet).appendQueryParameter("hashtags", "CSCI571NewsSearch");
                        String url = builder.toString();
                        Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(Intent.createChooser(twitterIntent, "Browse with"));

                    }
                });

                if (isBookmarked(context, current)) {
                    //Set the image for bookmarked
                    dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                    dialogBookmark.setTag("0");
                } else {
                    //set the image for not bookmarked
                    dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                    dialogBookmark.setTag("1");
                }
                dialogBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(context, current.getTitle(),Toast.LENGTH_LONG).show();
                        String tag = String.valueOf(v.getTag());
                        switch (tag) {
                            case "1":
//                        Toast.makeText(context, "1",Toast.LENGTH_SHORT).show();
                                String str3 = "\"" + current.getTitle() + "\" was added to Bookmarks";
                                Toast.makeText(context,str3,Toast.LENGTH_SHORT).show();
                                holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                                dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                                holder.bookmark.setTag("0");
                                dialogBookmark.setTag("0");
                                addToBookmarks(context, current);
                                break;
                            default:
//                        Toast.makeText(context, "default",Toast.LENGTH_SHORT).show();
                                String str4 = "\"" + current.getTitle() + "\" was removed from Bookmarks";
                                Toast.makeText(context,str4,Toast.LENGTH_SHORT).show();
                                holder.bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                                dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                                holder.bookmark.setTag("1");
                                dialogBookmark.setTag("1");
                                removeFromBookmarks(context, current);
                                break;
                        }

                    }
                });


                dialog.show();
                return true;
            }
        });

    }
        else{
            holder.actual_card.setVisibility(View.GONE);
            Toast.makeText(context,String.valueOf(checkPermissions()),Toast.LENGTH_SHORT);
            if(!checkPermissions()){
                holder.weatherCard.setVisibility(View.GONE);

            }else {
                if(cityName.length()>0 && stateName.length()>0 && summaryText.length()>0 && temperature.length()>0){
                    holder.weatherCard.setVisibility(View.VISIBLE);
                    String newSummary = summaryText;
                    holder.cityCard.setText(cityName);
                    holder.stateCard.setText(stateName);
                    holder.temperatureCard.setText(temperature);
                    holder.summaryCard.setText(summaryText);
                    if(newSummary.equals("clouds")){

                        holder.weatherImg.setImageDrawable(context.getResources().getDrawable(R.drawable.cloudy_weather));

                        //weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cloudy_weather));
                        //weatherCard.setBackgroundResource(R.drawable.cloudy_weather);
                    }else if(newSummary.equals("clear")){

                        holder.weatherImg.setImageDrawable(context.getResources().getDrawable(R.drawable.clear_weather));
                        //weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.clear_weather));
                        //weatherCard.setBackgroundResource(R.drawable.clear_weather);
                    }else if(newSummary.equals("snow")){

                        holder.weatherImg.setImageDrawable(context.getResources().getDrawable(R.drawable.snowy_weather));
                        //weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.snowy_weather));
                        //weatherCard.setBackgroundResource(R.drawable.snowy_weather);
                    }else if(newSummary.equals("rain") || newSummary.equals("drizzle")){

                        holder.weatherImg.setImageDrawable(context.getResources().getDrawable(R.drawable.rainy_weather));
                        //weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.rainy_weather));
                        //weatherCard.setBackgroundResource(R.drawable.rainy_weather);
                    }else if(newSummary.equals("thunderstorm")){

                        holder.weatherImg.setImageDrawable(context.getResources().getDrawable(R.drawable.thunder_weather));
                        //weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.thunder_weather));
                        //weatherCard.setBackgroundResource(R.drawable.thunder_weather);
                    }else{

                        holder.weatherImg.setImageDrawable(context.getResources().getDrawable(R.drawable.sunny_weather));
                        //weatherImg.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.sunny_weather));
                        //weatherCard.setBackgroundResource(R.drawable.sunny_weather);
                    }
                }
                else{
                    //holder.weatherCard.setVisibility(View.GONE);
                }
            }


        }



//        holder.titleView.setText("Mugdha");
//        holder.titleView.setText(testString);
    }

    @Override
    public int getItemCount(){
        return(dataArray.size() + 1);
    }

    public void hideWeatherCard() {
        hideWeatherCard = true;
        //Toast.makeText(context, "No permissions granted",Toast.LENGTH_SHORT).show();
        notifyItemChanged(0);
    }

    public void showWeatherCard(){
        hideWeatherCard = false;
        //Toast.makeText(context, "Permission granted",Toast.LENGTH_SHORT).show();
        notifyItemChanged(0);
    }

    public class ProgrammingViewHolder2 extends RecyclerView.ViewHolder{

        TextView titleView;
        ImageView imageView;
        ImageView bookmark;
        View clickable;
        TextView timeDiff;

        //For weather data
        CardView weatherCard;
        TextView cityCard;
        TextView stateCard;
        TextView temperatureCard;
        TextView summaryCard;
        ImageView weatherImg;
        CardView actual_card;

        public ProgrammingViewHolder2(View itemView){
            super(itemView);
            titleView = itemView.findViewById(R.id.homepage_card_title);
            imageView = itemView.findViewById(R.id.homepage_card_image);
            clickable = itemView.findViewById(R.id.homepage_clickable);
            bookmark = itemView.findViewById(R.id.hometab_card_bookmarkicon);
            timeDiff = itemView.findViewById(R.id.homepage_timediff);

            weatherCard = itemView.findViewById(R.id.weather_card_temp);
            cityCard = itemView.findViewById(R.id.city_card_temp);
            stateCard = itemView.findViewById(R.id.state_card_temp);
            temperatureCard = itemView.findViewById(R.id.temperature_card_temp);
            summaryCard = itemView.findViewById(R.id.summary_card_temp);
            weatherImg= itemView.findViewById(R.id.weather_img_temp);
            actual_card= itemView.findViewById(R.id.actual_news_card_home);
            context = itemView.getContext();

        }

    }

    public void addToBookmarks(Context context, HomePageCardStructure cardObj){
        BookmarkObjectStructure currObj = new BookmarkObjectStructure();
        currObj.setImage(cardObj.getImage());
        currObj.setTitle(cardObj.getTitle());
        currObj.setDate(cardObj.getDate());
        currObj.setSection(cardObj.getSection());
        currObj.setId(cardObj.getId());
        currObj.setUrl(cardObj.getWebUrl());
        currObj.setPeriodDiff(cardObj.getPeriodDiff());

        new SharedPreferenceHelper().addFavorite(context,currObj);
    }


    public void removeFromBookmarks(Context context, HomePageCardStructure cardObj){
        BookmarkObjectStructure currObj = new BookmarkObjectStructure();
        currObj.setImage(cardObj.getImage());
        currObj.setTitle(cardObj.getTitle());
        currObj.setDate(cardObj.getDate());
        currObj.setSection(cardObj.getSection());
        currObj.setId(cardObj.getId());
        currObj.setUrl(cardObj.getWebUrl());
        currObj.setPeriodDiff(cardObj.getPeriodDiff());

//                                Toast.makeText(context, cardObj.getId(),Toast.LENGTH_SHORT).show();
        new SharedPreferenceHelper().removeFavorite(context,currObj);
    }

    public boolean isBookmarked(Context context, HomePageCardStructure cardObj){
        BookmarkObjectStructure currObj = new BookmarkObjectStructure();
        currObj.setImage(cardObj.getImage());
        currObj.setTitle(cardObj.getTitle());
        currObj.setDate(cardObj.getDate());
        currObj.setSection(cardObj.getSection());
        currObj.setId(cardObj.getId());
        currObj.setUrl(cardObj.getWebUrl());
        currObj.setPeriodDiff(cardObj.getPeriodDiff());

        return new SharedPreferenceHelper().checkIfBookmarked(context,currObj);

    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

}
