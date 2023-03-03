package io.radien.usermanagement.resource;

import io.netty.util.internal.StringUtil;
import io.radien.eventsourcing.core.ETag;
import io.radien.usermanagement.command.UserCommands;
import io.radien.usermanagement.handler.CreateUserAccount;
import io.radien.usermanagement.handler.DeleteUserAccount;
import io.radien.usermanagement.handler.UpdateUserEmail;
import io.radien.usermanagement.handler.UpdateUserFirstName;
import io.radien.usermanagement.handler.UpdateUserLastName;
import io.radien.usermanagement.handler.UpdateUserName;
import io.radien.usermanagement.service.UserCommandBusinessService;
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
public class UserCommandResource {

    @Inject
    UserCommandBusinessService businessService;

    @POST
    public ETag createUser(UserCommands.Create command) {
        UUID userId = UUID.randomUUID();
        return businessService.createUser(
                new CreateUserAccount(userId, command.firstName(), command.lastName(), command.userName(), command.userEmail())
        );
    }

    @PUT
    public ETag updateUser(UserCommands.Update command) {
        Long expectedVersion = command.expectedVersion();
        ETag result = null;
        if(!StringUtil.isNullOrEmpty(command.firstName())) {
            result = businessService.updateUser(new UpdateUserFirstName(
                    command.userId(), command.firstName(), expectedVersion
            ));
            expectedVersion++;
        }
        if(!StringUtil.isNullOrEmpty(command.lastName())) {
            result = businessService.updateUser(new UpdateUserLastName(
                    command.userId(), command.lastName(), expectedVersion
            ));
            expectedVersion++;
        }
        if(!StringUtil.isNullOrEmpty(command.userName())) {
            result = businessService.updateUser(new UpdateUserName(
                    command.userId(), command.userName(), expectedVersion
            ));
            expectedVersion++;
        }
        if(!StringUtil.isNullOrEmpty(command.userEmail())) {
            result = businessService.updateUser(new UpdateUserEmail(
                    command.userId(), command.userEmail(), expectedVersion
            ));
        }
        return result;
    }

    @DELETE
    public ETag deleteUser(UserCommands.Delete command) {
        return businessService.deleteUser(
                new DeleteUserAccount(
                        command.userId(), command.terminationDate(), command.expectedVersion()
                )
        );
    }
}
