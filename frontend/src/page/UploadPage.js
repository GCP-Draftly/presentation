import React from 'react';
import { Upload } from 'lucide-react';

const UploadPage = ({ uploadedFiles, setUploadedFiles, setCurrentPage }) => {
  // 파일 업로드 핸들러
  const handleFileUpload = (event) => {
    const files = Array.from(event.target.files);
    setUploadedFiles(prev => [...prev, ...files]);
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-2xl font-bold mb-8 text-gray-800">콘텐츠 업로드</h1>
      
      <div className="border-2 border-dashed border-gray-300 rounded-lg p-12 text-center mb-6">
        <div className="flex items-center justify-center mb-6">
          <div className="flex space-x-4">
            <div className="bg-blue-100 p-4 rounded-lg">
              <span className="text-blue-600 font-semibold">DOC</span>
            </div>
            <div className="bg-green-100 p-4 rounded-lg">
              <span className="text-green-600 font-semibold">PDF</span>
            </div>
            <div className="bg-purple-100 p-4 rounded-lg">
              <span className="text-purple-600 font-semibold">HWP</span>
            </div>
            <div className="bg-orange-100 p-4 rounded-lg">
              <span className="text-orange-600 font-semibold">TEXT</span>
            </div>
          </div>
        </div>
        
        <div className="mb-6">
          <Upload className="w-12 h-12 mx-auto mb-4 text-gray-400" />
          <h2 className="text-xl font-semibold mb-2 text-gray-700">파일과 폴더를 여기로 드래그하여 업로드하세요</h2>
          <p className="text-gray-500 mb-4">PDF, DOC, DOCTHWP, TEXT 파일 형식의 문서 파일을 업로드할 수 있습니다</p>
          
          <input
            type="file"
            multiple
            onChange={handleFileUpload}
            className="hidden"
            id="fileUpload"
            accept=".pdf, .doc,.doct,.hwp,.txt"
          />
          <label
            htmlFor="fileUpload"
            className="bg-blue-500 hover:bg-blue-600 text-white px-6 py-2 rounded-lg cursor-pointer inline-block transition-colors"
          >
            파일 찾기 클릭 선택
          </label>
        </div>
      </div>

      {uploadedFiles.length > 0 && (
        <div className="mb-6">
          <h3 className="text-lg font-semibold mb-4">업로드된 파일:</h3>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {uploadedFiles.map((file, index) => (
              <div key={index} className="bg-gray-100 p-4 rounded-lg">
                <p className="text-sm font-medium truncate">{file.name}</p>
                <p className="text-xs text-gray-500">{(file.size / 1024 / 1024).toFixed(2)} MB</p>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="flex justify-center">
        <button
          onClick={() => setCurrentPage(2)}
          disabled={uploadedFiles.length === 0}
          className={`px-8 py-3 rounded-lg font-semibold ${
            uploadedFiles.length > 0
              ? 'bg-blue-500 hover:bg-blue-600 text-white'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          } transition-colors`}
        >
          다음 단계로
        </button>
      </div>
    </div>
  );
};

export default UploadPage;