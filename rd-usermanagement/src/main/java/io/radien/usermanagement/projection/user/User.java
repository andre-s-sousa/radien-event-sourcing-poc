package io.radien.usermanagement.projection.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.radien.eventsourcing.core.EventMetadata;
import io.radien.projections.data.model.VersionedView;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User implements VersionedView {
    @Id
    private String userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = false)
    private String firstName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = false)
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = false)
    private String userName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = false)
    private String userEmail;

    @JsonIgnore
    @Column(nullable = false)
    private long version;

    @JsonIgnore
    @Column(nullable = false)
    private long lastProcessedPosition;


    public User(
            String userId,
            String firstName,
            String lastName,
            String userName,
            String userEmail,
            long version,
            long lastProcessedPosition
    ) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.userEmail = userEmail;
        this.version = version;
        this.lastProcessedPosition = lastProcessedPosition;
    }

    public User() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        this.userId = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getLastProcessedPosition() {
        return lastProcessedPosition;
    }

    public void setLastProcessedPosition(long lastProcessedPosition) {
        this.lastProcessedPosition = lastProcessedPosition;
    }

    @JsonIgnore
    public void setMetadata(EventMetadata eventMetadata) {
        this.version = eventMetadata.streamPosition();
        this.lastProcessedPosition = eventMetadata.logPosition();
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
