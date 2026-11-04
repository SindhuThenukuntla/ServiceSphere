// ServiceSphere REST API Client for Asset Management & Ticket-Asset Linking
const API_BASE_URL = 'http://localhost:8080/api';

// Fallback seed data in case backend server is temporarily offline
export const INITIAL_SEED_DATA = {
  users: [
    { id: 1, fullName: 'Marcus Vance', email: 'marcus.vance@servicesphere.local', role: 'IT Asset Manager', department: 'IT Operations', status: 'ACTIVE' },
    { id: 2, fullName: 'Sarah Chen', email: 'sarah.chen@servicesphere.local', role: 'Employee', department: 'Engineering', status: 'ACTIVE' },
    { id: 3, fullName: 'David Kim', email: 'david.kim@servicesphere.local', role: 'Employee', department: 'Product & Design', status: 'ACTIVE' },
    { id: 4, fullName: 'Alex Rivera', email: 'alex.rivera@servicesphere.local', role: 'Support Agent', department: 'IT Operations', status: 'ACTIVE' },
    { id: 5, fullName: 'Elena Rostova', email: 'elena.rostova@servicesphere.local', role: 'Support Lead / Manager', department: 'IT Operations', status: 'ACTIVE' }
  ],
  assets: [
    {
      id: 1,
      assetTag: 'AST-001',
      assetType: 'Laptop',
      brand: 'Apple',
      model: 'MacBook Pro 16" M3 Max',
      serialNumber: 'C02G4581MD6R',
      status: 'In Stock',
      purchaseDate: '2024-05-15',
      warrantyEndDate: '2027-05-15',
      location: 'HQ Storage Room B',
      notes: 'Configured with standard developer profile, 36GB RAM, 1TB SSD',
      warrantyExpired: false,
      warrantyExpiringSoon: false,
      daysUntilWarrantyExpiry: 923,
      currentAssignment: null
    },
    {
      id: 2,
      assetTag: 'AST-002',
      assetType: 'Laptop',
      brand: 'Lenovo',
      model: 'ThinkPad X1 Carbon Gen 11',
      serialNumber: 'PF49X201',
      status: 'Assigned',
      purchaseDate: '2023-11-10',
      warrantyEndDate: '2026-11-10',
      location: 'Floor 3 Desk 304',
      notes: 'Standard engineering laptop issue',
      warrantyExpired: false,
      warrantyExpiringSoon: true, // within 30 days
      daysUntilWarrantyExpiry: 6,
      currentAssignment: {
        id: 101,
        assetId: 2,
        assetTag: 'AST-002',
        userId: 2,
        userName: 'Sarah Chen',
        userEmail: 'sarah.chen@servicesphere.local',
        userDepartment: 'Engineering',
        assignedAt: '2024-07-01T09:30:00',
        returnedAt: null,
        conditionOut: 'Brand new with USB-C 65W charger and privacy screen',
        conditionIn: null,
        assignedById: 1,
        assignedByName: 'Marcus Vance',
        active: true
      }
    },
    {
      id: 3,
      assetTag: 'AST-003',
      assetType: 'Monitor',
      brand: 'Dell',
      model: 'UltraSharp 32" 4K U3223QE',
      serialNumber: 'CN09187452',
      status: 'In Stock',
      purchaseDate: '2024-08-01',
      warrantyEndDate: '2027-08-01',
      location: 'HQ Inventory Rack 4',
      notes: 'IPS Black display with 90W USB-C hub connectivity',
      warrantyExpired: false,
      warrantyExpiringSoon: false,
      daysUntilWarrantyExpiry: 1001,
      currentAssignment: null
    },
    {
      id: 4,
      assetTag: 'AST-004',
      assetType: 'Phone',
      brand: 'Apple',
      model: 'iPhone 15 Pro 256GB',
      serialNumber: 'DX38947621',
      status: 'In Repair',
      purchaseDate: '2023-09-01',
      warrantyEndDate: '2024-09-01',
      location: 'Service Center',
      notes: 'Battery diagnostic and swollen cell inspection',
      warrantyExpired: true,
      warrantyExpiringSoon: false,
      daysUntilWarrantyExpiry: -64,
      currentAssignment: null
    },
    {
      id: 5,
      assetTag: 'AST-005',
      assetType: 'Desktop',
      brand: 'Apple',
      model: 'Mac Studio M2 Ultra',
      serialNumber: 'MJ92837411',
      status: 'Assigned',
      purchaseDate: '2023-12-20',
      warrantyEndDate: '2026-11-20',
      location: 'Studio Lab 1',
      notes: 'Primary 3D modeling and animation workstation',
      warrantyExpired: false,
      warrantyExpiringSoon: true,
      daysUntilWarrantyExpiry: 16,
      currentAssignment: {
        id: 102,
        assetId: 5,
        assetTag: 'AST-005',
        userId: 3,
        userName: 'David Kim',
        userEmail: 'david.kim@servicesphere.local',
        userDepartment: 'Product & Design',
        assignedAt: '2024-05-10T14:15:00',
        returnedAt: null,
        conditionOut: 'Dual Studio Display setup in Lab 1',
        conditionIn: null,
        assignedById: 1,
        assignedByName: 'Marcus Vance',
        active: true
      }
    },
    {
      id: 6,
      assetTag: 'AST-006',
      assetType: 'Network Equipment',
      brand: 'Cisco',
      model: 'Catalyst 9300 48-Port Switch',
      serialNumber: 'FOC24483921',
      status: 'In Stock',
      purchaseDate: '2023-01-10',
      warrantyEndDate: '2026-01-10',
      location: 'Server Room Alpha',
      notes: 'Managed Layer 3 switch for floor expansion',
      warrantyExpired: true,
      warrantyExpiringSoon: false,
      daysUntilWarrantyExpiry: -298,
      currentAssignment: null
    },
    {
      id: 7,
      assetTag: 'AST-007',
      assetType: 'Peripheral',
      brand: 'Logitech',
      model: 'MX Master 3S Wireless Mouse',
      serialNumber: 'LZ89230198',
      status: 'Retired',
      purchaseDate: '2021-02-14',
      warrantyEndDate: '2023-02-14',
      location: 'E-Waste Bin',
      notes: 'Left microswitch bounce after 3 years service',
      warrantyExpired: true,
      warrantyExpiringSoon: false,
      daysUntilWarrantyExpiry: -628,
      currentAssignment: null
    }
  ],
  assignmentsHistory: [
    {
      id: 101,
      assetId: 2,
      assetTag: 'AST-002',
      userId: 2,
      userName: 'Sarah Chen',
      userEmail: 'sarah.chen@servicesphere.local',
      userDepartment: 'Engineering',
      assignedAt: '2024-07-01T09:30:00',
      returnedAt: null,
      conditionOut: 'Brand new with USB-C 65W charger and privacy screen',
      conditionIn: null,
      assignedById: 1,
      assignedByName: 'Marcus Vance',
      active: true
    },
    {
      id: 102,
      assetId: 5,
      assetTag: 'AST-005',
      userId: 3,
      userName: 'David Kim',
      userEmail: 'david.kim@servicesphere.local',
      userDepartment: 'Product & Design',
      assignedAt: '2024-05-10T14:15:00',
      returnedAt: null,
      conditionOut: 'Dual Studio Display setup in Lab 1',
      conditionIn: null,
      assignedById: 1,
      assignedByName: 'Marcus Vance',
      active: true
    },
    {
      id: 100,
      assetId: 7,
      assetTag: 'AST-007',
      userId: 2,
      userName: 'Sarah Chen',
      userEmail: 'sarah.chen@servicesphere.local',
      userDepartment: 'Engineering',
      assignedAt: '2023-03-01T10:00:00',
      returnedAt: '2024-06-25T16:45:00',
      conditionOut: 'Brand new',
      conditionIn: 'Left click erratic; marked retired',
      assignedById: 1,
      assignedByName: 'Marcus Vance',
      active: false
    }
  ],
  tickets: [
    {
      id: 1,
      ticketNumber: 'TICK-1001',
      title: 'Lenovo ThinkPad trackpoint drift and thermal throttling',
      description: 'Sarah reported frequent cursor drift and fan noise under standard build workload.',
      priority: 'High',
      status: 'Open',
      requesterId: 2,
      requesterName: 'Sarah Chen',
      assigneeId: 4,
      assigneeName: 'Alex Rivera',
      departmentName: 'Engineering',
      linkedAsset: {
        id: 2,
        assetTag: 'AST-002',
        brand: 'Lenovo',
        model: 'ThinkPad X1 Carbon Gen 11',
        status: 'Assigned'
      }
    },
    {
      id: 2,
      ticketNumber: 'TICK-1002',
      title: 'Request dual-monitor cable for Mac Studio',
      description: 'Thunderbolt 4 to DisplayPort converter needed for second screen.',
      priority: 'Medium',
      status: 'In Progress',
      requesterId: 3,
      requesterName: 'David Kim',
      assigneeId: 4,
      assigneeName: 'Alex Rivera',
      departmentName: 'Product & Design',
      linkedAsset: {
        id: 5,
        assetTag: 'AST-005',
        brand: 'Apple',
        model: 'Mac Studio M2 Ultra',
        status: 'Assigned'
      }
    }
  ]
};

