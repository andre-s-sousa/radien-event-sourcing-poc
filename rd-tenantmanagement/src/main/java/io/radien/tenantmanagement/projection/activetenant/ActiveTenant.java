package io.radien.tenantmanagement.projection.activetenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.radien.eventsourcing.core.EventMetadata;
import io.radien.projections.data.model.VersionedView;
import io.radien.tenantmanagement.domain.TenantType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ActiveTenant implements VersionedView {
    @Id
    private String activeTenantId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = false)
    private String tenantId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = false)
    private String userId;

    @JsonIgnore
    @Column(nullable = false)
    private long version;

    @JsonIgnore
    @Column(nullable = false)
    private long lastProcessedPosition;

    public ActiveTenant(
            String activeTenantId, String tenantId, String userId,
            long version, long lastProcessedPosition) {
        this.activeTenantId = activeTenantId;
        this.tenantId = tenantId;
        this.userId = userId;
        this.version = version;
        this.lastProcessedPosition = lastProcessedPosition;
    }

    public ActiveTenant() {
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getActiveTenantId() {
        return activeTenantId;
    }

    public void setActiveTenantId(String activeTenantId) {
        this.activeTenantId = activeTenantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
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
}
