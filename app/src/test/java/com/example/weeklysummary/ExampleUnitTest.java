package com.example.weeklysummary;

import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.bliss.model.Mood;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetMax() {
        List<Integer> list = List.of(1, 3, 7, 0, 5);
        int max = list.get(0);
        for (int num: list) {
            if (num > max) {
                max = num;
            }
        }
        assertEquals(max, 7);
    }

    @Test
    public void testGetMin() {
        List<Integer> list = List.of(1, 3, 7, 0, 5);
        int min = list.get(0);
        for (int num: list) {
            if (num < min) {
                min = num;
            }
        }
        assertEquals(min, 0);
    }
}