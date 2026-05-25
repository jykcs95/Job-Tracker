import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';

export default function AnalyticsChart() {
    const [metrics, setMetrics] = useState([
        { name: 'APPLIED', count: 0, fill: '#60a5fa' },
        { name: 'INTERVIEWING', count: 0, fill: '#fbbf24' },
        { name: 'DONE', count: 0, fill: '#34d399' }
    ]);
    const [toast, setToast] = useState({ show: false, message: '' });

    const INITIAL_API_URL = 'http://localhost:8081/api/analytics/daily';
    const STREAM_API_URL = 'http://localhost:8081/api/analytics/stream';

    const normalizeMetrics = (rawData) => {
        const empty = [
            { name: 'APPLIED', count: 0, fill: getBarColor('APPLIED') },
            { name: 'INTERVIEWING', count: 0, fill: getBarColor('INTERVIEWING') },
            { name: 'DONE', count: 0, fill: getBarColor('DONE') }
        ];
        if (!rawData || rawData.length === 0) return empty;

        // Prefer rows for today's date if available, otherwise use latest available
        const today = new Date().toISOString().split('T')[0];
        const todays = rawData.filter(d => d.logDate && d.logDate.startsWith(today));
        const rows = todays.length > 0 ? todays : rawData;

        // Aggregate counts by state to guard against multiple rows
        const byState = rows.reduce((acc, item) => {
            const state = item.state;
            const count = Number(item.totalCount) || 0;
            acc[state] = (acc[state] || 0) + count;
            return acc;
        }, {});

        return [
            { name: 'APPLIED', count: byState['APPLIED'] || 0, fill: getBarColor('APPLIED') },
            { name: 'INTERVIEWING', count: byState['INTERVIEWING'] || 0, fill: getBarColor('INTERVIEWING') },
            { name: 'DONE', count: byState['DONE'] || 0, fill: getBarColor('DONE') }
        ];
    };

    useEffect(() => {
        // 1. Fetch initial values on page mount layout
        fetch(INITIAL_API_URL)
        .then(res => res.json())
        .then(data => {
            if (data && data.length > 0) {
                setMetrics(normalizeMetrics(data));
            }
        })
        .catch(err => console.error("Initial metrics fetch failed:", err));

        // 2. OPEN NATIVE SSE STREAM EVENT LISTENER WIRE (No polling timers!)
        const eventSource = new EventSource(STREAM_API_URL);

        eventSource.addEventListener('telemetry-update', (event) => {
            const freshData = JSON.parse(event.data);
            console.log(">>> SSE Push Event Caught Live:", freshData);

            // Normalize and update our chart states instantly
            setMetrics(normalizeMetrics(freshData));

            // Trigger our live toast notification
            setToast({ show: true, message: '⚡ SSE Push Event Synchronized!' });
            setTimeout(() => setToast({ show: false, message: '' }), 3500);
        });

        // Clean up our streaming wire when the user closes the website
        return () => eventSource.close();
    }, []);

    const getBarColor = (name) => {
        if (name === 'APPLIED') return '#60a5fa';     
        if (name === 'INTERVIEWING') return '#fbbf24';  
        if (name === 'DONE') return '#34d399';          
        return '#9ca3af';                              
    };

    const CustomTooltip = ({ active, payload }) => {
        if (active && payload && payload.length) {
            const data = payload[0].payload;
            const labels = {
                'APPLIED': '📨 Applications Sent',
                'INTERVIEWING': '🎤 In Interviews',
                'DONE': '✅ Completed'
            };
            return (
                <div className="bg-gray-900 border border-gray-700 px-3 py-2 rounded-lg">
                    <p className="text-white font-semibold text-sm">{labels[data.name] || data.name}</p>
                    <p className="text-gray-300 text-sm font-bold">{data.count} {data.count === 1 ? 'job' : 'jobs'}</p>
                </div>
            );
        }
        return null;
    };

    return (
        <div className="w-full bg-gray-800/40 border border-gray-800 p-5 rounded-xl mb-8 relative overflow-hidden">
        
        {/* GLOWING POP-UP TOAST ALERT LAYER */}
        {toast.show && (
            <div className="absolute top-4 right-4 bg-emerald-500 text-gray-900 px-4 py-2 rounded-lg font-bold text-xs shadow-lg animate-bounce border border-emerald-400 flex items-center gap-2 z-50">
            <span>{toast.message}</span>
            </div>
        )}

        <div className="mb-4">
            <h2 className="text-xl font-black text-white tracking-tight flex items-center gap-2">
                <span className="flex h-2 w-2 rounded-full bg-emerald-400 animate-pulse" />{' '}Real-Time Data Telemetry Engine
            </h2>
            <p className="text-xs text-gray-500 mt-0.5">Asynchronous metrics pushed instantly via Server-Sent Events (SSE)</p>
        </div>

        <div className="h-[220px] w-full relative">
            <ResponsiveContainer width="100%" height="100%">
            <BarChart data={metrics} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <XAxis dataKey="name" stroke="#9ca3af" fontSize={11} tickLine={false} />
                <YAxis stroke="#9ca3af" fontSize={11} tickLine={false} allowDecimals={false} />
                <Tooltip 
                content={<CustomTooltip />}
                cursor={{ fill: 'rgba(255,255,255,0.03)' }}
                />
                <Bar dataKey="count" radius={5} maxBarSize={60} />
            </BarChart>
            </ResponsiveContainer>
        </div>
        </div>
    );
}

