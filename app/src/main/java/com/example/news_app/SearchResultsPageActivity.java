package com.example.news_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchResultsPageActivity extends AppCompatActivity {


    ProgressBar progressBar;
    ProgrammingAdapter programmingAdapter;
    RecyclerView recyclerView;
    TextView temp;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    RelativeLayout spinnerView;

    @Override
    protected void onStart(){
        super.onStart();
        try{
            SharedPreferences pref = this.getSharedPreferences("Mugdha_hw9", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            String isChanged = pref.getString("changed","");
            if(isChanged.equals("true")){
                String positionOfItem = pref.getString("itemPosition","");
                // Toast.makeText(getActivity(), positionOfItem,Toast.LENGTH_SHORT).show();
                programmingAdapter.reloadHelper(positionOfItem);
            }

        }catch (Exception e){
            // Do nothing for now
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_search_results_page);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_searchresults);

        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#2D42BD"), PorterDuff.Mode.MULTIPLY);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_searchresults);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        spinnerView = (RelativeLayout)findViewById(R.id.spinner_search);

        Intent intent = getIntent();
        final String keyword = intent.getStringExtra("keyword");
        temp = (TextView)findViewById(R.id.temp_search);
//        temp.setText(keyword);

        ActionBar currActionBar= getSupportActionBar();
        currActionBar.setTitle("Search results for " + keyword);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_search);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getSearchResults().execute(keyword);
            }
        });

        new getSearchResults().execute(keyword);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        super.onOptionsItemSelected(menuItem);
        onBackPressed();
        return true;
    }

    private class getSearchResults extends AsyncTask<String,Void,String>{


        @Override
        protected void onPreExecute(){
            if(!mSwipeRefreshLayout.isRefreshing())
                {
                    //progressBar.setVisibility(View.VISIBLE);
                    spinnerView.setVisibility(View.VISIBLE);
                }
        }

        @Override
        protected String doInBackground(String ... params){


            try{
                URL url = new URL("https://frontendinreact.wn.r.appspot.com/get_search_results_guardian?keyword="+params[0]);
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
            }catch (Exception e){
                e.printStackTrace();
                return e.toString();
            }


        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String response){
            if(response!=null){
                programmingAdapter = new ProgrammingAdapter(response);
                recyclerView.setAdapter(programmingAdapter);
//                temp.setText(response);
            }

            //progressBar.setVisibility(View.GONE);
            spinnerView.setVisibility(View.GONE);
            if(mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }



    }
}
