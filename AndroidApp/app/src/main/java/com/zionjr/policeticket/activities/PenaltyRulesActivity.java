package com.zionjr.policeticket.activities;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.functions.FirebaseFunctions;
import com.zionjr.policeticket.R;
import com.zionjr.policeticket.cloud_functions.response_models.PenaltyRuleResponse;
import com.zionjr.policeticket.databinding.ActivityPenaltyRulesBinding;
import com.zionjr.policeticket.model.entities.PenaltyRule;
import com.zionjr.policeticket.ui.adapters.RecyclerAdapterPenalty;
import com.zionjr.policeticket.ui.decorators.SpacesItemDecoration;
import com.zionjr.policeticket.utils.ParseUtils;

import java.util.ArrayList;
import java.util.List;

public class PenaltyRulesActivity extends AppCompatActivity {

    private ActivityPenaltyRulesBinding binding;
    private RecyclerAdapterPenalty adapter;
    private List<PenaltyRule> penaltyRules = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPenaltyRulesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toggleLayoutEmpty(false);

        initSearchView();

        getPenaltyRules();
    }

    private void initSearchView() {

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
                            return;
                        }

                        initRecyclerView();
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

    private void initRecyclerView() {

        binding.searchViewPenalty.setQuery("", false);

        binding.recyclerViewPenalty
                .setLayoutManager(new LinearLayoutManager(this));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_margin);
        binding.recyclerViewPenalty.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        adapter = new RecyclerAdapterPenalty(penaltyRules);
        binding.recyclerViewPenalty.setAdapter(adapter);
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
}