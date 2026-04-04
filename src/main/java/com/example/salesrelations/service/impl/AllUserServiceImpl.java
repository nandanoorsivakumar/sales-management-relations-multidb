package com.example.salesrelations.service.impl;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.salesrelations.dto.AllUserDTO;
import com.example.salesrelations.entity.AllUser;
import com.example.salesrelations.exception.BadRequestException;
import com.example.salesrelations.exception.DuplicateResourceException;
import com.example.salesrelations.exception.ResourceNotFoundException;
import com.example.salesrelations.repository.AllUserRepository;
import com.example.salesrelations.service.AllUserService;

@Service
public class AllUserServiceImpl implements AllUserService {

    private final AllUserRepository allUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AllUserServiceImpl(AllUserRepository allUserRepository,
                              PasswordEncoder passwordEncoder) {
        this.allUserRepository = allUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public AllUserDTO createUser(AllUserDTO allUserDTO) {
        if (allUserRepository.existsByUsername(allUserDTO.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + allUserDTO.getUsername());
        }

        if (!allUserDTO.getRole().equalsIgnoreCase("ADMIN") &&
                !allUserDTO.getRole().equalsIgnoreCase("USER")) {
            throw new BadRequestException("Role must be ADMIN or USER");
        }

        AllUser user = new AllUser();
        user.setUsername(allUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode(allUserDTO.getPassword()));
        user.setRole(allUserDTO.getRole().toUpperCase());
        user.setEnabled(allUserDTO.getEnabled());
        user.setEmployeeId(allUserDTO.getEmployeeId());

        AllUser savedUser = allUserRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<AllUserDTO> getAllUsers() {
        return allUserRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public AllUserDTO getUserById(Long id) {
        AllUser user = allUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDTO(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public AllUserDTO updateUserRole(Long id, String role) {
        AllUser user = allUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("USER")) {
            throw new BadRequestException("Role must be ADMIN or USER");
        }

        user.setRole(role.toUpperCase());
        AllUser updatedUser = allUserRepository.save(user);
        return mapToDTO(updatedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public AllUserDTO updateUserStatus(Long id, Boolean enabled) {
        AllUser user = allUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(enabled);
        AllUser updatedUser = allUserRepository.save(user);
        return mapToDTO(updatedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public AllUserDTO changePassword(Long id, String newPassword) {
        AllUser user = allUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setPassword(passwordEncoder.encode(newPassword));
        AllUser updatedUser = allUserRepository.save(user);
        return mapToDTO(updatedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        AllUser user = allUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        allUserRepository.delete(user);
    }

    private AllUserDTO mapToDTO(AllUser user) {
        return new AllUserDTO(
                user.getId(),
                user.getUsername(),
                null,
                user.getRole(),
                user.getEnabled(),
                user.getEmployeeId()
        );
    }
}