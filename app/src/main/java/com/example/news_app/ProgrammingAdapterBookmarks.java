package com.example.news_app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProgrammingAdapterBookmarks extends RecyclerView.Adapter<ProgrammingAdapterBookmarks.ProgrammingViewHolderBookmarks> {

    private List<BookmarkObjectStructure> dataArray;
    private Context context;
    private BookmarksFragment parentFragment;

    public void setParentFragment(BookmarksFragment parentFragment){
        this.parentFragment = parentFragment;
    }

    public int getNumberOfArticles(){
        if(dataArray == null)
            return 0;
        return dataArray.size();
    }

    public void reloadHelper(String position1){
//        notifyItemChanged(Integer.parseInt(position1));
//        notifyDataSetChanged();

        dataArray.remove(Integer.parseInt(position1));
        notifyItemRemoved(Integer.parseInt(position1));
    }

    public void revampDataArray(Context ctx){

        SharedPreferenceHelper helper = new SharedPreferenceHelper();
        ArrayList<BookmarkObjectStructure> bookmarksArray = helper.getFavorites(ctx);
        this.dataArray = bookmarksArray;

    }

    public ProgrammingAdapterBookmarks(ArrayList<BookmarkObjectStructure> currBookmarks){
        this.dataArray = new ArrayList<BookmarkObjectStructure>();
        if(currBookmarks!=null)
        {
            for(BookmarkObjectStructure element: currBookmarks){
                dataArray.add(element);
            }
        }

    }

    @NonNull
    @Override
    public ProgrammingViewHolderBookmarks onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.bookmark_card_layout,parent,false);
        return new ProgrammingViewHolderBookmarks(view);
    }


    @Override
    public int getItemCount(){
        return dataArray.size();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onBindViewHolder(@NonNull final ProgrammingViewHolderBookmarks holder, final int position){
        final BookmarkObjectStructure current = dataArray.get(position);
        holder.titleView.setText(current.getTitle());

        try{

            Instant timestamp = Instant.parse(current.getDate());
            ZonedDateTime zonedDate = timestamp.atZone(ZoneId.of("America/Los_Angeles"));

            int day = zonedDate.getDayOfMonth();
            String dayStr = String.valueOf(day);
            if(day<10){
                dayStr = "0" + dayStr;
            }

            Month month = zonedDate.getMonth();
            String monStr = String.valueOf(month);
            monStr = monStr.toLowerCase();
            monStr = monStr.substring(0,1).toUpperCase() + monStr.substring(1);

            int year = zonedDate.getYear();

            holder.date.setText(dayStr + " " + monStr.substring(0,3) + " | " + current.getSection());

        }catch (Exception e){
            holder.date.setText("07 May | " + current.getSection());
        }

        //holder.date.setText(current.getDate());
        //holder.date.setText(dayStr + " " + monStr.substring(0,3) + " | " + "A very long string, let us test the results");


        //holder.date.setText(current.getPeriodDiff());
        try{
            // Picasso.get().load(current.getImage()).fit().into(holder.mainImageView);
            Picasso.get().load(current.getImage()).resize(120,120).into(holder.mainImageView);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(isBookmarked(context,current)){
            holder.bookmarkIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
            holder.bookmarkIcon.setTag("0");

        }
        else
        {
            holder.bookmarkIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
            holder.bookmarkIcon.setTag("1");
        }

        holder.clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DetailsPageActivity.class);
                intent.putExtra("id",current.getId());

                intent.putExtra("itemPosition",String.valueOf(position));

                // Add perdiodDiff as extra information
                intent.putExtra("periodDifference",current.getPeriodDiff());
                intent.putExtra("title",current.getTitle());
                intent.putExtra("sharing_url",current.getUrl());

                SharedPreferences pref = context.getSharedPreferences("Mugdha_hw9",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("changed","false");
                editor.commit();

                context.startActivity(intent);
            }
        });

        holder.bookmarkIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, current.getTitle(), Toast.LENGTH_LONG).show();
                String tag = String.valueOf(v.getTag());
                switch (tag){
                    case "1" :
//                        Toast.makeText(context, "1",Toast.LENGTH_SHORT).show();
                        String str1 = "\"" + current.getTitle() + "\" was added to Bookmarks";
                        Toast.makeText(context,str1,Toast.LENGTH_SHORT).show();
                        holder.bookmarkIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                        v.setTag("0");
                        addToBookmarks(context, current);
                        break;
                    default:
//                        Toast.makeText(context, "default",Toast.LENGTH_SHORT).show();
                        String str2 = "\"" + current.getTitle() + "\" was removed from Bookmarks";
                        Toast.makeText(context,str2,Toast.LENGTH_SHORT).show();
                        holder.bookmarkIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                        v.setTag("1");
                        removeFromBookmarks(context, current);
                        break;
                }

                notifyDataSetChanged();
            }

        });

        holder.clickable.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.modal_layout);

                ImageView dialogImage = (ImageView)dialog.findViewById(R.id.modal_main_image);
                TextView dialogTitle = (TextView) dialog.findViewById(R.id.modal_title);
                ImageView twitterButton = (ImageView)dialog.findViewById(R.id.modal_twitter);
                final ImageView dialogBookmark = (ImageView)dialog.findViewById(R.id.modal_bookmark_icon);

                Picasso.get().load(current.getImage()).resize(150,150).into(dialogImage);
                dialogTitle.setText(current.getTitle());

                twitterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String URL = "https://twitter.com/intent/tweet";
                        Uri.Builder builder = Uri.parse(URL).buildUpon();
                        String tweet = "Check out this Link:" + "\n" + current.getUrl();
                        builder.appendQueryParameter("text",tweet).appendQueryParameter("hashtags","CSCI571NewsSearch");
                        String url = builder.toString();
                        Intent twitterIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                        context.startActivity(Intent.createChooser(twitterIntent,"Browse with"));

                    }
                });

                if(isBookmarked(context,current))
                {
                    //Set the image for bookmarked
                    dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                    dialogBookmark.setTag("0");
                }
                else
                {
                    //set the image for not bookmarked
                    dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                    dialogBookmark.setTag("1");
                }

                dialogBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tag = String.valueOf(v.getTag());
                        switch (tag){
                            case "1" :
//                        Toast.makeText(context, "1",Toast.LENGTH_SHORT).show();
                                String str3 = "\"" + current.getTitle() + "\" was added to Bookmarks";
                                Toast.makeText(context,str3,Toast.LENGTH_SHORT).show();
                                holder.bookmarkIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                                dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp));
                                holder.bookmarkIcon.setTag("0");
                                dialogBookmark.setTag("0");
                                addToBookmarks(context, current);
                                notifyDataSetChanged();
                                break;
                            default:
