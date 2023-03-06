package io.radien.tenantmanagement.resource;

import io.radien.eventsourcing.core.ETag;
import io.radien.tenantmanagement.command.ActiveTenantCommands;
import io.radien.tenantmanagement.handler.activetenant.CreateActiveTenant;
import io.radien.tenantmanagement.handler.activetenant.DeleteActiveTenant;
import io.radien.tenantmanagement.handler.activetenant.UpdateActiveTenant;
import io.radien.tenantmanagement.service.ActiveTenantCommandBusinessService;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/activeTenant/command")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActiveTenantCommandResource {
    @Inject
    ActiveTenantCommandBusinessService businessService;

    @POST
    public ETag createActiveTenant(ActiveTenantCommands.Create command) {
        UUID activeTenantId = UUID.randomUUID();
        return businessService.createActiveTenant(
                new CreateActiveTenant(activeTenantId, command.tenantId(), command.userId())
        );
    }

    @PUT
    public ETag updateTenant(ActiveTenantCommands.Update command) {
        return businessService.updateActiveTenant(
                new UpdateActiveTenant(command.id(), command.tenantId(), command.expectedVersion())
        );
    }

    @DELETE
    public ETag deleteTenant(ActiveTenantCommands.Delete command) {
        return businessService.deleteActiveTenant(
                new DeleteActiveTenant(
                        command.id(), command.terminationDate(), command.expectedVersion()
                )
        );
    }
}
