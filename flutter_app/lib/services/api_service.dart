import 'dart:convert';
import 'package:http/http.dart' as http;
import '../config/api_config.dart';
import '../utils/storage_helper.dart';

class ApiService {
  static Map<String, String> _getHeaders({bool includeAuth = true}) {
    final headers = {
      'Content-Type': 'application/json',
    };

    if (includeAuth) {
      final token = StorageHelper.getToken();
      if (token != null) {
        headers['Authorization'] = 'Bearer $token';
      }
    }

    return headers;
  }

  // 로그인
  static Future<Map<String, dynamic>> login(
      String username, String password) async {
    final response = await http.post(
      Uri.parse('${ApiConfig.baseUrl}/auth/login'),
      headers: _getHeaders(includeAuth: false),
      body: json.encode({
        'username': username,
        'password': password,
      }),
    );

    if (response.statusCode == 200) {
      return json.decode(utf8.decode(response.bodyBytes));
    } else {
      final error = json.decode(utf8.decode(response.bodyBytes));
      throw Exception(error['message'] ?? '로그인에 실패했습니다');
    }
  }

  // 회원가입
  static Future<Map<String, dynamic>> register({
    required String username,
    required String password,
    required String name,
    String? phone,
    String? email,
  }) async {
    final response = await http.post(
      Uri.parse('${ApiConfig.baseUrl}/auth/register'),
      headers: _getHeaders(includeAuth: false),
      body: json.encode({
        'username': username,
        'password': password,
        'name': name,
        'phone': phone?.trim().isEmpty == true ? null : phone?.trim(),
        'email': email?.trim().isEmpty == true ? null : email?.trim(),
      }),
    );

    if (response.statusCode == 200) {
      return json.decode(utf8.decode(response.bodyBytes));
    } else {
      try {
        final errorBody = json.decode(utf8.decode(response.bodyBytes));
        final errorMessage = errorBody['message'] ?? '회원가입에 실패했습니다';
        throw Exception(errorMessage);
      } catch (e) {
        if (e is Exception) {
          rethrow;
        }
        throw Exception('회원가입에 실패했습니다. 네트워크를 확인해주세요.');
      }
    }
  }

  // 활성화된 예배 목록 조회
  static Future<List<dynamic>> getActiveServices() async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/services'),
      headers: _getHeaders(),
    );

    if (response.statusCode == 200) {
      return json.decode(utf8.decode(response.bodyBytes));
    } else {
      throw Exception('예배 목록을 불러오는데 실패했습니다');
    }
  }

  // 다음 예배 조회
  static Future<Map<String, dynamic>> getNextService() async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/services/next'),
      headers: _getHeaders(),
    );

    if (response.statusCode == 200) {
      return json.decode(utf8.decode(response.bodyBytes));
    } else {
      throw Exception('다음 예배 정보를 불러오는데 실패했습니다');
    }
  }

  // 출석 체크
  static Future<Map<String, dynamic>> checkAttendance({
    required int serviceId,
    required double latitude,
    required double longitude,
  }) async {
    final response = await http.post(
      Uri.parse('${ApiConfig.baseUrl}/attendance/check'),
      headers: _getHeaders(),
      body: json.encode({
        'serviceId': serviceId,
        'latitude': latitude,
        'longitude': longitude,
      }),
    );

    if (response.statusCode == 200) {
      return json.decode(utf8.decode(response.bodyBytes));
    } else {
      final error = json.decode(utf8.decode(response.bodyBytes));
      throw Exception(error['message'] ?? '출석 체크에 실패했습니다');
    }
  }

  // 내 출석 기록 조회
  static Future<List<dynamic>> getMyAttendances() async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/attendance/my'),
      headers: _getHeaders(),
    );

    if (response.statusCode == 200) {
      return json.decode(utf8.decode(response.bodyBytes));
    } else {
      throw Exception('출석 기록을 불러오는데 실패했습니다');
    }
  }

  // 네이버 로그인
  static Future<Map<String, dynamic>> naverLogin(String accessToken) async {
    final response = await http.post(
      Uri.parse('${ApiConfig.baseUrl}/auth/naver/login'),
      headers: _getHeaders(includeAuth: false),
      body: json.encode({
        'accessToken': accessToken,
      }),
    );

    if (response.statusCode == 200) {
      return json.decode(utf8.decode(response.bodyBytes));
    } else {
      try {
        final errorBody = json.decode(utf8.decode(response.bodyBytes));
        final errorMessage = errorBody['message'] ?? '네이버 로그인에 실패했습니다';
        throw Exception(errorMessage);
      } catch (e) {
        if (e is Exception) {
          rethrow;
        }
        throw Exception('네이버 로그인에 실패했습니다. 네트워크를 확인해주세요.');
      }
    }
  }
}

