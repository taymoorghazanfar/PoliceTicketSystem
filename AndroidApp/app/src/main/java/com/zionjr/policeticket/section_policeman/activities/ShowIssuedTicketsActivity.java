package com.zionjr.policeticket.section_policeman.activities;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.zionjr.policeticket.R;
import com.zionjr.policeticket.databinding.ActivityShowIssuedTicketsBinding;
import com.zionjr.policeticket.databinding.RecyclerItemTicketBinding;
import com.zionjr.policeticket.model.entities.PenaltyRule;
import com.zionjr.policeticket.model.entities.Ticket;
import com.zionjr.policeticket.section_policeman.prefs.Session;
import com.zionjr.policeticket.ui.decorators.SpacesItemDecoration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShowIssuedTicketsActivity extends AppCompatActivity {

    private ActivityShowIssuedTicketsBinding binding;
    private RecyclerAdapterTickets adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowIssuedTicketsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Session.user.getTicketsIssued().isEmpty()) {

            toggleLayoutEmpty(true);

        } else {

            toggleLayoutEmpty(false);
            initViews();
        }
    }

    private void initViews() {

        initSearchView();

        initRecyclerView();
    }

    private void initRecyclerView() {

        sortTickets();

        binding.searchView.setQuery("", false);

        binding.recyclerView
                .setLayoutManager(new LinearLayoutManager(this));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_margin);
        binding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        adapter = new RecyclerAdapterTickets(Session.user.getTicketsIssued());
        binding.recyclerView.setAdapter(adapter);
    }

    private void sortTickets() {

        Collections.sort(Session.user.getTicketsIssued(), new Comparator<Ticket>() {
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

    public void goBack(View view) {

        finish();
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

            return new RecyclerAdapterTickets.TicketViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapterTickets.TicketViewHolder holder, int position) {

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

                binding.textViewStatus.setText(ticket.isPayed() ? "[ PAID ]" : "[ UNPAID ]");
                binding.textViewStatus.setTextColor(ticket.isPayed() ? Color.GREEN : Color.RED);

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

                binding.getRoot().setOnClickListener(view -> {

                    Intent intent = new Intent(ShowIssuedTicketsActivity.this,
                            ShowTicketActivityPolice.class);
                    Gson gson = new Gson();
                    intent.putExtra("ticket", gson.toJson(ticket));
                    startActivity(intent);

                });
            }
        }
    }
}