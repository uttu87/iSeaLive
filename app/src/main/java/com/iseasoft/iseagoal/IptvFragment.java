package com.iseasoft.iseagoal;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iseasoft.iseagoal.adapters.LiveAdapter;
import com.iseasoft.iseagoal.models.League;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

@SuppressWarnings("WeakerAccess")
public class IptvFragment extends BaseFragment {

    public static final String TAG = IptvFragment.class.getSimpleName();
    Unbinder unbinder;
    private boolean init = false;

    public static IptvFragment newInstance() {
        return new IptvFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_iptv, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Optional()
    @OnClick({R.id.btn_server, R.id.btn_local})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_server:
                downloadFile();
                break;
        }
    }

    private void downloadFile() {
        String DownloadUrl = "http://jack.dyndns.tv:15800/get.php?username=lorazepam&password=mapezarol&type=m3u";
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request1.setDescription("Sample Music File");   //appears the same in Notification bar while downloading
        request1.setTitle("File1.m3u");
        request1.setVisibleInDownloadsUi(false);

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request1.allowScanningByMediaScanner();
            request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        */

        request1.setDestinationInExternalFilesDir(LiveApplication.getContext(), "/File", "playlist.m3u");

        DownloadManager manager1 = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Objects.requireNonNull(manager1).enqueue(request1);
        if (DownloadManager.STATUS_SUCCESSFUL == 8) {
            //loadFile();
        }
    }

    private void loadFile(String fileName) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        unbinder.unbind();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }
}
