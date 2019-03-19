package com.iseasoft.iseagoal;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iseasoft.iseagoal.api.APIListener;
import com.iseasoft.iseagoal.api.ISeaLiveAPI;
import com.iseasoft.iseagoal.models.League;
import com.iseasoft.iseagoal.models.Match;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iseasoft.iseagoal.ISeaLiveConstants.LEAGUE_COLLECTION;
import static com.iseasoft.iseagoal.ISeaLiveConstants.YOUTUBE_API_KEY;
import static com.iseasoft.iseagoal.WebViewFrament.WEB_URL;

public class AdminActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = AdminActivity.class.getSimpleName();

    private TextView name;
    private TextView imageURL;
    private TextView streamURL;
    private ImageView imageView;
    private CheckBox checkBoxLive;
    private CheckBox checkBoxYoutube;
    private CheckBox checkBoxHidden;
    private Spinner spinner;
    private EditText inputMatchUrl;
    private EditText enterLeagueName;
    private EditText enterLeagueDesc;
    private Button btnPost;
    private Button btnGotoMain;
    private Button btnFindMatch;
    private Button btnLoadWeb;
    private Match match;
    private String leagueId;
    private boolean isAddNewLeague;
    //private ArrayList<League> mLeagues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin);
        super.onCreate(savedInstanceState);
        name = findViewById(R.id.name);
        imageURL = findViewById(R.id.image_url);
        streamURL = findViewById(R.id.stream_url);
        imageView = findViewById(R.id.imageView);
        btnPost = findViewById(R.id.btn_post);
        btnGotoMain = findViewById(R.id.btn_goto_main);
        spinner = findViewById(R.id.spinner);
        inputMatchUrl = findViewById(R.id.edt_enter_url);
        btnFindMatch = findViewById(R.id.btn_parse);
        btnLoadWeb = findViewById(R.id.btn_load_web);
        checkBoxLive = findViewById(R.id.live_checkbox);
        checkBoxYoutube = findViewById(R.id.youtube_checkbox);
        checkBoxHidden = findViewById(R.id.hidden_checkbox);
        enterLeagueName = findViewById(R.id.enter_league_name);
        enterLeagueDesc = findViewById(R.id.enter_league_description);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        ISeaLiveAPI.getInstance().getAllLeague(new APIListener<ArrayList<League>>() {
            @Override
            public void onRequestCompleted(ArrayList<League> leagues, String json) {
                if (!isStateSafe()) {
                    return;
                }
                setupSpinnerLeague(leagues);
            }

            @Override
            public void onError(Error e) {
            }
        });


        btnFindMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showInterstitialAds();
                if (TextUtils.isEmpty(inputMatchUrl.getText())) {
                    return;
                }
                if (checkBoxYoutube.isChecked()) {
                    getYoutubeVideoDetails(inputMatchUrl.getText().toString());
                } else {
                    match = ISeaLiveAPI.getInstance().createMatchFromWeb(inputMatchUrl.getText().toString());
                    if (match == null) {
                        return;
                    }

                    if (match != null) {
                        match.setLeague(leagueId);
                        boolean isLive = checkBoxLive.isChecked();
                        boolean isYoutube = checkBoxYoutube.isChecked();
                        boolean isHidden = checkBoxHidden.isChecked();
                        match.setLive(isLive);
                        match.setYoutube(isYoutube);
                        match.setHidden(isHidden);
                        match.setType(isLive ? "Live" : "highlight");
                        name.setText("name: " + match.getName());
                        imageURL.setText("imageURL: " + match.getThumbnailUrl());
                        Glide.with(AdminActivity.this)
                                .load(match.getThumbnailUrl())
                                .into(imageView);
                        streamURL.setText("streamURL: " + match.getStreamUrl());
                    }
                }
            }
        });


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (match != null) {
                    postToFirestore(match);
                }
            }
        });

        btnGotoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationToMainScreen(false);
            }
        });

        btnLoadWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxYoutube.isChecked()) {
                    inputMatchUrl.setText("https://www.youtube.com/");
                }
                if (TextUtils.isEmpty(inputMatchUrl.getText())) {
                    return;
                }
                loadWeb(inputMatchUrl.getText().toString());
            }
        });

        setupWeview(ISeaLiveAPI.getBaseURLDev());
    }

    private void loadWeb(String url) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        WebViewFrament webViewFrament = (WebViewFrament) fm.findFragmentByTag(WebViewFrament.TAG);

        if (webViewFrament != null) {
            webViewFrament.loadWeb(url);
        }

    }

    private void setupWeview(String url) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Bundle args = new Bundle();
        args.putString(WEB_URL, url);

        WebViewFrament webViewFrament = new WebViewFrament();
        webViewFrament.setArguments(args);
        ft.replace(R.id.webview_fragment, webViewFrament, WebViewFrament.TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void setupSpinnerLeague(ArrayList<League> leagues) {
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        for (League league : leagues) {
            categories.add("" + league.getId() + " - " + league.getName());
        }
        int addNewLeagueId = leagues.get(leagues.size() - 1).getId() + 1;
        categories.add("" + addNewLeagueId + " - Add new League");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    public void getYoutubeVideoDetails(String videoId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.googleapis.com/youtube/v3/videos?id={VIDEO_ID}&part=snippet&key=" + YOUTUBE_API_KEY;
        url = url.replace("{VIDEO_ID}", videoId);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            parseMatch(response, videoId);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    private void parseMatch(String response, String videoId) throws JSONException {
        match = new Match();
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("items") && jsonObject.getJSONArray("items").length() > 0) {
            JSONArray items = jsonObject.getJSONArray("items");
            JSONObject videoJson = items.getJSONObject(0);
            JSONObject snippet = videoJson.getJSONObject("snippet");
            if (snippet.has("title")) {
                match.setName(snippet.getString("title"));
            }
        }
        String thumbnail = "https://i.ytimg.com/vi/{VIDEO_ID}/hqdefault.jpg";
        thumbnail = thumbnail.replace("{VIDEO_ID}", videoId);
        match.setThumbnailUrl(thumbnail);
        match.setStreamUrl(videoId);
        match.setLeague(leagueId);
        boolean isLive = checkBoxLive.isChecked();
        boolean isYoutube = checkBoxYoutube.isChecked();
        boolean isHidden = checkBoxHidden.isChecked();
        match.setLive(isLive);
        match.setYoutube(isYoutube);
        match.setHidden(isHidden);
        match.setType(isLive ? "Live" : "highlight");

        name.setText("name: " + match.getName());
        imageURL.setText("imageURL: " + match.getThumbnailUrl());
        Glide.with(AdminActivity.this)
                .load(match.getThumbnailUrl())
                .into(imageView);
        streamURL.setText("streamURL: " + match.getStreamUrl());
    }

    private void postToFirestore(Match match) {
        if (isAddNewLeague) {
            Map<String, Object> newLeague = new HashMap<>();
            newLeague.put("id", leagueId);
            newLeague.put("name", enterLeagueName.getText().toString());
            //newLeague.put("description", enterLeagueDesc.getText().toString());

            Map<String, String> matchData = new HashMap<>();
            matchData.put("id", String.valueOf(match.getId()));
            matchData.put("name", match.getName());
            matchData.put("streamURL", match.getStreamUrl());
            matchData.put("imageURL", match.getThumbnailUrl());
            //matchData.put("type", match.getType());
            matchData.put("league", leagueId);
            matchData.put("isLive", String.valueOf(match.isLive()));
            matchData.put("isYoutube", String.valueOf(match.isYoutube()));
            matchData.put("isHidden", String.valueOf(match.isHidden()));

            ArrayList<Map<String, String>> matchList = new ArrayList<>();
            matchList.add(matchData);
            newLeague.put("match", matchList);

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = firebaseFirestore.collection(LEAGUE_COLLECTION);
            collectionReference.document(leagueId).set(newLeague)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AdminActivity.this, "Add new league: "
                                    + match.getLeague() + " successful", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminActivity.this, "Add new league: "
                                    + match.getLeague() + " failed", Toast.LENGTH_LONG).show();
                        }
                    });

        } else {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection(LEAGUE_COLLECTION)
                    .document(leagueId);
            Map<String, String> matchData = new HashMap<>();
            matchData.put("id", String.valueOf(match.getId()));
            matchData.put("name", match.getName());
            matchData.put("streamURL", match.getStreamUrl());
            matchData.put("imageURL", match.getThumbnailUrl());
            //matchData.put("type", match.getType());
            matchData.put("league", match.getLeague());
            matchData.put("isLive", String.valueOf(match.isLive()));
            matchData.put("isYoutube", String.valueOf(match.isYoutube()));
            matchData.put("isHidden", String.valueOf(match.isHidden()));
            documentReference.update("match", FieldValue.arrayUnion(matchData));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        leagueId = item.substring(0, 4);
        isAddNewLeague = position == (parent.getAdapter().getCount() - 1);
        enterLeagueName.setVisibility(isAddNewLeague ? View.VISIBLE : View.GONE);
        enterLeagueDesc.setVisibility(isAddNewLeague ? View.VISIBLE : View.GONE);
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected void navigationToMainScreen(boolean isFinish) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public void updateMatchUrl(String url) {
        inputMatchUrl.setText(url);
    }

    protected boolean isStateSafe() {
        return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED);
    }
}
