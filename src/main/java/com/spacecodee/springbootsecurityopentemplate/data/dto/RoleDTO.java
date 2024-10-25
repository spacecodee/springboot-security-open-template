package com.spacecodee.springbootsecurityopentemplate.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link com.spacecodee.ticklyspace.persistence.entity.RoleEntity}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RoleDTO(Integer id, @NotNull String name) implements Serializable {
}