package com.followme.lusir.omgandriodnew;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView mTextView;
    private EditText mEditText;

    private ProgressDialog progressDialog;

    private Button mButton;
    private ListView mListView;
    private List<String> mNameList;
    //    private ArrayAdapter mArrayAdapter;
    private JSONAdapter mJsonAdapter;
    private ShareActionProvider mShareActionProvider;
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    SharedPreferences mSharedPreferences;

    private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNameList = new ArrayList();

        mTextView = findViewById(R.id.main_textView);
        mButton = findViewById(R.id.main_button);
        mListView = findViewById(R.id.main_listview);
        mEditText = findViewById(R.id.main_edit_text);

        mButton.setOnClickListener(this);
        mListView.setOnItemClickListener(this);

        //ver1.0的遗留
//        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mNameList);
//        mListView.setAdapter(mArrayAdapter);


        mJsonAdapter = new JSONAdapter(this, getLayoutInflater());
        mListView.setAdapter(mJsonAdapter);


        displayWelcome();
    }

    private void displayWelcome() {
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        String name = mSharedPreferences.getString(PREF_NAME, "");
        if (name.length() > 0) {
            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        } else {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("What is your name?");

            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    String inputName = input.getText().toString();

                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(PREF_NAME, inputName);
                    e.commit();

                    Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_button:
              /*  //改变textview
                mTextView.setText("change");
                */



                /*
                //更新listview
                String name = mEditText.getText().toString();
                mNameList.add(name);
                //一定要刷新
                mArrayAdapter.notifyDataSetChanged();
                mEditText.setText("");
                */

                queryBooks(mEditText.getText().toString());

                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//       ver1.0遗留
//        Log.d("omg android", position + " : " + mNameList.get(position));

        JSONObject jsonObject = (JSONObject) mJsonAdapter.getItem(position);
        String coverID = jsonObject.optString("cover_i", "");

        Intent detailIntent = new Intent(this, DetailActivity.class);

        detailIntent.putExtra("coverID", coverID);

        startActivity(detailIntent);
    }

    //分享功能
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }

        setShareIntent();

        return true;
    }

    private void setShareIntent() {
        if (mShareActionProvider != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mTextView.getText());
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }


    //搜索功能
    private void queryBooks(String searchString) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在搜索...");
        progressDialog.show();

        String urlString = "";
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(QUERY_URL + urlString, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
//                Log.d("omg android", jsonObject.toString());
                mJsonAdapter.updateData(jsonObject.optJSONArray("docs"));
            }

            @Override
            public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }

                Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("omg android", statusCode + " " + throwable.getMessage());
            }
        });
    }
}
