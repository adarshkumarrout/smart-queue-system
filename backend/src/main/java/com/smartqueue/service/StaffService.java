package com.smartqueue.service;

import com.smartqueue.dto.response.StaffResponse;
import com.smartqueue.exception.BusinessException;
import com.smartqueue.exception.ResourceNotFoundException;
import com.smartqueue.model.*;
import com.smartqueue.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final QueueRepository queueRepository;

    @Transactional
    public StaffResponse addStaff(Long userId, Long branchId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        if (staffRepository.findByUserId(userId).isPresent())
            throw new BusinessException("User is already a staff member");

        user.setRole(User.UserRole.STAFF);
        userRepository.save(user);

        Staff staff = Staff.builder().user(user).branch(branch).build();
        return toResponse(staffRepository.save(staff));
    }

    @Transactional
    public StaffResponse assignToQueue(Long staffId, Long queueId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));
        if (!queue.getAssignedStaff().contains(staff))
            queue.getAssignedStaff().add(staff);
        queueRepository.save(queue);
        return toResponse(staff);
    }

    public List<StaffResponse> getStaffByBranch(Long branchId) {
        return staffRepository.findByBranchId(branchId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public StaffResponse updateStatus(Long staffId, Staff.StaffStatus status) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));
        staff.setStatus(status);
        return toResponse(staffRepository.save(staff));
    }

    public StaffResponse toResponse(Staff s) {
        return StaffResponse.builder()
                .id(s.getId()).userId(s.getUser().getId())
                .fullName(s.getUser().getFullName())
                .username(s.getUser().getUsername())
                .branchId(s.getBranch().getId()).branchName(s.getBranch().getName())
                .status(s.getStatus())
                .tokensServedToday(s.getTokensServedToday())
                .avgHandlingTimeMinutes(s.getAvgHandlingTimeMinutes())
                .build();
    }
}
