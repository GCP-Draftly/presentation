import React, { useState } from 'react';
import './styles/App.css';
import './styles/index.css';
import UploadPage from './page/UploadPage';
import SettingsPage from './page/SettingsPage';
import OutlinePage from './page/OutlinePage';
import LoginPage from './page/LoginPage';
import SignupPage from './page/SignupPage';
import ProfileEditPage from './page/ProfileEditPage';
import UserProfile from './components/UserProfile';
import logo from './img/DraftlyLogo.png';

function App() {
  // 인증 상태 관리
  const [user, setUser] = useState(null);
  const [authPage, setAuthPage] = useState('login'); // 'login' 또는 'signup'
  
  // 페이지 상태 관리
  const [currentPage, setCurrentPage] = useState(1);
  const [showProfileEdit, setShowProfileEdit] = useState(false);
  
  // 업로드된 파일 상태
  const [uploadedFiles, setUploadedFiles] = useState([]);
  
  // 설정 상태
  const [presentationTitle, setPresentationTitle] = useState('');
  const [selectedTheme, setSelectedTheme] = useState('');
  const [slideCount, setSlideCount] = useState(5);
  
  // 생성된 카드 상태
  const [generatedCards, setGeneratedCards] = useState([]);
  const [isGenerating, setIsGenerating] = useState(false);

  // 인증 관련 함수들
  const handleLogin = (userData) => {
    setUser(userData);
    setCurrentPage(1); // 로그인 후 업로드 페이지로 이동
  };

  const handleSignup = (userData) => {
    setUser(userData);
    setCurrentPage(1); // 회원가입 후 업로드 페이지로 이동
  };

  const handleLogout = () => {
    setUser(null);
    setAuthPage('login');
    setCurrentPage(1);
    setShowProfileEdit(false);
    setUploadedFiles([]);
    setPresentationTitle('');
    setSelectedTheme('');
    setSlideCount(5);
    setGeneratedCards([]);
  };

  const handleUpdateProfile = (updatedUser) => {
    setUser(updatedUser);
    setShowProfileEdit(false);
  };

  const handleEditProfile = () => {
    setShowProfileEdit(true);
  };

  const handleBackFromProfileEdit = () => {
    setShowProfileEdit(false);
  };

  const switchToSignup = () => {
    setAuthPage('signup');
  };

  const switchToLogin = () => {
    setAuthPage('login');
  };

  // 프레젠테이션 생성 함수
  const generatePresentation = async () => {
    if (!presentationTitle.trim() || !selectedTheme || uploadedFiles.length === 0) {
      alert('모든 필수 항목을 입력하고 파일을 업로드해주세요.');
      return;
    }

    setIsGenerating(true);
    
    try {
      // Google Gemini API 호출을 시뮬레이션
      // 실제 구현에서는 여기에 Gemini API 호출 코드가 들어갑니다
      await new Promise(resolve => setTimeout(resolve, 2000)); // 로딩 시뮬레이션
      
      // 임시 데이터 생성 (실제로는 Gemini API 응답을 사용)
      const mockCards = [];
      for (let i = 0; i < slideCount; i++) {
        mockCards.push({
          id: i + 1,
          title: `슬라이드 ${i + 1}`,
          content: [
            `업로드된 파일들을 분석한 내용 ${i + 1}-1`,
            `주제: ${selectedTheme}에 맞는 내용 ${i + 1}-2`,
            `프레젠테이션 목적에 맞는 핵심 포인트 ${i + 1}-3`
          ]
        });
      }
      
      setGeneratedCards(mockCards);
      setCurrentPage(3); // OutlinePage로 이동
    } catch (error) {
      console.error('프레젠테이션 생성 중 오류:', error);
      alert('프레젠테이션 생성 중 오류가 발생했습니다.');
    } finally {
      setIsGenerating(false);
    }
  };

  // 인증 페이지 렌더링
  const renderAuthPage = () => {
    if (authPage === 'login') {
      return (
        <LoginPage 
          onLogin={handleLogin}
          onSwitchToSignup={switchToSignup}
        />
      );
    } else {
      return (
        <SignupPage 
          onSignup={handleSignup}
          onSwitchToLogin={switchToLogin}
        />
      );
    }
  };

  // 페이지 렌더링
  const renderPage = () => {
    switch (currentPage) {
      case 1:
        return (
          <UploadPage 
            uploadedFiles={uploadedFiles}
            setUploadedFiles={setUploadedFiles}
            setCurrentPage={setCurrentPage}
          />
        );
      case 2:
        return (
          <SettingsPage
            presentationTitle={presentationTitle}
            setPresentationTitle={setPresentationTitle}
            selectedTheme={selectedTheme}
            setSelectedTheme={setSelectedTheme}
            slideCount={slideCount}
            setSlideCount={setSlideCount}
            generatePresentation={generatePresentation}
            isGenerating={isGenerating}
          />
        );
      case 3:
        return (
          <OutlinePage 
            generatedCards={generatedCards}
          />
        );
      default:
        return (
          <UploadPage 
            uploadedFiles={uploadedFiles}
            setUploadedFiles={setUploadedFiles}
            setCurrentPage={setCurrentPage}
          />
        );
    }
  };

  // 로그인하지 않은 경우 인증 페이지 표시
  if (!user) {
    return (
      <div className="App min-h-screen bg-gray-50">
        <header className="w-full flex items-center px-6 py-4 bg-white shadow-sm mb-6">
          <img 
            src={logo} 
            alt="Draftly Logo" 
            className="h-10 mr-3 cursor-pointer hover:opacity-80 transition-opacity" 
            onClick={() => setAuthPage('login')}
          />
          <span className="text-2xl font-bold text-gray-800">Draftly</span>
        </header>
        {renderAuthPage()}
      </div>
    );
  }

  // 프로필 수정 페이지 표시
  if (showProfileEdit) {
    return (
      <div className="App min-h-screen bg-gray-50">
        <header className="w-full flex items-center justify-between px-6 py-4 bg-white shadow-sm mb-6">
          <div className="flex items-center">
            <img 
              src={logo} 
              alt="Draftly Logo" 
              className="h-10 mr-3 cursor-pointer hover:opacity-80 transition-opacity" 
              onClick={() => setCurrentPage(1)}
            />
            <span className="text-2xl font-bold text-gray-800">Draftly</span>
          </div>
          <UserProfile user={user} onLogout={handleLogout} onEditProfile={handleEditProfile} />
        </header>
        <ProfileEditPage 
          user={user} 
          onUpdateProfile={handleUpdateProfile} 
          onBack={handleBackFromProfileEdit} 
        />
      </div>
    );
  }

  // 로그인한 경우 메인 앱 표시
  return (
    <div className="App min-h-screen bg-gray-50">
      <header className="w-full flex items-center justify-between px-6 py-4 bg-white shadow-sm mb-6">
        <div className="flex items-center">
          <img 
            src={logo} 
            alt="Draftly Logo" 
            className="h-10 mr-3 cursor-pointer hover:opacity-80 transition-opacity" 
            onClick={() => setCurrentPage(1)}
          />
          <span className="text-2xl font-bold text-gray-800"></span>
        </div>
        <UserProfile user={user} onLogout={handleLogout} onEditProfile={handleEditProfile} />
      </header>
      {renderPage()}
    </div>
  );
}

export default App;
