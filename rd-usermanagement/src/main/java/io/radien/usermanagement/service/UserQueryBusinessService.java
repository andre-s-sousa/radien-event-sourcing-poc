package io.radien.usermanagement.service;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.radien.usermanagement.projection.user.User;
import io.radien.usermanagement.projection.user.UserProjectionRepository;
import io.radien.usermanagement.query.UserQueries;
import io.smallrye.mutiny.Uni;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class UserQueryBusinessService {
    private final Logger logger = LoggerFactory.getLogger(UserQueryBusinessService.class);
    @Inject
    UserProjectionRepository entityStore;

    public Uni<List<User>> getUsers(UserQueries.Paginated query) {
        Map<String, Object> filterParams = query.getFilterParams();
        PanacheQuery<User> findAll;
        if(filterParams != null) {
            String queryFilter = filterParams.keySet()
                    .stream().map(key -> MessageFormat.format(query.exact() ? "{0} = :{0}" : "{0} like :{0}", key))
                    .collect(Collectors.joining(query.conjunction() ?  " and " : " or "));
            findAll = entityStore.find(queryFilter, filterParams);
        } else {
            findAll =  entityStore.findAll();
        }
        return findAll
                .page(Page.of(query.pageNo(), query.pageSize()))
                .list()
                .onFailure().invoke(err -> logger.info("Error retrieving users page", err));
    }

    public Uni<User> getUser(UUID userId) {
        return entityStore.findById(userId.toString())
                .onFailure().invoke(err -> logger.info("Could not retrieve user by id", err));
    }
}
