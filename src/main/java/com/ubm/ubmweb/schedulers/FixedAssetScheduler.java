package com.ubm.ubmweb.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ubm.ubmweb.services.FixedAssetService;

@Component
public class FixedAssetScheduler {

    @Autowired
    private FixedAssetService fixedAssetService;

    @Scheduled(cron = "0 0 0 * * ?") // Run every day at midnight
    public void updateFixedAssetRemainingCosts() {
        fixedAssetService.updateRemainingCosts();
    }
}
