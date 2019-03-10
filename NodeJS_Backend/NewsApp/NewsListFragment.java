package com.example.myapplication.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Activities.DetailPage;
import com.example.myapplication.Activities.HeadlinesActivity;

import com.example.myapplication.Adapters.MainNewsAdapter;
import com.example.myapplication.R;


import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;


public  class NewsListFragment extends Fragment {

    Gson gson = new Gson();
    private MainNewsAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView no_articles;
    ListView news_list;
    private static final int Loader_flag = 1;
    LoaderManager loaderManager;
    boolean isConnected;
    private View loader;
    TextView fetching_news;
    List<Mydata>news_collection;
    public NewsListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("World on create called ");
        View rootView = inflater.inflate(R.layout.news_list, container, false);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh_items);
        news_list = (ListView) rootView.findViewById(R.id.list);
        ConnectivityManager connect =(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
       
     

        no_articles = (TextView) rootView.findViewById(R.id.no_bookmarks);
        news_list.setEmptyView(no_articles);
        loader = (View) rootView.findViewById(R.id.progress_bar);
        fetching_news=(TextView) rootView.findViewById(R.id.fetching_news);
        mAdapter = new MainNewsAdapter(getContext(), new ArrayList<Mydata>());
        news_list.setAdapter(mAdapter);
        if (isConnected){
            loaderManager = getLoaderManager();
            loaderManager.initLoader(Loader_flag, null, this);
        } else {
            loader.setVisibility(View.GONE);
            fetching_news.setVisibility(View.GONE);

        }

        news_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mydata Mydata = mAdapter.getItem(position);
                Intent openMainNews = new Intent(getContext(), DetailPage.class);
                System.out.println("detail page web url before detail is "+ Mydata.getUrlOfStory());
                openMainNews.putExtra(DetailPage.NEWS_INFO, Mydata);
                SharedPreferences pref = getActivity().getSharedPreferences("Bookmarked_articles", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                String json = gson.toJson(Mydata);
                editor.putString(DetailPage.NEWS_INFO, json);
                editor.putInt(DetailPage.DETAIL_ARTICLE_POSITION,position);
                editor.commit();
                startActivity(openMainNews);
                //return true;


            }
        });

        news_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Mydata Mydata = mAdapter.getItem(position);
                String title=Mydata.getTitle();
                String image=Mydata.thumbnail;
                String weburl=Mydata.getUrlOfStory();
                String bookmarked=Mydata.issaved();
                openDialog(title,image,weburl,bookmarked, Mydata,view);
                return true;
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                CallYourRefreshingMethod();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });


        return rootView;
    }

    private void CallYourRefreshingMethod() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="http://thisishw8.appspot.com/api/world";
        final HashMap<String, Integer> map = new HashMap<>();
        int counter=0;
        for (Mydata var :news_collection)
        {
            String currentkey=var.getUrlOfStory();
            map.put(currentkey,counter);
            counter++;

        }



        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        List<Mydata> fetchedlist= new ArrayList<>();;

                        try {
                            JSONObject baseJson = new JSONObject(response);
                            if (baseJson.has("response")) {


                                baseJson=baseJson.getJSONObject("response");
                            }
                            JSONArray news_array = baseJson.getJSONArray("results");
                            System.out.println("swipe refresh news_array is " +news_array);
                            int counter=0;
                            for (int i = 0; i < news_array.length(); i++) {

                                try {
                                    System.out.println("swipe refresh iteration is " + i);
                                    JSONObject article = news_array.getJSONObject(i);
                                    String url = article.getString("webUrl");
                                    if (map.containsKey(url)) {
                                        int index = map.get(url);
                                        Mydata temp =news_collection.get(index);
                                        fetchedlist.add(temp);
                                        System.out.println("swipe refresh  already exists");
                                        continue;

                                    }
                                    String bookmarked = "no";
                                    String nameOfSection = article.getString("sectionName");

                                    String title = article.getString("webTitle");
                                    String date = article.getString("webPublicationDate");

                                    String id = article.getString("id");


                                    JSONArray elements = article.getJSONObject("blocks").getJSONObject("main").getJSONArray("elements");
                                    JSONArray bodyArray = article.getJSONObject("blocks").getJSONArray("body");
                                    JSONObject bodyelem = bodyArray.getJSONObject(0);
                                    String body = bodyelem.getString("bodyTextSummary");
                                    System.out.println("testing elements is " + elements);
                                    JSONObject temp = elements.getJSONObject(0);
                                    System.out.println("testing temp is " + temp);
                                    JSONArray assets = temp.getJSONArray("assets");
                                    System.out.println("testing assets is " + assets);
                                    if (assets.length() > 0) {
                                        JSONObject t2 = assets.getJSONObject(0);
                                        System.out.println("testing t2 is " + t2);
                                        String thumbnail = t2.getString("file");
                                        System.out.println("testing thumbnail is" + thumbnail);
                                        List<String> contributor = new ArrayList<>();
                                        Mydata news = new Mydata(title, thumbnail, url, date, nameOfSection, contributor, body, id, bookmarked);
                                        //newsArrayList.add(news);
                                        fetchedlist.add(news);
                                        news_collection.add(0,news);
                                        news_collection.remove(news_collection.size() - 1);//.insert(news,counter);
                                        System.out.println("swipe refresh   adapter  updated at " + counter);
                                        counter++;
                                    }
                                    System.out.println("swipe refresh   adapter added");
                                }   catch(Exception e){
                                    System.out.println("swipe refresh error is " + e.toString());
                                    continue;

                                }
                            }

                        }catch(Exception e){
                            Log.d("world enws fragment","error refetching articles");
                        }
                        mAdapter.clear();
                        mAdapter.addAll(news_collection);
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void openDialog(final String title, final String imageUrl, final String weburl, final String bookmarked, final Mydata Mydata, final View view) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_layout_v2);
        ImageView dialog_image=(ImageView) dialog.findViewById(R.id.dialog_image);

        Picasso.get().setLoggingEnabled(true);
        TextView dialogtitle=(TextView) dialog.findViewById(R.id.dialog_title);
        dialogtitle.setText(title);
        System.out.println("dialog image url is");
        System.out.print("dialog image url is " + imageUrl);
        Picasso.get()
                .load(imageUrl)
                .into(dialog_image);
        ImageButton twitter_btn=(ImageButton) dialog.findViewById(R.id.dialog_twitter);
        final ImageButton bookmark_btn=(ImageButton) dialog.findViewById(R.id.dialog_bookmark);
        if(bookmarked.equals("yes")){
            bookmark_btn.setImageResource(R.drawable.bookmark_fill);
        }
        else{
            bookmark_btn.setImageResource(R.drawable.bookmark_empty);
        }

        twitter_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String TWITTER_URL = "https://twitter.com/intent/tweet";
                Uri.Builder builder = Uri.parse(TWITTER_URL).buildUpon();
                String tweet="Check out this Link:" + "\n" + weburl;
                builder.appendQueryParameter("text", tweet)
                        .appendQueryParameter("hashtags", "CSCI571NewsSearch");
                String url=builder.toString();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                // Note the Chooser below. If no applications match,
                // Android displays a system message.So here there is no need for try-catch.
                startActivity(Intent.createChooser(intent, "Browse with"));
            }
        });
        //Bookmark Button Listener
        bookmark_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button

                System.out.println("Figure out World Called");
                String toast_Add_msg='"'+title+'"'+ " was added to Bookmarks";
                String toast_Remove_msg='"'+title+'"'+ " was removed from Bookmarks";
                ImageButton card_bookmark_btn= (ImageButton) view.findViewById(R.id.bookmark_image);

                if(Mydata.issaved().equals("yes")){
                    bookmark_btn.setImageResource(R.drawable.bookmark_empty);
                    card_bookmark_btn.setImageResource(R.drawable.bookmark_empty);
                    Mydata.savedstate("no");

                    Toast.makeText(getActivity(),toast_Remove_msg,Toast.LENGTH_SHORT).show();
                    SharedPreferences pref = getActivity().getSharedPreferences("Bookmarked_articles", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    if(pref.contains(weburl)){
                        editor.remove(weburl).commit();
                    }

                }
                else{
                    bookmark_btn.setImageResource(R.drawable.bookmark_fill);
                    card_bookmark_btn.setImageResource(R.drawable.bookmark_fill);
                    Mydata.savedstate("yes");
                    Toast.makeText(getActivity(),toast_Add_msg,Toast.LENGTH_SHORT).show();

                    SharedPreferences pref = getActivity().getSharedPreferences("Bookmarked_articles", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    String json = gson.toJson(Mydata);
                    editor.putString(weburl, json);

                    editor.commit();
                }
            }
        });


        dialog.show();
    }

