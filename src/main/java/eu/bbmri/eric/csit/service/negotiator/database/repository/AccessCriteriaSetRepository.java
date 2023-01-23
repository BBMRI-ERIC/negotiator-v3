package eu.bbmri.eric.csit.service.negotiator.database.repository;

import eu.bbmri.eric.csit.service.negotiator.database.model.AccessCriteriaSet;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessCriteriaSetRepository extends JpaRepository<AccessCriteriaSet, Long> {
  @Query(
      value =
          "SELECT DISTINCT a "
              + "FROM AccessCriteriaSet a "
              + "JOIN FETCH a.sections acs "
              + "JOIN FETCH acs.accessCriteria ac "
              + "JOIN a.resources r "
              + "WHERE r.sourceId = :entityId")
  AccessCriteriaSet findByResourceEntityId(String entityId);
}
