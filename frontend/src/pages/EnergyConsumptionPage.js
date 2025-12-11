import React, { useState, useEffect, useRef } from 'react';
import { fetchDevices, fetchAssignments, fetchDailyConsumption, fetchTotalConsumption } from '../api';
import { authService } from '../authService';

const REFRESH_INTERVAL = 500;

function EnergyConsumptionPage() {
    const [myDevices, setMyDevices] = useState([]);
    const [selectedDevice, setSelectedDevice] = useState('');
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
    const [consumptionData, setConsumptionData] = useState(null);
    const [totalConsumption, setTotalConsumption] = useState(null);
    const [chartType, setChartType] = useState('bar');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [loadingChart, setLoadingChart] = useState(false);
    const [loadingTotal, setLoadingTotal] = useState(false);
    const intervalRef = useRef(null);

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
            if (assignedDevices.length > 0) {
                setSelectedDevice(assignedDevices[0].id);
            }
            setError(null);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const loadConsumptionData = async (silent = false) => {
        if (!selectedDevice || !selectedDate) return;

        try {
            if (!silent) setLoadingChart(true);
            const data = await fetchDailyConsumption(selectedDevice, selectedDate);
            setConsumptionData(data);
            setError(null);
        } catch (err) {
            if (!silent) setError(err.message);
            setConsumptionData(null);
        } finally {
            if (!silent) setLoadingChart(false);
        }
    };

    const loadTotalConsumption = async (silent = false) => {
        if (myDevices.length === 0 || !selectedDate) return;

        try {
            if (!silent) setLoadingTotal(true);
            const data = await fetchTotalConsumption(myDevices, selectedDate);
            setTotalConsumption(data);
        } catch (err) {
            console.error('Failed to load total consumption:', err);
            setTotalConsumption(null);
        } finally {
            if (!silent) setLoadingTotal(false);
        }
    };

    useEffect(() => {
        if (selectedDevice && selectedDate && myDevices.length > 0) {
            intervalRef.current = setInterval(() => {
                loadConsumptionData(true);
                loadTotalConsumption(true);
            }, REFRESH_INTERVAL);
        }
        
        return () => {
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
            }
        };
    }, [selectedDevice, selectedDate, myDevices]);

    useEffect(() => {
        if (selectedDevice && selectedDate) {
            loadConsumptionData();
        }
    }, [selectedDevice, selectedDate]);

    useEffect(() => {
        if (myDevices.length > 0 && selectedDate) {
            loadTotalConsumption();
        }
    }, [myDevices, selectedDate]);

    const renderChart = () => {
        if (!consumptionData || !consumptionData.hourlyData) return null;

        const maxKwh = Math.max(...consumptionData.hourlyData.map(h => h.kwh), 0.1);
        const chartHeight = 300;
        const chartWidth = 800;
        const barWidth = chartWidth / 24 - 4;

        if (chartType === 'bar') {
            return (
                <svg width={chartWidth + 60} height={chartHeight + 60} style={{ background: '#fafafa', borderRadius: '8px' }}>
                    <line x1="50" y1="10" x2="50" y2={chartHeight + 10} stroke="#333" strokeWidth="2" />
                    <line x1="50" y1={chartHeight + 10} x2={chartWidth + 50} y2={chartHeight + 10} stroke="#333" strokeWidth="2" />
                    
                    {[0, 0.25, 0.5, 0.75, 1].map((ratio, i) => (
                        <g key={i}>
                            <text x="45" y={chartHeight + 10 - ratio * chartHeight} textAnchor="end" fontSize="12" fill="#666">
                                {(maxKwh * ratio).toFixed(2)}
                            </text>
                            <line x1="48" y1={chartHeight + 10 - ratio * chartHeight} x2="52" y2={chartHeight + 10 - ratio * chartHeight} stroke="#333" />
                        </g>
                    ))}

                    {consumptionData.hourlyData.map((hourData, index) => {
                        const barHeight = (hourData.kwh / maxKwh) * chartHeight;
                        return (
                            <g key={index}>
                                <rect
                                    x={55 + index * (barWidth + 4)}
                                    y={chartHeight + 10 - barHeight}
                                    width={barWidth}
                                    height={barHeight}
                                    fill="#4a90d9"
                                    rx="2"
                                >
                                    <title>{`Hour ${hourData.hour}: ${hourData.kwh.toFixed(4)} kWh`}</title>
                                </rect>
                                {index % 3 === 0 && (
                                    <text 
                                        x={55 + index * (barWidth + 4) + barWidth / 2} 
                                        y={chartHeight + 30} 
                                        textAnchor="middle" 
                                        fontSize="11" 
                                        fill="#666"
                                    >
                                        {hourData.hour}:00
                                    </text>
                                )}
                            </g>
                        );
                    })}

                    <text x={chartWidth / 2 + 30} y={chartHeight + 55} textAnchor="middle" fontSize="14" fill="#333">Hours</text>
                    <text x="15" y={chartHeight / 2} textAnchor="middle" fontSize="14" fill="#333" transform={`rotate(-90, 15, ${chartHeight / 2})`}>kWh</text>
                </svg>
            );
        } else {
            const points = consumptionData.hourlyData.map((h, i) => {
                const x = 55 + i * ((chartWidth - 10) / 23);
                const y = chartHeight + 10 - (h.kwh / maxKwh) * chartHeight;
                return `${x},${y}`;
            }).join(' ');

            return (
                <svg width={chartWidth + 60} height={chartHeight + 60} style={{ background: '#fafafa', borderRadius: '8px' }}>
                    <line x1="50" y1="10" x2="50" y2={chartHeight + 10} stroke="#333" strokeWidth="2" />
                    <line x1="50" y1={chartHeight + 10} x2={chartWidth + 50} y2={chartHeight + 10} stroke="#333" strokeWidth="2" />
                    
                    {[0, 0.25, 0.5, 0.75, 1].map((ratio, i) => (
                        <g key={i}>
                            <text x="45" y={chartHeight + 10 - ratio * chartHeight} textAnchor="end" fontSize="12" fill="#666">
                                {(maxKwh * ratio).toFixed(2)}
                            </text>
                            <line x1="48" y1={chartHeight + 10 - ratio * chartHeight} x2={chartWidth + 50} y2={chartHeight + 10 - ratio * chartHeight} stroke="#eee" />
                        </g>
                    ))}

                    <polyline
                        points={points}
                        fill="none"
                        stroke="#4a90d9"
                        strokeWidth="2"
                    />

                    {consumptionData.hourlyData.map((h, i) => {
                        const x = 55 + i * ((chartWidth - 10) / 23);
                        const y = chartHeight + 10 - (h.kwh / maxKwh) * chartHeight;
                        return (
                            <circle key={i} cx={x} cy={y} r="4" fill="#4a90d9">
                                <title>{`Hour ${h.hour}: ${h.kwh.toFixed(4)} kWh`}</title>
                            </circle>
                        );
                    })}

                    {[0, 6, 12, 18, 23].map(hour => {
                        const x = 55 + hour * ((chartWidth - 10) / 23);
                        return (
                            <text key={hour} x={x} y={chartHeight + 30} textAnchor="middle" fontSize="11" fill="#666">
                                {hour}:00
                            </text>
                        );
                    })}

                    <text x={chartWidth / 2 + 30} y={chartHeight + 55} textAnchor="middle" fontSize="14" fill="#333">Hours</text>
                    <text x="15" y={chartHeight / 2} textAnchor="middle" fontSize="14" fill="#333" transform={`rotate(-90, 15, ${chartHeight / 2})`}>kWh</text>
                </svg>
            );
        }
    };

    if (loading) {
        return <div style={{ padding: '20px' }}>Loading...</div>;
    }

    return (
        <div style={{ padding: '20px' }}>
            <h2>Energy Consumption History</h2>

            {error && (
                <div style={{ padding: '10px', background: '#ffebee', color: '#c62828', borderRadius: '4px', marginBottom: '20px' }}>
                    {error}
                </div>
            )}

            {myDevices.length === 0 ? (
                <div style={{ padding: '20px', background: '#f9f9f9', borderRadius: '4px' }}>
                    <p>You don't have any devices assigned yet.</p>
                </div>
            ) : (
                <>
                    <div style={{ 
                        marginBottom: '25px', 
                        padding: '20px', 
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', 
                        borderRadius: '12px',
                        color: 'white',
                        boxShadow: '0 4px 15px rgba(102, 126, 234, 0.4)'
                    }}>
                        <h3 style={{ marginTop: 0, marginBottom: '15px' }}>📊 Total Energy Consumption - {selectedDate}</h3>
                        {loadingTotal ? (
                            <p>Loading total consumption...</p>
                        ) : totalConsumption ? (
                            <div>
                                <div style={{ fontSize: '32px', fontWeight: 'bold', marginBottom: '15px' }}>
                                    {totalConsumption.totalKwh.toFixed(4)} kWh
                                </div>
                                <div style={{ fontSize: '14px', opacity: 0.9 }}>
                                    <strong>Breakdown by device:</strong>
                                    <div style={{ marginTop: '10px', display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                                        {totalConsumption.deviceConsumptions && totalConsumption.deviceConsumptions.map((dc, idx) => (
                                            <div key={idx} style={{ 
                                                background: 'rgba(255,255,255,0.2)', 
                                                padding: '8px 12px', 
                                                borderRadius: '6px',
                                                fontSize: '13px'
                                            }}>
                                                <strong>{dc.deviceName}:</strong> {dc.kwh.toFixed(4)} kWh
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <p>No consumption data available</p>
                        )}
                    </div>

                    <div style={{ display: 'flex', gap: '20px', marginBottom: '20px', flexWrap: 'wrap', alignItems: 'center' }}>
                        <div>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Select Device:</label>
                            <select
                                value={selectedDevice}
                                onChange={(e) => setSelectedDevice(e.target.value)}
                                style={{ padding: '8px 12px', borderRadius: '4px', border: '1px solid #ccc', minWidth: '200px' }}
                            >
                                {myDevices.map(device => (
                                    <option key={device.id} value={device.id}>
                                        {device.name}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Select Date:</label>
                            <input
                                type="date"
                                value={selectedDate}
                                onChange={(e) => setSelectedDate(e.target.value)}
                                style={{ padding: '8px 12px', borderRadius: '4px', border: '1px solid #ccc' }}
                            />
                        </div>

                        <div>
                            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Chart Type:</label>
                            <select
                                value={chartType}
                                onChange={(e) => setChartType(e.target.value)}
                                style={{ padding: '8px 12px', borderRadius: '4px', border: '1px solid #ccc' }}
                            >
                                <option value="bar">Bar Chart</option>
                                <option value="line">Line Chart</option>
                            </select>
                        </div>
                    </div>

                    <div style={{ marginTop: '20px', overflowX: 'auto' }}>
                        {loadingChart ? (
                            <div style={{ padding: '50px', textAlign: 'center' }}>Loading chart data...</div>
                        ) : consumptionData ? (
                            <>
                                <h3 style={{ marginBottom: '10px' }}>
                                    Hourly Energy Consumption - {selectedDate}
                                </h3>
                                {renderChart()}
                                
                                <div style={{ marginTop: '20px', padding: '15px', background: '#e7f3ff', borderRadius: '4px', maxWidth: '400px' }}>
                                    <h4 style={{ marginTop: 0 }}>Daily Summary</h4>
                                    <p><strong>Device:</strong> {myDevices.find(d => d.id === selectedDevice)?.name}</p>
                                    <p><strong>Date:</strong> {selectedDate}</p>
                                    <p><strong>Total Consumption:</strong> {consumptionData.totalDayKwh.toFixed(4)} kWh</p>
                                </div>

                                <details style={{ marginTop: '20px' }}>
                                    <summary style={{ cursor: 'pointer', fontWeight: 'bold' }}>View Hourly Data Table</summary>
                                    <table style={{ marginTop: '10px', fontSize: '14px' }}>
                                        <thead>
                                            <tr>
                                                <th>Hour</th>
                                                <th>Consumption (kWh)</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {consumptionData.hourlyData.map(h => (
                                                <tr key={h.hour}>
                                                    <td>{h.hour}:00 - {h.hour + 1}:00</td>
                                                    <td>{h.kwh.toFixed(4)}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </details>
                            </>
                        ) : (
                            <div style={{ padding: '50px', textAlign: 'center', color: '#666' }}>
                                Select a device and date to view consumption data
                            </div>
                        )}
                    </div>
                </>
            )}
        </div>
    );
}

export default EnergyConsumptionPage;
