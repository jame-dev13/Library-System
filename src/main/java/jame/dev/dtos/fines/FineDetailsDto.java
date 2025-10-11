package jame.dev.dtos.fines;

import lombok.Builder;

import java.util.UUID;

/**
 * Builds an immutable object witch is composed by the join of
 * FineEntity and UserEntity.
 * @param uuid
 * @param nameUser
 * @param idUser
 * @param cause
 * @param daysRemaining
 */
@Builder
public record FineDetailsDto(
        UUID uuid,
        String nameUser,
        int idUser,
        String cause,
        int daysRemaining
) {
}
