import React from 'react';
import { Plus } from 'lucide-react';

const OutlinePage = ({ generatedCards }) => {
  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="bg-blue-50 rounded-lg p-6">
        <h1 className="text-xl font-semibold mb-6 text-gray-800">Outline</h1>
        
        <div className="space-y-6">
          {generatedCards.map((card, index) => (
            <div key={card.id} className="bg-white rounded-lg p-6 shadow-sm">
              <div className="flex items-start space-x-4">
                <div className="w-8 h-8 bg-blue-500 text-white rounded-full flex items-center justify-center font-semibold">
                  {index + 1}
                </div>
                <div className="flex-1">
                  <h3 className="text-lg font-semibold text-gray-800 mb-3">{card.title}</h3>
                  <ul className="space-y-2">
                    {card.content.map((item, itemIndex) => (
                      <li key={itemIndex} className="flex items-start space-x-2">
                        <span className="w-2 h-2 bg-gray-400 rounded-full mt-2 flex-shrink-0"></span>
                        <span className="text-gray-700">{item}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
            </div>
          ))}
          
          <div className="bg-white rounded-lg p-6 shadow-sm border-2 border-dashed border-gray-300">
            <button className="w-full flex items-center justify-center space-x-2 text-gray-500 hover:text-gray-700 transition-colors">
              <Plus className="w-5 h-5" />
              <span>Add card</span>
            </button>
          </div>
        </div>
        
        <div className="flex justify-between items-center mt-6 text-sm text-gray-500">
          <span>{generatedCards.length} cards total</span>
          <div className="flex items-center space-x-4">
            <span>Type --- for card breaks</span>
            <span>542/20000</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OutlinePage;