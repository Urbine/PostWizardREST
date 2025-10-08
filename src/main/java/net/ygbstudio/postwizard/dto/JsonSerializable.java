package net.ygbstudio.postwizard.dto;

/**
 * Marker interface for JsonB-ready DTOs that can be returned as resulting data to a client request.
 * It allows for different types to be handled uniformly in operations that can deliver different
 * elements to clients in JSON format. Other marker interfaces like {@link BatchDeliverable} have a
 * similar function but the distinction between the two is that {@link JsonSerializable} is used for
 * individual results irrespective of type while {@link BatchDeliverable} is used for batch
 * operations among the same DTO type.
 *
 * @see ServerResult
 * @author Yoham Gabriel @ YGB Studio
 */
public interface JsonSerializable {}
