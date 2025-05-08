package com.ubm.ubmweb.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ubm.ubmweb.service.FixedAssetService;

@Component
public class FixedAssetScheduler {

    @Autowired
    private FixedAssetService fixedAssetService;

    @Scheduled(cron = "0 0 0 * * ?") // Run every day at midnight
    public void updateFixedAssetRemainingCosts() {
        fixedAssetService.updateRemainingCosts();
    }
}