// Local cache for fallback state
let localData = JSON.parse(JSON.stringify(INITIAL_SEED_DATA));

export async function fetchAssets(type = '', status = '', search = '', page = 0, size = 20) {
  try {
    const params = new URLSearchParams();
    if (type) params.append('type', type);
    if (status) params.append('status', status);
    if (search) params.append('search', search);
    params.append('page', page);
    params.append('size', size);

    const res = await fetch(`${API_BASE_URL}/assets?${params.toString()}`);
    if (res.ok) {
      const data = await res.json();
      return {
        content: data.content || data,
        totalElements: data.totalElements || data.length,
        totalPages: data.totalPages || 1
      };
    }
  } catch (err) {
    console.warn('Backend unavailable, using local store:', err.message);
  }

  // Fallback filter logic
  let filtered = [...localData.assets];
  if (type) filtered = filtered.filter(a => a.assetType === type);
  if (status) filtered = filtered.filter(a => a.status === status);
  if (search) {
    const s = search.toLowerCase();
    filtered = filtered.filter(a =>
      a.assetTag.toLowerCase().includes(s) ||
      a.brand.toLowerCase().includes(s) ||
      a.model.toLowerCase().includes(s) ||
      (a.serialNumber && a.serialNumber.toLowerCase().includes(s)) ||
      (a.location && a.location.toLowerCase().includes(s))
    );
  }

  return {
    content: filtered,
    totalElements: filtered.length,
    totalPages: 1
  };
}

