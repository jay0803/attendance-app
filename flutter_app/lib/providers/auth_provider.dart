import 'package:flutter/material.dart';
import '../models/user.dart';
import '../services/api_service.dart';
import '../utils/storage_helper.dart';

class AuthProvider with ChangeNotifier {
  User? _user;
  bool _isAuthenticated = false;
  bool _isLoading = false;

  User? get user => _user;
  bool get isAuthenticated => _isAuthenticated;
  bool get isLoading => _isLoading;

  // 자동 로그인 체크
  Future<void> checkAutoLogin() async {
    _isLoading = true;
    notifyListeners();

    final token = StorageHelper.getToken();
    final userInfo = StorageHelper.getUserInfo();

    if (token != null && userInfo != null) {
      _user = User.fromJson(userInfo);
      _isAuthenticated = true;
    }

    _isLoading = false;
    notifyListeners();
  }

  // 로그인
  Future<void> login(String username, String password) async {
    try {
      final response = await ApiService.login(username, password);

      // 토큰 저장
      await StorageHelper.saveToken(response['token']);

      // 사용자 정보 저장
      await StorageHelper.saveUserInfo(response);

      _user = User.fromJson(response);
      _isAuthenticated = true;
      notifyListeners();
    } catch (e) {
      rethrow;
    }
  }

  // 회원가입
  Future<void> register({
    required String username,
    required String password,
    required String name,
    String? phone,
    String? email,
  }) async {
    try {
      final response = await ApiService.register(
        username: username,
        password: password,
        name: name,
        phone: phone,
        email: email,
      );

      // 토큰 저장
      await StorageHelper.saveToken(response['token']);

      // 사용자 정보 저장
      await StorageHelper.saveUserInfo(response);

      _user = User.fromJson(response);
      _isAuthenticated = true;
      notifyListeners();
    } catch (e) {
      rethrow;
    }
  }

  // 로그아웃
  Future<void> logout() async {
    await StorageHelper.clearAll();
    _user = null;
    _isAuthenticated = false;
    notifyListeners();
  }
}

