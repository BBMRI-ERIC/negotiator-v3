package eu.bbmri_eric.negotiator.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.bbmri_eric.negotiator.dto.OrganizationDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResourceDTO {

  @NotNull private String id;

  @Nullable private String name;

  @Nullable private OrganizationDTO organization;
}
