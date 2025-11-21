import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { pendingUserAPI } from '../services/api';
import { logout as authLogout, getUserInfo } from '../utils/auth';

function PendingUsers({ setIsAuthenticated }) {
  const navigate = useNavigate();
  const [pendingUsers, setPendingUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showAddForm, setShowAddForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    email: '',
    notes: '',
  });
  const [error, setError] = useState('');
  const userInfo = getUserInfo();

  useEffect(() => {
    loadPendingUsers();
  }, []);

  const loadPendingUsers = async () => {
    setIsLoading(true);
    try {
      const response = await pendingUserAPI.getAll();
      setPendingUsers(response.data);
    } catch (error) {
      console.error('사전 등록 목록 로드 실패:', error);
      setError('사전 등록 목록을 불러오는데 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!formData.name.trim()) {
      setError('이름은 필수입니다.');
      return;
    }

    if (!formData.phone?.trim() && !formData.email?.trim()) {
      setError('전화번호 또는 이메일 중 하나는 필수입니다.');
      return;
    }

    try {
      await pendingUserAPI.create(formData);
      setShowAddForm(false);
      setFormData({ name: '', phone: '', email: '', notes: '' });
      loadPendingUsers();
    } catch (error) {
      console.error('사전 등록 추가 실패:', error);
      setError(error.response?.data?.message || '사전 등록 추가에 실패했습니다.');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('정말 삭제하시겠습니까?')) {
      return;
    }

    try {
      await pendingUserAPI.delete(id);
      loadPendingUsers();
    } catch (error) {
      console.error('사전 등록 삭제 실패:', error);
      setError('사전 등록 삭제에 실패했습니다.');
    }
  };

  const handleLogout = () => {
    authLogout();
    setIsAuthenticated(false);
    navigate('/login');
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
          <h2>출석 관리</h2>
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
          <Link to="/attendance">출석 기록</Link>
          <Link to="/pending-users" className="active">사전 등록 관리</Link>
        </nav>
      </div>

      <div className="main-content">
        <div className="header">
          <h1>사전 등록 관리</h1>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <span>{userInfo?.name || '관리자'}</span>
            <button className="logout-btn" onClick={handleLogout}>
              로그아웃
            </button>
          </div>
        </div>

        {error && (
          <div style={{ 
            padding: '12px', 
            marginBottom: '16px', 
            backgroundColor: '#ffebee', 
            color: '#c62828',
            borderRadius: '4px'
          }}>
            {error}
          </div>
        )}

        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h2 style={{ margin: 0 }}>사전 등록 목록</h2>
            <button 
              className="btn-primary" 
              onClick={() => setShowAddForm(!showAddForm)}
            >
              {showAddForm ? '취소' : '+ 사전 등록 추가'}
            </button>
          </div>

          {showAddForm && (
            <form onSubmit={handleSubmit} style={{ 
              padding: '16px', 
              backgroundColor: '#f5f5f5', 
              borderRadius: '4px',
              marginBottom: '16px'
            }}>
              <div style={{ marginBottom: '12px' }}>
                <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold' }}>
                  이름 <span style={{ color: 'red' }}>*</span>
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                  required
                />
              </div>
              <div style={{ marginBottom: '12px' }}>
                <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold' }}>
                  전화번호
                </label>
                <input
                  type="tel"
                  value={formData.phone}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                  placeholder="010-1234-5678"
                />
              </div>
              <div style={{ marginBottom: '12px' }}>
                <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold' }}>
                  이메일
                </label>
                <input
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd' }}
                  placeholder="example@email.com"
                />
              </div>
              <div style={{ marginBottom: '12px' }}>
                <label style={{ display: 'block', marginBottom: '4px', fontWeight: 'bold' }}>
                  비고
                </label>
                <textarea
                  value={formData.notes}
                  onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                  style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ddd', minHeight: '60px' }}
                  placeholder="추가 정보 (선택사항)"
                />
              </div>
              <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                등록
              </button>
            </form>
          )}

          {pendingUsers.length === 0 ? (
            <p style={{ color: '#666', textAlign: 'center', padding: '32px' }}>
              등록된 사전 등록 사용자가 없습니다
            </p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>이름</th>
                  <th>전화번호</th>
                  <th>이메일</th>
                  <th>비고</th>
                  <th>등록일</th>
                  <th>작업</th>
                </tr>
              </thead>
              <tbody>
                {pendingUsers.map((user) => (
                  <tr key={user.id}>
                    <td>{user.name}</td>
                    <td>{user.phone || '-'}</td>
                    <td>{user.email || '-'}</td>
                    <td>{user.notes || '-'}</td>
                    <td>{formatDateTime(user.createdAt)}</td>
                    <td>
                      <button
                        className="btn-danger"
                        onClick={() => handleDelete(user.id)}
                        style={{ padding: '4px 12px', fontSize: '14px' }}
                      >
                        삭제
                      </button>
                    </td>
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

export default PendingUsers;

