package eu.bbmri.eric.csit.service.negotiator.database.model;

public enum NegotiationResourceEvent {
  CONTACT,
  MARK_AS_UNREACHABLE,
  RETURN_FOR_RESUBMISSION,
  MARK_AS_CHECKING_AVAILABILITY,
  MARK_AS_AVAILABLE,
  MARK_AS_UNAVAILABLE,
  INDICATE_ACCESS_CONDITIONS,
  MARK_ACCESS_CONDITIONS_AS_MET,
  GRANT_ACCESS_TO_RESOURCE
}
