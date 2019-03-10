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
//import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class BusinessFragment extends Fragment {

    private  int page;
    private String title;
    RecyclerView recyclerView;
    ProgrammingAdapter programmingAdapter;
    ProgressBar progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    RelativeLayout spinnerView;

    public BusinessFragment() {
        // Required empty public constructor
    }

    public static  BusinessFragment newInstance(int page, String title){
        BusinessFragment frag = new BusinessFragment();
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
        View rootView =  inflater.inflate(R.layout.fragment_business, container, false);
//        new getBusinessHeadlines().execute();

        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#2D42BD"), PorterDuff.Mode.MULTIPLY);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));

//        String[] languages = {"Java","Javascript","C#","Python","Ruby","PHP","Java","Javascript","C#","Python","Ruby","PHP","Java","Javascript","C#","Python","Ruby","PHP","Java","Javascript","C#","Python","Ruby","PHP","Java","Javascript","C#","Python","Ruby","PHP","Java","Javascript","C#","Python","Ruby","PHP","Java","Javascript","C#","Python","Ruby","PHP","Java","Javascript","C#","Python","Ruby","PHP","Java","Javascript","C#","Python","Ruby","PHP","Java","Javascript","C#","Python","Ruby","PHP","Java","Javascript","C#","Python","Ruby","PHP"};
//        programmingAdapter = new ProgrammingAdapter(languages);

        spinnerView = (RelativeLayout)rootView.findViewById(R.id.spinner_business);

        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_business);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new getBusinessHeadlines().execute();
            }
        });


        new getBusinessHeadlines().execute();

        //        recyclerView.setAdapter(listAdapter);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
        return  rootView;
    }


    private class getBusinessHeadlines extends AsyncTask<Void,Void,String>{


        @Override
        protected void onPreExecute(){
            if(!mSwipeRefreshLayout.isRefreshing())
                {
                   // progressBar.setVisibility(View.VISIBLE);
                    spinnerView.setVisibility(View.VISIBLE);
                }

        }

        @Override
        protected String doInBackground(Void ... params){
            //Do nothing
            try {
                URL url = new URL("https://frontendinreact.wn.r.appspot.com/get_top10_business_guardian");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try{
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while( (line = bufferedReader.readLine())!=null ){
                        //stringBuilder.append(line).append("\n");
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
//                return e.toString();
            }

            return null;


        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String response){
//            TextView textView = (TextView) getView().findViewById(R.id.business_fragment_text);
//            textView.setText(response);

                if(response!=null){

                  programmingAdapter = new ProgrammingAdapter(response);
                  recyclerView.setAdapter(programmingAdapter);
//                  JsonObject gson = new JsonParser().parse(response).getAsJsonObject();
//                  try {
//                      JSONObject jsonObject = new JSONObject(gson.toString());
//
//
//                  } catch (JSONException e) {
//                      e.printStackTrace();
//                  }
              }
                spinnerView.setVisibility(View.GONE);
            //progressBar.setVisibility(View.GONE);
                if(mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);

        }


    }



}
