package com.iseasoft.isealive.widgets;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iseasoft.isealive.R;
import com.iseasoft.isealive.listeners.OnConfirmationDialogListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ConfirmationDialog extends DialogFragment {

    public static final String TAG = ConfirmationDialog.class.getSimpleName();
    private static final int NO_MODULE = -1;


    @BindView(R.id.popup_title)
    TextView popupTitle;
    @BindView(R.id.popup_description)
    TextView popupDescription;
    @BindView(R.id.module_container)
    FrameLayout moduleContainer;
    @BindView(R.id.two_buttons_container)
    LinearLayout twoButtonsContainer;
    @BindView(R.id.btn_ok)
    TextView btnOk;
    @BindView(R.id.btn_cancel)
    TextView btnCancel;
    @BindView(R.id.one_button_container)
    LinearLayout oneButtonContainer;
    @BindView(R.id.btn_dismiss)
    TextView btnDismiss;

    Unbinder unbinder;
    protected String title;
    private String description;
    protected String okText;
    protected OnConfirmationDialogListener onConfirmationDialogListener;
    protected int moduleLayout;
    private boolean isQuitPopup;


    public static ConfirmationDialog newInstance(String title, @Nullable String description, String okText, OnConfirmationDialogListener listener) {
        return newInstance(title, description, okText, NO_MODULE, listener);
    }

    public static ConfirmationDialog newInstance(String title, String okText, int moduleLayout, OnConfirmationDialogListener listener) {
        return newInstance(title, null, okText, moduleLayout, listener);
    }

    public static ConfirmationDialog newInstance(String title, @Nullable String description, String okText, int moduleLayout, @Nullable OnConfirmationDialogListener listener) {
        ConfirmationDialog fragment = new ConfirmationDialog();
        fragment.title = title;
        fragment.description = description;
        fragment.okText = okText;
        fragment.moduleLayout = moduleLayout;
        fragment.onConfirmationDialogListener = listener;
        fragment.isQuitPopup = false;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_confirmation_popup, container, false);
        unbinder = ButterKnife.bind(this, view);
        popupTitle.setText(title);
        if (description != null) {
            popupDescription.setText(description);
            popupDescription.setVisibility(View.VISIBLE);
        }

        if (moduleLayout != NO_MODULE) {
            try {
                View moduleView = inflater.inflate(moduleLayout, container, false);
                moduleContainer.addView(moduleView);
                moduleContainer.setVisibility(View.VISIBLE);
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "", e);
            }
        }

        btnOk.setText(okText);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_cancel, R.id.btn_ok, R.id.btn_dismiss})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                cancelled();
                break;
            case R.id.btn_ok:
                confirmed();
                break;
            case R.id.btn_dismiss:
                cancelled();
                break;
        }
    }

    protected void confirmed() {
        new Handler(Looper.getMainLooper()).post(() -> {
            setPopupTitle(getString(R.string.rating));
            setOkText(getString(R.string.common_dialog_ok));
            setCancelText(getString(R.string.common_dialog_cancel));
        });
        if (isQuitPopup) {
            if (onConfirmationDialogListener != null) {
                onConfirmationDialogListener.onConfirmed();
            }

            dismiss();
        }

        isQuitPopup = true;
    }

    private void cancelled() {
        new Handler(Looper.getMainLooper()).post(() -> {
            setPopupTitle(getString(R.string.feedback));
            setOkText(getString(R.string.common_dialog_ok));
            setCancelText(getString(R.string.common_dialog_cancel));
        });

        if (isQuitPopup) {
            if (onConfirmationDialogListener != null) {
                onConfirmationDialogListener.onCanceled();
            }
            dismiss();
        }
        isQuitPopup = true;
    }

    protected void setPopupTitle(String title) {
        if (title == null || TextUtils.isEmpty(title)) {
            return;
        }
        this.title = title;
        popupTitle.setText(title);
    }

    protected void setOkText(String okText) {
        if (okText == null || TextUtils.isEmpty(okText)) {
            return;
        }
        this.okText = okText;
        btnOk.setText(okText);
    }

    protected void setCancelText(String cancelText) {
        if (cancelText == null || TextUtils.isEmpty(cancelText)) {
            return;
        }
        btnCancel.setText(cancelText);
    }

    protected void setOkEnable(boolean enable) {
        btnOk.setAlpha(enable ? 1.0f : 0.3f);
        btnOk.setEnabled(enable);
    }

    protected void setEnableOneButton(boolean enable) {
        new Handler(Looper.getMainLooper()).post(() -> {
            oneButtonContainer.setVisibility((enable ? View.VISIBLE : View.GONE));
            twoButtonsContainer.setVisibility(enable ? View.GONE : View.VISIBLE);
        });

    }

    protected void setOneButtonText(String text) {
        if (text == null || TextUtils.isEmpty(text)) {
            return;
        }
        btnDismiss.setText(text);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null) {
            int widthDialog = LinearLayout.LayoutParams.MATCH_PARENT;
            int heightDialog = LinearLayout.LayoutParams.WRAP_CONTENT;
            window.setLayout(widthDialog, heightDialog);
            window.setGravity(Gravity.CENTER);
        }
    }
}
