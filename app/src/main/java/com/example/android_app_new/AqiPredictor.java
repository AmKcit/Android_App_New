package com.example.android_app_new;

public class AqiPredictor {

    /**
     * Simple moving average prediction for AQI
     * Uses last 7 days of data to predict next day
     */
    public static double predictAqiSimpleAverage(double[] aqiValues) {
        if (aqiValues == null || aqiValues.length == 0) {
            return 0;
        }

        double sum = 0;
        for (double value : aqiValues) {
            sum += value;
        }
        return sum / aqiValues.length;
    }

    /**
     * Linear regression prediction for AQI
     * Fits a line to historical data and predicts future value
     */
    public static double predictAqiRegression(double[] aqiValues) {
        if (aqiValues == null || aqiValues.length < 2) {
            return predictAqiSimpleAverage(aqiValues);
        }

        int n = aqiValues.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        // Calculate sums for linear regression formula
        for (int i = 0; i < n; i++) {
            double x = i + 1; // Day number (1, 2, 3, ...)
            double y = aqiValues[i]; // AQI value

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        // Linear regression: y = mx + b
        // m = (n*sumXY - sumX*sumY) / (n*sumX2 - sumX*sumX)
        double denominator = (n * sumX2) - (sumX * sumX);
        if (denominator == 0) {
            return predictAqiSimpleAverage(aqiValues);
        }

        double m = ((n * sumXY) - (sumX * sumY)) / denominator;
        double b = (sumY - (m * sumX)) / n;

        // Predict next day (day n+1)
        double nextDay = n + 1;
        double prediction = m * nextDay + b;

        // Ensure prediction is not negative
        return Math.max(0, prediction);
    }

    /**
     * Weighted average prediction (recent days weighted more)
     */
    public static double predictAqiWeighted(double[] aqiValues) {
        if (aqiValues == null || aqiValues.length == 0) {
            return 0;
        }

        // Weight recent values more heavily
        // Last value gets weight n, first value gets weight 1
        double sumWeighted = 0;
        double sumWeights = 0;

        for (int i = 0; i < aqiValues.length; i++) {
            double weight = i + 1; // Increasing weight
            sumWeighted += aqiValues[i] * weight;
            sumWeights += weight;
        }

        return sumWeighted / sumWeights;
    }

    /**
     * Get prediction confidence level
     */
    public static String getPredictionConfidence(double[] aqiValues) {
        if (aqiValues == null || aqiValues.length < 3) {
            return "Low";
        }

        // Calculate trend consistency
        double[] changes = new double[aqiValues.length - 1];
        for (int i = 0; i < changes.length; i++) {
            changes[i] = aqiValues[i + 1] - aqiValues[i];
        }

        // Calculate standard deviation of changes
        double mean = 0;
        for (double change : changes) {
            mean += Math.abs(change);
        }
        mean /= changes.length;

        double variance = 0;
        for (double change : changes) {
            variance += Math.pow(Math.abs(change) - mean, 2);
        }
        variance /= changes.length;
        double stdDev = Math.sqrt(variance);

        if (stdDev < 5) {
            return "High";
        } else if (stdDev < 15) {
            return "Medium";
        } else {
            return "Low";
        }
    }
}

