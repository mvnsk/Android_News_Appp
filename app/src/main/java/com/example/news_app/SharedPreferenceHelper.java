package com.example.news_app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;

public class SharedPreferenceHelper {

    public static final String PREFS_NAME = "Mugdha_hw9";
    public static final String FAVORITES = "Product_Favorite";

    public SharedPreferenceHelper(){
        super();
    }


    //Get the number of bookmarked articles
    public int getNumberOfBookmarkedArticles(Context context){
        List<BookmarkObjectStructure> favorites = getFavorites(context);
        if(favorites == null)
        {
            return 0;
        }
        return favorites.size();
    }

    //The following 4 methods are for maintaining favorites

    public void saveFavorites(Context context, List<BookmarkObjectStructure> favorites){
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString(FAVORITES,jsonFavorites);
        editor.commit();
    }

    public void addFavorite(Context context, BookmarkObjectStructure obj){
        List<BookmarkObjectStructure> favorites = getFavorites(context);
        if (favorites == null){
            favorites = new ArrayList<BookmarkObjectStructure>();
        }
        favorites.add(obj);
        saveFavorites(context,favorites);
    }


    public void removeFavorite(Context context, BookmarkObjectStructure obj){
        ArrayList<BookmarkObjectStructure> favorites = getFavorites(context);
        if(favorites!=null){
            /*
            favorites.remove(obj);
            saveFavorites(context,favorites);
             */
            ArrayList<BookmarkObjectStructure> newArrayList = new ArrayList<BookmarkObjectStructure>();
            for(BookmarkObjectStructure element: favorites){
                if(element.getId().equals(obj.getId()))
                {
                    //Do nothing
                }
                else
                {
                    newArrayList.add(element);
                }
            }
            saveFavorites(context,newArrayList);
        }
    }

    public ArrayList<BookmarkObjectStructure> getFavorites (Context context)
    {
        SharedPreferences settings;
        List<BookmarkObjectStructure> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if(settings.contains(FAVORITES)){
            String jsonFavorites = settings.getString(FAVORITES,null);
            Gson gson = new Gson();
            BookmarkObjectStructure[] favoriteItems = gson.fromJson(jsonFavorites, BookmarkObjectStructure[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<BookmarkObjectStructure>(favorites);
        }
        else
            return null;
        return (ArrayList<BookmarkObjectStructure>) favorites;
    }

    public boolean checkIfBookmarked(Context context,BookmarkObjectStructure obj){

        SharedPreferences settings;
        List<BookmarkObjectStructure> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        Boolean answer = false;
        if(settings.contains(FAVORITES)){
            String jsonFavorites = settings.getString(FAVORITES,null);
            Gson gson = new Gson();
            BookmarkObjectStructure[] favoriteItems = gson.fromJson(jsonFavorites, BookmarkObjectStructure[].class);
            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<BookmarkObjectStructure>(favorites);


                for(BookmarkObjectStructure curr: favorites){


                        if(curr.getId().equals(obj.getId())){
                            answer = true;
                            break;
                        }


                }


        }

       return answer;

    }
}
