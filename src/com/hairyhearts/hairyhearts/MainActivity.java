package com.hairyhearts.hairyhearts;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gracenote.mmid.MobileSDK.GNOperations;
import com.gracenote.mmid.MobileSDK.GNSearchResponse;
import com.gracenote.mmid.MobileSDK.GNSearchResult;
import com.gracenote.mmid.MobileSDK.GNSearchResultReady;
import com.gracenote.mmid.MobileSDK.GNConfig;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private GNConfig config;
	private TextView song_info;
	private TextView key_textview;
	private EditText msg_ori;
	private TextView msg_encoded;
	private Button fp_button;

	RequestQueue queue;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		config = GNConfig.init("7486464-12AF0CC1BCE8C9726F6ADC0F77D3AF6D",this.getApplicationContext());
		fp_button = (Button) findViewById(R.id.fp_button);
		fp_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				RecognizeFromMic task = new RecognizeFromMic();
				task.doFingerprint();
				findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);

			}
		});

		queue = Volley.newRequestQueue(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	class RecognizeFromMic implements GNSearchResultReady {

		public void GNResultReady(GNSearchResult result) {
			song_info = (TextView) findViewById(R.id.song_info);
			key_textview = (TextView) findViewById(R.id.key);
			msg_ori = (EditText) findViewById(R.id.msg_ori);
			msg_encoded = (TextView) findViewById(R.id.msg_encoded);

			if (result.isFingerprintSearchNoMatchStatus()) {
				song_info.setText("no match");
			} else {
				GNSearchResponse response = result.getBestResponse();
				song_info.setText(response.getTrackTitle() + " by " + response.getArtist());

				String trackTitle, trackArtist;
				try {
					trackTitle = URLEncoder.encode(response.getTrackTitle() , "utf-8");
					trackArtist = URLEncoder.encode(response.getArtist(), "utf-8");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					trackTitle = "empty";
					trackArtist = "empty";
				}


				String url = "http://api.musixmatch.com/ws/1.1/track.search?" +
						"apikey=40b6339efb4fd3c2d3dfd5eb73854362" +
						"&q_track= "  +
						trackTitle   +
						"&q_artist=" + 
						trackArtist ;



				final String url_snippet = "http://api.musixmatch.com/ws/1.1/track.snippet.get?" +
						"apikey=40b6339efb4fd3c2d3dfd5eb73854362" +
						"&track_id= ";

				String snippet = "nulla";

				JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						// TODO Auto-generated method stub
						//Log.i("HairyHearts", " Response " + response.toString());
						//song_info.setText("Response => "+response.toString());
						try {
							String trackid = response.getJSONObject("message").getJSONObject("body")
									.getJSONArray("track_list").getJSONObject(0).getJSONObject("track")
									.getString("track_id");
							song_info.setText("Track ID => "+ trackid);


							JsonObjectRequest jsObjRequestSnippet = new JsonObjectRequest(Request.Method.GET, url_snippet + trackid, null, new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject response) {
									// TODO Auto-generated method stub
									//Log.i("HairyHearts", " Response " + response.toString());
									//song_info.setText("Response => "+response.toString());
									String snippet;
									try {
										snippet = response.getJSONObject("message").getJSONObject("body")
												.getJSONObject("snippet")
												.getString("snippet_body");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										snippet = "Nulla";
									}
									key_textview.setText("Key => "+ snippet);

									String msgToDecode = msg_ori.getText().toString();
									String msgEnc = "";
									try {
										Coding encoder = new Coding(snippet);
										msgEnc = encoder.encrypt(msgToDecode);
										msg_encoded.setText(msgEnc);
										Log.i("HHearts", " Decoded " + encoder.decrypt(msgEnc));

									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										msg_encoded.setText("Error");


									}

								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError error) {
									// TODO Auto-generated method stub

								}
							});

							queue.add(jsObjRequestSnippet);


						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.i("HairyHearts", " Track Id " + "nulla");

						}

						findViewById(R.id.progressBar1).setVisibility(View.GONE);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});



				queue.add(jsObjRequest);

			}
		}
		public void doFingerprint() {
			GNOperations.recognizeMIDStreamFromMic(this,config);

		}
	}

}
