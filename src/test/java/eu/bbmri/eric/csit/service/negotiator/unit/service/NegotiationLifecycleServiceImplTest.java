package eu.bbmri.eric.csit.service.negotiator.unit.service;

import eu.bbmri.eric.csit.service.negotiator.NegotiatorApplication;
import eu.bbmri.eric.csit.service.negotiator.database.model.NegotiationEvent;
import eu.bbmri.eric.csit.service.negotiator.database.model.NegotiationState;
import eu.bbmri.eric.csit.service.negotiator.dto.negotiation.NegotiationCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.dto.negotiation.NegotiationDTO;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotFoundException;
import eu.bbmri.eric.csit.service.negotiator.exceptions.WrongRequestException;
import eu.bbmri.eric.csit.service.negotiator.integration.api.v3.TestUtils;
import eu.bbmri.eric.csit.service.negotiator.service.NegotiationLifecycleServiceImpl;
import eu.bbmri.eric.csit.service.negotiator.service.NegotiationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = NegotiatorApplication.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NegotiationLifecycleServiceImplTest {

  final NegotiationState INITIAL_STATE = NegotiationState.SUBMITTED;
  final NegotiationState SECOND_STATE = NegotiationState.APPROVED;
  final NegotiationEvent TRANSITION_EVENT = NegotiationEvent.APPROVE;
  final String NEGOTIATION_ID = "negotiationID-1";
  @Autowired
  NegotiationLifecycleServiceImpl negotiationStateService;
  @Autowired
  NegotiationService negotiationService;
  @Autowired
  JpaStateMachineRepository jpaStateMachineRepository;

  @AfterEach
  void tearDown() {
    negotiationStateService.removeStateMachine(NEGOTIATION_ID);
  }

  @Test
  void getStateForNonExistentNegotiationThrowsIllegalArgException() {
    negotiationStateService.initializeTheStateMachine(NEGOTIATION_ID);
    assertThrows(
        EntityNotFoundException.class,
        () -> negotiationStateService.getCurrentState("fake")
    );
  }

  @Test
  public void getStateReturnsInitialValueAfterInitializingStateMachine() {
    negotiationStateService.initializeTheStateMachine(NEGOTIATION_ID);
    assertEquals(INITIAL_STATE, negotiationStateService.getCurrentState(NEGOTIATION_ID));
  }

  @Test
  public void getPossibleEventsForExistingNegotiation() {
    negotiationStateService.initializeTheStateMachine(NEGOTIATION_ID);
    assertEquals(INITIAL_STATE, negotiationStateService.getCurrentState(NEGOTIATION_ID));
    assertEquals(Set.of(NegotiationEvent.APPROVE, NegotiationEvent.DECLINE),
        negotiationStateService.getPossibleEvents(NEGOTIATION_ID));
  }

  @Test
  public void getPossibleEventsForNonExistingNegotiation() {
    assertThrows(
        EntityNotFoundException.class,
        () -> negotiationStateService.getPossibleEvents("fakeId")
    );
  }

  @Test
  public void sendValidEventReturnsNewStateAndIsEqualToTheCurrentState() {
    String negotiationID = "negotiationID-1";
    negotiationStateService.initializeTheStateMachine(negotiationID);
    assertEquals(SECOND_STATE, negotiationStateService.sendEvent(negotiationID, TRANSITION_EVENT));
    assertEquals(SECOND_STATE, negotiationStateService.getCurrentState(negotiationID));
  }

  @Test
  public void sendEventForNonExistentNegotiationThrowException() {
    assertThrows(
        EntityNotFoundException.class,
        () -> negotiationStateService.sendEvent("fakeId", TRANSITION_EVENT)
    );
  }

  @Test
  public void testSendInvalidEventForNegotiation() {
    String negotiationID = "negotiationID-1";
    negotiationStateService.initializeTheStateMachine(negotiationID);
    assertThrows(
        WrongRequestException.class,
        () -> negotiationStateService.sendEvent(negotiationID, NegotiationEvent.ABANDON)
    );
  }

  @Test
  void stateMachineChangeUpdatesNegotiationDTO() throws IOException {
    NegotiationCreateDTO negotiationCreateDTO = TestUtils.createNegotiation(Set.of("request-2"));
    NegotiationDTO negotiationDTO = negotiationService.create(negotiationCreateDTO, 101L);
    assertEquals("SUBMITTED",
        negotiationService.findById(negotiationDTO.getId(), false).getStatus());
    negotiationStateService.sendEvent(negotiationDTO.getId(), NegotiationEvent.APPROVE);
    assertEquals("APPROVED",
        negotiationService.findById(negotiationDTO.getId(), false).getStatus());
  }

//  @Test
//  void resourceStateMachineChangeUpdatesNegotiationDTO() throws IOException {
//    NegotiationCreateDTO negotiationCreateDTO = TestUtils.createNegotiation(Set.of("request-2"));
//    NegotiationDTO negotiationDTO = negotiationService.create(negotiationCreateDTO, 101L);
//    assertEquals("CONTACTED",
//        negotiationService.findById(negotiationDTO.getId(), false).getResourceStatus()
//            .get("biobank:1:collection:2").textValue());
//    negotiationStateService.sendEvent(negotiationDTO.getId(), "biobank:1:collection:2",
//        NegotiationEvent.APPROVE);
//    assertEquals("APPROVED",
//        negotiationService.findById(negotiationDTO.getId(), false).getResourceStatus()
//            .get("biobank:1:collection:2").textValue());
//  }
}
