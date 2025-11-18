class ServiceModel {
  final int id;
  final String name;
  final DateTime serviceTime;
  final String type;
  final bool active;
  final bool canCheckAttendance;

  ServiceModel({
    required this.id,
    required this.name,
    required this.serviceTime,
    required this.type,
    required this.active,
    required this.canCheckAttendance,
  });

  factory ServiceModel.fromJson(Map<String, dynamic> json) {
    return ServiceModel(
      id: json['id'],
      name: json['name'],
      serviceTime: DateTime.parse(json['serviceTime']),
      type: json['type'],
      active: json['active'],
      canCheckAttendance: json['canCheckAttendance'] ?? false,
    );
  }

  String getTypeDisplayName() {
    switch (type) {
      case 'SUNDAY':
        return '주일 예배';
      case 'WEDNESDAY':
        return '수요 예배';
      case 'FRIDAY':
        return '금요 예배';
      case 'SPECIAL':
        return '특별 예배';
      default:
        return type;
    }
  }
}

