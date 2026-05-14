import api from './api';

// Public browse endpoints — available to all authenticated users
export const getBusinesses = () => api.get('/api/public/businesses');
export const getBranches = (businessId) => api.get(`/api/public/businesses/${businessId}/branches`);
export const getQueuesByBranch = (branchId) => api.get(`/api/public/branches/${branchId}/queues`);
