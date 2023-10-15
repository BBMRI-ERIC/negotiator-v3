package eu.bbmri_eric.negotiator.database.model;

import eu.bbmri_eric.negotiator.configuration.state_machine.negotiation.NegotiationState;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public final class NegotiationLifecycleRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  private ZonedDateTime recordedAt;

  @Enumerated(EnumType.STRING)
  private NegotiationState changedTo;
}
