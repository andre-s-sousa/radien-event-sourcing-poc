package io.radien.tenantmanagement.domain;

import io.radien.tenantmanagement.event.TenantEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface SystemTenant {
    UUID id();
    String tenantKey();
    String tenantName();

    record RootTenant(
            UUID id,
            String tenantKey,
            String tenantName
    ) implements SystemTenant {}

    record ClientTenant(
            UUID id,
            String tenantKey,
            String tenantName,
            String clientAddress,
            String clientZipCode,
            String clientCity,
            String clientCountry,
            String clientPhoneNumber,
            String clientEmail,
            UUID parentId
    ) implements SystemTenant {}

    record SubTenant(
            UUID id,
            String tenantKey,
            String tenantName,
            UUID parentId
    ) implements SystemTenant {}

    record DeletedTenant(
            UUID id,
            String tenantKey,
            String tenantName,
            LocalDateTime terminationDate
    ) implements SystemTenant {}

    default boolean isDeleted() {
        return this instanceof DeletedTenant;
    }

    static String mapToStreamId(UUID userId) {
        return "Tenant-%s".formatted(userId.toString());
    }

    static SystemTenant empty() {
        return new RootTenant(null, null, null);
    }

    static SystemTenant when(SystemTenant current, TenantEvent event) {
        if (event instanceof TenantEvent.RootTenantCreated createRootEvent) {
            return new RootTenant(
                    createRootEvent.tenantId(),
                    createRootEvent.tenantKey(),
                    createRootEvent.tenantName()
            );
        } else if (event instanceof TenantEvent.ClientTenantCreated createClientTenant) {
            return new ClientTenant(
                    createClientTenant.tenantId(),
                    createClientTenant.tenantKey(),
                    createClientTenant.tenantName(),
                    createClientTenant.clientAddress(),
                    createClientTenant.clientZipCode(),
                    createClientTenant.clientCity(),
                    createClientTenant.clientCountry(),
                    createClientTenant.clientPhoneNumber(),
                    createClientTenant.clientEmail(),
                    createClientTenant.parentId()
            );
        } else if(event instanceof TenantEvent.SubTenantCreated createSubTenant) {
            return new SubTenant(
                    createSubTenant.tenantId(),
                    createSubTenant.tenantKey(),
                    createSubTenant.tenantName(),
                    createSubTenant.parentId()
            );
        }  else if(event instanceof TenantEvent.TenantDeleted tenantDeleted) {
            return new DeletedTenant(
                    current.id(),
                    current.tenantKey(),
                    current.tenantName(),
                    tenantDeleted.teminationDate()
            );
        } else {
            return whenUpdate(current, event);
        }
    }

    static SystemTenant whenUpdate(SystemTenant current, TenantEvent event) {
        if(current instanceof RootTenant root) {
            if (event instanceof TenantEvent.TenantKeyUpdated tenantKeyUpdated) {
                return new RootTenant(
                        root.id(),
                        tenantKeyUpdated.tenantKey(),
                        root.tenantName()
                );
            } else if (event instanceof TenantEvent.TenantNameUpdated tenantNameUpdated) {
                return new RootTenant(
                        root.id(),
                        root.tenantKey(),
                        tenantNameUpdated.tenantName()
                );
            }
        } else if(current instanceof ClientTenant client) {
            return whenUpdate(client, event);
        } else if(current instanceof SubTenant sub) {
            return whenUpdate(sub, event);
        }
        //TODO: ex
        throw new RuntimeException("Invalid event provided");
    }

    static SystemTenant whenUpdate(ClientTenant current, TenantEvent event) {
        if(event instanceof TenantEvent.TenantKeyUpdated tenantKeyUpdated) {
            return new ClientTenant(
                    current.id(),
                    tenantKeyUpdated.tenantKey(),
                    current.tenantName(),
                    current.clientAddress(),
                    current.clientZipCode(),
                    current.clientCity(),
                    current.clientCountry(),
                    current.clientPhoneNumber(),
                    current.clientEmail(),
                    current.parentId()
            );
        } else if(event instanceof TenantEvent.TenantNameUpdated tenantNameUpdated) {
            return new ClientTenant(
                    current.id(),
                    current.tenantKey(),
                    tenantNameUpdated.tenantName(),
                    current.clientAddress(),
                    current.clientZipCode(),
                    current.clientCity(),
                    current.clientCountry(),
                    current.clientPhoneNumber(),
                    current.clientEmail(),
                    current.parentId()
            );
        } else if(event instanceof TenantEvent.TenantClientAddressUpdated tenantClientAddressUpdated) {
            return new ClientTenant(
                    current.id(),
                    current.tenantKey(),
                    current.tenantName(),
                    tenantClientAddressUpdated.clientAddress(),
                    current.clientZipCode(),
                    current.clientCity(),
                    current.clientCountry(),
                    current.clientPhoneNumber(),
                    current.clientEmail(),
                    current.parentId()
            );
        } else if(event instanceof TenantEvent.TenantClientZipCodeUpdated tenantClientZipCodeUpdated) {
            return new ClientTenant(
                    current.id(),
                    current.tenantKey(),
                    current.tenantName(),
                    current.clientAddress(),
                    tenantClientZipCodeUpdated.clientZipCode(),
                    current.clientCity(),
                    current.clientCountry(),
                    current.clientPhoneNumber(),
                    current.clientEmail(),
                    current.parentId()
            );
        } else if(event instanceof TenantEvent.TenantClientCityUpdated tenantClientCityUpdated) {
            return new ClientTenant(
                    current.id(),
                    current.tenantKey(),
                    current.tenantName(),
                    current.clientAddress(),
                    current.clientZipCode(),
                    tenantClientCityUpdated.clientCity(),
                    current.clientCountry(),
                    current.clientPhoneNumber(),
                    current.clientEmail(),
                    current.parentId()
            );
        } else if(event instanceof TenantEvent.TenantClientCountryUpdated tenantClientCountryUpdated) {
            return new ClientTenant(
                    current.id(),
                    current.tenantKey(),
                    current.tenantName(),
                    current.clientAddress(),
                    current.clientZipCode(),
                    current.clientCity(),
                    tenantClientCountryUpdated.clientCountry(),
                    current.clientPhoneNumber(),
                    current.clientEmail(),
                    current.parentId()
            );
        } else if(event instanceof TenantEvent.TenantClientPhoneNumberUpdated tenantClientPhoneNumberUpdated) {
            return new ClientTenant(
                    current.id(),
                    current.tenantKey(),
                    current.tenantName(),
                    current.clientAddress(),
                    current.clientZipCode(),
                    current.clientCity(),
                    current.clientCountry(),
                    tenantClientPhoneNumberUpdated.clientPhoneNumber(),
                    current.clientEmail(),
                    current.parentId()
            );
        } else if(event instanceof TenantEvent.TenantClientEmailUpdated tenantClientEmailUpdated) {
            return new ClientTenant(
                    current.id(),
                    current.tenantKey(),
                    current.tenantName(),
                    current.clientAddress(),
                    current.clientZipCode(),
                    current.clientCity(),
                    current.clientCountry(),
                    current.clientPhoneNumber(),
                    tenantClientEmailUpdated.clientEmail(),
                    current.parentId()
            );
        }
        //TODO: ex
        throw new RuntimeException("Invalid event provided");
    }

    static SystemTenant whenUpdate(SubTenant current, TenantEvent event) {
        if(event instanceof TenantEvent.TenantKeyUpdated tenantKeyUpdated) {
            return new SubTenant(
                    current.id(),
                    tenantKeyUpdated.tenantKey(),
                    current.tenantName(),
                    current.parentId()
            );
        } else if(event instanceof TenantEvent.TenantNameUpdated tenantNameUpdated) {
            return new SubTenant(
                    current.id(),
                    current.tenantKey(),
                    tenantNameUpdated.tenantName(),
                    current.parentId()
            );
        }
        //TODO: ex
        throw new RuntimeException("Invalid event provided");
    }
}
