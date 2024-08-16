package com.dimon.discord_bot.game;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoleAssigner {

    public enum Role {
        MAFIA,
        DOCTOR,
        SHERIFF,
        HOOKER,
        PEACEFUL_CITIZEN
    }

    public static List<Role> assignRoles(int mafiaCount, int doctorCount, int sheriffCount, int hookerCount, int peacefulCitizenCount) {
        List<Role> roles = Stream.concat(
                Stream.concat(
                        Stream.concat(
                                Stream.concat(
                                        Collections.nCopies(mafiaCount, Role.MAFIA).stream(),
                                        Collections.nCopies(doctorCount, Role.DOCTOR).stream()
                                ),
                                Collections.nCopies(sheriffCount, Role.SHERIFF).stream()
                        ),
                        Collections.nCopies(hookerCount, Role.HOOKER).stream()
                ),
                Collections.nCopies(peacefulCitizenCount, Role.PEACEFUL_CITIZEN).stream()
        ).collect(Collectors.toList());

        Collections.shuffle(roles); // Shuffle the roles for randomness
        return roles;
    }
}
