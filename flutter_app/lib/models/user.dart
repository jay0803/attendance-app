class User {
  final int id;
  final String username;
  final String name;
  final String role;
  final String? phone;
  final String? email;

  User({
    required this.id,
    required this.username,
    required this.name,
    required this.role,
    this.phone,
    this.email,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['userId'],
      username: json['username'],
      name: json['name'],
      role: json['role'],
      phone: json['phone'],
      email: json['email'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': id,
      'username': username,
      'name': name,
      'role': role,
      'phone': phone,
      'email': email,
    };
  }
}

