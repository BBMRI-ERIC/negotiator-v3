package eu.bbmri_eric.negotiator.database.repository;

import eu.bbmri_eric.negotiator.database.model.attachments.Attachment;
import eu.bbmri_eric.negotiator.database.model.attachments.AttachmentProjection;
import eu.bbmri_eric.negotiator.database.model.attachments.MetadataAttachmentProjection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {

  Optional<AttachmentProjection> findCompleteById(String lastname);

  Optional<MetadataAttachmentProjection> findMetadataById(String id);

  @EntityGraph("attachment-metadata")
  List<MetadataAttachmentProjection> findMetadataByNegotiationId(String negotiationId);

  Optional<MetadataAttachmentProjection> findMetadataByIdAndNegotiationId(String id, String negotiationId);
}
