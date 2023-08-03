package eu.bbmri.eric.csit.service.negotiator.configuration.state_machine.resource;

public enum NegotiationResourceEvent {
  CONTACT,
  MARK_AS_UNREACHABLE,
  RETURN_FOR_RESUBMISSION,
  MARK_AS_CHECKING_AVAILABILITY,
  MARK_AS_AVAILABLE,
  MARK_AS_WILLING_TO_COLLECT,
  MARK_AS_UNAVAILABLE,
  INDICATE_ACCESS_CONDITIONS,
  MARK_ACCESS_CONDITIONS_AS_MET,
  MARK_ACCESS_CONDITIONS_AS_DECLINED,
  GRANT_ACCESS_TO_RESOURCE
}