package com.example.news_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DetailsPageActivity extends AppCompatActivity {

    private String id;
    private String title;
    private String image;
    private String periodDiff;
    private String section;
    String date;


    ProgressBar progressBar;

    ImageView detailpage_image;
    TextView detailpage_title;
    TextView detailpage_section;
    TextView detailpage_date;
    TextView detailpage_description;
    TextView detailpage_url;

    MenuItem menuTitle;
    MenuItem menuBookmark;
    MenuItem menuTwitter;


    RelativeLayout spinnerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_details_page);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        periodDiff = intent.getStringExtra("periodDifference");
        progressBar = findViewById(R.id.progressbar_details);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#2D42BD"), PorterDuff.Mode.MULTIPLY);

        detailpage_image = findViewById(R.id.detailpage_image);
        detailpage_title = findViewById(R.id.detailpage_title);
        detailpage_section = findViewById(R.id.detailpage_section);
        detailpage_date = findViewById(R.id.detailpage_date);
        detailpage_description = findViewById(R.id.detailpage_description);
        detailpage_url = findViewById(R.id.detailpage_url);

        spinnerView = findViewById(R.id.spinner_detail);

//        detailpage_title.setText(id);
        new getDetailsData().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_page_menu,menu);

        // menuTitle = menu.findItem(R.id.detailmenu_title);

        //menuTitle.setTitleCondensed(getIntent().getStringExtra("title"));

        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        menuBookmark = menu.findItem(R.id.detailmenu_bookmark);

        //Check if it is bookmarked or not
        if(isBookmarked(this,getIntent().getStringExtra("id"))){
            //It is bookmarked
            //tag for bookmarked is 0
            menuBookmark.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_bookmark_black_24dp));

        }
        else{
            //It is not bookmarked
            //Tag for not bookmarked is 1

            menuBookmark.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_bookmark_border_black_24dp));

        }


        menuTwitter = menu.findItem(R.id.detailmenu_share);

        //menuTitle.setVisible(true);
        menuBookmark.setVisible(true);
        menuTwitter.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        super.onOptionsItemSelected(menuItem);

        switch (menuItem.getItemId()){
            case R.id.detailmenu_share :
                String URL = "https://twitter.com/intent/tweet";
                Uri.Builder builder = Uri.parse(URL).buildUpon();
                String tweet = "Check out this Link:" + "\n" + getIntent().getStringExtra("sharing_url");
                builder.appendQueryParameter("text",tweet).appendQueryParameter("hashtags","CSCI571NewsSearch");
                String url = builder.toString();
                Intent twitterIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                startActivity(Intent.createChooser(twitterIntent,"Browse with"));
                break;
            case R.id.detailmenu_bookmark :
                //Toast.makeText(this, getIntent().getStringExtra("title"),Toast.LENGTH_LONG).show();
                SharedPreferences pref = getSharedPreferences("Mugdha_hw9",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("changed","true");
                editor.putString("itemPosition",getIntent().getStringExtra("itemPosition"));
                editor.commit();
                //Toast.makeText(this, getIntent().getStringExtra("itemPosition"),Toast.LENGTH_SHORT).show();
                //If it is bookmarked, you need to delete it else add it
                if(isBookmarked(this,getIntent().getStringExtra("id"))){
                    String str1 = "\"" + getIntent().getStringExtra("title") + "\" was removed from Bookmarks";
                    Toast.makeText(this,str1,Toast.LENGTH_SHORT).show();
                    removeFromBookmarks(this, getIntent().getStringExtra("id"));
                    menuBookmark.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_bookmark_border_black_24dp));
                }
                else{
                    String str2 = "\"" + getIntent().getStringExtra("title") + "\" was added to Bookmarks";
                    Toast.makeText(this,str2,Toast.LENGTH_SHORT).show();
                    addToBookmarks(this, getIntent().getStringExtra("id"));
                    menuBookmark.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_bookmark_black_24dp));
                }


                break;
