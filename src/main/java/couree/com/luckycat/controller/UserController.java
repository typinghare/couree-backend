package couree.com.luckycat.controller;

import couree.com.luckycat.core.annotation.SuccessMessage;
import couree.com.luckycat.dto.UserDto;
import couree.com.luckycat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
}
