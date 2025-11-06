import React, { useState, useEffect } from 'react';
import { fetchUsers, fetchDevices, assignDeviceToUser, fetchAssignments, deleteAllAssignments, deleteAssignment } from '../api';

function AssignDevicePage() {
    const [users, setUsers] = useState([]);
    const [devices, setDevices] = useState([]);
    const [assignments, setAssignments] = useState([]);
    const [selectedUser, setSelectedUser] = useState('');
    const [selectedDevice, setSelectedDevice] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState(null);

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const usersData = await fetchUsers();
            const devicesData = await fetchDevices();
            const assignmentsData = await fetchAssignments();
            setUsers(usersData);
            setDevices(devicesData);
            setAssignments(assignmentsData);
            if (usersData.length > 0) setSelectedUser(usersData[0].id);
            if (devicesData.length > 0) setSelectedDevice(devicesData[0].id);
            setError(null);
        } catch (err) {
            setError(err.message);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await assignDeviceToUser(selectedUser, selectedDevice);
            setMessage('Device assigned successfully!');
            setTimeout(() => setMessage(''), 3000);
            loadData();
        } catch (err) {
            setError(err.message);
        }
    };

    const handleDeleteAll = async () => {
        if (window.confirm('Delete ALL assignments?')) {
            try {
                await deleteAllAssignments();
                setMessage('All assignments deleted!');
                setTimeout(() => setMessage(''), 3000);
                loadData();
            } catch (err) {
                setError(err.message);
            }
        }
    };

    const handleDeleteOne = async (userId, deviceId) => {
        if (window.confirm('Delete this assignment?')) {
            try {
                await deleteAssignment(userId, deviceId);
                setMessage('Assignment deleted successfully!');
                setTimeout(() => setMessage(''), 3000);
                loadData();
            } catch (err) {
                setError(err.message);
            }
        }
    };

    const getUserName = (userId) => {
        const user = users.find(u => u.id === userId);
        return user ? user.name : userId;
    };

    const getDeviceName = (deviceId) => {
        const device = devices.find(d => d.id === deviceId);
        return device ? device.name : deviceId;
    };
    
    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div style={{ padding: '20px' }}>
            <h2>Assign Device to User</h2>
            
            <form onSubmit={handleSubmit} style={{ marginBottom: '30px', padding: '15px', border: '1px solid #ccc', background: '#f9f9f9' }}>
                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', marginBottom: '5px' }}>Select User:</label>
                    <select 
                        value={selectedUser} 
                        onChange={e => setSelectedUser(e.target.value)}
                        style={{ padding: '8px', width: '100%', maxWidth: '300px' }}
                    >
                        {users.map(user => (
                            <option key={user.id} value={user.id}>
                                {user.name} (Age: {user.age})
                            </option>
                        ))}
                    </select>
                </div>
                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', marginBottom: '5px' }}>Select Device:</label>
                    <select 
                        value={selectedDevice} 
                        onChange={e => setSelectedDevice(e.target.value)}
                        style={{ padding: '8px', width: '100%', maxWidth: '300px' }}
                    >
                        {devices.map(device => (
                            <option key={device.id} value={device.id}>
                                {device.name} (Consumption: {device.consumption})
                            </option>
                        ))}
                    </select>
                </div>
                <button type="submit" style={{ padding: '10px 20px', cursor: 'pointer', background: '#007bff', color: 'white', border: 'none' }}>
                    Assign Device
                </button>
            </form>

            {message && <div style={{ padding: '10px', background: '#d4edda', color: '#155724', marginBottom: '20px' }}>{message}</div>}

            <div style={{ marginTop: '30px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '15px' }}>
                    <h3>Current Assignments</h3>
                    {assignments.length > 0 && (
                        <button 
                            onClick={handleDeleteAll}
                            style={{ padding: '8px 15px', background: '#dc3545', color: 'white', border: 'none', cursor: 'pointer' }}
                        >
                            Delete All Assignments
                        </button>
                    )}
                </div>
                
                <table>
                    <thead>
                        <tr>
                            <th>User ID</th>
                            <th>User Name</th>
                            <th>Device ID</th>
                            <th>Device Name</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {assignments.length === 0 ? (
                            <tr><td colSpan="5" style={{ textAlign: 'center' }}>No assignments found</td></tr>
                        ) : (
                            assignments.map((assignment, index) => (
                                <tr key={index}>
                                    <td>{assignment.userID}</td>
                                    <td>{getUserName(assignment.userID)}</td>
                                    <td>{assignment.deviceID}</td>
                                    <td>{getDeviceName(assignment.deviceID)}</td>
                                    <td>
                                        <button 
                                            onClick={() => handleDeleteOne(assignment.userID, assignment.deviceID)}
                                            style={{ padding: '5px 10px', background: '#dc3545', color: 'white', border: 'none', cursor: 'pointer' }}
                                        >
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default AssignDevicePage;
