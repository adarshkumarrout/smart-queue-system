import api from './api';

export const getQueuesByBranch = (branchId) => api.get(`/api/queues/branch/${branchId}`);
export const getQueueSnapshot = (queueId) => api.get(`/api/queues/${queueId}`);
export const joinQueue = (data) => api.post('/api/queues/join', data);
export const serveNext = (queueId) => api.post(`/api/queues/${queueId}/serve-next`);
export const getQueueTokens = (queueId, status) =>
  api.get(`/api/queues/${queueId}/tokens`, { params: status ? { status } : {} });
export const getMyTokens = () => api.get('/api/queues/my-tokens');
