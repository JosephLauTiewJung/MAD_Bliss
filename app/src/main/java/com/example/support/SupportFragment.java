package com.example.support;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.bliss.R;
import com.example.main.HomeFragment;
import com.google.android.material.button.MaterialButton;

public class SupportFragment extends Fragment {

    private LinearLayout additionalServicesLayout;
    private ViewGroup supportContainer;
    private MaterialButton seeMoreButton;
    private boolean isExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        additionalServicesLayout = view.findViewById(R.id.additionalServicesLayout);
        supportContainer = view.findViewById(R.id.supportContainer);
        seeMoreButton = view.findViewById(R.id.seeMoreButton);

        // 1. EMERGENCY CALL
        view.findViewById(R.id.emergencyCallButton).setOnClickListener(v -> makeCall("999"));

        // 2. SETUP ROWS (Tap to Call + Long Press to Copy)
        setupRow(view.findViewById(R.id.rowBefrienders), "0376272929", "Befrienders KL");
        setupRow(view.findViewById(R.id.rowHeal), "15555", "Talian HEAL");
        setupRow(view.findViewById(R.id.rowMiasa), "1800820066", "MIASA");
        setupRow(view.findViewById(R.id.rowMmha), "0327806803", "MMHA");
        setupRow(view.findViewById(R.id.rowTalianKasih), "15999", "Talian Kasih");

        // 3. SETUP WEBSITES
        view.findViewById(R.id.webBefrienders).setOnClickListener(v -> openWeb("https://www.befrienders.org.my/"));
        view.findViewById(R.id.webHeal).setOnClickListener(v -> openWeb("https://www.infosihat.gov.my/talian-heal.html"));
        view.findViewById(R.id.webMiasa).setOnClickListener(v -> openWeb("https://miasa.org.my/"));
        view.findViewById(R.id.webMmha).setOnClickListener(v -> openWeb("https://mmha.org.my/"));
        view.findViewById(R.id.webTalianKasih).setOnClickListener(v -> openWeb("https://www.kpwkm.gov.my/"));

        // 4. SEE MORE TOGGLE
        seeMoreButton.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(supportContainer, new AutoTransition());
            isExpanded = !isExpanded;
            additionalServicesLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            seeMoreButton.setText(isExpanded ? "See less..." : "See more...");
        });

        // 5. BACK BUTTON - NAVIGATE TO HOME
        view.findViewById(R.id.backButton).setOnClickListener(v -> {
            // This Toast helps you test if the button is even being "sensed"
            // Toast.makeText(getContext(), "Back Clicked", Toast.LENGTH_SHORT).show();

            if (getParentFragmentManager() != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }
        });

        return view;
    }

    private void setupRow(View row, String number, String name) {
        row.setOnClickListener(v -> makeCall(number));
        row.setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(name, number);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), name + " number copied!", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void makeCall(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void openWeb(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}