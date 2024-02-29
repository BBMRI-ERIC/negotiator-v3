package eu.bbmri_eric.negotiator.unit.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.bbmri_eric.negotiator.database.model.DataSource.ApiType;
import eu.bbmri_eric.negotiator.database.repository.DataSourceRepository;
import eu.bbmri_eric.negotiator.dto.datasource.DataSourceCreateDTO;
import eu.bbmri_eric.negotiator.exceptions.EntityNotStorableException;
import eu.bbmri_eric.negotiator.service.DataSourceServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

public class DataSourceServiceTest {

  @Mock DataSourceRepository dataSourceRepository;

  @Mock ModelMapper modelMapper;

  @InjectMocks DataSourceServiceImpl service;

  private AutoCloseable closeable;

  @BeforeEach
  void before() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void after() throws Exception {
    closeable.close();
  }

  private DataSourceCreateDTO getTestDTO() {
    return DataSourceCreateDTO.builder()
        .description("Test Data Source")
        .name("Name of the data source")
        .url("http://datasource")
        .apiUrl("http://datasource/api")
        .apiType(ApiType.MOLGENIS)
        .apiUsername("test")
        .apiPassword("test")
        .resourceNetwork("test_ds_network")
        .resourceBiobank("test_ds_biobank")
        .resourceCollection("test_ds_collection")
        .syncActive(true)
        .sourcePrefix("prefix")
        .build();
  }

  @Test
  public void testCreateRaiseException_WhenDBFails() {
    DataSourceCreateDTO dto = getTestDTO();
    when(dataSourceRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
    assertThrows(EntityNotStorableException.class, () -> service.create(dto));
  }
}