package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.*;
import com.assignment.kam_lead_management_system.dto.InteractionRequestDTO;
import com.assignment.kam_lead_management_system.dto.InteractionResponseDTO;
import com.assignment.kam_lead_management_system.exception.KamCustomException;
import com.assignment.kam_lead_management_system.repository.LeadRepository;
import com.assignment.kam_lead_management_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final LeadRepository leadRepository;


    public InteractionResponseDTO createOrder(Long leadId, InteractionRequestDTO interactionRequestDTO) {

        InteractionResponseDTO interactionResponseDTO;

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new KamCustomException("Lead not found with id {}" + leadId, HttpStatus.NOT_FOUND));

        Kam kam = lead.getKam();

        // Validate the POC and ensure it belongs to the given Lead
        Poc poc = lead.getPocs().stream()
                .filter(p -> p.getId().equals(interactionRequestDTO.getPocId()))
                .findFirst()
                .orElseThrow(() -> new KamCustomException("POC is not associated with the lead", HttpStatus.NOT_FOUND));

        // Only create an order if the interaction type is "Order"
        if (InteractionType.ORDER.equals(interactionRequestDTO.getType())) {
            Order order = Order.builder()
                    .orderDetails(interactionRequestDTO.getOrderDetails())
                    .quantity(interactionRequestDTO.getQuantity())
                    .orderDate(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                    .lead(lead)
                    .kam(kam)
                    .poc(poc)
                    .build();

            orderRepository.save(order);

            //convert the lead status to converted if order is placed
            lead.setStatus(Status.CONVERTED);
            leadRepository.save(lead);

            // Convert the Order entity to OrderResponseDTO
            interactionResponseDTO = InteractionResponseDTO.builder()
                    .id(order.getId())
                    .orderDetails(order.getOrderDetails())
                    .quantity(order.getQuantity())
                    .date(order.getOrderDate())
                    .pocId(poc.getId())
                    .pocName(poc.getName())
                    .pocRole(poc.getRole())
                    .message("Order created successfully with " + poc.getName())
                    .build();
        } else {
            throw new KamCustomException("Only order interactions can be created using this endpoint", HttpStatus.FORBIDDEN);
        }

        return interactionResponseDTO;
    }

    public List<InteractionResponseDTO> getOrdersForLead(Long leadId) {
        List<Order> orders = orderRepository.findByLeadId(leadId);

        return orders.stream()
                .map(order -> InteractionResponseDTO.builder()
                        .orderId(order.getId())
                        .orderDetails(order.getOrderDetails())
                        .quantity(order.getQuantity())
                        .date(order.getOrderDate())
                        .build())
                .collect(Collectors.toList());
    }
}
