package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @Validated
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getUsers();
    }

    @Validated
    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("addUser attempt {}", user);
        userService.createUser(user);
        log.info("addUser {} success", user);
        return user;
    }

    @Validated
    @PatchMapping("/{id}")
    public User updateUser(@PathVariable @Positive Long id, @RequestBody User user) {
        if (user.getId() != null && id != user.getId()){
            throw  new ObjectNotFoundException("");
        }
        userService.updateUser(id, user);
        log.info("updateUser attempt {}", id);
        return userService.getUserById(id);
    }

    @Validated
    @GetMapping("/{id}")
    public User getUserById(@PathVariable @Positive Long id) {
        log.info("attempt to get user by id {}", id);
        return userService.getUserById(id);
    }

    @Validated
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable @Positive Long id) {
        userService.deleteUserById(id);
    }
}