int position;
        String detail_bookmarked;
        System.out.println("World on Load finished called ");
       fetching_news.setVisibility(View.GONE);
        loader.setVisibility(View.GONE);
        if(!HeadlinesActivity.WORLD_LOADED) {
            mAdapter.clear();
        }
        else{
            SharedPreferences pref = getActivity().getSharedPreferences("Bookmarked_articles", 0); // 0 - for private mode
            if(pref.contains(DetailPage.DETAIL_ARTICLE_POSITION)) {
                position = pref.getInt(DetailPage.DETAIL_ARTICLE_POSITION,-1);


                System.out.println("world on else called");
                try{
                    Mydata tempnews = mAdapter.getItem(position);
                    if(pref.contains(DetailPage.BOOKMARK_DETAIL_TAG)) {
                        detail_bookmarked = pref.getString(DetailPage.BOOKMARK_DETAIL_TAG, null);

                        if (detail_bookmarked.equals("yes")) {
                            tempnews.savedstate("yes");
                        } else {
                            tempnews.savedstate("no");

                        }
                       if(item.size()>position){
                        item.set(position, tempnews);}
                    }}
                catch(Exception e){
                    System.out.println("world on else called error " + e);
                }

            }
        }

        if (item != null) {
            news_collection=item;
            HeadlinesActivity.WORLD_LOADED=true;
            mAdapter.addAll(news_collection); }
    }
}
