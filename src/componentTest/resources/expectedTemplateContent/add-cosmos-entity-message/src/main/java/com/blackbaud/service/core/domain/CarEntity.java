package com.blackbaud.service.core.domain;

import com.blackbaud.cosmos.AuditableEntity;
import com.blackbaud.cosmos.VersionedEntity;
import com.blackbaud.cosmos.sharded.ContributorEntity;
import com.blackbaud.cosmos.sharded.ShardKey;
import com.blackbaud.cosmos.sharded.ShardedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = CarEntity.COLLECTION_NAME)
public class CarEntity implements ShardedEntity<ObjectId, String>, VersionedEntity {

    public static final String COLLECTION_NAME = "cars";
    public static int CURRENT_SCHEMA_VERSION = 1;

    @Id
    private ObjectId id;
    private String environmentId;

    @Version
    private Long version;

    @Builder.Default
    private int schemaVersion = CURRENT_SCHEMA_VERSION;


    @Override
    public ShardKey<String> getShardKey() {
        return ShardKey.fromEnvironmentId(environmentId);
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

}
