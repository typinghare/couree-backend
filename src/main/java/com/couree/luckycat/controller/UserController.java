package com.couree.luckycat.controller;

import com.couree.luckycat.dto.UserDto;
import com.couree.luckycat.glacier.annotation.Controller;
import com.couree.luckycat.glacier.app.responder.annotation.SuccessMessage;
import com.couree.luckycat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}/")
    @SuccessMessage("Successfully get the name of the user.")
    public UserDto getUserInfo(@PathVariable Long userId) {
        return userService.getUserDto(userId);
    }

    @GetMapping("/name")
    @SuccessMessage("Successfully get the name.")
    public String name() {
        return "James Chan";
    }
}
