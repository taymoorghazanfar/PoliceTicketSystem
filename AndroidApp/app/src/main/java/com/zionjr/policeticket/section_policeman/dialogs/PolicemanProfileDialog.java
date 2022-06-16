package com.zionjr.policeticket.section_policeman.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zionjr.policeticket.databinding.DialogPolicemanProfileBinding;
import com.zionjr.policeticket.model.users.Policeman;
import com.zionjr.policeticket.section_policeman.prefs.Session;

import java.util.Objects;

public class PolicemanProfileDialog extends DialogFragment {

    private DialogPolicemanProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DialogPolicemanProfileBinding.inflate(getLayoutInflater());
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

        Policeman policeman = Session.user;
        binding.textViewName.setText(policeman.getName());
        binding.textViewBadgeNumber.setText(policeman.getBadgeNumber());
        binding.textViewEmail.setText(policeman.getEmail());
        binding.textViewTicketsIssued
                .setText(String.valueOf(policeman.getTicketsIssued().size()));
    }
}