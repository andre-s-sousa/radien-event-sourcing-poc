package io.radien.tenantmanagement.resource;

import io.netty.util.internal.StringUtil;
import io.radien.eventsourcing.core.ETag;
import io.radien.tenantmanagement.command.TenantCommands;
import io.radien.tenantmanagement.handler.CreateClientTenant;
import io.radien.tenantmanagement.handler.CreateRootTenant;
import io.radien.tenantmanagement.handler.CreateSubTenant;
import io.radien.tenantmanagement.handler.DeleteTenant;
import io.radien.tenantmanagement.handler.UpdateTenantClientAddress;
import io.radien.tenantmanagement.handler.UpdateTenantClientCity;
import io.radien.tenantmanagement.handler.UpdateTenantClientCountry;
import io.radien.tenantmanagement.handler.UpdateTenantClientEmail;
import io.radien.tenantmanagement.handler.UpdateTenantClientPhoneNumber;
import io.radien.tenantmanagement.handler.UpdateTenantClientZipCode;
import io.radien.tenantmanagement.handler.UpdateTenantKey;
import io.radien.tenantmanagement.handler.UpdateTenantName;
import io.radien.tenantmanagement.service.TenantCommandBusinessService;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user/command")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TenantCommandResource {
    @Inject
    TenantCommandBusinessService businessService;

    @POST
    @Path("/root")
    public ETag createRoot(TenantCommands.CreateRoot command) {
        UUID tenantId = UUID.randomUUID();
        return businessService.createTenant(
                new CreateRootTenant(tenantId, command.tenantKey(), command.tenantName())
        );
    }

    @POST
    @Path("/client")
    public ETag createClient(TenantCommands.CreateClient command) {
        UUID tenantId = UUID.randomUUID();
        return businessService.createTenant(
                new CreateClientTenant(
                        tenantId, command.tenantKey(), command.tenantName(), command.clientAddress(), command.clientZipCode(),
                        command.clientCity(), command.clientCountry(), command.clientPhoneNumber(), command.clientEmail(), command.parentId()
                )
        );
    }

    @POST
    @Path("/sub")
    public ETag createSub(TenantCommands.CreateSub command) {
        UUID tenantId = UUID.randomUUID();
        return businessService.createTenant(
                new CreateSubTenant(tenantId, command.tenantKey(), command.tenantName(), command.parentId())
        );
    }

    @PUT
    public ETag updateTenant(TenantCommands.Update command) {
        Long expectedVersion = command.expectedVersion();
        ETag result = null;
        if(!StringUtil.isNullOrEmpty(command.tenantKey())) {
            result = businessService.updateTenant(new UpdateTenantKey(
                    command.id(), command.tenantKey(), expectedVersion
            ));
            expectedVersion++;
        }
        if(!StringUtil.isNullOrEmpty(command.tenantName())) {
            result = businessService.updateTenant(new UpdateTenantName(
                    command.id(), command.tenantName(), expectedVersion
            ));
            expectedVersion++;
        }
        if(!StringUtil.isNullOrEmpty(command.clientAddress())) {
            result = businessService.updateTenant(new UpdateTenantClientAddress(
                    command.id(), command.clientAddress(), expectedVersion
            ));
            expectedVersion++;
        }
        if(!StringUtil.isNullOrEmpty(command.clientZipCode())) {
            result = businessService.updateTenant(new UpdateTenantClientZipCode(
                    command.id(), command.clientZipCode(), expectedVersion
            ));
            expectedVersion++;
        }
        if(!StringUtil.isNullOrEmpty(command.clientCity())) {
            result = businessService.updateTenant(new UpdateTenantClientCity(
                    command.id(), command.clientCity(), expectedVersion
            ));
            expectedVersion++;
        }
        if(!StringUtil.isNullOrEmpty(command.clientCountry())) {
            result = businessService.updateTenant(new UpdateTenantClientCountry(
                    command.id(), command.clientCountry(), expectedVersion
            ));
            expectedVersion++;
        }
        if(!StringUtil.isNullOrEmpty(command.clientPhoneNumber())) {
            result = businessService.updateTenant(new UpdateTenantClientPhoneNumber(
                    command.id(), command.clientPhoneNumber(), expectedVersion
            ));
            expectedVersion++;
        }
        if(!StringUtil.isNullOrEmpty(command.clientEmail())) {
            result = businessService.updateTenant(new UpdateTenantClientEmail(
                    command.id(), command.clientEmail(), expectedVersion
            ));
        }

        return result;
    }

    @DELETE
    public ETag deleteTenant(TenantCommands.Delete command) {
        return businessService.deleteTenant(
                new DeleteTenant(
                        command.id(), command.terminationDate(), command.expectedVersion()
                )
        );
    }
}
