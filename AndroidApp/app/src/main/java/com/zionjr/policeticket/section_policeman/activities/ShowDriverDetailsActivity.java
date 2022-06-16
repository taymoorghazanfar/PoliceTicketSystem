package com.zionjr.policeticket.section_policeman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.gson.Gson;
import com.zionjr.policeticket.databinding.ActivityShowDriverDetailsBinding;
import com.zionjr.policeticket.model.entities.Ticket;
import com.zionjr.policeticket.model.users.Driver;
import com.zionjr.policeticket.section_policeman.fragments.DriverTicketsFragment;
import com.zionjr.policeticket.utils.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ShowDriverDetailsActivity extends AppCompatActivity {

    private ActivityShowDriverDetailsBinding binding;
    private Driver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowDriverDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        initViews();
    }

    private void getIntentData() {

        String driverJson = getIntent().getStringExtra("driver");
        Gson gson = new Gson();
        driver = gson.fromJson(driverJson, Driver.class);

        Log.d("driver_details", "driver: " + driver.toString());
    }

    private void initViews() {

        binding.textViewName.setText(driver.getName());
        binding.textViewLicenseNumber.setText(driver.getLicenseNumber());
        binding.textViewPlateNumber.setText(driver.getPlateNumber());
        binding.textViewLicenseExpiry.setText(driver.getLicenseExpiry());
        binding.textViewEmail.setText(driver.getEmail());
        binding.textViewTickets
                .setText(Html.fromHtml(MessageFormat
                        .format("Tickets: <b>{0}</b>",
                                driver.getTickets().size())));

        boolean isExpired = StringUtils.isDatePassed(driver.getLicenseExpiry());
        binding.textViewExpired.setVisibility(isExpired ? View.VISIBLE : View.GONE);

        initViewPager();
    }

    private void initViewPager() {

        List<Ticket> paidTickets = new ArrayList<>();
        List<Ticket> unpaidTickets = new ArrayList<>();

        for (Ticket ticket : driver.getTickets()) {

            if (ticket != null) {

                if (ticket.isPayed()) {

                    paidTickets.add(ticket);

                } else {

                    unpaidTickets.add(ticket);
                }
            }
        }

        PagerAdapterTickets adapter = new PagerAdapterTickets(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                paidTickets, unpaidTickets);

        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(2);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    public void goBack(View view) {

        finish();
    }

    public void issueTicket(View view) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Issue Ticket ?");

        alert.setPositiveButton("Yes", (dialog, whichButton) -> {

            dialog.dismiss();

            Intent intent = new Intent(ShowDriverDetailsActivity.this,
                    IssueTicketActivity.class);

            Gson gson = new Gson();
            intent.putExtra("driver", gson.toJson(driver));

            startActivity(intent);
            finish();
        });

        alert.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        alert.show();
    }

    public static class PagerAdapterTickets extends FragmentPagerAdapter {

        private final List<Ticket> paidTickets;
        private final List<Ticket> unpaidTickets;

        public PagerAdapterTickets(@NonNull FragmentManager fm, int behavior,
                                   List<Ticket> paidTickets, List<Ticket> unpaidTickets) {
            super(fm, behavior);
            this.paidTickets = paidTickets;
            this.unpaidTickets = unpaidTickets;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            return position == 0 ?
                    new DriverTicketsFragment(paidTickets) :
                    new DriverTicketsFragment(unpaidTickets);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {

                case 0:
                    return Html.fromHtml("Paid: <b>" + paidTickets.size() + "</b>");

                case 1:
                    return Html.fromHtml("Unpaid: <b>" + unpaidTickets.size() + "</b>");

            }
            return "";
        }
    }
}