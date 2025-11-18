import 'package:flutter/material.dart';
import '../models/attendance.dart';
import '../models/service.dart';
import '../services/api_service.dart';

class AttendanceProvider with ChangeNotifier {
  List<ServiceModel> _services = [];
  List<Attendance> _myAttendances = [];
  bool _isLoading = false;

  List<ServiceModel> get services => _services;
  List<Attendance> get myAttendances => _myAttendances;
  bool get isLoading => _isLoading;

  // 활성화된 예배 목록 조회
  Future<void> loadActiveServices() async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await ApiService.getActiveServices();
      _services = response.map((json) => ServiceModel.fromJson(json)).toList();
    } catch (e) {
      rethrow;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // 다음 예배 조회
  Future<ServiceModel> getNextService() async {
    try {
      final response = await ApiService.getNextService();
      return ServiceModel.fromJson(response);
    } catch (e) {
      rethrow;
    }
  }

  // 출석 체크
  Future<Attendance> checkAttendance({
    required int serviceId,
    required double latitude,
    required double longitude,
  }) async {
    try {
      final response = await ApiService.checkAttendance(
        serviceId: serviceId,
        latitude: latitude,
        longitude: longitude,
      );
      
      // 내 출석 기록 새로고침
      await loadMyAttendances();
      
      return Attendance.fromJson(response);
    } catch (e) {
      rethrow;
    }
  }

  // 내 출석 기록 조회
  Future<void> loadMyAttendances() async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await ApiService.getMyAttendances();
      _myAttendances = response.map((json) => Attendance.fromJson(json)).toList();
    } catch (e) {
      rethrow;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}

