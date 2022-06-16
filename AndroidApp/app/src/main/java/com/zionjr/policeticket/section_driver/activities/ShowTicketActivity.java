package com.zionjr.policeticket.section_driver.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.zionjr.policeticket.R;
import com.zionjr.policeticket.databinding.ActivityShowTicketBinding;
import com.zionjr.policeticket.databinding.RecyclerItemViolationBinding;
import com.zionjr.policeticket.model.entities.PenaltyRule;
import com.zionjr.policeticket.model.entities.Ticket;
import com.zionjr.policeticket.ui.decorators.SpacesItemDecoration;

import java.text.MessageFormat;
import java.util.List;

public class ShowTicketActivity extends AppCompatActivity {

    private ActivityShowTicketBinding binding;
    private Ticket ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();

        initViews();
    }

    private void getIntentData() {

        String ticketJson = getIntent().getStringExtra("ticket");
        Gson gson = new Gson();
        ticket = gson.fromJson(ticketJson, Ticket.class);
    }

    private void initViews() {

        binding.textViewId
                .setText(Html.fromHtml("Ticket: <b>" + ticket.getId() + "</b>"));

        binding.textViewDateIssued.setText(ticket.getDateIssued());

        binding.textViewDateDue.setText(ticket.getDateDue());

        binding.textViewName.setText(ticket.getIssuer().getName());

        binding.textViewBadgeNumber.setText(ticket.getIssuer().getBadgeNumber());

        binding.textViewStatus.setText(ticket.isPayed() ? "PAYED" : "UNPAID");
        binding.textViewStatus.setTextColor(ticket.isPayed() ? Color.GREEN : Color.RED);

        binding.textViewViolationCount
                .setText(Html.fromHtml("Violations <b>("
                        + ticket.getPenalties().size() + ")"));

        double fineAmount = 0;

        for (PenaltyRule penaltyRule : ticket.getPenalties()) {

            fineAmount += penaltyRule.getAmount();
        }

        binding.textViewFineAmount.setText(MessageFormat.format("G{0}", fineAmount));

        initRecyclerView();
    }

    private void initRecyclerView() {

        binding.recyclerView
                .setLayoutManager(new LinearLayoutManager(this));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_margin);
        binding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        binding.recyclerView.setAdapter(new RecyclerAdapterPenalty(ticket.getPenalties()));
    }

    public void goBack(View view) {

        finish();
    }

    public static class RecyclerAdapterPenalty
            extends RecyclerView.Adapter<RecyclerAdapterPenalty.PenaltyViewHolder> {

        private final List<PenaltyRule> penalties;

        public RecyclerAdapterPenalty(List<PenaltyRule> penalties) {
            this.penalties = penalties;
        }

        @NonNull
        @Override
        public PenaltyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            RecyclerItemViolationBinding binding = RecyclerItemViolationBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new PenaltyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull PenaltyViewHolder holder, int position) {

            PenaltyRule penalty = penalties.get(position);
            holder.bind(penalty);
        }

        @Override
        public int getItemCount() {
            return penalties.size();
        }

        static class PenaltyViewHolder extends RecyclerView.ViewHolder {

            private final RecyclerItemViolationBinding binding;

            public PenaltyViewHolder(RecyclerItemViolationBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(PenaltyRule penalty) {

                binding.textViewId.setText(penalty.getId());
                binding.textViewTitle.setText(penalty.getTitle());
                binding.textViewPenalty
                        .setText(Html.fromHtml("Fine: G <b>" + penalty.getAmount() + "</b>"));
            }
        }
    }
}