//                        Toast.makeText(context, "default",Toast.LENGTH_SHORT).show();
                                String str4 = "\"" + current.getTitle() + "\" was removed from Bookmarks";
                                Toast.makeText(context,str4,Toast.LENGTH_SHORT).show();
                                holder.bookmarkIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                                dialogBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                                holder.bookmarkIcon.setTag("1");
                                dialogBookmark.setTag("1");
                                removeFromBookmarks(context, current);
                                notifyDataSetChanged();
                                break;
                        }

                    }
                });


                dialog.show();
                return true;
            }
        });


    }

    public class ProgrammingViewHolderBookmarks extends RecyclerView.ViewHolder{
        ImageView mainImageView;
        TextView titleView;
        ImageView bookmarkIcon;
        View clickable;
        TextView date;

        public ProgrammingViewHolderBookmarks(View itemView){
            super(itemView);
            mainImageView = itemView.findViewById(R.id.bookmark_card_image);
            titleView = itemView.findViewById(R.id.bookmark_card_title);
            bookmarkIcon = itemView.findViewById(R.id.bookmark_card_bookmark_icon);
            clickable = itemView.findViewById(R.id.bookmark_card_clickable);
            date = itemView.findViewById(R.id.date_bookmark);
            context = itemView.getContext();
        }

    }




    public boolean isBookmarked(Context context, BookmarkObjectStructure cardObj){
        return new SharedPreferenceHelper().checkIfBookmarked(context,cardObj);
    }

    public void addToBookmarks(Context context, BookmarkObjectStructure cardObj){
        new SharedPreferenceHelper().addFavorite(context,cardObj);
        this.dataArray.add(cardObj);
    }


    public void removeFromBookmarks(Context context, BookmarkObjectStructure cardObj){
        new SharedPreferenceHelper().removeFavorite(context,cardObj);
        ArrayList<BookmarkObjectStructure> newDataArray = new ArrayList<>();
        for(BookmarkObjectStructure element: dataArray){
            if(element.getId().equals(cardObj.getId())){
                //Do nothing
            }
            else{
                newDataArray.add(element);
            }

        }
        this.dataArray = newDataArray;
        if(this.dataArray.size() == 0){
            parentFragment.noDataHelper();
        }
    }
}
