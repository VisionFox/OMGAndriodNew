package com.followme.lusir.omgandriodnew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONAdapter extends BaseAdapter {

    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";

    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    public JSONAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mJsonArray = new JSONArray();

    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        return mJsonArray.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void updateData(JSONArray jsonArray){
        mJsonArray=jsonArray;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_book, null);

            holder = new ViewHolder();
            holder.thumbnailImageView = convertView.findViewById(R.id.img_thumbnail);
            holder.titleTextView = convertView.findViewById(R.id.text_title);
            holder.authorTextView = convertView.findViewById(R.id.text_author);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        //封面和默认图标
        JSONObject jsonObject = (JSONObject) getItem(position);
        if (jsonObject.has("cover_i")) {
            String imageID = jsonObject.optString("cover_i");

            String imageURL = IMAGE_URL_BASE + imageID + "-L.jpg";

            Picasso.with(mContext).load(imageURL).placeholder(R.drawable.ic_books).into(holder.thumbnailImageView);

        } else {
            holder.thumbnailImageView.setImageResource(R.drawable.ic_books);
        }

        //书名和作者姓名
        String bookTitle = "";
        String authorName = "";

        if (jsonObject.has("title")){
            bookTitle=jsonObject.optString("title");
        }

        if (jsonObject.has("author_name")){
            authorName=jsonObject.optJSONArray("author_name").optString(0);
        }

        holder.titleTextView.setText(bookTitle);
        holder.authorTextView.setText(authorName);

        return convertView;
    }

    private static class ViewHolder {
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView authorTextView;
    }
}
