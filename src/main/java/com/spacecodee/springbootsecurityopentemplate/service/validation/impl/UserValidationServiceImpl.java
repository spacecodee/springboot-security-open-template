package com.spacecodee.springbootsecurityopentemplate.service.validation.impl;

import com.spacecodee.springbootsecurityopentemplate.data.vo.user.AdminUVO;
import com.spacecodee.springbootsecurityopentemplate.data.vo.user.DeveloperUVO;
import com.spacecodee.springbootsecurityopentemplate.data.vo.user.TechnicianUVO;
import com.spacecodee.springbootsecurityopentemplate.enums.RoleEnum;
import com.spacecodee.springbootsecurityopentemplate.exceptions.ExceptionShortComponent;
import com.spacecodee.springbootsecurityopentemplate.persistence.entity.UserEntity;
import com.spacecodee.springbootsecurityopentemplate.persistence.repository.IUserRepository;
import com.spacecodee.springbootsecurityopentemplate.service.validation.IUserValidationService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserValidationServiceImpl implements IUserValidationService {
    private final IUserRepository userRepository;
    private final ExceptionShortComponent exceptionComponent;

    @Override
    public boolean checkAndUpdateUserChanges(@NotNull Object userVO, @NotNull UserEntity existingUser) {
        boolean hasChanges = false;

        String username;
        String fullname;
        String lastname;

        switch (userVO) {
            case AdminUVO adminUVO -> {
                username = adminUVO.getUsername();
                fullname = adminUVO.getFullname();
                lastname = adminUVO.getLastname();
            }
            case DeveloperUVO developerUVO -> {
                username = developerUVO.getUsername();
                fullname = developerUVO.getFullname();
                lastname = developerUVO.getLastname();
            }
            case TechnicianUVO technicianUVO -> {
                username = technicianUVO.getUsername();
                fullname = technicianUVO.getFullname();
                lastname = technicianUVO.getLastname();
            }
            default -> throw new IllegalArgumentException("Unsupported user type");
        }

        if (!existingUser.getUsername().equals(username)) {
            existingUser.setUsername(username);
            hasChanges = true;
        }

        if (!existingUser.getFullname().equals(fullname)) {
            existingUser.setFullname(fullname);
            hasChanges = true;
        }

        if (!existingUser.getLastname().equals(lastname)) {
            existingUser.setLastname(lastname);
            hasChanges = true;
        }

        return hasChanges;
    }

    @Override
    public UserEntity validateUserUpdate(int id, String newUsername, String messagePrefix, String locale) {
        if (id <= 0) {
            throw this.exceptionComponent.invalidParameterException(messagePrefix + ".invalid.id", locale);
        }

        var existingUser = this.userRepository.findById(id)
                .orElseThrow(() -> this.exceptionComponent.doNotExistsByIdException(messagePrefix + ".not.exists.by.id",
                        locale));

        if (!existingUser.getUsername().equals(newUsername)) {
            validateUsername(newUsername, messagePrefix, locale);
        }

        return existingUser;
    }

    @Override
    public void validateLastUserByRole(String roleName, String messagePrefix, String locale) {
        var count = this.userRepository.countByRoleEntity_Name(RoleEnum.valueOf(roleName));
        if (count <= 1) {
            throw this.exceptionComponent.noDeletedException(messagePrefix + ".deleted.failed.last", locale);
        }
    }

    @Override
    public void validateUsername(String username, String messagePrefix, String locale) {
        if (this.userRepository.existsByUsername(username)) {
            throw this.exceptionComponent.alreadyExistsException(messagePrefix + ".exists.by.username", locale);
        }
    }
}