package com.jeremyalv.flow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Optional;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@Getter
public final class PublishResult {
    private final boolean success;
    private final Throwable error;
    private final Object nativeMetadata;

    public static PublishResult success(Object nativeMetadata) {
        Objects.requireNonNull(nativeMetadata, "Native metadata cannot be null for explicit success");
        return new PublishResult(true, null, nativeMetadata);
    }

    public static PublishResult success() {
        return new PublishResult(true, null, null);
    }

    public static PublishResult failure(Throwable error) {
        Objects.requireNonNull(error, "Error cannot be null for failure");
        return new PublishResult(false, error, null);
    }

    public <M> Optional<M> getMetadata(Class<M> metadataType) {
        Objects.requireNonNull(metadataType, "metadataType cannot be null");
        if (nativeMetadata != null && metadataType.isInstance(nativeMetadata)) {
            return Optional.of(metadataType.cast(nativeMetadata));
        }
        return Optional.empty();
    }
}
