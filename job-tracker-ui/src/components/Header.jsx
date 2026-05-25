import React from 'react';

// This component is entirely focused on look and text presentation. No logic!
export default function Header() {
    return (
    <header className="mb-10 max-w-full mx-auto flex flex-col md:flex-row justify-between items-start md:items-center border-b border-gray-800 pb-6">
        <div>
        <h1 className="text-4xl font-black text-emerald-400 tracking-tight">Job Tracker Dashboard</h1>
        <p className="text-gray-400 mt-1">Distributed System Powered by Spring Boot, Redis, and Apache Kafka</p>
        </div>
    </header>
    );
}