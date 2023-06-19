package limbo.drive.module.lifeforms.combat.tabletop.equipment.weapon;

import limbo.drive.module.lifeforms.combat.tabletop.damage.DamageType;

import java.util.ArrayList;
import java.util.List;

public enum WeaponClass {
    CLUB(WeaponType.SIMPLE, DamageType.BLUDGEONING, 2, 0.1, 1, 4, true, WeaponRange.MELEE),
    DAGGER(WeaponType.SIMPLE, DamageType.PIERCING, 1, 1.0, 1, 4, true, WeaponRange.MELEE, WeaponRange.RANGED_SHORT),
    GREATCLUB(WeaponType.SIMPLE, DamageType.BLUDGEONING, 10, 0.2, 1, 8, false, WeaponRange.MELEE),
    HANDAXE(WeaponType.SIMPLE, DamageType.SLASHING, 2, 5, 1, 6, true, WeaponRange.MELEE, WeaponRange.RANGED_SHORT),
    JAVELIN(WeaponType.SIMPLE, DamageType.PIERCING, 2, 0.5, 1, 6, true, WeaponRange.MELEE, WeaponRange.RANGED_SHORT),
    LIGHT_HAMMER(WeaponType.SIMPLE, DamageType.BLUDGEONING, 2, 0.2, 1, 4, true, WeaponRange.MELEE, WeaponRange.RANGED_SHORT),
    MACE(WeaponType.SIMPLE, DamageType.BLUDGEONING, 4, 5, 1, 6, true, WeaponRange.MELEE),
    QUATERSTAFF(WeaponType.SIMPLE, DamageType.BLUDGEONING, 4, 0.2, 1, 6, true, WeaponRange.MELEE),
    SICKLE(WeaponType.SIMPLE, DamageType.SLASHING, 2, 1, 1, 4, true, WeaponRange.MELEE),
    SPEAR(WeaponType.SIMPLE, DamageType.PIERCING, 3, 1, 1, 6, true, WeaponRange.MELEE, WeaponRange.RANGED_SHORT),



    CROSSSBOW_LIGHT(WeaponType.SIMPLE, DamageType.PIERCING, 5, 25, 1, 8, false, WeaponRange.RANGED_LONG),
    DART(WeaponType.SIMPLE, DamageType.PIERCING, 0.25, 0.05, 1, 4, false, WeaponRange.RANGED_SHORT),
    SHORTBOW(WeaponType.SIMPLE, DamageType.PIERCING, 2, 25, 1, 6, false, WeaponRange.RANGED_LONG),
    SLING(WeaponType.SIMPLE, DamageType.BLUDGEONING, 0, 0.1, 1, 4, true, WeaponRange.RANGED_MEDIUM),



    BATTLEAXE(WeaponType.MARTIAL, DamageType.SLASHING, 4, 10, 1, 8, true, WeaponRange.MELEE),
    FLAIL(WeaponType.MARTIAL, DamageType.BLUDGEONING, 2, 10, 1, 8, true, WeaponRange.MELEE),
    GLAIVE(WeaponType.MARTIAL, DamageType.SLASHING, 6, 20, 1, 10, false, WeaponRange.MELEE),
    GREATAXE(WeaponType.MARTIAL, DamageType.SLASHING, 7, 30, 1, 12, false, WeaponRange.MELEE),
    GREATSWORD(WeaponType.MARTIAL, DamageType.SLASHING, 6, 50, 2, 6, false, WeaponRange.MELEE),
    HALBERD(WeaponType.MARTIAL, DamageType.SLASHING, 6, 20, 1, 10, false, WeaponRange.MELEE),
    LANCE(WeaponType.MARTIAL, DamageType.PIERCING, 6, 10, 1, 10, false, WeaponRange.MELEE),
    LONGSWORD(WeaponType.MARTIAL, DamageType.SLASHING, 3, 15, 1, 8, false, WeaponRange.MELEE),
    MAUL(WeaponType.MARTIAL, DamageType.BLUDGEONING, 10, 10, 2, 6, false, WeaponRange.MELEE),
    MORNINGSTAR(WeaponType.MARTIAL, DamageType.PIERCING, 4, 15, 1, 8, false, WeaponRange.MELEE),
    PIKE(WeaponType.MARTIAL, DamageType.PIERCING, 18, 5, 1, 10, false, WeaponRange.MELEE),
    RAPIER(WeaponType.MARTIAL, DamageType.PIERCING, 2, 25, 1, 8, true, WeaponRange.MELEE),
    SCIMITAR(WeaponType.MARTIAL, DamageType.SLASHING, 3, 25, 1, 6, true, WeaponRange.MELEE),
    SHORTSWORD(WeaponType.MARTIAL, DamageType.PIERCING, 2, 10, 1, 6, true, WeaponRange.MELEE),
    TRIDENT(WeaponType.MARTIAL, DamageType.PIERCING, 4, 5, 1, 6, true, WeaponRange.MELEE, WeaponRange.RANGED_SHORT),
    WARPICK(WeaponType.MARTIAL, DamageType.PIERCING, 2, 5, 1, 8, true, WeaponRange.MELEE),
    WARHAMMER(WeaponType.MARTIAL, DamageType.BLUDGEONING, 2, 15, 1, 8, false, WeaponRange.MELEE),
    WHIP(WeaponType.MARTIAL, DamageType.SLASHING, 3, 2, 1, 4, true, WeaponRange.MELEE),



    BLOWGUN(WeaponType.MARTIAL, DamageType.PIERCING, 1, 10, 1, 1, true, WeaponRange.RANGED_MEDIUM),
    CROSSBOW_HAND(WeaponType.MARTIAL, DamageType.PIERCING, 3, 75, 1, 6, true, WeaponRange.RANGED_MEDIUM),
    CROSSBOW_HEAVY(WeaponType.MARTIAL, DamageType.PIERCING, 18, 50, 1, 10, false, WeaponRange.RANGED_LONG),
    LONGBOW(WeaponType.MARTIAL, DamageType.PIERCING, 2, 50, 1, 8, true, WeaponRange.RANGED_INFINITE),

    ;

    public final WeaponType type;
    public final List<WeaponRange> range;
    public final DamageType damage;
    public final double worth;
    public final double weight;
    public final int rolls;
    public final int sides;
    public final boolean dualwieldable;

    WeaponClass(
        WeaponType type,
        DamageType damage,
        double weight,
        double worth,
        int rolls,
        int sides,
        boolean dualwieldable,
        WeaponRange... range
    ) {
        this.type = type;
        this.worth = worth;
        this.rolls = rolls;
        this.sides = sides;
        this.weight = weight;
        this.damage = damage;
        this.dualwieldable = dualwieldable;
        this.range = List.of(range);
    }
}
