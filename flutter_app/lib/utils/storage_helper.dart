import 'package:shared_preferences/shared_preferences.dart';

class StorageHelper {
  static late SharedPreferences _prefs;

  static Future<void> init() async {
    _prefs = await SharedPreferences.getInstance();
  }

  // Token
  static Future<void> saveToken(String token) async {
    await _prefs.setString('token', token);
  }

  static String? getToken() {
    return _prefs.getString('token');
  }

  static Future<void> removeToken() async {
    await _prefs.remove('token');
  }

  // User Info
  static Future<void> saveUserInfo(Map<String, dynamic> userInfo) async {
    await _prefs.setInt('userId', userInfo['userId']);
    await _prefs.setString('username', userInfo['username']);
    await _prefs.setString('name', userInfo['name']);
    await _prefs.setString('role', userInfo['role']);
  }

  static Map<String, dynamic>? getUserInfo() {
    final userId = _prefs.getInt('userId');
    final username = _prefs.getString('username');
    final name = _prefs.getString('name');
    final role = _prefs.getString('role');

    if (userId == null || username == null || name == null || role == null) {
      return null;
    }

    return {
      'userId': userId,
      'username': username,
      'name': name,
      'role': role,
    };
  }

  static Future<void> clearAll() async {
    await _prefs.clear();
  }
}