export async function fetchAssetSummary() {
  try {
    const res = await fetch(`${API_BASE_URL}/assets/summary`);
    if (res.ok) {
      return await res.json();
    }
  } catch (err) {
    console.warn('Backend unavailable, calculating summary locally');
  }

  const assets = localData.assets;
  return {
    totalAssets: assets.length,
    inStockCount: assets.filter(a => a.status === 'In Stock').length,
    assignedCount: assets.filter(a => a.status === 'Assigned').length,
    inRepairCount: assets.filter(a => a.status === 'In Repair').length,
    retiredCount: assets.filter(a => a.status === 'Retired').length,
    upcomingWarrantyExpiryCount: assets.filter(a => a.warrantyExpiringSoon).length,
    expiredWarrantyCount: assets.filter(a => a.warrantyExpired).length
  };
}

export async function fetchUsers() {
  try {
    const res = await fetch(`${API_BASE_URL}/users`);
    if (res.ok) {
      return await res.json();
    }
  } catch (err) {
    console.warn('Backend unavailable, returning fallback users');
  }
  return localData.users;
}

export async function createAsset(assetPayload) {
  try {
    const res = await fetch(`${API_BASE_URL}/assets`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(assetPayload)
    });
    if (res.ok) {
      return await res.json();
    }
    const errData = await res.json();
    throw new Error(errData.message || 'Failed to create asset');
  } catch (err) {
    if (err.message && !err.message.includes('fetch')) {
      throw err;
    }
    // Local fallback
    if (localData.assets.some(a => a.assetTag.toUpperCase() === assetPayload.assetTag.toUpperCase())) {
      throw new Error(`Asset tag '${assetPayload.assetTag}' already exists`);
    }
    const newAsset = {
      id: Date.now(),
      ...assetPayload,
      assetTag: assetPayload.assetTag.toUpperCase(),
      status: assetPayload.status || 'In Stock',
      warrantyExpired: false,
      warrantyExpiringSoon: false,
      daysUntilWarrantyExpiry: 365,
      currentAssignment: null
    };
    localData.assets.unshift(newAsset);
    return newAsset;
  }
}

export async function assignAsset(assetId, { userId, conditionOut, assignedBy }) {
  try {
    const res = await fetch(`${API_BASE_URL}/assets/${assetId}/assign`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId, conditionOut, assignedBy })
    });
    if (res.ok) {
      return await res.json();
    }
    const errData = await res.json();
    throw new Error(errData.message || 'Failed to assign asset');
  } catch (err) {
    if (err.message && !err.message.includes('fetch')) throw err;

    // Fallback simulation with full integrity rules
    const asset = localData.assets.find(a => a.id === assetId);
    if (!asset) throw new Error('Asset not found');
    if (asset.status === 'Retired') throw new Error(`Cannot assign asset '${asset.assetTag}' because it is Retired.`);
    if (asset.status === 'In Repair') throw new Error(`Cannot assign asset '${asset.assetTag}' because it is In Repair.`);
    if (asset.status === 'Assigned') throw new Error(`Asset '${asset.assetTag}' already has an active assignment.`);

    const user = localData.users.find(u => u.id === Number(userId));
    const assigner = localData.users.find(u => u.id === Number(assignedBy)) || localData.users[0];

    const assignment = {
      id: Date.now(),
      assetId: asset.id,
      assetTag: asset.assetTag,
      userId: user.id,
      userName: user.fullName,
      userEmail: user.email,
      userDepartment: user.department,
      assignedAt: new Date().toISOString(),
      returnedAt: null,
      conditionOut,
      conditionIn: null,
      assignedById: assigner.id,
      assignedByName: assigner.fullName,
      active: true
    };

    localData.assignmentsHistory.unshift(assignment);
    asset.status = 'Assigned';
    asset.currentAssignment = assignment;
    return asset;
  }
}

