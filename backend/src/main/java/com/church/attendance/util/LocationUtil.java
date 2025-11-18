package com.church.attendance.util;

public class LocationUtil {
    
    private static final double EARTH_RADIUS_METERS = 6371000.0; // 지구 반지름 (미터)
    
    /**
     * Haversine 공식을 사용하여 두 좌표 간의 거리를 계산합니다.
     * 
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 두 지점 간의 거리(미터)
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_METERS * c;
    }
    
    /**
     * 특정 좌표가 지정된 반경 내에 있는지 확인합니다.
     * 
     * @param centerLat 중심점의 위도
     * @param centerLon 중심점의 경도
     * @param targetLat 확인할 지점의 위도
     * @param targetLon 확인할 지점의 경도
     * @param radiusMeters 반경(미터)
     * @return 반경 내에 있으면 true, 아니면 false
     */
    public static boolean isWithinRadius(double centerLat, double centerLon, 
                                        double targetLat, double targetLon, 
                                        double radiusMeters) {
        double distance = calculateDistance(centerLat, centerLon, targetLat, targetLon);
        return distance <= radiusMeters;
    }
}


