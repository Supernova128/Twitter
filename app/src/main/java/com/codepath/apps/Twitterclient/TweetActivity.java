package com.codepath.apps.Twitterclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.Twitterclient.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "TweetActivity";

    EditText etTweet;
    Button btnTweet;
    TextView tvChar;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        client = TwitterApplication.getRestClient(this);

        etTweet = findViewById(R.id.etTweet);
        btnTweet = findViewById(R.id.btnTweet);
        tvChar = findViewById(R.id.tvChar);

        // get click listener on button
        // make call to post tweet on twitter.

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etTweet.getText().toString();
                if (tweetContent.isEmpty()){
                    Toast.makeText(TweetActivity.this,"Tweet can not be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(TweetActivity.this,"Tweet is too long",Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(TweetActivity.this,"Tweet Successful",Toast.LENGTH_LONG).show();

                client.PublishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG,"onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK,intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"onFailure to publish tweet",throwable);
                    }
                });
            }
        });
        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String Left = Integer.toString(0) + "/" + Integer.toString(MAX_TWEET_LENGTH);
                tvChar.setText(Left);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = etTweet.getText().toString().length();
                String Left = Integer.toString(length) + "/" + Integer.toString(MAX_TWEET_LENGTH);
                tvChar.setText(Left);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}