export async function returnAsset(assetId, { conditionIn, nextStatus }) {
  try {
    const res = await fetch(`${API_BASE_URL}/assets/${assetId}/return`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ conditionIn, nextStatus })
    });
    if (res.ok) {
      return await res.json();
    }
    const errData = await res.json();
    throw new Error(errData.message || 'Failed to return asset');
  } catch (err) {
    if (err.message && !err.message.includes('fetch')) throw err;

    const asset = localData.assets.find(a => a.id === assetId);
    if (!asset) throw new Error('Asset not found');
    if (asset.status !== 'Assigned' && !asset.currentAssignment) {
      throw new Error(`Asset '${asset.assetTag}' has no active assignment to return.`);
    }

    // Close assignment in history
    const active = localData.assignmentsHistory.find(h => h.assetId === assetId && h.active);
    if (active) {
      active.active = false;
      active.returnedAt = new Date().toISOString();
      active.conditionIn = conditionIn;
    }

    asset.status = nextStatus || 'In Stock';
    asset.currentAssignment = null;
    return asset;
  }
}

export async function fetchAssetHistory(assetId) {
  try {
    const res = await fetch(`${API_BASE_URL}/assets/${assetId}/history`);
    if (res.ok) {
      return await res.json();
    }
  } catch (err) {
    console.warn('Backend unavailable, using local history');
  }
  return localData.assignmentsHistory.filter(h => h.assetId === assetId);
}

export async function fetchTicketsForAsset(assetId) {
  try {
    const res = await fetch(`${API_BASE_URL}/assets/${assetId}/tickets`);
    if (res.ok) {
      return await res.json();
    }
  } catch (err) {
    console.warn('Backend unavailable, using local tickets');
  }
  return localData.tickets.filter(t => t.linkedAsset && t.linkedAsset.id === assetId);
}

export async function fetchAllTickets() {
  try {
    const res = await fetch(`${API_BASE_URL}/tickets`);
    if (res.ok) {
      return await res.json();
    }
  } catch (err) {
    console.warn('Backend unavailable, using local tickets');
  }
  return localData.tickets;
}

export async function linkAssetToTicket(ticketId, assetId) {
  try {
    const res = await fetch(`${API_BASE_URL}/tickets/${ticketId}/link-asset/${assetId}`, {
      method: 'POST'
    });
    if (res.ok) {
      return await res.json();
    }
    const err = await res.json();
    throw new Error(err.message || 'Failed to link asset');
  } catch (err) {
    if (err.message && !err.message.includes('fetch')) throw err;

    const ticket = localData.tickets.find(t => t.id === Number(ticketId));
    const asset = localData.assets.find(a => a.id === Number(assetId));
    if (!ticket) throw new Error('Ticket not found');
    if (!asset) throw new Error('Asset not found');

    if (asset.status === 'Retired' && ticket.status !== 'Closed') {
      throw new Error(`Cannot link retired asset '${asset.assetTag}' to open ticket '${ticket.ticketNumber}'.`);
    }

    ticket.linkedAsset = {
      id: asset.id,
      assetTag: asset.assetTag,
      brand: asset.brand,
      model: asset.model,
      status: asset.status
    };
    return ticket;
  }
}

export async function unlinkAssetFromTicket(ticketId) {
  try {
    const res = await fetch(`${API_BASE_URL}/tickets/${ticketId}/unlink-asset`, {
      method: 'DELETE'
    });
    if (res.ok) {
      return await res.json();
    }
    const err = await res.json();
    throw new Error(err.message || 'Failed to unlink asset');
  } catch (err) {
    if (err.message && !err.message.includes('fetch')) throw err;

    const ticket = localData.tickets.find(t => t.id === Number(ticketId));
    if (ticket) ticket.linkedAsset = null;
    return ticket;
  }
}

