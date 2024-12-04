package com.dreamgames.backendengineeringcasestudy.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotEmpty
    private String username;
}
