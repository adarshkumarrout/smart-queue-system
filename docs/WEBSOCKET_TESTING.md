# WebSocket Testing Guide

## Tools Required
- [Postman](https://www.postman.com/) (supports WebSocket)
- Browser DevTools Console
- [wscat](https://github.com/websockets/wscat)

## Quick Browser Test

Open browser console at http://localhost:3000 after logging in, then:

```javascript
// Already connected via the app — check console for "WS connected"
// To manually test:
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const c = new Client({
  webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
  connectHeaders: { Authorization: 'Bearer YOUR_JWT_TOKEN' },
  onConnect: () => {
    c.subscribe('/topic/queue/1', msg => console.log(JSON.parse(msg.body)));
  }
});
c.activate();
```

## Expected Messages

### /topic/queue/{queueId} — Queue Snapshot Update
```json
{
  "id": 1,
  "name": "General Queue",
  "waitingCount": 5,
  "estimatedWaitMinutes": 25,
  "activeStaff": 2,
  "status": "ACTIVE"
}
```

### /user/{userId}/queue/notification — Near Turn Alert
```json
{
  "type": "NEAR_TURN",
  "message": "Your token QUE-240115-0003 is 1 position(s) away!",
  "tokenNumber": "QUE-240115-0003",
  "position": 1
}
```

## Kafka Topics Verification

```bash
# List topics
docker exec smartqueue-kafka kafka-topics --list --bootstrap-server localhost:9092

# Watch join events
docker exec smartqueue-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic queue.join --from-beginning

# Watch served events  
docker exec smartqueue-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic queue.served --from-beginning
```
