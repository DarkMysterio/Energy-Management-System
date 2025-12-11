import React, { useState, useEffect } from 'react';
import { fetchDevices, fetchAssignments } from '../api';
import { authService } from '../authService';

function ClientDevicesPage() {
    const [myDevices, setMyDevices] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadMyDevices();
    }, []);

    const loadMyDevices = async () => {
        try {
            setLoading(true);
            const userId = authService.getUserId();
            
            if (!userId) {
                setError('User not found - please login again');
                setLoading(false);
                return;
            }

            const [devices, assignments] = await Promise.all([
                fetchDevices(),
                fetchAssignments()
            ]);

            const myAssignments = assignments.filter(a => a.userID === userId);
            
            const assignedDevices = myAssignments.map(assignment => {
                const device = devices.find(d => d.id === assignment.deviceID);
                return device;
            }).filter(d => d !== undefined);

            setMyDevices(assignedDevices);
            setError(null);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div style={{ padding: '20px' }}>Loading your devices...</div>;
    }

    if (error) {
        return <div style={{ padding: '20px', color: 'red' }}>Error: {error}</div>;
    }

    return (
        <div style={{ padding: '20px' }}>
            <h2>My Devices</h2>
            
            {myDevices.length === 0 ? (
                <div style={{ padding: '20px', background: '#f9f9f9', borderRadius: '4px', marginTop: '20px' }}>
                    <p>You don't have any devices assigned yet.</p>
                    <p style={{ fontSize: '14px', color: '#666' }}>
                        Please contact an administrator to assign devices to you.
                    </p>
                </div>
            ) : (
                <div style={{ marginTop: '20px' }}>
                    <table>
                        <thead>
                            <tr>
                                <th>Device Name</th>
                                <th>Consumption (kWh)</th>
                                <th>Device ID</th>
                            </tr>
                        </thead>
                        <tbody>
                            {myDevices.map(device => (
                                <tr key={device.id}>
                                    <td style={{ fontWeight: 'bold' }}>{device.name}</td>
                                    <td>{device.consumption}</td>
                                    <td style={{ fontSize: '12px', color: '#666' }}>{device.id}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    
                    <div style={{ marginTop: '20px', padding: '15px', background: '#e7f3ff', borderRadius: '4px' }}>
                        <h4 style={{ marginTop: 0 }}>Summary</h4>
                        <p><strong>Total Devices:</strong> {myDevices.length}</p>
                        <p><strong>Total Consumption:</strong> {myDevices.reduce((sum, d) => sum + d.consumption, 0).toFixed(2)} kWh</p>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ClientDevicesPage;
