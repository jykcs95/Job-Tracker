import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';

export default function AnalyticsChart() {
    const [metrics, setMetrics] = useState([
        { name: 'APPLIED', count: 0 },
        { name: 'INTERVIEWING', count: 0 },
        { name: 'DONE', count: 0 }
    ]);

    const ANALYTICS_API_URL = 'http://localhost:8081/api/analytics/daily';

    const fetchMetrics = async () => {
        try {
        const response = await fetch(ANALYTICS_API_URL);
        if (response.ok) {
            const data = await response.json();
            
            // If the database returns rows, map them accurately by checking the exact JSON keys
            if (data) {
            const states = ['APPLIED', 'INTERVIEWING', 'DONE'];
            const formatted = states.map((state) => {
                const item = data.find((entry) => entry.state === state);
                return {
                name: state,
                count: item?.totalCount ?? 0
                };
            });
            setMetrics(formatted);
            }
        }
        } catch (error) {
        console.error("Failed to connect to Analytics Service:", error);
        }
    };

    useEffect(() => {
        fetchMetrics();
        const interval = setInterval(fetchMetrics, 2000);
        return () => clearInterval(interval);
    }, []);

    const getBarColor = (name) => {
        if (name === 'APPLIED') return '#60a5fa';     
        if (name === 'INTERVIEWING') return '#fbbf24';  
        if (name === 'DONE') return '#34d399';          
        return '#9ca3af';                              
    };

    return (
        <div className="w-full bg-gray-800/40 border border-gray-800 p-5 rounded-xl mb-8">
        <div className="mb-4">
            <h2 className="text-xl font-black text-white tracking-tight flex items-center gap-2">
            <span className="flex h-2 w-2 rounded-full bg-emerald-400 animate-pulse" />
            Real-Time Data Telemetry Engine
            </h2>
            <p className="text-xs text-gray-500 mt-0.5">Asynchronous metrics aggregated via Apache Kafka streams</p>
        </div>

        <div className="h-[220px] w-full relative">
            <ResponsiveContainer width="100%" height="100%">
            <BarChart data={metrics} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <XAxis dataKey="name" stroke="#9ca3af" fontSize={11} tickLine={false} />
                <YAxis stroke="#9ca3af" fontSize={11} tickLine={false} allowDecimals={false} />
                <Tooltip 
                contentStyle={{ backgroundColor: '#1f2937', borderColor: '#374151', borderRadius: '8px', color: '#fff', fontSize: '12px' }}
                cursor={{ fill: 'rgba(255,255,255,0.03)' }}
                />
                <Bar dataKey="count" radius={5} maxBarSize={60}>
                {metrics.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={getBarColor(entry.name)} />
                ))}
                </Bar>
            </BarChart>
            </ResponsiveContainer>
        </div>
        </div>
    );
}
