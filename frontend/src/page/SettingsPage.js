import React from 'react';
import { Plus, Minus, Star } from 'lucide-react';

const SettingsPage = ({ 
  presentationTitle, 
  setPresentationTitle, 
  selectedTheme, 
  setSelectedTheme, 
  slideCount, 
  setSlideCount, 
  generatePresentation 
}) => {
  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-2xl font-bold mb-8 text-gray-800">프레젠테이션 기본 설정</h1>
      
      <div className="space-y-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            프레젠테이션 제목 <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={presentationTitle}
            onChange={(e) => setPresentationTitle(e.target.value)}
            placeholder="제목을 입력하세요"
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">목적 선택</label>
          <select
            value={selectedTheme}
            onChange={(e) => setSelectedTheme(e.target.value)}
            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">주제를 선택하세요</option>
            <option value="business">비즈니스 프레젠테이션</option>
            <option value="education">교육용 프레젠테이션</option>
            <option value="marketing">마케팅 프레젠테이션</option>
            <option value="report">보고서 프레젠테이션</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">슬라이드 수</label>
          <div className="flex items-center space-x-4">
            <button
              onClick={() => setSlideCount(Math.max(1, slideCount - 1))}
              className="w-12 h-12 bg-gray-200 hover:bg-gray-300 rounded-lg flex items-center justify-center transition-colors"
            >
              <Minus className="w-5 h-5" />
            </button>
            
            <div className="flex-1 text-center">
              <input
                type="number"
                value={slideCount}
                onChange={(e) => setSlideCount(Math.max(1, Math.min(50, parseInt(e.target.value) || 1)))}
                className="w-20 text-center text-xl font-semibold border border-gray-300 rounded-lg py-2"
                min="1"
                max="50"
              />
            </div>
            
            <button
              onClick={() => setSlideCount(Math.min(50, slideCount + 1))}
              className="w-12 h-12 bg-gray-200 hover:bg-gray-300 rounded-lg flex items-center justify-center transition-colors"
            >
              <Plus className="w-5 h-5" />
            </button>
          </div>
          <p className="text-sm text-gray-500 mt-2">1-50 슬라이드 사이로 설정하세요</p>
        </div>
      </div>

      <div className="flex justify-center mt-8">
        <button
          onClick={generatePresentation}
          disabled={!presentationTitle.trim() || !selectedTheme}
          className={`px-8 py-3 rounded-lg font-semibold flex items-center space-x-2 ${
            presentationTitle.trim() && selectedTheme
              ? 'bg-blue-500 hover:bg-blue-600 text-white'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          } transition-colors`}
        >
          <Star className="w-5 h-5" />
          <span>프레젠테이션 생성하기</span>
        </button>
      </div>
    </div>
  );
};

export default SettingsPage;