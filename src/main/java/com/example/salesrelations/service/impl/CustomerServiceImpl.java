package com.example.salesrelations.service.impl;

import java.util.List;

import com.example.salesrelations.mongo.enums.AuditAction;
import com.example.salesrelations.mongo.enums.AuditStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.salesrelations.dto.CustomerDTO;
import com.example.salesrelations.entity.Customer;
import com.example.salesrelations.exception.ResourceNotFoundException;
import com.example.salesrelations.mapper.CustomerMapper;
import com.example.salesrelations.mongo.service.AuditLogService;
import com.example.salesrelations.repository.CustomerRepository;
import com.example.salesrelations.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AuditLogService auditLogService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               CustomerMapper customerMapper,
                               AuditLogService auditLogService) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        String username = getCurrentUsername();

        try {
            Customer customer = customerMapper.toEntity(customerDTO);
            Customer savedCustomer = customerRepository.save(customer);

            auditLogService.saveLog(
                    AuditAction.CREATE_CUSTOMER,
                    username,
                    "Customer",
                    String.valueOf(savedCustomer.getId()),
                    AuditStatus.SUCCESS,
                    "Customer created successfully"
            );

            return customerMapper.toDTO(savedCustomer);

        } catch (Exception ex) {
            auditLogService.saveLog(
                    AuditAction.CREATE_CUSTOMER,
                    username,
                    "Customer",
                    "N/A",
                    AuditStatus.FAILED,
                    ex.getMessage()
            );
            throw ex;
        }
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        return customerMapper.toDTO(customer);
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        String username = getCurrentUsername();

        try {
            Customer existingCustomer = customerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

            existingCustomer.setCustomerName(customerDTO.getCustomerName());
            existingCustomer.setEmail(customerDTO.getEmail());
            existingCustomer.setPhone(customerDTO.getPhone());
            existingCustomer.setCity(customerDTO.getCity());

            Customer updatedCustomer = customerRepository.save(existingCustomer);

            auditLogService.saveLog(
                    AuditAction.UPDATE_CUSTOMER,
                    username,
                    "Customer",
                    String.valueOf(updatedCustomer.getId()),
                    AuditStatus.SUCCESS,
                    "Customer updated successfully"
            );

            return customerMapper.toDTO(updatedCustomer);

        } catch (Exception ex) {
            auditLogService.saveLog(
                    AuditAction.UPDATE_CUSTOMER,
                    username,
                    "Customer",
                    String.valueOf(id),
                    AuditStatus.FAILED,
                    ex.getMessage()
            );
            throw ex;
        }
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        String username = getCurrentUsername();

        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

            customerRepository.delete(customer);

            auditLogService.saveLog(
                    AuditAction.DELETE_CUSTOMER,
                    username,
                    "Customer",
                    String.valueOf(id),
                    AuditStatus.SUCCESS,
                    "Customer deleted successfully"
            );

        } catch (Exception ex) {
            auditLogService.saveLog(
                    AuditAction.DELETE_CUSTOMER,
                    username,
                    "Customer",
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