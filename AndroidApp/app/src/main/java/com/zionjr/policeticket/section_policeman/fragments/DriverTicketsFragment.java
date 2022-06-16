package com.zionjr.policeticket.section_policeman.fragments;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zionjr.policeticket.R;
import com.zionjr.policeticket.databinding.FragmentDriverTicketsBinding;
import com.zionjr.policeticket.databinding.RecyclerItemTicketPolicemanBinding;
import com.zionjr.policeticket.model.entities.PenaltyRule;
import com.zionjr.policeticket.model.entities.Ticket;
import com.zionjr.policeticket.ui.decorators.SpacesItemDecoration;
import com.zionjr.policeticket.utils.StringUtils;

import java.util.List;

public class DriverTicketsFragment extends Fragment {

    private FragmentDriverTicketsBinding binding;
    private List<Ticket> tickets;

    public DriverTicketsFragment() {
        // Required empty public constructor
    }

    public DriverTicketsFragment(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDriverTicketsBinding.inflate(getLayoutInflater());
        initViews();
        return binding.getRoot();
    }

    private void initViews() {

        Log.d("driver_details", "initViews: init");

        binding.layoutEmpty.setVisibility(tickets.isEmpty() ? View.VISIBLE : View.GONE);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_margin);
        binding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        binding.recyclerView.setAdapter(new RecyclerAdapterTicket(tickets));
    }

    public static class RecyclerAdapterTicket
            extends RecyclerView.Adapter<RecyclerAdapterTicket.TicketViewHolder> {

        private final List<Ticket> tickets;

        public RecyclerAdapterTicket(List<Ticket> tickets) {
            this.tickets = tickets;
        }

        @NonNull
        @Override
        public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            RecyclerItemTicketPolicemanBinding binding = RecyclerItemTicketPolicemanBinding
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

        static class TicketViewHolder extends RecyclerView.ViewHolder {

            private final RecyclerItemTicketPolicemanBinding binding;

            public TicketViewHolder(RecyclerItemTicketPolicemanBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(Ticket ticket) {

                binding.textViewId.setText(ticket.getId());

                if (ticket.isPayed()) {

                    binding.textViewPastDue
                            .setVisibility(View.GONE);

                } else {

                    binding.textViewPastDue
                            .setVisibility(StringUtils
                                    .isDatePassed(ticket.getDateDue()) ?
                                    View.VISIBLE : View.GONE);
                }

                binding.textViewViolationCount
                        .setText(Html.fromHtml("Violations <b>("

                                + ticket.getPenalties().size() + ")</b>"));

                StringBuilder violations = new StringBuilder();

                for (PenaltyRule penaltyRule : ticket.getPenalties()) {

                    violations.append("- ").append(penaltyRule.getTitle())
                            .append(" <b>G ").append(penaltyRule.getAmount())
                            .append("</b><br>");
                }

                binding.textViewViolations
                        .setText(Html.fromHtml(violations
                                .substring(0, (violations.toString().length() - 4))));

                binding.textViewDateIssued
                        .setText(Html.fromHtml("Issued On: <b>"
                                + ticket.getDateIssued() + "</b>"));

                binding.textViewDateDue
                        .setText(Html.fromHtml("Due On: <b>"
                                + ticket.getDateDue() + "</b>"));

                binding.expandableLayout.toggle();

                binding.getRoot().setOnClickListener(view -> {

                    if (binding.imageViewDropdown.getTag().equals("down")) {

                        binding.imageViewDropdown.setImageResource(R.drawable.ic_dropdown_up);
                        binding.imageViewDropdown.setTag("up");

                    } else {

                        binding.imageViewDropdown.setImageResource(R.drawable.ic_dropdown);
                        binding.imageViewDropdown.setTag("down");
                    }

                    binding.expandableLayout.toggle();
                });
            }
        }
    }
}