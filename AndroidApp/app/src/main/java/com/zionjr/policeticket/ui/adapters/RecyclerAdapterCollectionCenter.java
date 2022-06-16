package com.zionjr.policeticket.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zionjr.policeticket.databinding.RecyclerItemCollectionCenterBinding;
import com.zionjr.policeticket.model.entities.CollectionCenter;
import com.zionjr.policeticket.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterCollectionCenter
        extends RecyclerView.Adapter<RecyclerAdapterCollectionCenter.CollectionCenterViewHolder>
        implements Filterable {

    private final List<CollectionCenter> collectionCenters;
    private final List<CollectionCenter> collectionCentersFull;
    private final Filter filter;
    private final ICollectionCenterClickListener listener;
    private final Context context;

    public RecyclerAdapterCollectionCenter(Context context,
                                           ICollectionCenterClickListener listener,
                                           List<CollectionCenter> collectionCenters) {

        this.context = context;
        this.listener = listener;
        this.collectionCenters = collectionCenters;
        this.collectionCentersFull = new ArrayList<>(collectionCenters);
        this.filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                List<CollectionCenter> filteredList = new ArrayList<>();

                if (charSequence.length() == 0) {

                    filteredList.addAll(collectionCentersFull);

                } else {

                    String stringPattern = charSequence.toString().toLowerCase().trim();

                    for (CollectionCenter item : collectionCentersFull) {

                        if (item.getName().toLowerCase().contains(stringPattern)) {

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

                collectionCenters.clear();
                collectionCenters.addAll((List) filterResults.values);
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
    public CollectionCenterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerItemCollectionCenterBinding binding = RecyclerItemCollectionCenterBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new CollectionCenterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionCenterViewHolder holder, int position) {

        CollectionCenter code = collectionCenters.get(position);
        holder.bind(code);
    }

    @Override
    public int getItemCount() {
        return collectionCenters.size();
    }

    public interface ICollectionCenterClickListener {

        void onGotoContactClick(String contactNumber);

        void onGotoMapClick(double lat, double lng);
    }

    class CollectionCenterViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerItemCollectionCenterBinding binding;

        public CollectionCenterViewHolder(RecyclerItemCollectionCenterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CollectionCenter collectionCenter) {

            binding.textViewName.setText(collectionCenter.getName());

            binding.cardViewContact.setOnClickListener(view ->
                    listener.onGotoContactClick(collectionCenter.getPhone()));

            binding.cardViewMap.setOnClickListener(view ->
                    listener.onGotoMapClick(collectionCenter.getLat(), collectionCenter.getLng()));
        }
    }
}

