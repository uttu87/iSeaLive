package com.iseasoft.iseagoal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
    PlaylistAdapter mPlaylistAdapter;
    private M3UParser m3UParser;
    private M3UPlaylist m3UPlaylist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_folders, container, false);


        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(null);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setTitle("");


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        fastScroller = (FastScroller) rootView.findViewById(R.id.fastscroller);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        panelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    private void loadFolders() {
        if (getActivity() != null)
            new loadFolders().execute("");
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
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search channel name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return filter(query);
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                //TODO here changes the search text)
                return filter(newText);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_storages:
                loadFolders();
                break;
            case R.id.action_server:
                break;
            case R.id.action_search:
                getActivity().setContentView(R.layout.searchable);
                break;
        }
        return super.onOptionsItemSelected(item);
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
        if (file != null) {
            if (m3UParser == null) {
                m3UParser = new M3UParser();
            }
            try {
                m3UPlaylist = m3UParser.parseFile(new FileInputStream(file));
                setupPlaylist(m3UPlaylist);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupPlaylist(M3UPlaylist m3UPlaylist) {
        if (mPlaylistAdapter == null) {
            mPlaylistAdapter = new PlaylistAdapter(getActivity());
        }
        recyclerView.setAdapter(mPlaylistAdapter);
        mPlaylistAdapter.update(m3UPlaylist.getPlaylistItems());
        if (getActivity() != null) {
            setItemDecoration();
        }
        mProgressBar.setVisibility(View.GONE);
        //fastScroller.setVisibility(View.VISIBLE);
        //fastScroller.setRecyclerView(recyclerView);
    }

    private boolean filter(final String newText) {
        if (mPlaylistAdapter != null) {
            if (!newText.isEmpty()) {
                mPlaylistAdapter.getFilter().filter(newText);
            }
            return true;
        }
        return false;
    }

    private class loadFolders extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Activity activity = getActivity();
            if (activity != null) {
                mAdapter = new FolderAdapter(activity, new File(PreferencesUtility.getInstance(activity).getLastFolder()));
                mAdapter.setListener(FoldersFragment.this);
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
            //fastScroller.setVisibility(View.VISIBLE);
            //fastScroller.setRecyclerView(recyclerView);
        }

        @Override
        protected void onPreExecute() {
        }
    }


}
