package com.alexmegremis.planningpokerapi.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateDTO {
    private String id;
    private String name;
    private String password;
}
