package com.example.weeklysummary;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.services.events.TimeStamp;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.bliss.model.Mood;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.weeklysummary", appContext.getPackageName());
    }

    @Test
    public void timeStampTest() {
        Timestamp current = Timestamp.now();
        System.out.println(current);
    }

    @Test
    public void testScore() {
        System.out.println(moodScoreToPoints().getValue());
    }
    MutableLiveData<Integer> moodScoreToPoints() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        // get the mood
        List<Mood> moods = new ArrayList<>();
        MutableLiveData<Integer> score = new MutableLiveData<>();
        firestore.collection("users").document(uid)
                .collection("mood_history")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            Mood mood = document.toObject(Mood.class);
                            Log.d("Moodddd", mood.toString());
                            moods.add(mood);
                        }
                        Log.d("MOOD LIST", moods.toString());
                        score.setValue(getScore(moods));
                    }
                });
        Log.d("Scoree", String.valueOf(score.getValue()));
        return score;
    }
    private int getScore(List<Mood> moods) {
        int score = 0;
        for (Mood mood : moods) {
            switch (mood.getMood()) {
                case "happy":
                    score += 2;
                    break;
                case "sad":
                    score -= 1;
                    break;
                case "calm":
                    score += 1;
                    break;
                case "stressed":
                    score -= 2;
                    break;
                default:
                    score += 0;
            }
        }
        return score;
    }


}