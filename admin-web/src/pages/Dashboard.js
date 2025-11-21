import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { attendanceAPI, serviceAPI } from '../services/api';
import { logout as authLogout, getUserInfo } from '../utils/auth';

function Dashboard({ setIsAuthenticated }) {
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    total: 0,
    present: 0,
    late: 0,
    absent: 0,
  });
  const [services, setServices] = useState([]);
  const [recentAttendances, setRecentAttendances] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const userInfo = getUserInfo();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setIsLoading(true);
    try {
      // 출석 데이터 로드
      const attendanceRes = await attendanceAPI.getAll();
      const attendances = attendanceRes.data;

      // 통계 계산
      setStats({
        total: attendances.length,
        present: attendances.filter(a => a.status === 'PRESENT').length,
        late: attendances.filter(a => a.status === 'LATE').length,
        absent: attendances.filter(a => a.status === 'ABSENT').length,
      });

      // 최근 출석 기록 (최신 10개)
      const sorted = [...attendances].sort(
        (a, b) => new Date(b.checkedAt) - new Date(a.checkedAt)
      );
      setRecentAttendances(sorted.slice(0, 10));

      // 예배 목록 로드
      const servicesRes = await serviceAPI.getAll();
      setServices(servicesRes.data);
    } catch (error) {
      console.error('데이터 로드 실패:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = () => {
    authLogout();
    setIsAuthenticated(false);
    navigate('/login');
  };

  const getStatusBadge = (status) => {
    const statusMap = {
      PRESENT: { label: '출석', className: 'present' },
      LATE: { label: '지각', className: 'late' },
      ABSENT: { label: '결석', className: 'absent' },
    };
    const { label, className } = statusMap[status] || { label: status, className: '' };
    return <span className={`badge ${className}`}>{label}</span>;
  };

  const formatDateTime = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  if (isLoading) {
    return (
      <div className="layout">
        <div className="sidebar">
          <h2>관리자</h2>
        </div>
        <div className="main-content">
          <div style={{ textAlign: 'center', padding: '48px' }}>
            <p>데이터를 불러오는 중...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="layout">
      <div className="sidebar">
        <h2>출석 관리</h2>
        <nav>
          <Link to="/dashboard" className="active">대시보드</Link>
          <Link to="/attendance">출석 기록</Link>
          <Link to="/pending-users">사전 등록 관리</Link>
        </nav>
      </div>

      <div className="main-content">
        <div className="header">
          <h1>대시보드</h1>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <span>{userInfo?.name || '관리자'}</span>
            <button className="logout-btn" onClick={handleLogout}>
              로그아웃
            </button>
          </div>
        </div>

        {/* 통계 카드 */}
        <div className="stats-grid">
          <div className="stat-card">
            <h3>전체 출석</h3>
            <div className="value">{stats.total}</div>
          </div>
          <div className="stat-card">
            <h3>정상 출석</h3>
            <div className="value" style={{ color: '#2e7d32' }}>{stats.present}</div>
          </div>
          <div className="stat-card">
            <h3>지각</h3>
            <div className="value" style={{ color: '#f57c00' }}>{stats.late}</div>
          </div>
          <div className="stat-card">
            <h3>결석</h3>
            <div className="value" style={{ color: '#c62828' }}>{stats.absent}</div>
          </div>
        </div>

        {/* 예배 목록 */}
        <div className="card">
          <h2 style={{ marginBottom: '16px' }}>예배 목록</h2>
          {services.length === 0 ? (
            <p style={{ color: '#666' }}>등록된 예배가 없습니다</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>예배명</th>
                  <th>예배 시간</th>
                  <th>유형</th>
                  <th>상태</th>
                </tr>
              </thead>
              <tbody>
                {services.map(service => (
                  <tr key={service.id}>
                    <td>{service.name}</td>
                    <td>{formatDateTime(service.serviceTime)}</td>
                    <td>{service.type}</td>
                    <td>
                      <span className={`badge ${service.active ? 'present' : 'absent'}`}>
                        {service.active ? '활성' : '비활성'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* 최근 출석 기록 */}
        <div className="card">
          <h2 style={{ marginBottom: '16px' }}>최근 출석 기록</h2>
          {recentAttendances.length === 0 ? (
            <p style={{ color: '#666' }}>출석 기록이 없습니다</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>이름</th>
                  <th>예배</th>
                  <th>상태</th>
                  <th>거리</th>
                  <th>출석 시간</th>
                </tr>
              </thead>
              <tbody>
                {recentAttendances.map(attendance => (
                  <tr key={attendance.id}>
                    <td>{attendance.userName}</td>
                    <td>{attendance.serviceName}</td>
                    <td>{getStatusBadge(attendance.status)}</td>
                    <td>{Math.round(attendance.distance)}m</td>
                    <td>{formatDateTime(attendance.checkedAt)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}

export default Dashboard;

