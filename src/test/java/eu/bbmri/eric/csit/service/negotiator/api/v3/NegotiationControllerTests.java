package eu.bbmri.eric.csit.service.negotiator.api.v3;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.bbmri.eric.csit.service.negotiator.NegotiatorApplication;
import eu.bbmri.eric.csit.service.negotiator.api.controller.v3.NegotiationController;
import eu.bbmri.eric.csit.service.negotiator.api.dto.negotiation.NegotiationCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.api.dto.request.RequestCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.database.model.Negotiation;
import eu.bbmri.eric.csit.service.negotiator.database.model.Request;
import eu.bbmri.eric.csit.service.negotiator.database.repository.NegotiationRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.RequestRepository;
import eu.bbmri.eric.csit.service.negotiator.service.RequestService;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = NegotiatorApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class NegotiationControllerTests {

  private static final String REQUESTS_ENDPOINT = "/v3/negotiations";
  private static final String CORRECT_TOKEN_VALUE = "researcher";
  private static final String FORBIDDEN_TOKEN_VALUE = "unknown";
  private static final String UNAUTHORIZED_TOKEN_VALUE = "unauthorized";

  @Autowired
  private WebApplicationContext context;
  @Autowired
  private NegotiationController negotiationController;
  @Autowired
  private NegotiationRepository negotiationRepository;
  @Autowired
  private RequestRepository requestRepository;
  @Autowired
  private ModelMapper modelMapper;
  @Autowired
  private RequestService requestService;

  private MockMvc mockMvc;
  private Request testRequest;

  @BeforeEach
  public void before() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    testRequest = createQueryEntity();
  }

  @AfterEach
  public void after() {
    requestRepository.deleteAll();
    negotiationRepository.deleteAll();
  }

  private Request createQueryEntity() {
    RequestCreateDTO queryRequest = TestUtils.createRequest(false);
    return requestService.create(queryRequest);
  }

  @Test
  public void testGetAll_Unauthorized_whenNoAuth() throws Exception {
    TestUtils.checkErrorResponse(
        mockMvc, HttpMethod.GET, "", status().isUnauthorized(), anonymous(), REQUESTS_ENDPOINT);
  }

  @Test
  public void testGetAll_Unauthorized_whenBasicAuth() throws Exception {
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.GET,
        "",
        status().isUnauthorized(),
        httpBasic("researcher", "wrong_pass"),
        REQUESTS_ENDPOINT);
  }

  @Test
  public void testGetAll_Unauthorized_whenInvalidToken() throws Exception {
    TestUtils.checkErrorResponse(
        mockMvc, HttpMethod.GET, "", status().isUnauthorized(), "", REQUESTS_ENDPOINT);
  }

  @Test
  public void testGetAll_Forbidden_whenNoPermission() throws Exception {
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.GET,
        "",
        status().isForbidden(),
        FORBIDDEN_TOKEN_VALUE,
        REQUESTS_ENDPOINT);
  }

  @Test
  public void testGetAll_Forbidden_whenBasicAuth() throws Exception {
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.GET,
        "",
        status().isForbidden(),
        httpBasic("directory", "directory"),
        REQUESTS_ENDPOINT);
  }

  @Test
  public void testGetAll_Ok() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    Negotiation negotiation = modelMapper.map(request, Negotiation.class);
    negotiation = negotiationRepository.save(negotiation);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUESTS_ENDPOINT)
                .header("Authorization", "Bearer %s".formatted(CORRECT_TOKEN_VALUE)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id", is(negotiation.getId())));
  }

  @Test
  public void testGetAll_Ok_whenEmptyResult() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(REQUESTS_ENDPOINT)
                .header("Authorization", "Bearer %s".formatted(CORRECT_TOKEN_VALUE)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json("[]"));
  }

  @Test
  public void testGetById_Unauthorized_whenNoAuth() throws Exception {
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.GET,
        "",
        status().isUnauthorized(),
        anonymous(),
        "%s/1".formatted(REQUESTS_ENDPOINT));
  }

  @Test
  public void testGetById_Unauthorized_whenBasicAuth() throws Exception {
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.GET,
        "",
        status().isUnauthorized(),
        httpBasic("researcher", "wrong_pass"),
        "%s/1".formatted(REQUESTS_ENDPOINT));
  }

  @Test
  public void testGetById_Forbidden_whenNoPermissionAuth() throws Exception {
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.GET,
        "",
        status().isForbidden(),
        FORBIDDEN_TOKEN_VALUE,
        "%s/1".formatted(REQUESTS_ENDPOINT));
  }

  @Test
  public void testGetById_Forbidden_whenBasicAuth() throws Exception {
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.GET,
        "",
        status().isForbidden(),
        httpBasic("directory", "directory"),
        "%s/1".formatted(REQUESTS_ENDPOINT));
  }

  @Test
  public void testGetById_NotFound_whenWrongId() throws Exception {
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.GET,
        "",
        status().isNotFound(),
        CORRECT_TOKEN_VALUE,
        "%s/-1".formatted(REQUESTS_ENDPOINT));
  }

  @Test
  public void testGetById_Ok_whenCorrectId() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    Negotiation entity = modelMapper.map(request, Negotiation.class);
    entity = negotiationRepository.save(entity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("%s/%s".formatted(REQUESTS_ENDPOINT, entity.getId()))
                .header("Authorization", "Bearer %s".formatted(CORRECT_TOKEN_VALUE)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(entity.getId())));
  }

  @Test
  public void testCreate_Unauthorized_whenNoAuth() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isUnauthorized(),
        anonymous(),
        REQUESTS_ENDPOINT);
  }

  @Test
  public void testCreate_Unauthorized_whenWrongAuth() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isUnauthorized(),
        httpBasic("researcher", "wrong_pass"),
        REQUESTS_ENDPOINT);
  }

  @Test
  public void testCreate_Forbidden_whenNoPermission() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isForbidden(),
        httpBasic("directory", "directory"),
        REQUESTS_ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenRequests_IsMissing() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    request.setRequests(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        CORRECT_TOKEN_VALUE,
        REQUESTS_ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenRequests_IsEmpty() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    request.setRequests(Collections.emptySet());
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        CORRECT_TOKEN_VALUE,
        REQUESTS_ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenSomeRequests_IsNotFound() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    request.setRequests(Set.of("unknownn"));
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        CORRECT_TOKEN_VALUE,
        REQUESTS_ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenQuery_IsAlreadyAssignedToAnotherRequest() throws Exception {
    NegotiationCreateDTO createRequest = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    // The data source to be updated
    Negotiation negotiationEntity = modelMapper.map(createRequest, Negotiation.class);
    negotiationRepository.save(negotiationEntity);

    testRequest.setNegotiation(negotiationEntity);
    requestRepository.save(testRequest);
    assertEquals(1, negotiationRepository.count());

    // Negotiation body with updated values
    NegotiationCreateDTO updateRequest = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    String requestBody = TestUtils.jsonFromRequest(updateRequest);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(REQUESTS_ENDPOINT)
                .header("Authorization", "Bearer %s".formatted(CORRECT_TOKEN_VALUE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    assertEquals(1, negotiationRepository.count());
  }

  @Test
  @Order(1)
  public void testCreate_Ok() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false, Set.of(testRequest.getId()));
    String requestBody = TestUtils.jsonFromRequest(request);
    long previousRequestCount = negotiationRepository.count();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(URI.create(REQUESTS_ENDPOINT))
                .header("Authorization", "Bearer %s".formatted(CORRECT_TOKEN_VALUE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isString())
        .andExpect(jsonPath("$.payload").isString())
        .andReturn();

    assertEquals(negotiationRepository.count(), previousRequestCount + 1);
  }

  @Test
  public void testUpdate_Unauthorized_whenNoAuth() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.PUT,
        request,
        status().isUnauthorized(),
        anonymous(),
        "%s/1".formatted(REQUESTS_ENDPOINT));
  }

  @Test
  public void testUpdate_Unauthorized_whenWrongAuth() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.PUT,
        request,
        status().isUnauthorized(),
        httpBasic("admin", "wrong_pass"),
        "%s/1".formatted(REQUESTS_ENDPOINT));
  }

  @Test
  public void testUpdate_Forbidden_whenNoPermission() throws Exception {
    NegotiationCreateDTO request = TestUtils.createNegotiation(false,
        Set.of(testRequest.getId()));
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.PUT,
        request,
        status().isForbidden(),
        httpBasic("directory", "directory"),
        "%s/1".formatted(REQUESTS_ENDPOINT));
  }

  @Test
  public void testUpdate_BadRequest_whenQueryIsAlreadyAssignedToAnotherRequest() throws Exception {
    // Create the negotiation that has the assigned request
    Negotiation negotiationEntityWithQuery =
        modelMapper.map(TestUtils.createNegotiation(false, null), Negotiation.class);
    negotiationRepository.save(negotiationEntityWithQuery);
    Request firstRequest = createQueryEntity();
    firstRequest.setNegotiation(negotiationEntityWithQuery);
    requestRepository.save(firstRequest);

    // Create the negotiation to update
    Negotiation negotiationEntityUpdate =
        modelMapper.map(TestUtils.createNegotiation(false, null), Negotiation.class);
    negotiationRepository.save(negotiationEntityUpdate);
    Request secondRequest = createQueryEntity();
    secondRequest.setNegotiation(negotiationEntityUpdate);
    requestRepository.save(secondRequest);

    // Negotiation body with updated values and request already assigned
    NegotiationCreateDTO request =
        TestUtils.createNegotiation(true,
            Set.of(firstRequest.getId(), secondRequest.getId()));
    String requestBody = TestUtils.jsonFromRequest(request);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(
                    "%s/%s".formatted(REQUESTS_ENDPOINT, negotiationEntityUpdate.getId()))
                .header("Authorization", "Bearer %s".formatted(CORRECT_TOKEN_VALUE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  public void testUpdate_Ok_whenChangeTitle() throws Exception {
    // The data source to be updated
    Negotiation negotiationEntity =
        modelMapper.map(
            TestUtils.createNegotiation(false, Set.of(testRequest.getId())),
            Negotiation.class);
    negotiationRepository.save(negotiationEntity);

    testRequest.setNegotiation(negotiationEntity);
    requestRepository.save(testRequest);

    // Negotiation body with updated values
    NegotiationCreateDTO request = TestUtils.createNegotiation(true,
        Set.of(testRequest.getId()));
    String requestBody = TestUtils.jsonFromRequest(request);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(
                    "%s/%s".formatted(REQUESTS_ENDPOINT, negotiationEntity.getId()))
                .header("Authorization", "Bearer %s".formatted(CORRECT_TOKEN_VALUE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isNoContent())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
  @Test
  public void testNoNegotiationsAreReturned() throws Exception {
    mockMvc
            .perform(
                    MockMvcRequestBuilders.get("%s?userRole=RESEARCHER".formatted(REQUESTS_ENDPOINT))
                            .header("Authorization", "Bearer %s".formatted(CORRECT_TOKEN_VALUE)))
            .andExpect(status().isOk()).andExpect(content().json("[]"));
  }
}
