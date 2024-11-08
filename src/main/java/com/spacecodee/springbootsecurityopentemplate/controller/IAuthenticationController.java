package com.spacecodee.springbootsecurityopentemplate.controller;

import com.spacecodee.springbootsecurityopentemplate.data.dto.user.details.UserDetailsDTO;
import com.spacecodee.springbootsecurityopentemplate.data.pojo.ApiResponseDataPojo;
import com.spacecodee.springbootsecurityopentemplate.data.pojo.ApiResponsePojo;
import com.spacecodee.springbootsecurityopentemplate.data.pojo.AuthenticationResponsePojo;
import com.spacecodee.springbootsecurityopentemplate.data.vo.auth.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface IAuthenticationController {

    @GetMapping("/validate-token")
    ResponseEntity<ApiResponseDataPojo<Boolean>> validate(@RequestHeader(name = "Accept-Language", required = false) String locale, @RequestParam String jwt);

    @PostMapping("/authenticate")
    ResponseEntity<ApiResponseDataPojo<AuthenticationResponsePojo>> authenticate(@RequestHeader(name = "Accept-Language", required = false) String locale, @RequestBody @Valid LoginUserVO request);

    @GetMapping("/profile")
    ResponseEntity<ApiResponseDataPojo<UserDetailsDTO>> profile(@RequestHeader(name = "Accept-Language", required = false) String locale);

    @PostMapping("/logout")
    ResponseEntity<ApiResponsePojo> logout(@RequestHeader(name = "Accept-Language", required = false) String locale, HttpServletRequest request);
}
