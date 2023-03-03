package io.radien.usermanagement.resource;

import io.radien.usermanagement.projection.user.User;
import io.radien.usermanagement.query.UserQueries;
import io.radien.usermanagement.service.UserQueryBusinessService;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/user/query")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserQueryResource {
    @Inject
    UserQueryBusinessService businessService;

    @GET
    public Uni<List<User>> getPage(@QueryParam("userId") Optional<String> userId, @QueryParam("firstName") Optional<String> firstName,
                                   @QueryParam("lastName") Optional<String> lastName, @QueryParam("userName") Optional<String> userName,
                                   @QueryParam("userEmail") Optional<String> userEmail,
                                   @QueryParam("conjunction") @DefaultValue("true") boolean conjunction,
                                   @QueryParam("exact") @DefaultValue("true") boolean exact,
                                   @QueryParam("pageNo") @DefaultValue("0") int pageNo,
                                   @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        return businessService.getUsers(
                new UserQueries.Paginated(
                        userId.map(UUID::fromString).orElse(null),
                        firstName.orElse(null), lastName.orElse(null),
                        userName.orElse(null), userEmail.orElse(null),
                        exact, conjunction, pageNo, pageSize
                ));
    }

    @GET
    @Path("userId")
    public Uni<User> getById(@QueryParam("userId") UUID userId) {
        return businessService.getUser(userId);
    }
}
