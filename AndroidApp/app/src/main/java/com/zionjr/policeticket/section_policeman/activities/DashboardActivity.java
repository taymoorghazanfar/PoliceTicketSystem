package com.zionjr.policeticket.section_policeman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.zionjr.policeticket.activities.PenaltyRulesActivity;
import com.zionjr.policeticket.databinding.ActivityDashboardPolicemanBinding;
import com.zionjr.policeticket.section_policeman.cloudfunctions.CloudFunctions;
import com.zionjr.policeticket.section_policeman.cloudfunctions.response_models.PolicemanResponse;
import com.zionjr.policeticket.section_policeman.dialogs.PolicemanProfileDialog;
import com.zionjr.policeticket.section_policeman.dialogs.SelectScanTypeDialog;
import com.zionjr.policeticket.section_policeman.interfaces.IDashboardListener;
import com.zionjr.policeticket.section_policeman.interfaces.ISelectScanListener;
import com.zionjr.policeticket.section_policeman.prefs.Session;

import java.text.MessageFormat;

public class DashboardActivity extends AppCompatActivity implements IDashboardListener, ISelectScanListener {

    private ActivityDashboardPolicemanBinding binding;
    private CloudFunctions cloudFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardPolicemanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cloudFunctions = new CloudFunctions(this);

        getLoggedInUser();
    }

    private void getLoggedInUser() {

        showLoading();
        cloudFunctions.getPoliceman(Session.getEmail(this));
    }

    @Override
    public void onGetPolicemanResponse(PolicemanResponse response, boolean error) {

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

        Session.user = response.getPoliceman();
        initViews();
        hideLoading();
    }

    private void initViews() {

        binding.textViewName
                .setText(Html.fromHtml(MessageFormat
                        .format("<b>{0}</b>",
                                Session.user.getName())));
    }

    public void gotoScanDocumentPage(View view) {

        DialogFragment dialog = new SelectScanTypeDialog(this, false);
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog_select_scan_type");
    }

    public void gotoIssueTicketPage(View view) {

        DialogFragment dialog = new SelectScanTypeDialog(this, true);
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog_select_scan_type");
    }

    public void gotoTicketsIssuedPage(View view) {

        startActivity(new Intent(DashboardActivity.this, ShowIssuedTicketsActivity.class));
    }

    public void gotoPenaltyRulesPage(View view) {

        startActivity(new Intent(DashboardActivity.this, PenaltyRulesActivity.class));
    }

    public void gotoProfilePage(View view) {

        DialogFragment dialog = new PolicemanProfileDialog();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog_policeman_profile");
    }

    @Override
    public void onScanLicenseSelected(boolean issueTicket) {


        Intent intent = new Intent(DashboardActivity.this,
                ScanLicenseAsPoliceActivity.class);
        intent.putExtra("issue_ticket", issueTicket);
        startActivity(intent);
    }

    @Override
    public void onScanPlateNumberSelected(boolean issueTicket) {

        Intent intent = new Intent(DashboardActivity.this,
                ScanPlateNumberAsPoliceActivity.class);
        intent.putExtra("issue_ticket", issueTicket);
        startActivity(intent);
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