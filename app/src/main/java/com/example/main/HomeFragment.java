package com.example.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bliss.R;
import com.example.music.TrackMoodFragment;
import com.example.support.SupportFragment;
import com.example.weeklysummary.WeeklySummaryFragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup card click listeners
        setupMoodTrackerCard(view);
        setupOtherCards(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).showHeader(true);
    }

    private void setupMoodTrackerCard(View view) {
        View cardMoodTracker = view.findViewById(R.id.cardMoodTracker);
        if (cardMoodTracker != null) {
            // Set up the mood tracker card content
            TextView tvTitle = cardMoodTracker.findViewById(R.id.itemTitle);
            TextView tvSub = cardMoodTracker.findViewById(R.id.itemSubtitle);
            ImageView imgIcon = cardMoodTracker.findViewById(R.id.itemIcon);

            tvTitle.setText("Track Your Mood");
            tvSub.setText("How are you feeling today?");
            imgIcon.setImageResource(R.drawable.ic_track_mood);

            cardMoodTracker.setOnClickListener(v -> {
                ((MainActivity) getActivity()).replaceFragment(new com.example.music.TrackMoodFragment(), true);
            });
        }
    }

    private void setupOtherCards(View view) {
        setupCard(view.findViewById(R.id.cardSupport),
                "Support", "Find help and comforting resources.",
                R.drawable.ic_support2,
                new SupportFragment());

        setupCard(view.findViewById(R.id.cardSummary),
                "Summary", "View your progress.",
                R.drawable.ic_summary,
                new WeeklySummaryFragment());

        // Setup inspiration card
        View cardInspiration = view.findViewById(R.id.cardInspiration);
        if (cardInspiration != null) {
            cardInspiration.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Daily inspiration coming soon!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void fetchUserName() {
        // Welcome message is now handled in MainActivity header
    }

    private void setupCard(View cardView, String title, String subtitle, int iconRes,Fragment targetFragment) {
        if (cardView == null) return;

        TextView tvTitle = cardView.findViewById(R.id.itemTitle);
        TextView tvSub = cardView.findViewById(R.id.itemSubtitle);
        ImageView imgIcon = cardView.findViewById(R.id.itemIcon);

        tvTitle.setText(title);
        tvSub.setText(subtitle);
        imgIcon.setImageResource(iconRes);

        cardView.setOnClickListener(v -> {
            if (targetFragment != null) {
                ((MainActivity) getActivity()).replaceFragment(targetFragment, true);
            } else {
                Toast.makeText(getContext(), title + " coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }


}