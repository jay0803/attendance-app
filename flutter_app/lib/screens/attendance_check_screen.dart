import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../providers/attendance_provider.dart';
import '../models/service.dart';
import '../services/location_service.dart';
import 'package:geolocator/geolocator.dart';

class AttendanceCheckScreen extends StatefulWidget {
  const AttendanceCheckScreen({super.key});

  @override
  State<AttendanceCheckScreen> createState() => _AttendanceCheckScreenState();
}

class _AttendanceCheckScreenState extends State<AttendanceCheckScreen> {
  bool _isChecking = false;
  Position? _currentPosition;

  Future<void> _checkAttendance(ServiceModel service) async {
    setState(() {
      _isChecking = true;
    });

    try {
      // 위치 가져오기
      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (context) => const Center(
          child: Card(
            child: Padding(
              padding: EdgeInsets.all(24.0),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  CircularProgressIndicator(),
                  SizedBox(height: 16),
                  Text('위치 정보를 가져오는 중...'),
                ],
              ),
            ),
          ),
        ),
      );

      final position = await LocationService.getCurrentLocation();
      setState(() {
        _currentPosition = position;
      });

      if (mounted) {
        Navigator.pop(context); // 로딩 다이얼로그 닫기
      }

      // 출석 체크
      final attendance = await context.read<AttendanceProvider>().checkAttendance(
            serviceId: service.id,
            latitude: position.latitude,
            longitude: position.longitude,
          );

      if (mounted) {
        showDialog(
          context: context,
          builder: (context) => AlertDialog(
            title: const Row(
              children: [
                Icon(Icons.check_circle, color: Colors.green),
                SizedBox(width: 8),
                Text('출석 체크 완료'),
              ],
            ),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('예배: ${attendance.serviceName}'),
                Text('상태: ${attendance.getStatusDisplayName()}'),
                Text('거리: ${attendance.distance.toStringAsFixed(0)}m'),
                Text(
                  '시간: ${DateFormat('yyyy-MM-dd HH:mm').format(attendance.checkedAt)}',
                ),
              ],
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('확인'),
              ),
            ],
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        // 로딩 다이얼로그가 열려있으면 닫기
        if (Navigator.canPop(context)) {
          Navigator.pop(context);
        }

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(e.toString().replaceAll('Exception: ', '')),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isChecking = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
      onRefresh: () async {
        await context.read<AttendanceProvider>().loadActiveServices();
      },
      child: Consumer<AttendanceProvider>(
        builder: (context, provider, _) {
          if (provider.isLoading) {
            return const Center(
              child: CircularProgressIndicator(),
            );
          }

          if (provider.services.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.event_busy,
                    size: 64,
                    color: Colors.grey[400],
                  ),
                  const SizedBox(height: 16),
                  Text(
                    '예정된 예배가 없습니다',
                    style: TextStyle(
                      fontSize: 18,
                      color: Colors.grey[600],
                    ),
                  ),
                ],
              ),
            );
          }

          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: provider.services.length,
            itemBuilder: (context, index) {
              final service = provider.services[index];
              final now = DateTime.now();
              final timeUntilService = service.serviceTime.difference(now);

              return Card(
                margin: const EdgeInsets.only(bottom: 16),
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Icon(
                            Icons.church,
                            color: Theme.of(context).primaryColor,
                          ),
                          const SizedBox(width: 8),
                          Expanded(
                            child: Text(
                              service.name,
                              style: const TextStyle(
                                fontSize: 20,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                          Chip(
                            label: Text(service.getTypeDisplayName()),
                            backgroundColor:
                                Theme.of(context).primaryColor.withOpacity(0.1),
                          ),
                        ],
                      ),
                      const SizedBox(height: 12),
                      Row(
                        children: [
                          const Icon(Icons.calendar_today, size: 16),
                          const SizedBox(width: 8),
                          Text(
                            DateFormat('yyyy년 MM월 dd일 EEEE', 'ko_KR')
                                .format(service.serviceTime),
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          const Icon(Icons.access_time, size: 16),
                          const SizedBox(width: 8),
                          Text(
                            DateFormat('HH:mm').format(service.serviceTime),
                            style: const TextStyle(fontSize: 16),
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      if (timeUntilService.isNegative)
                        const Row(
                          children: [
                            Icon(Icons.info, size: 16, color: Colors.orange),
                            SizedBox(width: 8),
                            Text(
                              '예배가 진행 중입니다',
                              style: TextStyle(color: Colors.orange),
                            ),
                          ],
                        )
                      else
                        Row(
                          children: [
                            const Icon(Icons.timer, size: 16),
                            const SizedBox(width: 8),
                            Text(
                              '${timeUntilService.inHours}시간 ${timeUntilService.inMinutes % 60}분 후',
                            ),
                          ],
                        ),
                      const SizedBox(height: 16),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton.icon(
                          onPressed: service.canCheckAttendance && !_isChecking
                              ? () => _checkAttendance(service)
                              : null,
                          icon: const Icon(Icons.check_circle),
                          label: Text(
                            service.canCheckAttendance
                                ? '출석 체크'
                                : '아직 출석 시간이 아닙니다',
                          ),
                          style: ElevatedButton.styleFrom(
                            padding: const EdgeInsets.symmetric(vertical: 12),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

