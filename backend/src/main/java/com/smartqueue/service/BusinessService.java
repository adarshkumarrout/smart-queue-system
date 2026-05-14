package com.smartqueue.service;

import com.smartqueue.dto.request.CreateBranchRequest;
import com.smartqueue.dto.request.CreateBusinessRequest;
import com.smartqueue.exception.BusinessException;
import com.smartqueue.exception.ResourceNotFoundException;
import com.smartqueue.model.Branch;
import com.smartqueue.model.Business;
import com.smartqueue.repository.BranchRepository;
import com.smartqueue.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BranchRepository branchRepository;

    @Transactional
    public Business createBusiness(CreateBusinessRequest req) {
        if (businessRepository.existsByName(req.getName()))
            throw new BusinessException("Business name already exists");
        return businessRepository.save(Business.builder()
                .name(req.getName()).description(req.getDescription())
                .ownerEmail(req.getOwnerEmail()).build());
    }

    @Transactional
    public Branch createBranch(CreateBranchRequest req) {
        Business biz = businessRepository.findById(req.getBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found"));
        return branchRepository.save(Branch.builder()
                .name(req.getName()).address(req.getAddress())
                .city(req.getCity()).business(biz).build());
    }

    public List<Branch> getBranchesByBusiness(Long businessId) {
        return branchRepository.findByBusinessId(businessId);
    }

    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }
}
