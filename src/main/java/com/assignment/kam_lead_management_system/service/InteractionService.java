package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.*;
import com.assignment.kam_lead_management_system.dto.InteractionRequestDTO;
import com.assignment.kam_lead_management_system.dto.InteractionResponseDTO;
import com.assignment.kam_lead_management_system.repository.InteractionRepository;
import com.assignment.kam_lead_management_system.repository.LeadRepository;
import com.assignment.kam_lead_management_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final OrderRepository orderRepository;
    private final LeadRepository leadRepository;

    @Transactional
    public InteractionResponseDTO createInteraction(Long leadId, InteractionRequestDTO interactionRequestDTO) {

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        Kam kam = lead.getKam();

        // Validate the POC and ensure it belongs to the given Lead
        Poc poc = lead.getPocs().stream()
                .filter(p -> p.getId().equals(interactionRequestDTO.getPocId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("POC is not associated with the lead"));

        Interaction interaction = Interaction.builder()
                .type(interactionRequestDTO.getType())
                .details(interactionRequestDTO.getDetails())
                .date(LocalDateTime.now())
                .lead(lead)
                .kam(kam)
                .poc(poc)
                .build();

        if (InteractionType.ORDER.equals(interactionRequestDTO.getType())) {
            Order order = Order.builder()
                    .orderDetails(interactionRequestDTO.getOrderDetails())
                    .quantity(interactionRequestDTO.getQuantity())
                    .orderDate(LocalDateTime.now())
                    .lead(lead)
                    .kam(kam)
                    .build();

            interaction.setOrder(order);
            orderRepository.save(order);
        }

        interaction = interactionRepository.save(interaction);

        updateLeadStatusAndLastCallDateBasedOnInteractionType(lead, interactionRequestDTO.getType());

        // Build and return the response DTO
        return InteractionResponseDTO.builder()
                .id(interaction.getId())
                .type(interaction.getType())
                .details(interaction.getDetails())
                .date(interaction.getDate())
                .pocId(poc.getId())
                .pocName(poc.getName())
                .pocRole(poc.getRole())
                .message("Interaction recorded successfully with " + poc.getName())
                .build();
    }

    private void updateLeadStatusAndLastCallDateBasedOnInteractionType(Lead lead, InteractionType interactionType){
        if (interactionType == InteractionType.CALL) {
            lead.setLastCallDate(LocalDateTime.now());

            if (lead.getStatus() == Status.NEW) {
                lead.setStatus(Status.CONTACTED);
            }
        } else if (interactionType == InteractionType.ORDER) {
            //convert the lead status to converted if order is placed
            lead.setStatus(Status.CONVERTED);
        }
        leadRepository.save(lead);
    }

    public List<InteractionResponseDTO> getInteractionsForLead(Long leadId) {

        leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        List<Interaction> interactions = interactionRepository.findByLeadId(leadId);

        return interactions.stream()
                .map(interaction -> InteractionResponseDTO.builder()
                        .id(interaction.getId())
                        .type(interaction.getType())
                        .details(interaction.getDetails())
                        .date(interaction.getDate())
                        .pocId(interaction.getPoc().getId())
                        .pocName(interaction.getPoc().getName())
                        .pocRole(interaction.getPoc().getRole())
                        .orderId(interaction.getOrder() != null ? interaction.getOrder().getId() : null)
                        .orderDetails(interaction.getOrder() != null ? interaction.getOrder().getOrderDetails() : null)
                        .quantity(interaction.getOrder() != null ? interaction.getOrder().getQuantity() : null)
                        .build())
                .collect(Collectors.toList());
    }

}
