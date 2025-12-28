package com.example.bliss.model;

import com.google.firebase.Timestamp;

public class Mood {
    private String mood;
    private Long time_millis;
    private Timestamp timestamp;

    public Mood(String mood, Timestamp timestamp, Long time_millis) {
        this.mood = mood;
        this.timestamp = timestamp;
        this.time_millis = time_millis;
    }

    public Mood() {
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    @Override
    public String toString() {
        return "Mood{" +
                "mood='" + mood + '\'' +
                ", time_millis=" + time_millis +
                ", timestamp=" + timestamp +
                '}';
    }

    public Long getTime_millis() {
        return time_millis;
    }

    public void setTime_millis(Long time_millis) {
        this.time_millis = time_millis;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
