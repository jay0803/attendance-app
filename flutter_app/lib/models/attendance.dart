class Attendance {
  final int id;
  final int userId;
  final String userName;
  final int serviceId;
  final String serviceName;
  final String status;
  final double latitude;
  final double longitude;
  final double distance;
  final DateTime checkedAt;
  final String? notes;

  Attendance({
    required this.id,
    required this.userId,
    required this.userName,
    required this.serviceId,
    required this.serviceName,
    required this.status,
    required this.latitude,
    required this.longitude,
    required this.distance,
    required this.checkedAt,
    this.notes,
  });

  factory Attendance.fromJson(Map<String, dynamic> json) {
    return Attendance(
      id: json['id'],
      userId: json['userId'],
      userName: json['userName'],
      serviceId: json['serviceId'],
      serviceName: json['serviceName'],
      status: json['status'],
      latitude: json['latitude'].toDouble(),
      longitude: json['longitude'].toDouble(),
      distance: json['distance'].toDouble(),
      checkedAt: DateTime.parse(json['checkedAt']),
      notes: json['notes'],
    );
  }

  String getStatusDisplayName() {
    switch (status) {
      case 'PRESENT':
        return '출석';
      case 'LATE':
        return '지각';
      case 'ABSENT':
        return '결석';
      default:
        return status;
    }
  }
}

