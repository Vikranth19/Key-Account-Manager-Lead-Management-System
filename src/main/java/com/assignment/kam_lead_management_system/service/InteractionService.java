package com.assignment.kam_lead_management_system.service;

import com.assignment.kam_lead_management_system.domain.*;
import com.assignment.kam_lead_management_system.dto.InteractionRequestDTO;
import com.assignment.kam_lead_management_system.dto.InteractionResponseDTO;
import com.assignment.kam_lead_management_system.exception.KamCustomException;
import com.assignment.kam_lead_management_system.repository.InteractionRepository;
import com.assignment.kam_lead_management_system.repository.LeadRepository;
import com.assignment.kam_lead_management_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class InteractionService {

    private final InteractionRepository interactionRepository;

    private final LeadRepository leadRepository;

    /**
     * Records a new interaction for a given lead, validates the interaction type, and updates the lead's status and last call date accordingly.
     *
     * @param leadId The ID of the lead associated with the interaction.
     * @param interactionRequestDTO The DTO containing interaction details (type, details, and associated POC).
     * @return InteractionResponseDTO containing the recorded interaction details and a success message.
     * @throws KamCustomException if the lead or POC is not found or if the interaction type is invalid.
     */
    @Transactional
    public InteractionResponseDTO createInteraction(Long leadId, InteractionRequestDTO interactionRequestDTO) {
        log.info("Creating interaction for lead with ID: {}", leadId);

        InteractionResponseDTO interactionResponseDTO;

        validateInteractionType(interactionRequestDTO);

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new KamCustomException("Lead not found", HttpStatus.NOT_FOUND));

        Kam kam = lead.getKam();

        // Validate the POC and ensure it belongs to the given Lead
        Poc poc = lead.getPocs().stream()
                .filter(p -> p.getId().equals(interactionRequestDTO.getPocId()))
                .findFirst()
                .orElseThrow(() -> new KamCustomException("POC is not associated with the lead", HttpStatus.NOT_FOUND));

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

        log.info("Interaction recorded successfully for lead with ID: {} with POC: {}", leadId, poc.getName());

        return interactionResponseDTO;
    }

    /**
     * Validates the interaction type to ensure only valid types are processed.
     *
     * @param interactionRequestDTO The DTO containing interaction details.
     * @throws KamCustomException if the interaction type is not allowed.
     */
    private void validateInteractionType(InteractionRequestDTO interactionRequestDTO) {
        log.debug("Validating interaction type: {}", interactionRequestDTO.getType());

        if (interactionRequestDTO.getType() == InteractionType.ORDER) {
            throw new KamCustomException("Only non-order interactions can be created using this endpoint", HttpStatus.FORBIDDEN);
        }

    }

    /**
     * Updates the lead's status and last call date based on the interaction type.
     *
     * @param lead The lead whose status and last call date need to be updated.
     * @param interactionType The type of the interaction that triggers the update.
     */
    private void updateLeadStatusAndLastCallDateBasedOnInteractionType(Lead lead, InteractionType interactionType){
        log.debug("Updating lead status and last call date based on interaction type: {}", interactionType);

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

    /**
     * Retrieves all interactions associated with a specific lead.
     *
     * @param leadId The ID of the lead for which interactions need to be fetched.
     * @return A list of InteractionResponseDTO containing details of all interactions for the lead.
     * @throws KamCustomException if the lead is not found.
     */
    public List<InteractionResponseDTO> getInteractionsForLead(Long leadId) {
        log.info("Fetching all interactions for lead with ID: {}", leadId);

        leadRepository.findById(leadId)
                .orElseThrow(() -> new KamCustomException("Lead not found", HttpStatus.NOT_FOUND));

        List<Interaction> interactions = interactionRepository.findByLeadId(leadId);

        log.info("Found {} interactions for lead with ID: {}", interactions.size(), leadId);

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
