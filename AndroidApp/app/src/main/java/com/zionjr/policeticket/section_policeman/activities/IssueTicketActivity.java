package com.zionjr.policeticket.section_policeman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.Gson;
import com.zionjr.policeticket.R;
import com.zionjr.policeticket.cloud_functions.response_models.PenaltyRuleResponse;
import com.zionjr.policeticket.databinding.ActivityIssueTicketBinding;
import com.zionjr.policeticket.databinding.RecyclerItemPenaltyShortBinding;
import com.zionjr.policeticket.model.entities.PenaltyRule;
import com.zionjr.policeticket.model.entities.Ticket;
import com.zionjr.policeticket.model.users.Driver;
import com.zionjr.policeticket.section_policeman.cloudfunctions.response_models.IssueTicketResponse;
import com.zionjr.policeticket.section_policeman.prefs.Session;
import com.zionjr.policeticket.ui.decorators.SpacesItemDecoration;
import com.zionjr.policeticket.utils.ParseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IssueTicketActivity extends AppCompatActivity {

    private final List<PenaltyItem> penaltyItems = new ArrayList<>();
    private ActivityIssueTicketBinding binding;
    private Driver driver;
    private RecyclerAdapterPenalty adapter;
    private List<PenaltyRule> penaltyRules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIssueTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentData();
        toggleLayoutEmpty(false);
        getPenaltyRules();
    }

    private void getIntentData() {

        String driverJson = getIntent().getStringExtra("driver");
        Gson gson = new Gson();
        driver = gson.fromJson(driverJson, Driver.class);
    }

    private void getPenaltyRules() {

        showLoading();

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("penalty_rule-get_all_penalty_rules")
                .call()
                .addOnSuccessListener(httpsCallableResult -> {

                    PenaltyRuleResponse response = ParseUtils
                            .parsePenaltyRuleResponse(httpsCallableResult.getData());

                    if (response.getCode() == 200) {

                        hideLoading();
                        toggleLayoutEmpty(false);
                        this.penaltyRules.clear();
                        this.penaltyRules = new ArrayList<>(response.getResult());

                        if (this.penaltyRules.isEmpty()) {

                            toggleLayoutEmpty(true);
                            initViews(false);
                            return;
                        }

                        this.penaltyItems.clear();

                        for (PenaltyRule penaltyRule : this.penaltyRules) {

                            this.penaltyItems.add(new PenaltyItem(penaltyRule));
                        }

                        initViews(true);
                        return;
                    }

                    hideLoading();
                    toggleLayoutEmpty(true);
                })
                .addOnFailureListener(e -> {

                    hideLoading();
                    toggleLayoutEmpty(true);
                    Toast.makeText(this,
                            "An error occurred. Check your internet connection",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void initViews(boolean initRecyclerView) {

        binding.textViewName.setText(driver.getName());
        binding.textViewLicenseNumber.setText(driver.getLicenseNumber());
        binding.textViewPlateNumber.setText(driver.getPlateNumber());
        binding.searchViewPenalty.setImeOptions(EditorInfo.IME_ACTION_DONE);

        binding.searchViewPenalty.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        if (initRecyclerView) {

            initRecyclerView();
        }
    }

    private void initRecyclerView() {

        binding.searchViewPenalty.setQuery("", false);

        binding.recyclerView
                .setLayoutManager(new LinearLayoutManager(this));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_margin);
        binding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        adapter = new RecyclerAdapterPenalty(penaltyItems);
        binding.recyclerView.setAdapter(adapter);
    }

    public void issueTickets(View view) {

        List<PenaltyRule> selectedPenalties = new ArrayList<>();

        for (PenaltyItem item : penaltyItems) {

            if (item.isSelected) {

                selectedPenalties.add(item.getPenaltyRule());
            }
        }

        if (selectedPenalties.isEmpty()) {

            Toast.makeText(this,
                    "Select at least one violation to issue a ticket",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        sendTicketRequest(selectedPenalties);
    }

    private void sendTicketRequest(List<PenaltyRule> penalties) {

        Ticket.Violator violator = new Ticket.Violator(driver.getName(),
                driver.getLicenseNumber(), driver.getPlateNumber());

        Ticket.Issuer issuer = new Ticket.Issuer(Session.user.getName(),
                Session.user.getBadgeNumber());

        Ticket ticket = new Ticket(violator, issuer, penalties);
        HashMap<String, Object> data = new HashMap<>();
        data.put("ticket", ticket.toJson());

        showLoading();
        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("policeman-issue_ticket")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {

                    IssueTicketResponse response = ParseUtils
                            .parseIssueTicketResponse(httpsCallableResult.getData());

                    if (response.getCode() != 200) {

                        hideLoading();
                        Toast.makeText(this,
                                "An error occurred. Check your internet connection",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ticket.setId(response.getTicketId());
                    ticket.setDateIssued(response.getDateIssued());
                    ticket.setDateDue(response.getDateDue());

                    // add issued ticket locally
                    Session.user.getTicketsIssued().add(ticket);

                    hideLoading();
                    Toast.makeText(this,
                            "Ticket issued successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IssueTicketActivity.this,
                            ShowTicketActivityPolice.class);
                    Gson gson = new Gson();
                    intent.putExtra("ticket", gson.toJson(ticket));
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {

                    Log.d("on_error", "sendTicketRequest: " + e.getMessage() + ", " + e.toString());
                    hideLoading();
                    Toast.makeText(this,
                            "An error occurred. Check your internet connection",
                            Toast.LENGTH_SHORT).show();
                });
    }

    public void goBack(View view) {

        finish();
    }

    public void showLoading() {

        binding.layoutContent.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {

        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.VISIBLE);
    }

    public void toggleLayoutEmpty(boolean show) {

        binding.layoutEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setSelectionCount(int count) {

        binding.textViewViolationCount
                .setText(Html.fromHtml("Select Violations <b>(" + count + " Selected)"));
    }

    static class PenaltyItem {

        private PenaltyRule penaltyRule;
        private boolean isSelected;

        public PenaltyItem(PenaltyRule penaltyRule) {
            this.penaltyRule = penaltyRule;
            this.isSelected = false;
        }

        public PenaltyRule getPenaltyRule() {
            return penaltyRule;
        }

        public void setPenaltyRule(PenaltyRule penaltyRule) {
            this.penaltyRule = penaltyRule;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

    public class RecyclerAdapterPenalty
            extends RecyclerView.Adapter<RecyclerAdapterPenalty.PenaltyViewHolder>
            implements Filterable {

        private final List<PenaltyItem> penaltyItems;
        private final List<PenaltyItem> penaltyItemsFull;
        private final Filter filter;

        public RecyclerAdapterPenalty(List<PenaltyItem> penaltyItems) {
            this.penaltyItems = penaltyItems;
            this.penaltyItemsFull = new ArrayList<>(penaltyItems);
            this.filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    List<PenaltyItem> filteredList = new ArrayList<>();

                    if (charSequence.length() == 0) {

                        filteredList.addAll(penaltyItemsFull);

                    } else {

                        String stringPattern = charSequence.toString().toLowerCase().trim();

                        for (PenaltyItem item : penaltyItemsFull) {

                            if (item.getPenaltyRule()
                                    .getId().toLowerCase().contains(stringPattern)
                                    || item.getPenaltyRule()
                                    .getTitle().toLowerCase().contains(stringPattern)) {

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

                    penaltyItems.clear();
                    penaltyItems.addAll((List) filterResults.values);
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
        public PenaltyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            RecyclerItemPenaltyShortBinding binding = RecyclerItemPenaltyShortBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new PenaltyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull PenaltyViewHolder holder, int position) {

            PenaltyItem item = penaltyItems.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return penaltyItems.size();
        }

        class PenaltyViewHolder extends RecyclerView.ViewHolder {

            private final RecyclerItemPenaltyShortBinding binding;

            public PenaltyViewHolder(RecyclerItemPenaltyShortBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bind(PenaltyItem item) {

                binding.textViewId.setText(item.getPenaltyRule().getId());
                binding.textViewTitle.setText(item.getPenaltyRule().getTitle());
                binding.textViewPenalty.setText("G " + item.getPenaltyRule().getAmount());

                binding.getRoot().setOnClickListener(view -> {

                    PenaltyItem penaltyItem = penaltyItems.get(getAdapterPosition());

                    penaltyItem.setSelected(!penaltyItem.isSelected);

                    binding.checkbox.setChecked(penaltyItem.isSelected);

                    int count = 0;

                    for (PenaltyItem i : penaltyItems) {

                        if (i.isSelected) {

                            count++;
                        }
                    }

                    setSelectionCount(count);

                });
            }
        }
    }
}