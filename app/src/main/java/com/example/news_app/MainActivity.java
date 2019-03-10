package com.example.news_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    private Handler handler;
    SearchView searchView;
    TextView temp;
    String message = "INITAL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        //this.getSharedPreferences("Mugdha_hw9",0).edit().clear().commit();
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.nav_view);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        temp = (TextView)findViewById(R.id.temp);
        openFragment(HomeFragment.newInstance("",""));


    }

    public  void openFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_home:
                    openFragment(HomeFragment.newInstance("", ""));
                    return true;
                case R.id.bottom_navigation_headlines:
                     openFragment(HeadlinesFragment.newInstance("", ""));
                    return true;
                case R.id.bottom_navigation_trending:
                    openFragment(TrendingFragment.newInstance("", ""));
                    return true;
                case R.id.bottom_navigation_bookmarks:
                     openFragment(BookmarksFragment.newInstance("",""));
                    return true;
            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu,menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView)item.getActionView();





        searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                this,android.R.layout.simple_list_item_1,null,new String[] {SearchManager.SUGGEST_COLUMN_TEXT_1},new int[]{android.R.id.text1}
        ));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if(query.trim().length()>=3)
                {
                    new FetchSearchTermSuggestionsTask().execute(query);
                }
                else
                {
                    searchView.getSuggestionsAdapter().changeCursor(null);
                }
                return true;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {

                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String term = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                cursor.close();

                searchView.setQuery(term, false);
                Intent intent = new Intent(MainActivity.this, SearchResultsPageActivity.class);
                intent.putExtra("keyword",term);

                startActivity(intent);

                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                return onSuggestionSelect(position);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    private class FetchSearchTermSuggestionsTask extends AsyncTask<String,Void, Cursor>{


         private final String[] sAutoCompleteColNames = new String[]{
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1
        };

        @Override
        protected Cursor doInBackground(String ... params){
//            Object[] row1= new Object[]{1, "India"};
//            Object[] row2 = new Object[]{2, "Modi"};
//            Object[] row3 = new Object[]{3, "USA"};
//            Object[] row4 = new Object[]{4, "USC"};

            MatrixCursor cursor = new MatrixCursor(sAutoCompleteColNames);
//            cursor.addRow(row1);
//            cursor.addRow(row2);
//            cursor.addRow(row3);
//            cursor.addRow(row4);
             message = "INITIAL";


            try{



                URL url = new URL("https://api.cognitive.microsoft.com/bing/v7.0/suggestions?mkt=fr-FR&q="+params[0]);
                HttpsURLConnection connection = null;
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Ocp-Apim-Subscription-Key","da80b735fc974f94994c83896d2f3a63");

                try{
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while( (line = bufferedReader.readLine())!=null ){
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    String response =  stringBuilder.toString();
                    message = response;

                    JsonObject gson = new JsonParser().parse(response).getAsJsonObject();
                    try{

                        JSONObject jsonObject = new JSONObject(gson.toString());
                        JSONArray suggestionGroups = jsonObject.getJSONArray("suggestionGroups");
                        JSONObject tempObj = suggestionGroups.getJSONObject(0);
                        JSONArray resultsRaw = tempObj.getJSONArray("searchSuggestions");

                        int tempIndex = 0;
                        for(int i = 0;i<resultsRaw.length();i++){
                            if(tempIndex<5){
                                JSONObject currObj = resultsRaw.getJSONObject(i);
                                String textValCurrent = currObj.getString("query");
                                Object[] row = new Object[]{i, textValCurrent };
                                cursor.addRow(row);

                            }
                            else
                            {
                                break;
                            }
                            tempIndex += 1;

                        }

                    }catch (Exception e){
                        message = e.toString();
                        e.printStackTrace();
                    }



                }catch (Exception e){
                    message = e.toString();
                }


            }catch (Exception e){

                message = e.toString();
                //Do nothing for now
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor result){
            searchView.getSuggestionsAdapter().changeCursor(result);
//            temp.setText(message);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        HomeFragment frag = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.container);
//        Toast.makeText(this, "MAIN 1",Toast.LENGTH_SHORT).show();
//        openFragment(HomeFragment.newInstance("", ""));
        frag.onActivityResult(requestCode,resultCode,data);

    }




}

// api_key='da80b735fc974f94994c83896d2f3a63'