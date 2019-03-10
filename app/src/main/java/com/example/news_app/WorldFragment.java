package com.example.news_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorldFragment extends Fragment {

    private int page;
    private  String title;
    RecyclerView recyclerView;
    ProgrammingAdapter programmingAdapter;
    ProgressBar progressBar;
    private SwipeRefreshLayout mswipeRefreshLayout;
    RelativeLayout spinnerView;


    public WorldFragment() {
        // Required empty public constructor
    }

    public static  WorldFragment newInstance(int page, String title){
        WorldFragment frag = new WorldFragment();
        Bundle args = new Bundle();
        args.putInt("someInt",page);
        args.putString("someTitle",title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView();

        if(view != null){
            // Don't know what to do
        }

        try{

            SharedPreferences pref = getActivity().getSharedPreferences("Mugdha_hw9", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            String isChanged = pref.getString("changed","");
            if(isChanged.equals("true")){
                String positionOfItem = pref.getString("itemPosition","");
                programmingAdapter.reloadHelper(positionOfItem);
            }

        }catch (Exception e){
            // Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt",0);
        title = getArguments().getString("someTitle","someTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_world, container, false);
        progressBar = rootView.findViewById(R.id.progress_bar_world);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#2D42BD"), PorterDuff.Mode.MULTIPLY);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_world);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        spinnerView = (RelativeLayout)rootView.findViewById(R.id.spinner_world);

        mswipeRefreshLayout = rootView.findViewById(R.id.refresh_world);
        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getWorldHeadlines().execute();
            }
        });


        new getWorldHeadlines().execute();

        return rootView;
    }

    private class getWorldHeadlines extends AsyncTask<Void,Void,String>{

        @Override
        protected void onPreExecute(){
            if(!mswipeRefreshLayout.isRefreshing())
                {
                //    progressBar.setVisibility(View.VISIBLE);
                spinnerView.setVisibility(View.VISIBLE);
                }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://frontendinreact.wn.r.appspot.com/get_top10_world_guardian");
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String response){
            if(response!=null){
                programmingAdapter = new ProgrammingAdapter(response);
                recyclerView.setAdapter(programmingAdapter);
            }
            if(mswipeRefreshLayout.isRefreshing())
                mswipeRefreshLayout.setRefreshing(false);
            //progressBar.setVisibility(View.GONE);
            spinnerView.setVisibility(View.GONE);
        }
    }

}
