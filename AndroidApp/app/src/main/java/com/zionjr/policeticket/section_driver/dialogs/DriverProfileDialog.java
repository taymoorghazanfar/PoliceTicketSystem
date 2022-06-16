package com.zionjr.policeticket.section_driver.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zionjr.policeticket.databinding.DialogDriverProfileBinding;
import com.zionjr.policeticket.model.entities.Ticket;
import com.zionjr.policeticket.model.users.Driver;
import com.zionjr.policeticket.section_driver.prefs.Session;

import java.util.Objects;

public class DriverProfileDialog extends DialogFragment {

    private DialogDriverProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = DialogDriverProfileBinding.inflate(getLayoutInflater());
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

        Driver driver = Session.user;
        binding.textViewName.setText(driver.getName());
        binding.textViewLicenseNumber.setText(driver.getLicenseNumber());
        binding.textViewPlateNumber.setText(driver.getPlateNumber());
        binding.textViewLicenseExpiry.setText(driver.getLicenseExpiry());
        binding.textViewEmail.setText(driver.getEmail());

        int paidTickets = 0;
        int unpaidTickets = 0;

        for (Ticket ticket : driver.getTickets()) {

            if (ticket != null) {

                if (ticket.isPayed()) {

                    paidTickets++;

                } else {

                    unpaidTickets++;
                }
            }
        }

        binding.textViewTicketsPaid.setText(String.valueOf(paidTickets));
        binding.textViewTicketsUnpaid.setText(String.valueOf(unpaidTickets));
    }
}