package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.InteractionType;
import com.assignment.kam_lead_management_system.domain.Kam;
import com.assignment.kam_lead_management_system.domain.Lead;
import com.assignment.kam_lead_management_system.domain.Order;
import com.assignment.kam_lead_management_system.dto.InteractionRequestDTO;
import com.assignment.kam_lead_management_system.dto.InteractionResponseDTO;
import com.assignment.kam_lead_management_system.repository.LeadRepository;
import com.assignment.kam_lead_management_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final LeadRepository leadRepository;


    public InteractionResponseDTO createOrder(Long leadId, InteractionRequestDTO interactionRequestDTO) {

        InteractionResponseDTO interactionResponseDTO = null;
        try {
            Lead lead = leadRepository.findById(leadId)
                    .orElseThrow(() -> new RuntimeException("Lead not found with id {}" + leadId));

            Kam kam = lead.getKam();

            // Only create an order if the interaction type is "Order"
            if (InteractionType.ORDER.equals(interactionRequestDTO.getType())) {
                Order order = Order.builder()
                        .orderDetails(interactionRequestDTO.getOrderDetails())
                        .quantity(interactionRequestDTO.getQuantity())
                        .orderDate(LocalDateTime.now())
                        .lead(lead)
                        .kam(kam)
                        .build();

                orderRepository.save(order);

                // Convert the Order entity to OrderResponseDTO
                interactionResponseDTO = InteractionResponseDTO.builder()
                        .id(order.getId())
                        .orderDetails(order.getOrderDetails())
                        .quantity(order.getQuantity())
                        .date(order.getOrderDate())
                        .leadId(lead.getId())
                        .kamId(kam.getId())
                        .message("Order created successfully")
                        .build();
            } else {
                throw new BadRequestException("Only order interactions can be created using this endpoint");
            }
        } catch (Exception e) {
            System.out.println("exception");
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
