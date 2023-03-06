package io.radien.tenantmanagement.projection.tenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.radien.eventsourcing.core.EventMetadata;
import io.radien.projections.data.model.VersionedView;
import io.radien.tenantmanagement.domain.TenantType;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tenant implements VersionedView {
    @Id
    private String tenantId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = false)
    private String tenantKey;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = false)
    private String tenantName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = false)
    private TenantType tenantType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = true)
    private String clientAddress;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = true)
    private String clientZipCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = true)
    private String clientCity;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = true)
    private String clientCountry;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = true)
    private String clientPhoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = true)
    private String clientEmail;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(nullable = true)
    private String parentId;

    @JsonIgnore
    @Column(nullable = false)
    private long version;

    @JsonIgnore
    @Column(nullable = false)
    private long lastProcessedPosition;

    public Tenant(
            String tenantId, String tenantKey, String tenantName, TenantType tenantType,
            String clientAddress, String clientZipCode, String clientCity, String clientCountry,
            String clientPhoneNumber, String clientEmail, String parentId,
            long version, long lastProcessedPosition) {
        this.tenantId = tenantId;
        this.tenantKey = tenantKey;
        this.tenantName = tenantName;
        this.tenantType = tenantType;
        this.clientAddress = clientAddress;
        this.clientZipCode = clientZipCode;
        this.clientCity = clientCity;
        this.clientCountry = clientCountry;
        this.clientPhoneNumber = clientPhoneNumber;
        this.clientEmail = clientEmail;
        this.parentId = parentId;
        this.version = version;
        this.lastProcessedPosition = lastProcessedPosition;
    }

    public Tenant() {
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantKey() {
        return tenantKey;
    }

    public void setTenantKey(String tenantKey) {
        this.tenantKey = tenantKey;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public TenantType getTenantType() {
        return tenantType;
    }

    public void setTenantType(TenantType tenantType) {
        this.tenantType = tenantType;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientZipCode() {
        return clientZipCode;
    }

    public void setClientZipCode(String clientZipCode) {
        this.clientZipCode = clientZipCode;
    }

    public String getClientCity() {
        return clientCity;
    }

    public void setClientCity(String clientCity) {
        this.clientCity = clientCity;
    }

    public String getClientCountry() {
        return clientCountry;
    }

    public void setClientCountry(String clientCountry) {
        this.clientCountry = clientCountry;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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
