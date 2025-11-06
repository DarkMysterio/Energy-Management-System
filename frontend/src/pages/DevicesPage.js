import React, { useState, useEffect } from 'react';
import { fetchDevices, createDevice, updateDevice, updateDeviceName, updateDeviceConsumption, deleteDevice, deleteAllDevices } from '../api';

function DevicesPage() {
    const [devices, setDevices] = useState([]);
    const [error, setError] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [editingDevice, setEditingDevice] = useState(null);
    const [formData, setFormData] = useState({ name: '', consumption: '' });
    const [partialEdit, setPartialEdit] = useState({ id: null, field: null, value: '' });

    useEffect(() => {
        loadDevices();
    }, []);

    const loadDevices = async () => {
        try {
            const data = await fetchDevices();
            setDevices(data);
            setError(null);
        } catch (err) {
            setError(err.message);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const deviceData = {
                name: formData.name,
                consumption: parseFloat(formData.consumption)
            };
            if (editingDevice) {
                await updateDevice(editingDevice.id, deviceData);
            } else {
                await createDevice(deviceData);
            }
            setFormData({ name: '', consumption: '' });
            setShowForm(false);
            setEditingDevice(null);
            loadDevices();
        } catch (err) {
            setError(err.message);
        }
    };

    const handleEdit = (device) => {
        setEditingDevice(device);
        setFormData({ name: device.name, consumption: device.consumption });
        setShowForm(true);
    };

    const handlePartialUpdate = async (id, field) => {
        try {
            if (field === 'name') {
                await updateDeviceName(id, partialEdit.value);
            } else {
                await updateDeviceConsumption(id, parseFloat(partialEdit.value));
            }
            setPartialEdit({ id: null, field: null, value: '' });
            loadDevices();
        } catch (err) {
            setError(err.message);
        }
    };

    const handleDeleteAll = async () => {
        if (window.confirm('Delete ALL devices?')) {
            try {
                await deleteAllDevices();
                loadDevices();
            } catch (err) {
                setError(err.message);
            }
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Delete this device? This will also remove all user assignments for this device.')) {
            try {
                await deleteDevice(id);
                loadDevices();
            } catch (err) {
                setError(err.message);
            }
        }
    };

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div style={{ padding: '20px' }}>
            <h2>Devices Management</h2>
            
            <div style={{ marginBottom: '20px' }}>
                <button onClick={() => { setShowForm(!showForm); setEditingDevice(null); setFormData({ name: '', consumption: '' }); }}>
                    {showForm ? 'Cancel' : '+ Add Device'}
                </button>
                {devices.length > 0 && (
                    <button onClick={handleDeleteAll} style={{ marginLeft: '10px', background: '#dc3545', color: 'white', border: 'none', padding: '8px 15px', cursor: 'pointer' }}>
                        Delete All
                    </button>
                )}
            </div>

            {showForm && (
                <form onSubmit={handleSubmit} style={{ marginBottom: '20px', padding: '15px', border: '1px solid #ccc', background: '#f9f9f9' }}>
                    <h3>{editingDevice ? 'Edit Device (Full Update)' : 'New Device'}</h3>
                    <div style={{ marginBottom: '10px' }}>
                        <label>Name: </label>
                        <input 
                            type="text" 
                            value={formData.name} 
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            required
                            style={{ marginLeft: '10px', padding: '5px' }}
                        />
                    </div>
                    <div style={{ marginBottom: '10px' }}>
                        <label>Consumption: </label>
                        <input 
                            type="number" 
                            step="0.01"
                            value={formData.consumption} 
                            onChange={(e) => setFormData({ ...formData, consumption: e.target.value })}
                            required
                            style={{ marginLeft: '10px', padding: '5px' }}
                        />
                    </div>
                    <button type="submit" style={{ padding: '8px 20px', cursor: 'pointer' }}>
                        {editingDevice ? 'Update' : 'Create'}
                    </button>
                </form>
            )}

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Consumption</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {devices.length === 0 ? (
                        <tr><td colSpan="4" style={{ textAlign: 'center' }}>No devices found</td></tr>
                    ) : (
                        devices.map(device => (
                            <tr key={device.id}>
                                <td>{device.id}</td>
                                <td>
                                    {partialEdit.id === device.id && partialEdit.field === 'name' ? (
                                        <div>
                                            <input 
                                                type="text" 
                                                value={partialEdit.value}
                                                onChange={(e) => setPartialEdit({ ...partialEdit, value: e.target.value })}
                                                style={{ width: '120px' }}
                                            />
                                            <button onClick={() => handlePartialUpdate(device.id, 'name')} style={{ marginLeft: '5px', padding: '2px 8px' }}>✓</button>
                                            <button onClick={() => setPartialEdit({ id: null, field: null, value: '' })} style={{ marginLeft: '5px', padding: '2px 8px' }}>✗</button>
                                        </div>
                                    ) : (
                                        <span>
                                            {device.name}
                                            <button onClick={() => setPartialEdit({ id: device.id, field: 'name', value: device.name })} style={{ marginLeft: '10px', padding: '2px 8px', fontSize: '10px' }}>
                                                Edit Name
                                            </button>
                                        </span>
                                    )}
                                </td>
                                <td>
                                    {partialEdit.id === device.id && partialEdit.field === 'consumption' ? (
                                        <div>
                                            <input 
                                                type="number"
                                                step="0.01"
                                                value={partialEdit.value}
                                                onChange={(e) => setPartialEdit({ ...partialEdit, value: e.target.value })}
                                                style={{ width: '80px' }}
                                            />
                                            <button onClick={() => handlePartialUpdate(device.id, 'consumption')} style={{ marginLeft: '5px', padding: '2px 8px' }}>✓</button>
                                            <button onClick={() => setPartialEdit({ id: null, field: null, value: '' })} style={{ marginLeft: '5px', padding: '2px 8px' }}>✗</button>
                                        </div>
                                    ) : (
                                        <span>
                                            {device.consumption}
                                            <button onClick={() => setPartialEdit({ id: device.id, field: 'consumption', value: device.consumption })} style={{ marginLeft: '10px', padding: '2px 8px', fontSize: '10px' }}>
                                                Edit Consumption
                                            </button>
                                        </span>
                                    )}
                                </td>
                                <td>
                                    <button onClick={() => handleEdit(device)} style={{ padding: '5px 10px', cursor: 'pointer', marginRight: '5px' }}>
                                        Full Edit
                                    </button>
                                    <button onClick={() => handleDelete(device.id)} style={{ background: '#dc3545', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer' }}>
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
}

export default DevicesPage;
