package com.zionjr.policeticket.section_policeman.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zionjr.policeticket.databinding.DialogSelectScanTypeBinding;
import com.zionjr.policeticket.section_policeman.interfaces.ISelectScanListener;

import java.util.Objects;

public class SelectScanTypeDialog extends DialogFragment {

    private final ISelectScanListener listener;
    private DialogSelectScanTypeBinding binding;
    private boolean issueTicket;

    public SelectScanTypeDialog(ISelectScanListener listener, boolean issueTicket) {
        this.listener = listener;
        this.issueTicket = issueTicket;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DialogSelectScanTypeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getDialog()).getWindow()
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initViews() {

        binding.imageViewClose.setOnClickListener(view -> dismiss());
        binding.cardViewLicense.setOnClickListener(view -> {

            dismiss();
            listener.onScanLicenseSelected(issueTicket);
        });
        binding.cardViewPlateNumber.setOnClickListener(view -> {

            dismiss();
            listener.onScanPlateNumberSelected(issueTicket);
        });
    }
}
