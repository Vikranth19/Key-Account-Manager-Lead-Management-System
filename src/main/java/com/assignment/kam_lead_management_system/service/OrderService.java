package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.*;
import com.assignment.kam_lead_management_system.dto.InteractionRequestDTO;
import com.assignment.kam_lead_management_system.dto.InteractionResponseDTO;
import com.assignment.kam_lead_management_system.exception.KamCustomException;
import com.assignment.kam_lead_management_system.repository.LeadRepository;
import com.assignment.kam_lead_management_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderService {

    private final OrderRepository orderRepository;

    private final LeadRepository leadRepository;

    /**
     * Creates a new order for a given lead, validates the POC, and updates the lead's status to 'CONVERTED' upon order creation.
     *
     * @param leadId The ID of the lead associated with the order.
     * @param interactionRequestDTO The DTO containing the order details (orderDetails, quantity, and associated POC).
     * @return InteractionResponseDTO containing the created order details and a success message.
     * @throws KamCustomException if the lead, POC is not found or if the interaction type is not "ORDER".
     */
    public InteractionResponseDTO createOrder(Long leadId, InteractionRequestDTO interactionRequestDTO) {
        log.info("Creating order for lead with ID: {}", leadId);

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
            log.debug("Creating order for lead with ID: {} and POC: {}", leadId, poc.getName());

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

            log.info("Order created successfully for lead with ID: {} and POC: {}", leadId, poc.getName());
        } else {
            log.error("Invalid interaction type: {}. Only 'ORDER' interactions are allowed.", interactionRequestDTO.getType());
            throw new KamCustomException("Only order interactions can be created using this endpoint", HttpStatus.FORBIDDEN);
        }

        return interactionResponseDTO;
    }

    /**
     * Retrieves all orders associated with a specific lead.
     *
     * @param leadId The ID of the lead for which orders need to be fetched.
     * @return A list of InteractionResponseDTO containing the details of all orders for the lead.
     * @throws KamCustomException if no orders are found for the lead.
     */
    public List<InteractionResponseDTO> getOrdersForLead(Long leadId) {
        log.info("Fetching orders for lead with ID: {}", leadId);

        List<Order> orders = orderRepository.findByLeadId(leadId);

        if (orders.isEmpty()) {
            log.warn("No orders found for lead with ID: {}", leadId);
        } else {
            log.info("Found {} orders for lead with ID: {}", orders.size(), leadId);
        }

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
