package com.example.weeklysummary;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.bliss.R;
import com.example.bliss.model.Mood;
import com.example.bliss.model.User;
import com.example.main.MainActivity;
import com.example.mooddistribution.MoodDistributionFragment; // 确保导入了目标 Fragment
import com.example.music.TrackMoodFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.stream.Collectors;

public class WeeklySummaryFragment extends Fragment {

    FirebaseFirestore firestore;
    FirebaseUser user;
    CollectionReference users;
    String uid;
    public WeeklySummaryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载布局文件
        return inflater.inflate(R.layout.fragment_weekly_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        users = firestore.collection("users");
        // 1. 初始化视图控件
        BarChart barChart = view.findViewById(R.id.barChart);
        ImageView btnBack = view.findViewById(R.id.btnBack);
        TextView tvBack = view.findViewById(R.id.tvBack);

        // 找到指向 Mood Distribution 的入口卡片 (请确保 XML 中 ID 为 cardMood)
        View cardMood = view.findViewById(R.id.cardMood);

        // 2. 处理返回逻辑
        View.OnClickListener backListener = v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        };
        TextView moodDistributionTab = view.findViewById(R.id.moodDistributionTab);
        moodDistributionTab.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Navigating to Mood Distribution", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weeklySummaryFragmentContainer, new MoodDistributionFragment())
                    .addToBackStack("MoodDistribution")
                    .commit();
        });
        btnBack.setOnClickListener(backListener);
        tvBack.setOnClickListener(backListener);
        // 3. 处理跳转逻辑 (点击卡片去 Mood Distribution)
        if (cardMood != null) {
            cardMood.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.weeklySummaryFragmentContainer, new MoodDistributionFragment())
                        .addToBackStack(null) // 加入回退栈，按返回键能回到本页
                        .commit();
            });
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // get mood data for each day of the week
        getMoodData().observe(getViewLifecycleOwner(), moods -> {
            List<Integer> moodPoints = new ArrayList<>(7);
            for (int i = 0; i < 7; i++) {
                moodPoints.add(0);
            }
            Log.d("Updated Moods", moods.toString());
            for (Mood mood : moods) {
                int day = mood.getTimestamp().toDate().getDay();
                int score = getScore(mood);
                moodPoints.set(day, moodPoints.get(day) + score);
            }
            Log.d("MOOD POINTS", moodPoints.toString());
            BarChart chart = view.findViewById(R.id.barChart);
            setupBarChart(chart);
            List<BarEntry> entries = new ArrayList<>(); 
            for (int i = 0; i < moodPoints.size(); i++) {
                // scale to 0-7
                double min = getMin(moodPoints);
                double max = getMax(moodPoints);
                double score = (moodPoints.get(i) - min) / (max - min) * 7;
                Log.d("Scaled score", String.valueOf(score));
                entries.add(new BarEntry(i, (float) score));
            }
            Log.d("Entries", entries.toString());
            setChartData(chart, entries);
        });
        
        // 4. 配置图表外观

        // 5. 填充图表数据
    }

    private double getMin(List<Integer> list) {
        double min = list.get(0);
        for (int num: list) {
            if (num < min) {
                min = num;
            }
        }
        return min;
    }

    private double getMax(List<Integer> list) {
        double max = list.get(0);
        for (int num: list) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }

    private int getScore(Mood mood) {
        switch (mood.getMood()) {
            case "happy":
                return 2;
            case "sad":
                return 1;
            case "calm":
                return -1;
            case "stressed":
                return -2;
            default:
                return 0;
        }
    }
    private void setupBarChart(BarChart barChart) {
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // 配置 X 轴
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);

        // 配置 Y 轴
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
    }
    void getUserData() {
        Log.d("user id", uid);
        // get the user
        firestore.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            User user = document.toObject(User.class);
                            Log.d(TAG, "User data: " + user.toString());
                        }
                    }
                });
    }

    MutableLiveData<List<Mood>> getMoodData() {
        // get the mood
        MutableLiveData<List<Mood>> moodsLiveData = new MutableLiveData<>();
        List<Mood> moods = new ArrayList<>();
        int score;
        firestore.collection("users").document(uid)
                .collection("mood_history")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Toast.makeText(getContext(), "Data retrieved successfully", Toast.LENGTH_SHORT).show();
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            Mood mood = document.toObject(Mood.class);
                            Log.d("Moodddd", mood.toString());
                            moods.add(mood); 
                        }
                        Log.d("MOOD LIST", moods.toString());
                        moodsLiveData.setValue(moods);
                    }

                });
        return moodsLiveData;
    }
    private void setChartData(BarChart barChart, List<BarEntry> barEntries) {

        // get the mood data
//        bars.add(new BarEntry(1, 420));
//        bars.add(new BarEntry(2, 475));
//        bars.add(new BarEntry(3, 508));
//        bars.add(new BarEntry(4, 660));
        BarDataSet barDataSet = new BarDataSet(barEntries, "Total Points");

        // 设置柱状图颜色
        barDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.purple_bar_chart));
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.setFitBars(true);

        // 动画刷新
        barChart.animateY(2000);
        barChart.invalidate();
    }
}