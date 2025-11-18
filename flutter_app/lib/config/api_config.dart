class ApiConfig {
  // 개발 환경에서는 localhost 대신 실제 IP 주소를 사용하세요
  // Android 에뮬레이터: 10.0.2.2
  // iOS 시뮬레이터: localhost
  // 실제 기기: 컴퓨터의 로컬 IP (예: 192.168.x.x)
  
  static const String baseUrl = 'http://10.0.2.2:8080/api';
  // static const String baseUrl = 'http://localhost:8080/api';
  // static const String baseUrl = 'http://192.168.0.100:8080/api';
  
  static const Duration connectionTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
}

