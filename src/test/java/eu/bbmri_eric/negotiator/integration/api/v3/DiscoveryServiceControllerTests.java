package eu.bbmri_eric.negotiator.integration.api.v3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import eu.bbmri_eric.negotiator.NegotiatorApplication;
import eu.bbmri_eric.negotiator.api.controller.v3.DiscoverServiceController;
import eu.bbmri_eric.negotiator.database.model.DiscoveryService;
import eu.bbmri_eric.negotiator.database.repository.DiscoveryServiceRepository;
import eu.bbmri_eric.negotiator.dto.discoveryservice.DiscoveryServiceCreateDTO;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = NegotiatorApplication.class)
@ActiveProfiles("test")
public class DiscoveryServiceControllerTests {
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DataSourceControllerTests {

  private static final String ENDPOINT = "/v3/discovery-service";
  private MockMvc mockMvc;
  @Autowired private WebApplicationContext context;
  @Autowired private DiscoverServiceController controller;
  @Autowired private DiscoveryServiceRepository repository;
  @Autowired private ModelMapper modelMapper;

  @BeforeEach
  public void before() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  public void testCreate_BadRequest_whenName_IsMissing() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    request.setName(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenDescription_IsMissing() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    request.setDescription(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenUrl_IsMissing() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    request.setUrl(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenApiType_IsMissing() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    request.setApiType(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenApiType_IsWrong() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    String requestBody = TestUtils.jsonFromRequest(request);
    requestBody = requestBody.replace("MOLGENIS", "UNKNOWN");
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        requestBody,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenApiUrl_IsMissing() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    request.setApiUrl(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenApiUsername_IsMissing() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    request.setApiUsername(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenApiPassword_IsMissing() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    request.setApiPassword(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenResourceNetwork_IsMissing() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    request.setResourceNetwork(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenResourceBiobank_IsMissing() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    request.setResourceBiobank(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  public void testCreate_BadRequest_whenResourceCollection_IsMissing() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    request.setResourceCollection(null);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isBadRequest(),
        httpBasic("admin", "admin"),
        ENDPOINT);
  }

  @Test
  @WithUserDetails("admin")
  @Transactional
  public void testCreated_whenRequest_IsCorrect() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    String requestBody = TestUtils.jsonFromRequest(request);

    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/v3/discovery-service")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name", Is.is(TestUtils.DISCOVERY_SERVICE_NAME)))
            .andExpect(jsonPath("$.url", Is.is(TestUtils.DISCOVERY_SERVICE_URL)))
            .andReturn();
    Integer discoveryServiceId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    Optional<DiscoveryService> discoveryService = repository.findById((long) discoveryServiceId);
    assert discoveryService.isPresent();
    assertEquals(discoveryService.get().getCreatedBy().getName(), "admin");

    assertEquals(repository.findAll().size(), 2);
    repository.deleteById(2L);
  }

  @Test
  public void testCreate_Unauthorized_whenNoAuth() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    TestUtils.checkErrorResponse(
        mockMvc, HttpMethod.POST, request, status().isUnauthorized(), anonymous(), ENDPOINT);
  }

  @Test
  public void testCreate_Unauthorized_whenWrongAuth() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isUnauthorized(),
        httpBasic("admin", "wrong_pass"),
        ENDPOINT);
  }

  @Test
  public void testCreate_Forbidden_whenNoPermission() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.POST,
        request,
        status().isForbidden(),
        httpBasic("researcher", "researcher"),
        ENDPOINT);
  }

  @Test
  public void testUpdate_whenIsCorrect() throws Exception {
    // The data source to be updated
    DiscoveryService discoveryServiceEntity =
        modelMapper.map(TestUtils.createDiscoveryServiceRequest(false), DiscoveryService.class);
    repository.save(discoveryServiceEntity);

    // Negotiation body with updated values
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(true);

    String requestBody = TestUtils.jsonFromRequest(request);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(
                    "/v3/discovery-service/%s".formatted(discoveryServiceEntity.getId()))
                .with(httpBasic("admin", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isNoContent())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    Optional<DiscoveryService> updateDiscoveryService =
        repository.findById(discoveryServiceEntity.getId());
    assert updateDiscoveryService.isPresent();
    assertEquals(updateDiscoveryService.get(), modelMapper.map(request, DiscoveryService.class));

    repository.deleteById(discoveryServiceEntity.getId());
  }

  @Test
  public void testUpdate_Unauthorized_whenNoAuth() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    TestUtils.checkErrorResponse(
        mockMvc, HttpMethod.PUT, request, status().isUnauthorized(), anonymous(), ENDPOINT);
  }

  @Test
  public void testUpdate_Unauthorized_whenWrongAuth() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.PUT,
        request,
        status().isUnauthorized(),
        httpBasic("admin", "wrong_pass"),
        ENDPOINT);
  }

  @Test
  public void testUpdate_Forbidden_whenNoPermission() throws Exception {
    DiscoveryServiceCreateDTO request = TestUtils.createDiscoveryServiceRequest(false);
    TestUtils.checkErrorResponse(
        mockMvc,
        HttpMethod.PUT,
        request,
        status().isForbidden(),
        httpBasic("researcher", "researcher"),
        ENDPOINT);
  }
}