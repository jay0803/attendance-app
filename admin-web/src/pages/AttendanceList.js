import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { attendanceAPI, serviceAPI } from '../services/api';
import { logout as authLogout, getUserInfo } from '../utils/auth';

function AttendanceList({ setIsAuthenticated }) {
  const navigate = useNavigate();
  const [attendances, setAttendances] = useState([]);
  const [services, setServices] = useState([]);
  const [selectedService, setSelectedService] = useState('all');
  const [searchName, setSearchName] = useState('');
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
      setAttendances(attendanceRes.data);

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

  // 필터링된 출석 기록
  const filteredAttendances = attendances.filter(attendance => {
    const matchesService = selectedService === 'all' || 
      attendance.serviceId.toString() === selectedService;
    const matchesName = searchName === '' || 
      attendance.userName.toLowerCase().includes(searchName.toLowerCase());
    return matchesService && matchesName;
  });

  // 통계 계산
  const stats = {
    total: filteredAttendances.length,
    present: filteredAttendances.filter(a => a.status === 'PRESENT').length,
    late: filteredAttendances.filter(a => a.status === 'LATE').length,
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
          <Link to="/dashboard">대시보드</Link>
          <Link to="/attendance" className="active">출석 기록</Link>
          <Link to="/pending-users">사전 등록 관리</Link>
        </nav>
      </div>

      <div className="main-content">
        <div className="header">
          <h1>출석 기록</h1>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <span>{userInfo?.name || '관리자'}</span>
            <button className="logout-btn" onClick={handleLogout}>
              로그아웃
            </button>
          </div>
        </div>

        {/* 통계 */}
        <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(3, 1fr)' }}>
          <div className="stat-card">
            <h3>전체</h3>
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
        </div>

        {/* 필터 */}
        <div className="filters">
          <div>
            <label>예배:</label>
            <select 
              value={selectedService} 
              onChange={(e) => setSelectedService(e.target.value)}
            >
              <option value="all">전체</option>
              {services.map(service => (
                <option key={service.id} value={service.id}>
                  {service.name}
                </option>
              ))}
            </select>

            <label style={{ marginLeft: '24px' }}>이름:</label>
            <input
              type="text"
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
              placeholder="이름으로 검색..."
            />

            <button 
              onClick={() => {
                setSelectedService('all');
                setSearchName('');
              }}
              style={{ 
                marginLeft: '16px', 
                backgroundColor: '#f5f5f5', 
                color: '#333' 
              }}
            >
              초기화
            </button>
          </div>
        </div>

        {/* 출석 기록 테이블 */}
        <div className="card">
          {filteredAttendances.length === 0 ? (
            <div className="empty-state">
              <svg 
                fill="none" 
                viewBox="0 0 24 24" 
                stroke="currentColor"
              >
                <path 
                  strokeLinecap="round" 
                  strokeLinejoin="round" 
                  strokeWidth={2} 
                  d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" 
                />
              </svg>
              <p>출석 기록이 없습니다</p>
            </div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>이름</th>
                  <th>예배</th>
                  <th>상태</th>
                  <th>거리</th>
                  <th>위치</th>
                  <th>출석 시간</th>
                </tr>
              </thead>
              <tbody>
                {filteredAttendances.map(attendance => (
                  <tr key={attendance.id}>
                    <td>{attendance.id}</td>
                    <td>{attendance.userName}</td>
                    <td>{attendance.serviceName}</td>
                    <td>{getStatusBadge(attendance.status)}</td>
                    <td>{Math.round(attendance.distance)}m</td>
                    <td>
                      <small>
                        {attendance.latitude.toFixed(4)}, {attendance.longitude.toFixed(4)}
                      </small>
                    </td>
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

export default AttendanceList;

