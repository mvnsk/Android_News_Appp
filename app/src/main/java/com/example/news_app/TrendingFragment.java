package com.example.news_app;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LineChart lineChart;
    TextView textView;
    //SearchView searchView;
    EditText searchView;
    String searchStr;

    ProgressBar progressBar;
    RelativeLayout spinnerView;

    public TrendingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrendingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrendingFragment newInstance(String param1, String param2) {
        TrendingFragment fragment = new TrendingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_trending, container, false);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar_trending);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#2D42BD"), PorterDuff.Mode.MULTIPLY);
        spinnerView = (RelativeLayout)rootView.findViewById(R.id.spinner_trending);



        lineChart = (LineChart)rootView.findViewById(R.id.line_chart);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        //textView = (TextView)rootView.findViewById(R.id.textview_trending);
        //searchView = (SearchView)rootView.findViewById(R.id.trending_page_search);

        searchView = (EditText)rootView.findViewById(R.id.trending_page_search);


        /*
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new getTrendsData().execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/

        searchView.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){

                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String currWord = searchView.getText().toString();
                    new getTrendsData().execute(currWord);
                    return true;
                }

                return false;
            }
        });

        searchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    String currWord = searchView.getText().toString();
                    new getTrendsData().execute(currWord);
                    return true;
                }

                return false;
            }
        });



        new getTrendsData().execute("Coronavirus");
        return rootView;
    }

    private class getTrendsData extends AsyncTask<String,Void,String>{


        @Override
        protected void onPreExecute(){

            spinnerView.setVisibility(View.VISIBLE);

        }



        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("https://android-backend-274622.wn.r.appspot.com/api/trends/?word="+params[0]);
                searchStr = params[0];
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

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(String response){
            if(response!=null){
                final List<Entry> entries_one= new ArrayList<>();
//
//                JsonObject gson = new JsonParser().parse(response).getAsJsonObject();
               /* try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray values = jsonObject.getJSONArray("data");

                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    StringBuilder sb = new StringBuilder("TEST");
                    for(int i = 0;i<values.length();i++)
                    {
                        int val = values.getInt(i);
                         entries.add(new Entry(i, val));

                    }*/
                try {
                    JSONObject responseobject = new JSONObject(response);
                    JSONArray timeline= responseobject.getJSONObject("default").getJSONArray("timelineData");

                    int[] values = new int[timeline.length()];
                    for(int i=0;i<timeline.length();i++){
                        JSONObject currentobject=timeline.getJSONObject(i);
                        System.out.println("draw chart currentobject " + currentobject);
                        int current_int= (int) currentobject.getJSONArray("value").get(0);
                        System.out.println("draw chart current_int " + current_int);
                        entries_one.add(new Entry(i,current_int));

                        values[i]=current_int;
                    }
//
/*
                    LineDataSet dataSet = new LineDataSet(entries, "Trends Chart");

                    LineData data = new LineData(dataSet);
                    lineChart.setData(data);

                    lineChart.invalidate();
*/
                  /*  LineDataSet dataSet1 = new LineDataSet(entries,"Trending chart for " + searchStr);

                    dataSet1.setColor(getActivity().getColor(R.color.colorPrimary));
                    dataSet1.setCircleColor(getActivity().getColor(R.color.colorPrimary));
                    //dataSet1.setCircleColorHole(getActivity().getColor(R.color.colorPrimary));
                    lineChart.getLegend().setTextColor(Color.BLACK);
                    lineChart.getLegend().setFormSize(24);
                    lineChart.getLegend().setTextSize(24);
                    List<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(dataSet1);

                    LineData data = new LineData(dataSets);
                    /*
                    lineChart.setData(data);
                    lineChart.invalidate();
                    */

                    //Removing grid lines
                    LineDataSet lineDataSet=new LineDataSet(entries_one,"Trending chart for "+ searchStr );
                    lineDataSet.setColor(Color.parseColor("#b589d6"));
                    lineDataSet.setHighLightColor(Color.parseColor("#b589d6"));
                    lineDataSet.setCircleColor(Color.parseColor("#b589d6"));
                    lineDataSet.setColor(Color.parseColor("#4b4680"));
                    List<ILineDataSet> dataSets=new ArrayList<>();
                    dataSets.add(lineDataSet);
                    lineChart.getLegend().setTextColor(Color.BLACK);
                    lineChart.getLegend().setFormSize(24);
                    lineChart.getLegend().setTextSize(24);
                    LineData data=new LineData(dataSets);

                    lineChart.getXAxis().setDrawGridLines(false);
                    lineChart.getAxisLeft().setDrawGridLines(false);
                    lineChart.getAxisRight().setDrawGridLines(false);


                    //Helper code
                    lineChart.getAxisLeft().setDrawGridLines(false);
                    lineChart.getAxisRight().setDrawGridLines(false);
                    lineChart.getAxisLeft().setDrawZeroLine(false);
                    lineChart.getXAxis().setAxisLineWidth(0);
                    lineChart.getAxisLeft().setZeroLineWidth(0);
                    lineChart.getAxisLeft().setAxisLineWidth(0);
                    lineChart.getAxisRight().setAxisLineWidth(0);
                    lineChart.getAxisRight().setDrawZeroLine(false);
                    lineChart.getAxisLeft().setAxisLineColor(Color.parseColor("#FFFFFF"));


                    lineChart.setData(data);
                    lineChart.invalidate();

                } catch (JSONException e) {
                    textView.setText(e.toString());
                }
            }
            spinnerView.setVisibility(View.GONE);
        }
    }
}
