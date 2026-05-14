import api from './api';

export const createBusiness = (data) => api.post('/api/admin/businesses', data);
export const getBusinesses = () => api.get('/api/admin/businesses');
export const createBranch = (data) => api.post('/api/admin/branches', data);
export const getBranches = (businessId) => api.get(`/api/admin/businesses/${businessId}/branches`);
export const createQueue = (data) => api.post('/api/admin/queues', data);
export const updateQueueStatus = (queueId, status) =>
  api.patch(`/api/admin/queues/${queueId}/status`, null, { params: { status } });
export const addStaff = (userId, branchId) => api.post(`/api/admin/staff/${userId}/branch/${branchId}`);
export const assignStaff = (staffId, queueId) => api.post(`/api/admin/staff/${staffId}/assign-queue/${queueId}`);
export const getStaff = (branchId) => api.get(`/api/admin/branches/${branchId}/staff`);
export const getAnalytics = (queueId) => api.get(`/api/admin/queues/${queueId}/analytics`);
export const markNoShow = (tokenId) => api.post(`/api/admin/tokens/${tokenId}/no-show`);
