import React from 'react';
import { useJobData } from './hooks/useJobData';
import Header from './components/Header';
import AddJobForm from './components/AddJobForm';
import KanbanBoard from './components/KanbanBoard';

function App() {
  // Pull all state memory and operations directly from our backend data hook!
  const {
    jobs,
    formData,
    setFormData,
    formatSalaryString,
    handleCreateJob,
    handleMoveCard,
    handleDeleteCard
  } = useJobData();

  return (
    <div className="min-h-screen bg-gray-900 text-gray-100 p-8 font-sans text-lg max-w-[1600px] mx-auto">
      <Header />
      
      <main className="max-w-full mx-auto grid grid-cols-1 lg:grid-cols-4 gap-8">
        <AddJobForm 
          formData={formData} 
          setFormData={setFormData} 
          formatSalaryString={formatSalaryString} 
          onSubmit={handleCreateJob} 
        />
        
        <KanbanBoard 
          jobs={jobs} 
          onMove={handleMoveCard} 
          onDelete={handleDeleteCard} 
        />
      </main>
    </div>
  );
}

export default App;