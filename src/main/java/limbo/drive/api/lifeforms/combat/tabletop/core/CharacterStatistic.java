package limbo.drive.api.lifeforms.combat.tabletop.core;

import limbo.drive.api.lifeforms.GenericNPC;

public record CharacterStatistic(
    String name,
    int minimum,
    int maximum,
    boolean canBeModified,
    int sidesPerRoll
) {
    public int roll(CharacterClass clazz, GenericNPC character) {
        return 0;
    }
}