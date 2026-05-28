import React, { useState, useMemo } from 'react';
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
    handleSalaryBlur,
    handleCreateJob,
    handleMoveCard,
    handleDeleteCard
  } = useJobData();

  // Search state for filtering jobs shown in the Kanban board
  const [searchTerm, setSearchTerm] = useState('');

  const filteredJobs = useMemo(() => {
    const term = (searchTerm || '').trim().toLowerCase();
    if (!term) return jobs || [];
    return (jobs || []).filter((j) => {
      const company = (j.companyName || '').toLowerCase();
      const role = (j.roleTitle || '').toLowerCase();
      const url = (j.jobUrl || '').toLowerCase();
      const salary = (j.salaryRange || '').toLowerCase();
      const state = (j.state || '').toLowerCase();
      return (
        company.includes(term) ||
        role.includes(term) ||
        url.includes(term) ||
        salary.includes(term) ||
        state.includes(term)
      );
    });
  }, [jobs, searchTerm]);

  return (
    <div className="min-h-screen bg-gray-900 text-gray-100 p-8 font-sans text-lg max-w-[1600px] mx-auto">
      <Header />
      
      <main className="max-w-full mx-auto grid grid-cols-1 lg:grid-cols-4 gap-8">
        <div>
          <AddJobForm 
            formData={formData} 
            setFormData={setFormData} 
            formatSalaryString={formatSalaryString} 
            handleSalaryBlur={handleSalaryBlur}
            onSubmit={handleCreateJob} 
          />

          {/* Search input placed below AddJobForm as requested */}
          <div className="mt-4">
            <label className="relative block">
              <span className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                {/* Magnifying glass icon */}
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-4.35-4.35M17 11a6 6 0 11-12 0 6 6 0 0112 0z" />
                </svg>
              </span>
              <input
                aria-label="Search jobs"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search applied / interviewing / done..."
                className="w-full bg-gray-800 border border-gray-700 text-gray-100 placeholder-gray-500 pl-10 px-3 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-emerald-400"
              />
            </label>
          </div>
        </div>

        {/* SECTION 2: Interactive Columns Container */}
        <section className="lg:col-span-3 flex flex-col">
          {/* Mount our live telemetry data graph stream */}
          <AnalyticsChart />

          {/* Mount our visual Kanban tracking columns */}
          <KanbanBoard 
            jobs={filteredJobs} 
            onMove={handleMoveCard} 
            onDelete={handleDeleteCard} 
          />

        </section>
      </main>
    </div>
  );
}

export default App;
