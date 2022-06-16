package com.zionjr.policeticket.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.functions.FirebaseFunctions;
import com.zionjr.policeticket.R;
import com.zionjr.policeticket.cloud_functions.response_models.CollectionCenterResponse;
import com.zionjr.policeticket.databinding.ActivityCollectionCentersBinding;
import com.zionjr.policeticket.model.entities.CollectionCenter;
import com.zionjr.policeticket.ui.adapters.RecyclerAdapterCollectionCenter;
import com.zionjr.policeticket.ui.decorators.SpacesItemDecoration;
import com.zionjr.policeticket.utils.ParseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CollectionCentersActivity extends AppCompatActivity
        implements RecyclerAdapterCollectionCenter.ICollectionCenterClickListener {

    private ActivityCollectionCentersBinding binding;
    private RecyclerAdapterCollectionCenter adapter;
    private List<CollectionCenter> collectionCenters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollectionCentersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toggleLayoutEmpty(false);

        initSearchView();

        getCollectionCenters();
    }

    private void initSearchView() {

        binding.searchViewCollectionCenter.setImeOptions(EditorInfo.IME_ACTION_DONE);

        binding.searchViewCollectionCenter
                .setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void getCollectionCenters() {

        showLoading();

        FirebaseFunctions
                .getInstance()
                .getHttpsCallable("collection_center-get_all_collection_centers")
                .call()
                .addOnSuccessListener(httpsCallableResult -> {

                    CollectionCenterResponse response = ParseUtils
                            .parseCollectionCenterResponse(httpsCallableResult.getData());

                    if (response.getCode() == 200) {

                        hideLoading();
                        toggleLayoutEmpty(false);
                        this.collectionCenters.clear();
                        this.collectionCenters = new ArrayList<>(response.getResult());

                        if (this.collectionCenters.isEmpty()) {

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

        binding.searchViewCollectionCenter.setQuery("", false);

        binding.recyclerViewCollectionCenter
                .setLayoutManager(new LinearLayoutManager(this));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_margin);
        binding.recyclerViewCollectionCenter.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        adapter = new RecyclerAdapterCollectionCenter(this, this, collectionCenters);
        binding.recyclerViewCollectionCenter.setAdapter(adapter);
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

    @Override
    public void onGotoContactClick(String contactNumber) {

        Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.fromParts("tel", contactNumber, null));
        startActivity(intent);
    }

    @Override
    public void onGotoMapClick(double lat, double lng) {

        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }
}