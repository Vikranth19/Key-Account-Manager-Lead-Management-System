package com.assignment.kam_lead_management_system.strategy;

public interface PerformanceStrategy {

    boolean evaluate(long orderCount, int orderThreshold);

    String getPerformanceStatus();
}
