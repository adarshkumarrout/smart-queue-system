import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

let stompClient = null;
let subscriptions = {};

export function connectWebSocket(userId, onQueueUpdate, onNotification) {
  // Don't reconnect if already connected
  if (stompClient && stompClient.connected) return stompClient;

  if (stompClient) {
    try { stompClient.deactivate(); } catch (e) {}
    stompClient = null;
  }

  stompClient = new Client({
    // Explicit full URL to backend — never relative
    webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
    reconnectDelay: 10000,
    onConnect: () => {
      console.log('WS connected');

      subscriptions['notif'] = stompClient.subscribe(
        `/user/${userId}/queue/notification`,
        (msg) => onNotification?.(JSON.parse(msg.body))
      );
      subscriptions['update'] = stompClient.subscribe(
        `/user/${userId}/queue/token-update`,
        (msg) => onQueueUpdate?.(JSON.parse(msg.body))
      );
    },
    onDisconnect: () => {
      subscriptions = {};
    },
    onStompError: (frame) => {
      console.warn('STOMP error:', frame.headers?.message);
    },
  });

  stompClient.activate();
  return stompClient;
}

export function subscribeToQueue(queueId, callback) {
  if (!stompClient?.connected) return null;
  const key = `queue_${queueId}`;
  if (subscriptions[key]) {
    try { subscriptions[key].unsubscribe(); } catch (e) {}
  }
  subscriptions[key] = stompClient.subscribe(
    `/topic/queue/${queueId}`,
    (msg) => callback(JSON.parse(msg.body))
  );
  return subscriptions[key];
}

export function disconnectWebSocket() {
  if (stompClient) {
    try { stompClient.deactivate(); } catch (e) {}
    stompClient = null;
    subscriptions = {};
  }
}