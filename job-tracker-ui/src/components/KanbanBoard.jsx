import React from 'react';
import JobCard from './JobCard';

export default function KanbanBoard({ jobs, onMove, onDelete }) {
    // Helper to quickly filter your state array data list
    const filterByState = (stateName) => jobs.filter(job => job.state === stateName);

    return (
        <section className="lg:col-span-3 grid grid-cols-1 md:grid-cols-3 gap-5">
        {['APPLIED', 'INTERVIEWING', 'DONE'].map((columnState) => (
            <div key={columnState} className="bg-gray-800/40 border border-gray-800 p-4 rounded-xl min-h-[500px] flex flex-col">
            
            {/* Column Header Counter Badge Panel */}
            <div className="flex justify-between items-center mb-4 border-b border-gray-800 pb-2">
                <h3 className={`text-sm font-black tracking-wider uppercase ${
                columnState === 'APPLIED' ? 'text-blue-400' : columnState === 'INTERVIEWING' ? 'text-amber-400' : 'text-emerald-400'
                }`}>{columnState}</h3>
                <span className="bg-gray-800 text-xs px-2 py-0.5 rounded-full font-bold text-gray-400">
                {filterByState(columnState).length}
                </span>
            </div>

            {/* Cards Pipeline Shell */}
            <div className="space-y-3 flex-1 overflow-y-auto">
                {filterByState(columnState).map((job) => (
                <JobCard 
                    key={job.id} 
                    job={job} 
                    columnState={columnState} 
                    onMove={onMove} 
                    onDelete={onDelete} 
                />
                ))}
                {filterByState(columnState).length === 0 && (
                <div className="text-center py-8 text-gray-600 text-xs border border-dashed border-gray-800 rounded-lg">
                    No active applications
                </div>
                )}
            </div>
            </div>
        ))}
        </section>
    );
}
