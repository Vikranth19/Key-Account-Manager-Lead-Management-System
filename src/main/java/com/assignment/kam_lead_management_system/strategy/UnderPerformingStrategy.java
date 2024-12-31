package com.assignment.kam_lead_management_system.strategy;

public class UnderPerformingStrategy implements PerformanceStrategy{

    @Override
    public boolean evaluate(long orderCount, int orderThreshold) {
        return orderCount < orderThreshold;
    }

    @Override
    public String getPerformanceStatus() {
        return "Under-Performing";
    }
}
