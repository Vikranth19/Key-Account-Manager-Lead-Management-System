package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.*;
import com.assignment.kam_lead_management_system.dto.InteractionRequestDTO;
import com.assignment.kam_lead_management_system.dto.InteractionResponseDTO;
import com.assignment.kam_lead_management_system.repository.InteractionRepository;
import com.assignment.kam_lead_management_system.repository.LeadRepository;
import com.assignment.kam_lead_management_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

        InteractionResponseDTO interactionResponseDTO = null;

        try {

            if (interactionRequestDTO.getType() == InteractionType.ORDER) {
                throw new BadRequestException("Only non-order interactions can be created using this endpoint");
            }

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
                    .date(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                    .lead(lead)
                    .kam(kam)
                    .poc(poc)
                    .build();


            interaction = interactionRepository.save(interaction);

            updateLeadStatusAndLastCallDateBasedOnInteractionType(lead, interactionRequestDTO.getType());

            // Build and return the response DTO
            interactionResponseDTO = InteractionResponseDTO.builder()
                    .id(interaction.getId())
                    .type(interaction.getType())
                    .details(interaction.getDetails())
                    .date(interaction.getDate())
                    .pocId(poc.getId())
                    .pocName(poc.getName())
                    .pocRole(poc.getRole())
                    .message("Interaction recorded successfully with " + poc.getName())
                    .build();
        } catch (Exception e){
            System.out.println("Exception");
        }

        return interactionResponseDTO;
    }

    private void updateLeadStatusAndLastCallDateBasedOnInteractionType(Lead lead, InteractionType interactionType){
        if (interactionType == InteractionType.CALL) {
            lead.setLastCallDate(Instant.now().truncatedTo(ChronoUnit.SECONDS));

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
                        .build())
                .collect(Collectors.toList());
    }

}
