import React from 'react';
import { useJobData } from './hooks/useJobData';
import Header from './components/Header';
import AddJobForm from './components/AddJobForm';
import KanbanBoard from './components/KanbanBoard';
import AnalyticsChart from './components/AnalyticsChart'; // Verify import is present

function App() {
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
        
        {/* SECTION 2: Interactive Columns Container */}
        {/* FIX: Ensure this section tag opens cleanly here */}
        <section className="lg:col-span-3 flex flex-col">
          
          {/* Mount our live telemetry data graph stream */}
          <AnalyticsChart />
          
          {/* Mount our visual Kanban tracking columns */}
          <KanbanBoard 
            jobs={jobs} 
            onMove={handleMoveCard} 
            onDelete={handleDeleteCard} 
          />
          
        </section> {/* FIX: Ensure this structural closing tag corresponds perfectly! */}
      </main>
    </div>
  );
}

export default App;
