package com.servicesphere.users.controller;

import com.servicesphere.common.exception.ResourceNotFoundException;
import com.servicesphere.users.dto.UserDTO;
import com.servicesphere.users.entity.User;
import com.servicesphere.users.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
        return ResponseEntity.ok(mapToDTO(user));
    }

    private UserDTO mapToDTO(User u) {
        return new UserDTO(
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getRole() != null ? u.getRole().getName() : "Employee",
                u.getDepartment() != null ? u.getDepartment().getName() : null,
                u.getStatus()
        );
    }
}

