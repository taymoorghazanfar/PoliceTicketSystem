package com.zionjr.policeticket.section_driver.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.zionjr.policeticket.databinding.ActivityShowTicketsBinding;
import com.zionjr.policeticket.model.entities.Ticket;
import com.zionjr.policeticket.section_driver.fragments.TicketsFragment;
import com.zionjr.policeticket.section_driver.prefs.Session;
import com.zionjr.policeticket.section_policeman.activities.ShowDriverDetailsActivity;
import com.zionjr.policeticket.section_policeman.fragments.DriverTicketsFragment;

import java.util.ArrayList;
import java.util.List;

public class ShowTicketsActivity extends AppCompatActivity {

    private ActivityShowTicketsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowTicketsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }

    private void initViews() {

        binding.textViewTickets
                .setText(Html.fromHtml("Tickets <b>("
                        + Session.user.getTickets().size() + ")</b>"));

        initViewPager();
    }

    private void initViewPager() {

        List<Ticket> paidTickets = new ArrayList<>();
        List<Ticket> unpaidTickets = new ArrayList<>();

        for (Ticket ticket : Session.user.getTickets()) {

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
                    new TicketsFragment(paidTickets) :
                    new TicketsFragment(unpaidTickets);
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
                    return Html.fromHtml("Paid Tickets: <b>" + paidTickets.size() + "</b>");

                case 1:
                    return Html.fromHtml("Unpaid Tickets: <b>" + unpaidTickets.size() + "</b>");

            }
            return "";
        }
    }
}