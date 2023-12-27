package eu.bbmri.eric.csit.service.negotiator.database.repository;

import eu.bbmri.eric.csit.service.negotiator.database.model.Notification;
import eu.bbmri.eric.csit.service.negotiator.database.model.NotificationEmailStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  List<Notification> findByRecipientId(Long personId);

  List<Notification> findByEmailStatus(NotificationEmailStatus status);

  List<Notification> findByRecipientIdAndEmailStatus(
      Long recipientId, NotificationEmailStatus status);
}