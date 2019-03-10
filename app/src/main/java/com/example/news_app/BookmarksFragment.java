package com.example.news_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    ProgrammingAdapterBookmarks programmingAdapter;

    TextView textView;
    TextView noData;


    public BookmarksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookmarksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookmarksFragment newInstance(String param1, String param2) {
        BookmarksFragment fragment = new BookmarksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView();
        if(view!=null){
            //Don't know what to do
        }

        try{
            SharedPreferences pref = getActivity().getSharedPreferences("Mugdha_hw9", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            String isChanged = pref.getString("changed","");
            if(isChanged.equals("true")){
                String positionOfItem = pref.getString("itemPosition","");
                programmingAdapter.reloadHelper(positionOfItem);
            }




        }catch(Exception e){
            //Do nothing
        }

        //Check the number of bookmarked articles
        int numArticles = new SharedPreferenceHelper().getNumberOfBookmarkedArticles(getActivity());
        if(numArticles!=programmingAdapter.getNumberOfArticles())
        {
            programmingAdapter.revampDataArray(getActivity());
        }

        //Check if bookmark data is present, if not, show no data present
        if(numArticles == 0){
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }



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
        View rootView =  inflater.inflate(R.layout.fragment_bookmarks, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_bookmarkspage);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        //        textView = (TextView)rootView.findViewById(R.id.bookmarks_text);
        noData = (TextView)rootView.findViewById(R.id.no_data_bookmarks);

        //Check if bookmarks data is present, otherwise show no data text
        int numArticles = new SharedPreferenceHelper().getNumberOfBookmarkedArticles(getActivity());
        if(numArticles == 0){
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }


        getBookmarksData();
        return rootView;

    }

    public void getBookmarksData(){
        SharedPreferenceHelper helper = new SharedPreferenceHelper();
        ArrayList<BookmarkObjectStructure> bookmarksArray = helper.getFavorites(getActivity());
        programmingAdapter = new ProgrammingAdapterBookmarks(bookmarksArray);
        recyclerView.setAdapter(programmingAdapter);
        programmingAdapter.setParentFragment(BookmarksFragment.this);
        //Set the adapter for recycler view here
//        textView.setText(String.valueOf(bookmarksArray.size()));
    }

    public void noDataHelper(){
        // Set the text to no bookmarks data
        noData.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

}
