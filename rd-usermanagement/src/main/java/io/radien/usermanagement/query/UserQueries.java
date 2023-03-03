package io.radien.usermanagement.query;

import io.quarkus.runtime.util.StringUtil;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UserQueries {
    public record Paginated(
            UUID userId,
            String firstName,
            String lastName,
            String userName,
            String userEmail,
            boolean exact,
            boolean conjunction,
            int pageNo,
            int pageSize
    ) {
        public Map<String, Object> getFilterParams() {
            Map<String, Object> params = new HashMap<>();
            if(this.userId() != null) {
                params.put("userId", this.exact() ? this.userId() : MessageFormat.format("%{0}%", this.userId()));
            }
            if(!StringUtil.isNullOrEmpty(this.firstName())) {
                params.put("firstName", this.exact() ? this.firstName() : MessageFormat.format("%{0}%", this.firstName()));
            }
            if(!StringUtil.isNullOrEmpty(this.lastName())) {
                params.put("lastName", this.exact() ? this.lastName() : MessageFormat.format("%{0}%", this.lastName()));
            }
            if(!StringUtil.isNullOrEmpty(this.userName())) {
                params.put("userName", this.exact() ? this.userName() : MessageFormat.format("%{0}%", this.userName()));
            }
            if(!StringUtil.isNullOrEmpty(this.userEmail())) {
                params.put("userEmail", this.exact() ? this.userEmail() : MessageFormat.format("%{0}%", this.userEmail()));
            }
            return params.isEmpty() ? null : params;
        }
    }

    public record Update(
            UUID userId,
            String firstName,
            String lastName,
            String userName,
            String userEmail,
            Long expectedVersion
    ) {}

    public record Delete(
            UUID userId,
            LocalDateTime terminationDate,
            Long expectedVersion
    ) {}
}

