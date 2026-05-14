package com.smartqueue.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBusinessRequest {
    @NotBlank private String name;
    @NotBlank private String description;
    @NotBlank private String ownerEmail;
}
