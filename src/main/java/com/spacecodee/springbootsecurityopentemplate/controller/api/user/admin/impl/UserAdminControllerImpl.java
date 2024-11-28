package com.spacecodee.springbootsecurityopentemplate.controller.api.user.admin.impl;

import com.spacecodee.springbootsecurityopentemplate.controller.api.user.admin.IUserAdminController;
import com.spacecodee.springbootsecurityopentemplate.controller.base.AbstractController;
import com.spacecodee.springbootsecurityopentemplate.data.pojo.ApiResponsePojo;
import com.spacecodee.springbootsecurityopentemplate.data.vo.user.AdminAVO;
import com.spacecodee.springbootsecurityopentemplate.data.vo.user.AdminUVO;
import com.spacecodee.springbootsecurityopentemplate.language.MessageUtilComponent;
import com.spacecodee.springbootsecurityopentemplate.service.core.user.admin.IUserAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-admin")
public class UserAdminControllerImpl extends AbstractController implements IUserAdminController {
    private final IUserAdminService userAdminService;

    public UserAdminControllerImpl(MessageUtilComponent messageUtilComponent,
                                   IUserAdminService userAdminService) {
        super(messageUtilComponent);
        this.userAdminService = userAdminService;
    }

    @Override
    public ResponseEntity<ApiResponsePojo> add(AdminAVO adminAVO, String locale) {
        this.userAdminService.add(adminAVO, locale);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(super.createResponse("admin.added.success", locale, HttpStatus.CREATED));
    }

    @Override
    public ResponseEntity<ApiResponsePojo> update(String locale, int id, @Valid AdminUVO adminAVO) {
        this.userAdminService.update(id, adminAVO, locale);
        return ResponseEntity.ok(super.createResponse("admin.updated.success", locale, HttpStatus.OK));
    }

    @Override
    public ResponseEntity<ApiResponsePojo> delete(String locale, int id) {
        this.userAdminService.delete(id, locale);
        return ResponseEntity.ok(super.createResponse("admin.deleted.success", locale, HttpStatus.OK));
    }
}