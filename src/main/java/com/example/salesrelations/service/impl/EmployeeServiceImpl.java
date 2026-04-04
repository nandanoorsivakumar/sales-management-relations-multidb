package com.example.salesrelations.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.salesrelations.dto.EmployeeDTO;
import com.example.salesrelations.entity.Employee;
import com.example.salesrelations.exception.ResourceNotFoundException;
import com.example.salesrelations.mapper.EmployeeMapper;
import com.example.salesrelations.mongo.enums.AuditAction;
import com.example.salesrelations.mongo.enums.AuditStatus;
import com.example.salesrelations.mongo.service.AuditLogService;
import com.example.salesrelations.repository.EmployeeRepository;
import com.example.salesrelations.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final AuditLogService auditLogService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               EmployeeMapper employeeMapper,
                               AuditLogService auditLogService) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        String username = getCurrentUsername();

        try {

            if ("FAIL_TEST".equals(employeeDTO.getName())) {
                throw new RuntimeException("Manual failure for testing");
            }


                Employee employee = employeeMapper.toEntity(employeeDTO);
            Employee savedEmployee = employeeRepository.save(employee);

            auditLogService.saveLog(
                    AuditAction.CREATE_EMPLOYEE,
                    username,
                    "Employee",
                    String.valueOf(savedEmployee.getId()),
                    AuditStatus.SUCCESS,
                    "Employee created successfully"
            );

            return employeeMapper.toDTO(savedEmployee);


        } catch (Exception ex) {
            System.out.println("sivaaaaa save employee"+ ex.getMessage());
            auditLogService.saveLog(
                    AuditAction.CREATE_EMPLOYEE,
                    username,
                    "Employee",
                    "N/A",
                    AuditStatus.FAILED,
                    ex.getMessage()
            );
            throw ex;
        }
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDTO)
                .toList();
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        return employeeMapper.toDTO(employee);
    }

    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        String username = getCurrentUsername();

        try {
            Employee existingEmployee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

            existingEmployee.setName(employeeDTO.getName());
            existingEmployee.setDepartment(employeeDTO.getDepartment());
            existingEmployee.setSalary(employeeDTO.getSalary());

            Employee updatedEmployee = employeeRepository.save(existingEmployee);

            auditLogService.saveLog(
                    AuditAction.UPDATE_EMPLOYEE,
                    username,
                    "Employee",
                    String.valueOf(updatedEmployee.getId()),
                    AuditStatus.SUCCESS,
                    "Employee updated successfully"
            );

            return employeeMapper.toDTO(updatedEmployee);

        } catch (Exception ex) {
            System.out.println("sivaaaaa update employee");
            auditLogService.saveLog(
                    AuditAction.UPDATE_EMPLOYEE,
                    username,
                    "Employee",
                    String.valueOf(id),
                    AuditStatus.FAILED,
                    ex.getMessage()
            );
            throw ex;
        }
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        String username = getCurrentUsername();

        try {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

            employeeRepository.delete(employee);

            auditLogService.saveLog(
                    AuditAction.DELETE_EMPLOYEE,
                    username,
                    "Employee",
                    String.valueOf(id),
                    AuditStatus.SUCCESS,
                    "Employee deleted successfully"
            );

        } catch (Exception ex) {
            System.out.println("sivaaaaa delete employee");
            auditLogService.saveLog(
                    AuditAction.DELETE_EMPLOYEE,
                    username,
                    "Employee",
                    String.valueOf(id),
                    AuditStatus.FAILED,
                    ex.getMessage()
            );
            throw ex;
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }

        return "SYSTEM";
    }
}