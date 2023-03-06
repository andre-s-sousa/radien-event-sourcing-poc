package io.radien.tenantmanagement.event;

import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface TenantEvent {
    record RootTenantCreated(
            UUID tenantId,
            String tenantKey,
            String tenantName
    ) implements TenantEvent {}

    record ClientTenantCreated(
            UUID tenantId,
            String tenantKey,
            String tenantName,
            String clientAddress,
            String clientZipCode,
            String clientCity,
            String clientCountry,
            String clientPhoneNumber,
            String clientEmail,
            UUID parentId
    ) implements TenantEvent {}

    record SubTenantCreated(
            UUID tenantId,
            String tenantKey,
            String tenantName,
            UUID parentId
    ) implements TenantEvent {}

    record TenantKeyUpdated(
            UUID tenantId,
            String tenantKey
    ) implements TenantEvent {}

    record TenantNameUpdated(
            UUID tenantId,
            String tenantName
    ) implements TenantEvent {}

    record TenantClientAddressUpdated(
            UUID tenantId,
            String clientAddress
    ) implements TenantEvent {}

    record TenantClientZipCodeUpdated(
            UUID tenantId,
            String clientZipCode
    ) implements TenantEvent {}

    record TenantClientCityUpdated(
            UUID tenantId,
            String clientCity
    ) implements TenantEvent {}

    record TenantClientCountryUpdated(
            UUID tenantId,
            String clientCountry
    ) implements TenantEvent {}

    record TenantClientPhoneNumberUpdated(
            UUID tenantId,
            String clientPhoneNumber
    ) implements TenantEvent {}

    record TenantClientEmailUpdated(
            UUID tenantId,
            String clientEmail
    ) implements TenantEvent {}

    record TenantDeleted(
            UUID tenantId,
            LocalDateTime teminationDate
    ) implements TenantEvent {}
}
