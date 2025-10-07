package jame.dev.dtos.fines;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FineDetailsDto(
        UUID uuid, String nameUser, int idUser, String cause, int daysRemaining
) {
}