//            case R.id.detailmenu_title :
//                break;
            default:
                onBackPressed();
                break;
        }
        return true;
    }

    private class getDetailsData extends AsyncTask<Void,Void,String>{


        @Override
        protected void onPreExecute(){
            spinnerView.setVisibility(View.VISIBLE);
            //progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {


            try {

                URL url = new URL("https://frontendinreact.wn.r.appspot.com/get_detailed_article_guardian_hw9?id=" + id);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try{
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while( (line = bufferedReader.readLine())!=null ){
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();

                }finally {
                    urlConnection.disconnect();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String response){

            spinnerView.setVisibility(View.GONE);
            //progressBar.setVisibility(View.GONE);
            if(response!=null){
                //Populate view with the data
                JsonObject gson = new JsonParser().parse(response).getAsJsonObject();
                try {
                    JSONObject jsonObject = new JSONObject(gson.toString());
                    final JSONObject obj = jsonObject.getJSONObject("data");


//                    detailpage_title.setText(obj.toString());
                    detailpage_title.setText(obj.getString("title"));
                    detailpage_section.setText(obj.getString("section"));
                    //detailpage_date.setText(obj.getString("date"));

                    try{

                        Instant timestamp = Instant.parse(obj.getString("date"));
                        ZonedDateTime zonedDate = timestamp.atZone(ZoneId.of("America/Los_Angeles"));

                        int day = zonedDate.getDayOfMonth();
                        String dayStr = String.valueOf(day);
                        if(day<10){
                            dayStr = "0" + dayStr;
                        }

                        Month month = zonedDate.getMonth();
                        String monStr = String.valueOf(month);
                        monStr = monStr.toLowerCase();
                        monStr = monStr.substring(0,1).toUpperCase() + monStr.substring(1);

                        int year = zonedDate.getYear();

                        detailpage_date.setText(dayStr + " " + monStr + " " + year);

                    }catch (Exception e){
                        detailpage_date.setText("07 May 2020");
                    }


                    //detailpage_description.setText(obj.getString("description"));

                    String currDescription = obj.getString("description");
                    //currDescription += "</br><a href=\"" + obj.getString("url") + "\"> <h3> View Full Article</h3></a>";
                    detailpage_description.setText(Html.fromHtml(currDescription));

                    String currURL = "<a href=\""+ obj.getString("url") +"\"> <h3> View Full Article</h3></a>";
                    SpannableString content = new SpannableString(Html.fromHtml(currURL));
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    // detailpage_url.setClickable(true);
                    // detailpage_url.setMovementMethod(LinkMovementMethod.getInstance());
                    detailpage_url.setText(content);

                    detailpage_url.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            Intent myIntent = null;
                            try {
                                myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(obj.getString("url")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(myIntent);
                        }
                    });

                    //detailpage_url.setText(obj.getString("url"));
//                    detailpage_image.setVisibility(View.VISIBLE);
                    // Picasso.get().load(obj.getString("image")).fit().into(detailpage_image);
                    Picasso.get().load(obj.getString("image")).into(detailpage_image);

                    title = obj.getString("title");
                    image = obj.getString("image");
                    section = obj.getString("section");
                    date = obj.getString("date");



                } catch (JSONException e) {
                    detailpage_title.setText(e.toString());
                }
            }
            else
            {
                detailpage_title.setText("ERROR");
            }

        }
    }


    public boolean isBookmarked(Context context, String id){
        BookmarkObjectStructure currObj = new BookmarkObjectStructure();
        currObj.setId(id);
        return new SharedPreferenceHelper().checkIfBookmarked(context,currObj);
    }

    public void addToBookmarks(Context context, String id){
        BookmarkObjectStructure currObj = new BookmarkObjectStructure();
        currObj.setId(id);
        new SharedPreferenceHelper().addFavorite(context,currObj);
    }

    public void removeFromBookmarks(Context context, String id){
        BookmarkObjectStructure currObj = new BookmarkObjectStructure();
        currObj.setTitle(title);
        currObj.setImage(image);
        currObj.setId(id);
        currObj.setPeriodDiff(periodDiff);
        currObj.setSection(section);
        currObj.setDate(date);

        new SharedPreferenceHelper().removeFavorite(context,currObj);
    }

}
