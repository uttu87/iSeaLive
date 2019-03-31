package com.iseasoft.iseagoal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.afollestad.appthemeengine.ATE;
import com.iseasoft.iseagoal.adapters.FolderAdapter;
import com.iseasoft.iseagoal.adapters.PlaylistAdapter;
import com.iseasoft.iseagoal.dialogs.StorageSelectDialog;
import com.iseasoft.iseagoal.http.HttpHandler;
import com.iseasoft.iseagoal.listeners.FolderListener;
import com.iseasoft.iseagoal.models.M3UPlaylist;
import com.iseasoft.iseagoal.parsers.M3UParser;
import com.iseasoft.iseagoal.permissions.Nammu;
import com.iseasoft.iseagoal.permissions.PermissionCallback;
import com.iseasoft.iseagoal.slidinguppanel.SlidingUpPanelLayout;
import com.iseasoft.iseagoal.utils.PreferencesUtility;
import com.iseasoft.iseagoal.utils.Utils;
import com.iseasoft.iseagoal.widgets.DividerItemDecoration;
import com.iseasoft.iseagoal.widgets.FastScroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by nv95 on 10.11.16.
 */

public class FoldersFragment extends Fragment implements StorageSelectDialog.OnDirSelectListener,
        FolderListener {

    private final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadFolders();
        }

        @Override
        public void permissionRefused() {

        }
    };
    private FolderAdapter mAdapter;
    private RecyclerView recyclerView;
    private FastScroller fastScroller;
    private ProgressBar mProgressBar;
    private SlidingUpPanelLayout panelLayout;
    private PlaylistAdapter playlistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_folders, container, false);


        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.folders);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        fastScroller = (FastScroller) rootView.findViewById(R.id.fastscroller);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        panelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    private void loadFolders() {
        if (getActivity() != null) {
            new LoadFolder().execute("");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean dark = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false);
        if (dark) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
        if (mAdapter != null) {
            mAdapter.applyTheme(dark);
            mAdapter.notifyDataSetChanged();
        }

        if (Utils.isMarshmallow()) {
            requestStoragePermission();
        } else {
            loadFolders();
        }
    }

    private void setItemDecoration() {
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_folders, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_storages) {

        }
        switch (item.getItemId()) {
            case R.id.action_storages:
                loadFolders();
                break;
            case R.id.action_server:
                loadServer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadServer() {
        mProgressBar.setVisibility(View.VISIBLE);
        String url = "http://20062016.com:8000/get.php?username=Master99&password=Master99&type=m3u";
        new LoadServer().execute(url);
    }

    public void updateTheme() {
        Context context = getActivity();
        if (context != null) {
            boolean dark = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false);
            mAdapter.applyTheme(dark);
        }
    }

    @Override
    public void onDirSelected(File dir) {
        mAdapter.updateDataSetAsync(dir);
    }

    private void requestStoragePermission() {
        if (Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadFolders();
        } else {
            if (Nammu.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout, "iSeaMusic will need to read external storage to display songs on your device.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Nammu.askForPermission(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
                            }
                        }).show();
            } else {
                Nammu.askForPermission(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionReadstorageCallback);
            }
        }
    }

    @Override
    public void onFileSelected(File file) {

        try {
            InputStream inputStream = new FileInputStream(file);
            parseAndUpdateUI(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void parseAndUpdateUI(InputStream inputStream) {

        M3UParser m3UParser = new M3UParser();
        try {
            M3UPlaylist playlist = m3UParser.parseFile(inputStream);
            new Handler(Looper.getMainLooper()).post(() -> {
                if (playlistAdapter == null) {
                    playlistAdapter = new PlaylistAdapter(getActivity());
                }
                playlistAdapter.update(playlist.getPlaylistItems());
                recyclerView.setAdapter(playlistAdapter);
                //to add spacing between cards
                if (getActivity() != null) {
                    setItemDecoration();
                }
                mProgressBar.setVisibility(View.GONE);
                fastScroller.setVisibility(View.VISIBLE);
                fastScroller.setRecyclerView(recyclerView);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadFolder extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Activity activity = getActivity();
            if (activity != null) {
                mAdapter = new FolderAdapter(activity, new File(PreferencesUtility.getInstance(activity).getLastFolder()));
                mAdapter.setFolderListener(FoldersFragment.this);
                updateTheme();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(mAdapter);
            //to add spacing between cards
            if (getActivity() != null) {
                setItemDecoration();
            }
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
            fastScroller.setVisibility(View.VISIBLE);
            fastScroller.setRecyclerView(recyclerView);
        }

        @Override
        protected void onPreExecute() {
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadServer extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {

            HttpHandler hh = new HttpHandler();
            InputStream inputStream = hh.makeServiceCall(urls[0]);

            parseAndUpdateUI(inputStream);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}