package com.smartqueue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBranchRequest {
    @NotBlank private String name;
    @NotBlank private String address;
    private String city;
    @NotNull private Long businessId;
}
