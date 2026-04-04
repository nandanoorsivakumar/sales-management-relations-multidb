package com.example.salesrelations.service.impl;

import java.util.List;

import com.example.salesrelations.mongo.enums.AuditAction;
import com.example.salesrelations.mongo.enums.AuditStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.salesrelations.dto.OrderDTO;
import com.example.salesrelations.dto.OrderResponseDTO;
import com.example.salesrelations.entity.Customer;
import com.example.salesrelations.entity.Employee;
import com.example.salesrelations.entity.Order;
import com.example.salesrelations.exception.ResourceNotFoundException;
import com.example.salesrelations.mapper.OrderMapper;
import com.example.salesrelations.mongo.service.AuditLogService;
import com.example.salesrelations.repository.CustomerRepository;
import com.example.salesrelations.repository.EmployeeRepository;
import com.example.salesrelations.repository.OrderRepository;
import com.example.salesrelations.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderMapper orderMapper;
    private final AuditLogService auditLogService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CustomerRepository customerRepository,
                            EmployeeRepository employeeRepository,
                            OrderMapper orderMapper,
                            AuditLogService auditLogService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.orderMapper = orderMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderDTO orderDTO) {
        String username = getCurrentUsername();

        try {
            Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer not found with id: " + orderDTO.getCustomerId()));

            Employee employee = employeeRepository.findById(orderDTO.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found with id: " + orderDTO.getEmployeeId()));

            Order order = new Order();
            order.setOrderNumber(orderDTO.getOrderNumber());
            order.setOrderDate(orderDTO.getOrderDate());
            order.setAmount(orderDTO.getAmount());
            order.setStatus(orderDTO.getStatus());
            order.setCustomer(customer);
            order.setEmployee(employee);

            Order savedOrder = orderRepository.save(order);

            auditLogService.saveLog(
                    AuditAction.CREATE_ORDER,
                    username,
                    "Order",
                    String.valueOf(savedOrder.getId()),
                    AuditStatus.SUCCESS,
                    "Order created successfully"
            );

            return orderMapper.toResponseDTO(savedOrder);

        } catch (Exception ex) {
            auditLogService.saveLog(
                    AuditAction.CREATE_ORDER,
                    username,
                    "Order",
                    "N/A",
                    AuditStatus.FAILED,
                    ex.getMessage()
            );
            throw ex;
        }
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderMapper.toResponseDTOList(orderRepository.findAll());
    }

    @Override
    public List<OrderResponseDTO> getOrdersByCustomer(Long customerId) {
        return orderMapper.toResponseDTOList(orderRepository.findByCustomerId(customerId));
    }

    @Override
    public List<OrderResponseDTO> getOrdersByEmployee(Long employeeId) {
        return orderMapper.toResponseDTOList(orderRepository.findByEmployeeId(employeeId));
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderDTO orderDTO) {
        String username = getCurrentUsername();

        try {
            Order existingOrder = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

            Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer not found with id: " + orderDTO.getCustomerId()));

            Employee employee = employeeRepository.findById(orderDTO.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee not found with id: " + orderDTO.getEmployeeId()));

            existingOrder.setOrderNumber(orderDTO.getOrderNumber());
            existingOrder.setOrderDate(orderDTO.getOrderDate());
            existingOrder.setAmount(orderDTO.getAmount());
            existingOrder.setStatus(orderDTO.getStatus());
            existingOrder.setCustomer(customer);
            existingOrder.setEmployee(employee);

            Order updatedOrder = orderRepository.save(existingOrder);

            auditLogService.saveLog(
                    AuditAction.UPDATE_ORDER,
                    username,
                    "Order",
                    String.valueOf(updatedOrder.getId()),
                    AuditStatus.SUCCESS,
                    "Order updated successfully"
            );

            return orderMapper.toResponseDTO(updatedOrder);

        } catch (Exception ex) {
            auditLogService.saveLog(
                    AuditAction.UPDATE_ORDER,
                    username,
                    "Order",
                    String.valueOf(id),
                    AuditStatus.FAILED,
                    ex.getMessage()
            );
            throw ex;
        }
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        String username = getCurrentUsername();

        try {
            Order existingOrder = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

            orderRepository.delete(existingOrder);

            auditLogService.saveLog(
                    AuditAction.DELETE_ORDER,
                    username,
                    "Order",
                    String.valueOf(id),
                    AuditStatus.SUCCESS,
                    "Order deleted successfully"
            );

        } catch (Exception ex) {
            auditLogService.saveLog(
                    AuditAction.DELETE_ORDER,
                    username,
                    "Order",
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