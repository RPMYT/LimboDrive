package limbo.drive.module.lifeforms.combat.tabletop.core;

import limbo.drive.module.lifeforms.GenericNPC;

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