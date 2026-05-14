package com.smartqueue.util;

import com.smartqueue.model.Queue;
import com.smartqueue.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final StaffRepository staffRepository;

    /**
     * estimated_time = (people_ahead / active_staff) * avg_service_time
     */
    public int estimateWaitTime(Queue queue, long peopleAhead) {
        long activeStaff = staffRepository.countActiveStaffByQueueId(queue.getId());
        if (activeStaff == 0) activeStaff = 1; // avoid divide by zero
        int avgServiceTime = queue.getAvgServiceTimeMinutes();
        return (int) Math.ceil((double) peopleAhead / activeStaff * avgServiceTime);
    }

    public int estimateWaitTimeForPosition(Queue queue, int position) {
        return estimateWaitTime(queue, position);
    }
}
