package com.paxees.sms;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        // Start the background service
        startService(new Intent(this, YourService.class));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}