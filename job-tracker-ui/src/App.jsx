import React, { useState, useEffect } from 'react';

function App() {
  // State memory to store our array list of job applications loaded from MySQL
  const [jobs, setJobs] = useState([]);
  
  // State memory to store form input data for creating a new job application item
  const [formData, setFormData] = useState({
    companyName: '',
    roleTitle: '',
    state: 'APPLIED',
    salaryRange: ''
  });

  // This hardcoded header simulates an authenticated user session ('user123')
  const USER_HEADER = { 'X-User-Id': 'user123' };
  const API_BASE_URL = 'http://localhost:8080/api/jobs';

  // Requirement 1: Automatically fetch the active Kanban Board from our backend on page load
  const fetchKanbanBoard = async () => {
    try {
      const response = await fetch(API_BASE_URL, { headers: USER_HEADER });
      if (response.ok) {
        const data = await response.json();
        setJobs(data); // Load the records directly into our UI state memory
      }
    } catch (error) {
      console.error("Failed to connect to our Spring Boot backend:", error);
    }
  };

  useEffect(() => {
    fetchKanbanBoard();
  }, []);

  // Requirement 2: Submit our new job tracker data to our REST endpoint
  const handleCreateJob = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(API_BASE_URL, {
        method: 'POST',
        headers: { ...USER_HEADER, 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });
      if (response.ok) {
        // Clear out our input form fields for the next entry
        setFormData({ companyName: '', roleTitle: '', state: 'APPLIED', salaryRange: '' });
        fetchKanbanBoard(); // Re-trigger a fetch look up to fetch the fresh board out of Redis/MySQL
      }
    } catch (error) {
      console.error("Error creating job application entry:", error);
    }
  };

  // Requirement 3: Trigger our PUT endpoint to move a card to a new state and fire Kafka events
  const handleMoveCard = async (id, targetState) => {
    try {
      const response = await fetch(`${API_BASE_URL}/${id}/state?newState=${targetState}`, {
        method: 'PUT',
        headers: USER_HEADER
      });
      if (response.ok) {
        fetchKanbanBoard(); // Refresh the board view instantly
      }
    } catch (error) {
      console.error("Error updating job application status:", error);
    }
  };

  // Helper method to filter our master list into 3 distinct Kanban columns
  const filterByState = (stateName) => jobs.filter(job => job.state === stateName);

  return (
    <div className="min-h-screen bg-gray-900 text-gray-100 p-6 font-sans">
      <header className="mb-8 max-w-6xl mx-auto flex flex-col md:flex-row justify-between items-start md:items-center border-b border-gray-800 pb-5">
        <div>
          <h1 className="text-3xl font-extrabold text-emerald-400 tracking-tight">Job Tracker Dashboard</h1>
          <p className="text-gray-400 mt-1">Distributed System Powered by Spring Boot, Redis, and Apache Kafka</p>
        </div>
      </header>

      {/* SECTION 1: Dynamic Data Submission Form Container */}
      <main className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-4 gap-6">
        <section className="bg-gray-800 p-5 rounded-xl border border-gray-700 h-fit">
          <h2 className="text-xl font-bold text-white mb-4">Add Application</h2>
          <form onSubmit={handleCreateJob} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-gray-400 uppercase mb-1">Company Name</label>
              <input 
                type="text" required
                value={formData.companyName}
                onChange={(e) => setFormData({...formData, companyName: e.target.value})}
                className="w-full bg-gray-900 border border-gray-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-emerald-500 text-sm"
                placeholder="e.g., Apple, Netflix"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-400 uppercase mb-1">Role Title</label>
              <input 
                type="text" required
                value={formData.roleTitle}
                onChange={(e) => setFormData({...formData, roleTitle: e.target.value})}
                className="w-full bg-gray-900 border border-gray-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-emerald-500 text-sm"
                placeholder="e.g., Backend Engineer"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-400 uppercase mb-1">Salary Range</label>
              <input 
                type="text"
                value={formData.salaryRange}
                onChange={(e) => setFormData({...formData, salaryRange: e.target.value})}
                className="w-full bg-gray-900 border border-gray-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-emerald-500 text-sm"
                placeholder="e.g., $140k - $170k"
              />
            </div>
            <button type="submit" className="w-full bg-emerald-500 hover:bg-emerald-600 text-gray-900 font-bold py-2 px-4 rounded-lg transition-colors text-sm cursor-pointer">
              Save to Database
            </button>
          </form>
        </section>

        {/* SECTION 2: Interactive Columns Container */}
        <section className="lg:col-span-3 grid grid-cols-1 md:grid-cols-3 gap-5">
          {['APPLIED', 'INTERVIEWING', 'DONE'].map((columnState) => (
            <div key={columnState} className="bg-gray-800/40 border border-gray-800 p-4 rounded-xl min-h-[500px] flex flex-col">
              <div className="flex justify-between items-center mb-4 border-b border-gray-800 pb-2">
                <h3 className={`text-sm font-black tracking-wider uppercase ${
                  columnState === 'APPLIED' ? 'text-blue-400' : columnState === 'INTERVIEWING' ? 'text-amber-400' : 'text-emerald-400'
                }`}>{columnState}</h3>
                <span className="bg-gray-800 text-xs px-2 py-0.5 rounded-full font-bold text-gray-400">{filterByState(columnState).length}</span>
              </div>

              {/* Individual Job Application Cards rendering pipeline */}
              <div className="space-y-3 flex-1 overflow-y-auto">
                {filterByState(columnState).map((job) => (
                  <div key={job.id} className="bg-gray-800 p-4 rounded-lg border border-gray-700/60 shadow-md group hover:border-gray-600 transition-all">
                    <h4 className="font-bold text-white text-base leading-tight">{job.roleTitle}</h4>
                    <p className="text-gray-400 text-sm mt-0.5">{job.companyName}</p>
                    {job.salaryRange && <span className="inline-block bg-gray-900/60 text-emerald-400/90 text-xs px-2 py-0.5 rounded mt-2 border border-emerald-500/10 font-mono">{job.salaryRange}</span>}
                    
                    {/* Interactive state transition controls */}
                    <div className="mt-4 flex gap-1 justify-end opacity-80 group-hover:opacity-100 transition-opacity">
                      {columnState !== 'APPLIED' && (
                        <button onClick={() => handleMoveCard(job.id, columnState === 'DONE' ? 'INTERVIEWING' : 'APPLIED')} className="text-xs bg-gray-700 hover:bg-gray-600 text-gray-300 px-2 py-1 rounded cursor-pointer">
                          ◀ Back
                        </button>
                      )}
                      {columnState !== 'DONE' && (
                        <button onClick={() => handleMoveCard(job.id, columnState === 'APPLIED' ? 'INTERVIEWING' : 'DONE')} className="text-xs bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 px-2 py-1 rounded border border-emerald-500/20 cursor-pointer">
                          Move Forward ▶
                        </button>
                      )}
                    </div>
                  </div>
                ))}
                {filterByState(columnState).length === 0 && (
                  <div className="text-center py-8 text-gray-600 text-xs border border-dashed border-gray-800 rounded-lg">No active applications</div>
                )}
              </div>
            </div>
          ))}
        </section>
      </main>
    </div>
  );
}

export default App;
