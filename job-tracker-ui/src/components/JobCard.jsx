import React from 'react';

export default function JobCard({ job, columnState, onMove, onDelete }) {
    const formatSalaryString = (value) => {
        if (!value) return '';
        const cleanValue = value.replace(/[^0-9-]/g, '');
        const parts = cleanValue.split('-');
        const formatNumber = (numStr) => {
            if (!numStr) return '';
            const num = Number.parseInt(numStr, 10);
            return Number.isNaN(num) ? '' : '$' + num.toLocaleString('en-US');
        };
        return parts.length > 1
            ? `${formatNumber(parts[0])} - ${formatNumber(parts[1])}`.trim()
            : formatNumber(parts[0]);
    };

    const salaryDisplay = job.salaryRange ? formatSalaryString(job.salaryRange) : '';
    const appliedDateValue = job.appliedDate || job.createdAt;
    const formattedDate = appliedDateValue
        ? new Date(appliedDateValue).toLocaleDateString('en-US', {
            month: 'short', day: 'numeric', year: 'numeric'
        })
        : null;

    return (
        <div className="bg-gray-800 p-4 rounded-lg border border-gray-700/60 shadow-md group hover:border-gray-600 transition-all">
        <h4 className="font-bold text-white text-base leading-tight">{job.roleTitle}</h4>
        <p className="text-gray-400 text-sm mt-0.5">{job.companyName}</p>
        {formattedDate && (
            <p className="text-gray-400 text-xs mt-2">Applied: {formattedDate}</p>
        )}
        
        {salaryDisplay && (
            <span className="inline-block bg-gray-900/60 text-emerald-400/90 text-xs px-2 py-0.5 rounded mt-2 border border-emerald-500/10 font-mono">
            {salaryDisplay}
            </span>
        )}
        
        {job.jobUrl && (
            <a 
            href={job.jobUrl} 
            target="_blank" 
            rel="noopener noreferrer" 
            className="inline-block mt-2 ml-2 text-xs text-emerald-400 hover:text-emerald-300 hover:underline font-medium cursor-pointer"
            >
            View Listing 🔗
            </a>
        )}
        
        {/* Dynamic Action Buttons Block */}
        <div className="mt-4 flex gap-1 justify-end opacity-80 group-hover:opacity-100 transition-opacity">
            {columnState !== 'APPLIED' && (
            <button 
                onClick={() => onMove(job.id, columnState === 'DONE' ? 'INTERVIEWING' : 'APPLIED')} 
                className="text-xs bg-gray-700 hover:bg-gray-600 text-gray-300 px-2 py-1 rounded cursor-pointer"
            >
                ◀ Back
            </button>
            )}

            {columnState !== 'DONE' && (
            <button 
                onClick={() => onMove(job.id, columnState === 'APPLIED' ? 'INTERVIEWING' : 'DONE')} 
                className="text-xs bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 px-2 py-1 rounded border border-emerald-500/20 cursor-pointer"
            >
                Move Forward ▶
            </button>
            )}

            <button 
            onClick={() => onDelete(job.id)} 
            className="text-xs bg-red-500/10 hover:bg-red-500/20 text-red-400 px-2 py-1 rounded border border-red-500/20 cursor-pointer transition-colors"
            >
            Delete 🗑
            </button>
            
        </div>
        </div>
    );
}
