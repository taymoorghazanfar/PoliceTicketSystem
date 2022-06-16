package com.zionjr.policeticket.ui.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zionjr.policeticket.R;
import com.zionjr.policeticket.databinding.RecyclerItemPenaltyBinding;
import com.zionjr.policeticket.model.entities.PenaltyRule;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterPenalty
        extends RecyclerView.Adapter<RecyclerAdapterPenalty.PenaltyRuleViewHolder>
        implements Filterable {

    private final List<PenaltyRule> PenaltyRules;
    private final List<PenaltyRule> PenaltyRulesFull;
    private final Filter filter;

    public RecyclerAdapterPenalty(List<PenaltyRule> PenaltyRules) {
        this.PenaltyRules = PenaltyRules;
        this.PenaltyRulesFull = new ArrayList<>(PenaltyRules);
        this.filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                List<PenaltyRule> filteredList = new ArrayList<>();

                if (charSequence.length() == 0) {

                    filteredList.addAll(PenaltyRulesFull);

                } else {

                    String stringPattern = charSequence.toString().toLowerCase().trim();

                    for (PenaltyRule item : PenaltyRulesFull) {

                        if (item.getId().toLowerCase().contains(stringPattern)
                                || item.getTitle().toLowerCase().contains(stringPattern)
                                || item.getDescription().toLowerCase().contains(stringPattern)) {

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

                PenaltyRules.clear();
                PenaltyRules.addAll((List) filterResults.values);
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
    public PenaltyRuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerItemPenaltyBinding binding = RecyclerItemPenaltyBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new PenaltyRuleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PenaltyRuleViewHolder holder, int position) {

        PenaltyRule code = PenaltyRules.get(position);
        holder.bind(code);
    }

    @Override
    public int getItemCount() {
        return PenaltyRules.size();
    }

    class PenaltyRuleViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerItemPenaltyBinding binding;

        public PenaltyRuleViewHolder(RecyclerItemPenaltyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PenaltyRule penaltyRule) {

            binding.textViewId.setText(penaltyRule.getId());
            binding.textViewTitle.setText(penaltyRule.getTitle());
            binding.textViewPenalty.setText(Html
                    .fromHtml("Fine: <b>G "
                            + penaltyRule.getAmount() + "</b>"));
            binding.textViewDescription.setText(penaltyRule.getDescription());
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
