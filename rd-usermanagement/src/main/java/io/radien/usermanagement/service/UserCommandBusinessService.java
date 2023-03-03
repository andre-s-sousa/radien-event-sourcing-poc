package io.radien.usermanagement.service;

import io.radien.eventsourcing.core.ETag;
import io.radien.eventsourcing.store.EntityStore;
import io.radien.usermanagement.domain.SystemUser;
import io.radien.usermanagement.event.UserEvent;
import io.radien.usermanagement.handler.CreateUserAccount;
import io.radien.usermanagement.handler.DeleteUserAccount;
import io.radien.usermanagement.handler.UpdateUserEmail;
import io.radien.usermanagement.handler.UpdateUserFirstName;
import io.radien.usermanagement.handler.UpdateUserLastName;
import io.radien.usermanagement.handler.UpdateUserName;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class UserCommandBusinessService {
    @Inject
    EntityStore<SystemUser, UserEvent> store;

    public ETag createUser(CreateUserAccount command) {
        return store.add(
                () -> CreateUserAccount.handle(command),
                command.userId()
        );
    }

    public ETag updateUser(UpdateUserFirstName command) {
        return store.getAndUpdate(
                current -> UpdateUserFirstName.handle(command, current),
                command.userId(),
                command.expectedVersion()
        );
    }

    public ETag updateUser(UpdateUserLastName command) {
        return store.getAndUpdate(
                current -> UpdateUserLastName.handle(command, current),
                command.userId(),
                command.expectedVersion()
        );
    }

    public ETag updateUser(UpdateUserName command) {
        return store.getAndUpdate(
                current -> UpdateUserName.handle(command, current),
                command.userId(),
                command.expectedVersion()
        );
    }

    public ETag updateUser(UpdateUserEmail command) {
        return store.getAndUpdate(
                current -> UpdateUserEmail.handle(command, current),
                command.userId(),
                command.expectedVersion()
        );
    }

    public ETag deleteUser(DeleteUserAccount command) {
        return store.getAndUpdate(
                current -> DeleteUserAccount.handle(command, current),
                command.userId(),
                command.expectedVersion()
        );
    }
}
