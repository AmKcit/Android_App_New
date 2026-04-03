package com.example.android_app_new;

public class HealthRecommendationEngine {

    /**
     * Get comprehensive health recommendations based on AQI and pollutant levels
     */
    public static HealthRecommendation getRecommendations(int aqi, double pm25, double pm10, double no2, double o3) {
        HealthRecommendation recommendation = new HealthRecommendation();

        // Set severity based on AQI
        recommendation.aqi = aqi;
        recommendation.severity = getAQISeverity(aqi);
        recommendation.mainAdvice = getMainAdvice(aqi);

        // Detailed pollutant-specific recommendations
        recommendation.pollutantAdvisories = new StringBuilder();

        if (pm25 > 35.4) {
            recommendation.pollutantAdvisories.append("🔴 PM2.5 CRITICAL: Avoid all outdoor activities\n");
            recommendation.addRiskGroup("Children", "Increased risk of respiratory issues");
            recommendation.addRiskGroup("Elderly", "Risk of cardiovascular problems");
            recommendation.addRiskGroup("Patients", "Avoid outdoor exposure");
        } else if (pm25 > 11.8) {
            recommendation.pollutantAdvisories.append("🟠 PM2.5 Elevated: Limit outdoor activities\n");
            recommendation.addRiskGroup("Sensitive groups", "Reduce strenuous outdoor exercise");
        } else {
            recommendation.pollutantAdvisories.append("🟢 PM2.5 Safe: Air quality is acceptable\n");
        }

        if (pm10 > 154) {
            recommendation.pollutantAdvisories.append("🔴 PM10 CRITICAL: Stay indoors\n");
        } else if (pm10 > 54) {
            recommendation.pollutantAdvisories.append("🟠 PM10 Elevated: Limit outdoor time\n");
        } else {
            recommendation.pollutantAdvisories.append("🟢 PM10 Safe: Air quality is acceptable\n");
        }

        if (no2 > 200) {
            recommendation.pollutantAdvisories.append("🔴 NO2 CRITICAL: Avoid outdoor exposure\n");
        } else if (no2 > 100) {
            recommendation.pollutantAdvisories.append("🟠 NO2 Elevated: Sensitive groups should limit activity\n");
        }

        if (o3 > 120) {
            recommendation.pollutantAdvisories.append("🔴 O3 CRITICAL: Avoid all outdoor activities\n");
        } else if (o3 > 80) {
            recommendation.pollutantAdvisories.append("🟠 O3 Elevated: Limit outdoor exposure\n");
        }

        // Add protective measures
        recommendation.protectiveMeasures = getProtectiveMeasures(aqi);

        // Add activity recommendations
        recommendation.activityRecommendations = getActivityRecommendations(aqi);

        return recommendation;
    }

    private static String getAQISeverity(int aqi) {
        if (aqi <= 50) return "GOOD";
        if (aqi <= 100) return "MODERATE";
        if (aqi <= 150) return "UNHEALTHY_FOR_SENSITIVE";
        if (aqi <= 200) return "UNHEALTHY";
        if (aqi <= 300) return "VERY_UNHEALTHY";
        return "HAZARDOUS";
    }

    private static String getMainAdvice(int aqi) {
        if (aqi <= 50) {
            return "Air quality is satisfactory. Enjoy outdoor activities!";
        } else if (aqi <= 100) {
            return "Air quality is acceptable. Unusually sensitive people should limit outdoor activities.";
        } else if (aqi <= 150) {
            return "Sensitive groups should consider limiting prolonged outdoor exertion.";
        } else if (aqi <= 200) {
            return "General public may experience health effects. Limit outdoor activities.";
        } else if (aqi <= 300) {
            return "Health alert: Everyone should avoid prolonged outdoor exertion.";
        } else {
            return "EMERGENCY: Stay indoors and close windows. Use air purifiers if available.";
        }
    }

    private static String getProtectiveMeasures(int aqi) {
        StringBuilder measures = new StringBuilder();

        if (aqi <= 50) {
            measures.append("✓ No special precautions needed\n");
        } else if (aqi <= 100) {
            measures.append("✓ Consider wearing a mask for prolonged outdoor activities\n");
        } else if (aqi <= 150) {
            measures.append("✓ Wear N95 mask during outdoor activities\n");
            measures.append("✓ Limit outdoor time\n");
        } else if (aqi <= 200) {
            measures.append("✓ Use N95 mask for all outdoor activities\n");
            measures.append("✓ Keep windows and doors closed\n");
            measures.append("✓ Use air purifier with HEPA filter indoors\n");
        } else {
            measures.append("✓ Stay indoors\n");
            measures.append("✓ Use air purifier with activated carbon filter\n");
            measures.append("✓ Keep all windows and doors sealed\n");
            measures.append("✓ Consider evacuation if symptoms worsen\n");
        }

        return measures.toString();
    }

    private static String getActivityRecommendations(int aqi) {
        StringBuilder activities = new StringBuilder();

        if (aqi <= 50) {
            activities.append("✓ All outdoor activities safe\n");
            activities.append("✓ Running, cycling, sports all acceptable\n");
        } else if (aqi <= 100) {
            activities.append("✓ Outdoor activities acceptable for most\n");
            activities.append("⚠ Sensitive groups should avoid strenuous exercise\n");
        } else if (aqi <= 150) {
            activities.append("⚠ Avoid outdoor activities\n");
            activities.append("✓ Light indoor activities recommended\n");
            activities.append("✓ Yoga, stretching, light walking indoors\n");
        } else if (aqi <= 200) {
            activities.append("❌ No outdoor activities\n");
            activities.append("✓ Stay indoors\n");
            activities.append("✓ Gentle indoor exercises only\n");
        } else {
            activities.append("❌ EMERGENCY: Stay indoors\n");
            activities.append("✓ Rest and limit physical exertion\n");
            activities.append("✓ Consult doctor if symptoms appear\n");
        }

        return activities.toString();
    }

    /**
     * Inner class to hold recommendation data
     */
    public static class HealthRecommendation {
        public int aqi;
        public String severity;
        public String mainAdvice;
        public StringBuilder pollutantAdvisories;
        public String protectiveMeasures;
        public String activityRecommendations;
        public StringBuilder riskGroups = new StringBuilder();

        public void addRiskGroup(String group, String risk) {
            riskGroups.append("⚠ ").append(group).append(": ").append(risk).append("\n");
        }

        public String getFullRecommendation() {
            StringBuilder full = new StringBuilder();
            full.append("═══════════════════════════════════════\n");
            full.append("AIR QUALITY HEALTH RECOMMENDATION\n");
            full.append("═══════════════════════════════════════\n\n");

            full.append("AQI: ").append(aqi).append(" (").append(severity).append(")\n\n");

            full.append("MAIN ADVICE:\n");
            full.append(mainAdvice).append("\n\n");

            full.append("POLLUTANT LEVELS:\n");
            full.append(pollutantAdvisories).append("\n");

            if (riskGroups.length() > 0) {
                full.append("AT-RISK GROUPS:\n");
                full.append(riskGroups).append("\n");
            }

            full.append("PROTECTIVE MEASURES:\n");
            full.append(protectiveMeasures).append("\n");

            full.append("ACTIVITY RECOMMENDATIONS:\n");
            full.append(activityRecommendations).append("\n");

            full.append("═══════════════════════════════════════\n");

            return full.toString();
        }
    }
}

