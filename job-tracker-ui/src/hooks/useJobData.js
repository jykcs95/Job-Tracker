import { useState, useEffect } from 'react';

// A Custom Hook is just a regular JavaScript function that uses React hooks.
// It acts as an isolated data package that any component can tap into.
export function useJobData() {
    const [jobs, setJobs] = useState([]);
    const today = new Date().toISOString().slice(0, 10);
    const [formData, setFormData] = useState({
        companyName: '',
        roleTitle: '',
        state: 'APPLIED',
        salaryRange: '',
        appliedDate: today,
        jobUrl: ''
    });

    const USER_HEADER = { 'X-User-Id': 'user123' };
    const API_BASE_URL = 'http://localhost:8080/api/jobs';

    // Helper function to format raw numbers into professional currency ranges
    const formatSalaryString = (value) => {
        if (!value) return '';
        const cleanValue = value.replace(/[^0-9-]/g, '');
        const parts = cleanValue.split('-');

        const formatNumber = (numStr) => {
        if (!numStr) return '';
        const num = Number.parseInt(numStr, 10);
        return Number.isNaN(num) ? '' : '$' + num.toLocaleString('en-US');
        };

        if (parts.length > 1) {
        return `${formatNumber(parts[0])} - ${formatNumber(parts[1])}`.trim();
        }
        return formatNumber(parts[0]);
    };

    const handleSalaryBlur = () => {
        setFormData((currentFormData) => ({
            ...currentFormData,
            salaryRange: formatSalaryString(currentFormData.salaryRange)
        }));
    };

    // 1. Fetch Board (With Auto-Retry)
    const fetchKanbanBoard = async () => {
        try {
        const response = await fetch(API_BASE_URL, { headers: USER_HEADER });
        if (response.ok) {
            const data = await response.json();
            setJobs(data);
        } else {
            setTimeout(fetchKanbanBoard, 2000);
        }
        }
        catch (error){
        console.error("Error fetching Kanban Board",error);
        }
    };

    useEffect(() => {
        fetchKanbanBoard();
    }, []);

    // 2. Submit New Job Tracker Item
    const handleCreateJob = async (e) => {
        e.preventDefault();
        try {
        const requestData = {
            ...formData,
            salaryRange: formatSalaryString(formData.salaryRange)
        };
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: { ...USER_HEADER, 'Content-Type': 'application/json' },
            body: JSON.stringify(requestData)
        });
        if (response.ok) {
            setFormData({ companyName: '', roleTitle: '', state: 'APPLIED', salaryRange: '', appliedDate: today, jobUrl: '' });
            fetchKanbanBoard();
        }
        } catch (error) {
        console.error("Error creating job application entry:", error);
        }
    };

    // 3. Move Card State (Kafka trigger)
    const handleMoveCard = async (id, targetState) => {
        try {
        const response = await fetch(`${API_BASE_URL}/${id}/state?newState=${targetState}`, {
            method: 'PUT',
            headers: USER_HEADER
        });
        if (response.ok) {
            fetchKanbanBoard();
        }
        } catch (error) {
        console.error("Error updating job application status:", error);
        }
    };

    // 4. Delete Card Item
    const handleDeleteCard = async (id) => {
        if (!globalThis.confirm("Are you sure you want to delete this job application?")) return;
        try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE',
            headers: USER_HEADER
        });
        if (response.ok) {
            fetchKanbanBoard();
        }
        } catch (error) {
        console.error("Error deleting job application:", error);
        }
    };

    // Return all the functions and state so our UI components can use them
    return {
        jobs,
        formData,
        setFormData,
        formatSalaryString,
        handleSalaryBlur,
        handleCreateJob,
        handleMoveCard,
        handleDeleteCard
    };
}
