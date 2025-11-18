import 'package:geolocator/geolocator.dart';
import 'package:permission_handler/permission_handler.dart';

class LocationService {
  // 위치 권한 확인
  static Future<bool> checkPermission() async {
    final status = await Permission.location.status;
    return status.isGranted;
  }

  // 위치 권한 요청
  static Future<bool> requestPermission() async {
    final status = await Permission.location.request();
    return status.isGranted;
  }

  // 위치 서비스 활성화 확인
  static Future<bool> isLocationServiceEnabled() async {
    return await Geolocator.isLocationServiceEnabled();
  }

  // 현재 위치 가져오기
  static Future<Position> getCurrentLocation() async {
    // 위치 서비스 확인
    bool serviceEnabled = await isLocationServiceEnabled();
    if (!serviceEnabled) {
      throw Exception('위치 서비스가 비활성화되어 있습니다. 설정에서 활성화해주세요.');
    }

    // 권한 확인
    bool hasPermission = await checkPermission();
    if (!hasPermission) {
      // 권한 요청
      hasPermission = await requestPermission();
      if (!hasPermission) {
        throw Exception('위치 권한이 거부되었습니다.');
      }
    }

    // 현재 위치 가져오기
    try {
      Position position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
        timeLimit: const Duration(seconds: 10),
      );
      return position;
    } catch (e) {
      throw Exception('위치를 가져오는데 실패했습니다: $e');
    }
  }

  // 두 좌표 간의 거리 계산 (미터)
  static double calculateDistance(
    double lat1,
    double lon1,
    double lat2,
    double lon2,
  ) {
    return Geolocator.distanceBetween(lat1, lon1, lat2, lon2);
  }
}

