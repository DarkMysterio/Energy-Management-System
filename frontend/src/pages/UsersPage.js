import React, { useState, useEffect } from 'react';
import { fetchUsers, createUser, updateUser, deleteUser, deleteAllUsers } from '../api';

function UsersPage() {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [editingUser, setEditingUser] = useState(null);
    const [formData, setFormData] = useState({ name: '', age: '', address: '', email: '', password: '' });

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        try {
            const data = await fetchUsers();
            setUsers(data);
            setError(null);
        } catch (err) {
            setError(err.message);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingUser) {
                await updateUser(editingUser.id, formData);
            } else {
                await createUser(formData);
            }
            setFormData({ name: '', age: '', address: '', email: '', password: '' });
            setShowForm(false);
            setEditingUser(null);
            loadUsers();
        } catch (err) {
            setError(err.message);
        }
    };

    const handleEdit = (user) => {
        setEditingUser(user);
        setFormData({ name: user.name, age: user.age, address: '', email: '', password: '' });
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Delete this user?')) {
            try {
                await deleteUser(id);
                loadUsers();
            } catch (err) {
                setError(err.message);
            }
        }
    };

    const handleDeleteAll = async () => {
        if (window.confirm('Delete ALL users?')) {
            try {
                await deleteAllUsers();
                loadUsers();
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
            <h2>Users Management</h2>
            
            <div style={{ marginBottom: '20px' }}>
                <button onClick={() => { setShowForm(!showForm); setEditingUser(null); setFormData({ name: '', age: '', address: '', email: '', password: '' }); }}>
                    {showForm ? 'Cancel' : '+ Add User'}
                </button>
                {users.length > 0 && (
                    <button onClick={handleDeleteAll} style={{ marginLeft: '10px', background: '#dc3545', color: 'white', border: 'none', padding: '8px 15px', cursor: 'pointer' }}>
                        Delete All
                    </button>
                )}
            </div>

            {showForm && (
                <form onSubmit={handleSubmit} style={{ marginBottom: '20px', padding: '15px', border: '1px solid #ccc', background: '#f9f9f9' }}>
                    <h3>{editingUser ? 'Edit User' : 'New User'}</h3>
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
                        <label>Age: </label>
                        <input 
                            type="number" 
                            value={formData.age} 
                            onChange={(e) => setFormData({ ...formData, age: e.target.value })}
                            required
                            min="18"
                            style={{ marginLeft: '10px', padding: '5px' }}
                        />
                        <small style={{ marginLeft: '10px', color: '#666' }}>(min 18)</small>
                    </div>
                    <div style={{ marginBottom: '10px' }}>
                        <label>Address: </label>
                        <input 
                            type="text" 
                            value={formData.address} 
                            onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                            required
                            style={{ marginLeft: '10px', padding: '5px', width: '250px' }}
                        />
                    </div>
                    <div style={{ marginBottom: '10px' }}>
                        <label>Email: </label>
                        <input 
                            type="email" 
                            value={formData.email} 
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            required
                            style={{ marginLeft: '10px', padding: '5px', width: '250px' }}
                        />
                    </div>
                    <div style={{ marginBottom: '10px' }}>
                        <label>Password: </label>
                        <input 
                            type="password" 
                            value={formData.password} 
                            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                            required
                            style={{ marginLeft: '10px', padding: '5px', width: '250px' }}
                        />
                    </div>
                    <button type="submit" style={{ padding: '8px 20px', cursor: 'pointer' }}>
                        {editingUser ? 'Update' : 'Create'}
                    </button>
                </form>
            )}

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Age</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {users.length === 0 ? (
                        <tr><td colSpan="4" style={{ textAlign: 'center' }}>No users found</td></tr>
                    ) : (
                        users.map(user => (
                            <tr key={user.id}>
                                <td>{user.id}</td>
                                <td>{user.name}</td>
                                <td>{user.age}</td>
                                <td>
                                    <button onClick={() => handleEdit(user)} style={{ marginRight: '5px', padding: '5px 10px', cursor: 'pointer' }}>
                                        Edit
                                    </button>
                                    <button onClick={() => handleDelete(user.id)} style={{ background: '#dc3545', color: 'white', border: 'none', padding: '5px 10px', cursor: 'pointer' }}>
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

export default UsersPage;
