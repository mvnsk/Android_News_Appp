package com.example.myapplication.Activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class DetailPage extends AppCompatActivity {
    public static BOOKMARK_DETAIL_TAG="thisisuniquebookarkkey";
    Gson gson = new Gson();
    MenuItem bookmark_item;
    private NewsAdapter mAdapter;
    boolean fetch_body;
    private List<Mydata> data;
    Intent intent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_page);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        showNews();
        getParentActivityIntent();
         intent= getIntent();
        if(intent.hasExtra("Fetch_body")){
            fetch_body=getIntent().getBooleanExtra("Fetch_body", false);
            System.out.println("fetch_body is true");
        }
        ImageView news_image_view = (ImageView) findViewById(R.id.detail_image);
        TextView body_of_news_story = (TextView) findViewById(R.id.detail_body);
        TextView news_title_text_view = (TextView) findViewById(R.id.detail_title);

        TextView sectionView = (TextView) findViewById(R.id.detail_section);
        TextView articleview=(TextView) findViewById(R.id.detail_link);


        news_title_text_view.setText(Mydata.getTitle());
        String action_title=Mydata.getTitle();
        setTitle(action_title);

        Picasso.get().load(Mydata.thumbnail).into(news_image_view);


        String mystring = Mydata.section();
        sectionView.setText(mystring);
        body_of_news_story.setText(Mydata.getBodyOfStory());
        String originalDateTime = Mydata.publish()();
        String time_woz =originalDateTime.substring(0, originalDateTime.length() - 1);
        LocalDateTime ldt=LocalDateTime.parse(time_woz);
        ZoneId zoneId = ZoneId.of( "America/Los_Angeles" );

        ZonedDateTime LA_time = ldt.atZone( zoneId );
        String month=LA_time.getMonth().toString().substring(0,3).toLowerCase();
        String cap = month.substring(0, 1).toUpperCase() + month.substring(1);
        String day=originalDateTime.substring(5,7);
        String year=originalDateTime.substring(0,4);

        TextView timeView = (TextView) findViewById(R.id.detail_time);
        String modified_Time=day+ " "+cap + " "+year;
        timeView.setText(modified_Time);

        linkview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriOfNews = Uri.parse(Mydata.getUrlOfStory());

                Intent intent = new Intent(Intent.ACTION_VIEW, uriOfNews);

                startActivity(intent);
            }
        });


            getBody();

    }

    private void getBody() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String article_id=Mydata.getIdOfStory();

        TextView body_view=findViewById(R.id.detail_body);
        System.out.println("body for home url is " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject responseJson = new JSONObject(response);
                            JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
                            System.out.println("fetched body response is " + response.toString());

                            JsonObject content = jsonObject.getAsJsonObject("response").getAsJsonObject("content");
                            System.out.println("fetched body content is"+ content.toString());

                            String fetched_nameOfSection = content.get("sectionName").getAsString() ; //.getString("sectionName");
                            System.out.println("fetched body section is"+ fetched_nameOfSection);
                            String fetched_title = content.get("webTitle").getAsString();
                            String fetched_date = content.get("webPublicationDate").getAsString();
                            String fetched_url = content.get("webUrl").getAsString();
                            String fetched_id = content.get("id").getAsString();

                            JsonArray bodyArray=content.get("blocks").getAsJsonObject().get("body").getAsJsonArray();
                            String fetched_body="";
                            for(int i=0;i<bodyArray.size();i++){

                                JsonObject currentbody=bodyArray.get(i).getAsJsonObject();
                                String bodyhtml=currentbody.get("bodyHtml").getAsString();
                                fetched_body=fetched_body.concat(bodyhtml);
                                System.out.println("fetched body iteration" + i);
                                if(i>20){break;}
                            }
                            System.out.println("fetched body is " + fetched_body);
                            JsonArray elements = content.get("blocks").getAsJsonObject().get("main").getAsJsonObject().get("elements").getAsJsonArray();

                            JsonObject temp= elements.get(0).getAsJsonObject();

                            JsonArray assets=temp.get("assets").getAsJsonArray();

                            if (assets.size()>0) {
                                JsonObject t2 = assets.get(0).getAsJsonObject();

                                String thumbnail = t2.get("file").getAsString();
                                ImageView news_image_view = (ImageView) findViewById(R.id.detail_image);
                               // Picasso.get().load(thumbnail).into(news_image_view);
                            }
                            TextView body_of_news_story = (TextView) findViewById(R.id.detail_body);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                body_of_news_story.setText(Html.fromHtml(fetched_body,Html.FROM_HTML_MODE_LEGACY));
                            } else {
                                body_of_news_story.setText(Html.fromHtml(fetched_body));
                            }

                            //body_of_news_story.setText(fetched_body);
                            ImageView news_image_view = (ImageView) findViewById(R.id.detail_image);
                            TextView sectionView = (TextView) findViewById(R.id.detail_section);
                            sectionView.setText(fetched_nameOfSection);

                        }

                         catch (JSONException e) {
                             System.out.println("fetched body error " + e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }



    private void showNews() {

        SharedPreferences pref = this.getSharedPreferences("Bookmarked_articles", 0); // 0 - for private mode
        Gson gson = new Gson();
        String json = pref.getString(NEWS_INFO, "");
        Mydata= gson.fromJson(json, Mydata.class);
        System.out.println("detail page web url within last method is is "+ Mydata.getUrlOfStory());

        //Updating bookmark state irrespective of click
        //SharedPreferences pref = getSharedPreferences("Bookmarked_articles", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String tempty=Mydata.issaved();
        if (tempty.equals("yes")) {
            editor.putString(BOOKMARK_DETAIL_TAG,"yes").commit();
        } else {
            //tempnews.savedstate("no");
            editor.putString(BOOKMARK_DETAIL_TAG,"no").commit();
        }


    }
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_toolbar_menu, menu);
        bookmark_item= menu.findItem(R.id.toolbar_bookmark);
       String bookmarked=Mydata.issaved();
       System.out.println("detail page bookmark within is" + bookmarked);
       if(bookmarked.equals("yes")){

           bookmark_item.setIcon(R.drawable.bookmark_fill);
       }
       else{
           bookmark_item.setIcon(R.drawable.bookmark_empty);
       }



        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        String title=Mydata.getTitle();
        String image=Mydata.thumbnail;
        String weburl=Mydata.getUrlOfStory();
        String bookmarked=Mydata.issaved();
        String toast_Add_msg='"'+title+'"'+ " was added to Bookmarks";
        String toast_Remove_msg='"'+title+'"'+ " was removed from Bookmarks";

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }


        switch (item.getItemId()) {
            case R.id.toolbar_bookmark:

                System.out.println("Figure out Business Called");
                if(Mydata.issaved().equals("yes")){

                    bookmark_item.setIcon(R.drawable.bookmark_empty);
                    Mydata.savedstate("no");
                    SharedPreferences pref = getSharedPreferences("Bookmarked_articles", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(BOOKMARK_DETAIL_TAG,"no");

                    Toast.makeText(this,toast_Remove_msg,Toast.LENGTH_SHORT).show();



                    if(pref.contains(weburl)){
                        editor.remove(weburl).commit();

                    }
                    if(pref.contains(BOOKMARK_DETAIL_TAG)){
                        editor.putString(BOOKMARK_DETAIL_TAG,"no").commit();
                    }
                    if(intent.hasExtra("classname")){
                        String ClassName=getIntent().getStringExtra("classname");
                     if(ClassName.equals("BookMarkActivity")){
                         BookMarkActivity.bookmark_removed=true;
                     }
                     else if (ClassName.equals("MainActivity")){
                            MainActivity.bookmark_removed=true;
                        }
                    }
                }
                else{

                    bookmark_item.setIcon(R.drawable.bookmark_fill);
                    Mydata.savedstate("yes");

                    Toast.makeText(this,toast_Add_msg,Toast.LENGTH_SHORT).show();

                    SharedPreferences pref = getSharedPreferences("Bookmarked_articles", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    String json = gson.toJson(Mydata);
                    editor.putString(weburl, json);
                    editor.putString(BOOKMARK_DETAIL_TAG,"yes");

                    editor.commit();
                }
                return true;

            case R.id.toolbar_twitter:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                String TWITTER_URL = "https://twitter.com/intent/tweet";
                Uri.Builder builder = Uri.parse(TWITTER_URL).buildUpon();
                String tweet="Check out this Link:" + "\n" + weburl;
                builder.appendQueryParameter("text", tweet)
                        .appendQueryParameter("hashtags", "CSCI571NewsSearch");
                String url=builder.toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                startActivity(Intent.createChooser(intent, "Browse with"));
                return true;
            case R.id.home:
                onBackPressed();
                return true;
            default:

                return super.onOptionsItemSelected(item);

        }
    }

}
