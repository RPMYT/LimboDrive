package limbo.drive.api.lifeforms.combat.tabletop.core;

import java.util.HashMap;

public record CharacterClass(
    String name,
    HashMap<CharacterStatistic, Integer> stats
) {}
