package com.zionjr.policeticket.section_driver.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.zionjr.policeticket.R;
import com.zionjr.policeticket.databinding.FragmentTicketsBinding;
import com.zionjr.policeticket.databinding.RecyclerItemTicketBinding;
import com.zionjr.policeticket.model.entities.PenaltyRule;
import com.zionjr.policeticket.model.entities.Ticket;
import com.zionjr.policeticket.section_driver.activities.ShowTicketActivity;
import com.zionjr.policeticket.ui.decorators.SpacesItemDecoration;
import com.zionjr.policeticket.utils.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TicketsFragment extends Fragment {

    private FragmentTicketsBinding binding;
    private RecyclerAdapterTickets adapter;
    private List<Ticket> tickets;

    public TicketsFragment() {
        // Required empty public constructor
    }

    public TicketsFragment(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTicketsBinding.inflate(getLayoutInflater());

        if (tickets.isEmpty()) {

            toggleLayoutEmpty(true);

        } else {

            toggleLayoutEmpty(false);
            initViews();
        }

        return binding.getRoot();
    }

    private void initViews() {

        initSearchView();

        initRecyclerView();
    }

    private void initRecyclerView() {

        sortTickets();

        binding.searchView.setQuery("", false);

        binding.recyclerView
                .setLayoutManager(new LinearLayoutManager(getContext()));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_margin);
        binding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        adapter = new RecyclerAdapterTickets(tickets);
        binding.recyclerView.setAdapter(adapter);
    }

    private void sortTickets() {

        Collections.sort(tickets, new Comparator<Ticket>() {
            final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            @Override
            public int compare(Ticket ticket1, Ticket ticket2) {
                try {
                    return dateFormat.parse(ticket2.getDateIssued())
                            .compareTo(dateFormat.parse(ticket1.getDateIssued()));

                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    private void initSearchView() {

        binding.searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (adapter != null) {

                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }

    public void toggleLayoutEmpty(boolean show) {

        binding.layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public class RecyclerAdapterTickets
            extends RecyclerView.Adapter<RecyclerAdapterTickets.TicketViewHolder>
            implements Filterable {

        private final List<Ticket> tickets;
        private final List<Ticket> ticketsFull;
        private final Filter filter;

        public RecyclerAdapterTickets(List<Ticket> tickets) {
            this.tickets = tickets;
            this.ticketsFull = new ArrayList<>(tickets);
            this.filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    List<Ticket> filteredList = new ArrayList<>();

                    if (charSequence.length() == 0) {

                        filteredList.addAll(ticketsFull);

                    } else {

                        String stringPattern = charSequence.toString().toLowerCase().trim();

                        for (Ticket item : ticketsFull) {

                            if (item.getId().toLowerCase().contains(stringPattern)
                                    || item.getDateIssued().contains(stringPattern)
                                    || item.getDateDue().contains(stringPattern)) {

                                filteredList.add(item);
                            }
                        }
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredList;

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                    tickets.clear();
                    tickets.addAll((List) filterResults.values);
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public Filter getFilter() {
            return filter;
        }

        @NonNull
        @Override
        public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            RecyclerItemTicketBinding binding = RecyclerItemTicketBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new TicketViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {

            Ticket ticket = tickets.get(position);
            holder.bind(ticket);
        }

        @Override
        public int getItemCount() {
            return tickets.size();
        }

        class TicketViewHolder extends RecyclerView.ViewHolder {

            private final RecyclerItemTicketBinding binding;

            public TicketViewHolder(RecyclerItemTicketBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(Ticket ticket) {

                boolean isPassedDue = StringUtils.isDatePassed(ticket.getDateDue());

                if (isPassedDue) {

                    binding.textViewStatus.setText("[ PAST DUE ]");
                    binding.textViewStatus.setTextColor(Color.RED);

                } else {

                    binding.textViewStatus.setVisibility(View.GONE);
                }

                binding.textViewId.setText(ticket.getId());

                binding.textViewDateIssued
                        .setText(Html.fromHtml("Issue Date: <b>" + ticket.getDateIssued() + "</b>"));

                binding.textViewDateDue
                        .setText(Html.fromHtml("Due Date: <b>" + ticket.getDateDue() + "</b>"));

                double fine = 0;

                for (PenaltyRule penaltyRule : ticket.getPenalties()) {

                    fine += penaltyRule.getAmount();
                }

                binding.textViewPenalty.setText(Html
                        .fromHtml("Total Fine: <b>G "
                                + fine + "</b>"));

                binding.textViewPenalty.setTextColor(Color.RED);

                binding.getRoot().setOnClickListener(view -> {

                    Intent intent = new Intent(getContext(),
                            ShowTicketActivity.class);
                    Gson gson = new Gson();
                    intent.putExtra("ticket", gson.toJson(ticket));
                    startActivity(intent);

                });
            }
        }
    }
}