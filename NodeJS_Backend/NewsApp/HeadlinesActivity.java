package com.example.myapplication.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.myapplication.Adapters.AutoSuggestAdapter;
import com.example.myapplication.R;

import com.example.myapplication.Adapters.SimpleFragmentPagerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import Helpers.BottomNavigationViewHelper;

public class HeadlinesActivity  extends AppCompatActivity {

    public static boolean WORLD_LOADED=false;
    AutoSuggestAdapter autoSuggestAdapter;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headlines);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tab_layout = (TabLayout) findViewById(R.id.toolbar_tabs);
        tab_layout.setupWithViewPager(viewPager);

        Toolbar toolbar=(Toolbar)findViewById(R.id.custom_home_toolbar);
        setSupportActionBar(toolbar);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu=bottomNavigationView.getMenu();
        MenuItem menuItem=menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent homeIntent = new Intent(HeadlinesActivity.this, MainActivity.class);
                        startActivity(homeIntent);
                        break;

                    case R.id.headline:

                        break;
                    case R.id.trending:
                        Intent trendingIntent = new Intent(HeadlinesActivity.this, TrendingNewsActivity.class);
                        startActivity(trendingIntent);
                        break;
                    case R.id.bookmark:
                        Intent favouriteIntent = new Intent(HeadlinesActivity.this, BookMarkActivity.class);
                        startActivity(favouriteIntent);
                        break;
                }

                return false;
            }
        });
            }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.dummy_search,menu);
        MenuItem searchitem=menu.findItem(R.id.action_search);
        SearchView searchView=(   SearchView)   searchitem.getActionView();
        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setThreshold(3);
        searchAutoComplete.setAdapter(autoSuggestAdapter);


        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);


                Intent opensearchpage= new Intent(HeadlinesActivity.this, SearchPageActivity.class);

                opensearchpage.putExtra("query",queryString);
                startActivity(opensearchpage);
            }
        });
        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
                System.out.println("sai baba before text called");
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println("sai baba query text submit called");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("sai baba text change called");
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void makeApiCall(String text) {
        ApiCall.make(this, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)      {

                List<String> stringList = new ArrayList<>();
                try {

                    JSONObject responseObject = new JSONObject(response);
                    System.out.println("sai baba responded object is " + responseObject);
                    JSONArray suggestionGroups = responseObject.getJSONArray("suggestionGroups");
                    JSONObject temp=   suggestionGroups.getJSONObject(0);
                    JSONArray searchSuggestions=temp.getJSONArray("searchSuggestions");

                    for (int i = 0; i < searchSuggestions.length(); i++) {
                        //restricting to  5 suggestions
                        if (i==5){break;}
                        JSONObject row = searchSuggestions.getJSONObject(i);
                        stringList.add(row.getString("displayText"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                autoSuggestAdapter.setData(stringList);


                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }


    }

