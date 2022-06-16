package com.zionjr.policeticket.section_driver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.zionjr.policeticket.activities.CollectionCentersActivity;
import com.zionjr.policeticket.activities.PenaltyRulesActivity;
import com.zionjr.policeticket.databinding.ActivityDashboardDriverBinding;
import com.zionjr.policeticket.section_driver.cloudfunctions.CloudFunctions;
import com.zionjr.policeticket.section_driver.cloudfunctions.response_models.DriverResponse;
import com.zionjr.policeticket.section_driver.dialogs.DriverProfileDialog;
import com.zionjr.policeticket.section_driver.interfaces.IDashboardListener;
import com.zionjr.policeticket.section_driver.prefs.Session;

import java.text.MessageFormat;

public class DashboardActivity extends AppCompatActivity implements IDashboardListener {

    private ActivityDashboardDriverBinding binding;
    private CloudFunctions cloudFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cloudFunctions = new CloudFunctions(this);

        getLoggedInUser();
    }

    private void getLoggedInUser() {

        showLoading();
        cloudFunctions.getDriver(Session.getEmail(this));
    }

    @Override
    public void onGetDriverResponse(DriverResponse response, boolean error) {

        if (error) {

            hideLoading();
            Toast.makeText(this, "An error occurred. Check your internet connection",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (response.getCode() == 400) {

            hideLoading();
            return;
        }

        Session.user = response.getDriver();
        initViews();
        hideLoading();
    }

    private void initViews() {

        Log.d("logged_in_driver", "onCreate: user: " + Session.user.toString());
        binding.textViewName
                .setText(Html.fromHtml(MessageFormat
                        .format("<b>{0}</b>",
                                Session.user.getName())));
    }

    public void gotoProfilePage(View view) {

        DialogFragment dialog = new DriverProfileDialog();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog_driver_profile");
    }

    public void gotoTicketsPage(View view) {

        startActivity(new Intent(DashboardActivity.this, ShowTicketsActivity.class));
    }

    public void gotoTicketCentersPage(View view) {

        startActivity(new Intent(DashboardActivity.this,
                CollectionCentersActivity.class));
    }

    public void gotoPenaltyRulesPage(View view) {

        startActivity(new Intent(DashboardActivity.this, PenaltyRulesActivity.class));
    }

    public void logout(View view) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Logout ?");

        alert.setPositiveButton("Yes", (dialog, whichButton) -> {

            dialog.dismiss();
            Session.destroy(this);
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        });

        alert.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        alert.show();
    }

    public void showLoading() {

        binding.layoutContent.setVisibility(View.GONE);
        binding.layoutLoading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {

        binding.layoutLoading.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.VISIBLE);
    }
}