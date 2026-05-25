import React from 'react';

// We pass down our state data and form triggers from our custom hook as properties (props)
export default function AddJobForm({ formData, setFormData, formatSalaryString, onSubmit }) {
    return (
        <section className="bg-gray-800 p-5 rounded-xl border border-gray-700 h-fit">
        <h2 className="text-2xl font-bold text-white mb-6">Add Application</h2>
        <form onSubmit={onSubmit} className="space-y-4">
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
                onChange={(e) => setFormData({...formData, salaryRange: formatSalaryString(e.target.value)})}
                className="w-full bg-gray-900 border border-gray-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-emerald-500 text-sm"
                placeholder="e.g., 140000-170000"
            />
            </div>
            <div>
            <label className="block text-xs font-semibold text-gray-400 uppercase mb-1">Listing URL</label>
            <input 
                type="url"
                value={formData.jobUrl}
                onChange={(e) => setFormData({...formData, jobUrl: e.target.value})}
                className="w-full bg-gray-900 border border-gray-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-emerald-500 text-sm"
                placeholder="e.g., https://linkedin.com..."
            />
            </div>
            <button type="submit" className="w-full bg-emerald-500 hover:bg-emerald-600 text-gray-900 font-bold py-2 px-4 rounded-lg transition-colors text-sm cursor-pointer">
            Save to Database
            </button>
        </form>
        </section>
    );
}
