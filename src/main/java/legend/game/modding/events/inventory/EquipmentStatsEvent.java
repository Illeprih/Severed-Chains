package legend.game.modding.events.inventory;

import legend.game.characters.ElementSet;
import legend.game.inventory.Equipment;
import legend.game.types.EquipmentSlot;
import org.legendofdragoon.modloader.events.Event;

public class EquipmentStatsEvent extends Event {
  public final int charId;
  public final Equipment equipment;

  public int flags_00;
  public EquipmentSlot slot;
  public int _02;
  public int equipableFlags_03;
  public ElementSet attackElement_04 = new ElementSet();
  public int _05;
  public ElementSet elementalResistance_06 = new ElementSet();
  public ElementSet elementalImmunity_07 = new ElementSet();
  public int statusResist_08;
  public int _09;
  public int attack1_0a;
  public int mpPerPhysicalHit;
  public int spPerPhysicalHit;
  public int mpPerMagicalHit;
  public int spPerMagicalHit;
  public int hpMultiplier;
  public int mpMultiplier;
  public int spMultiplier;
  public boolean magicalResistance;
  public boolean physicalResistance;
  public boolean magicalImmunity;
  public boolean physicalImmunity;
  public int revive;
  public int hpRegen;
  public int mpRegen;
  public int spRegen;
  public int escapeBonus;
  public int icon_0e;
  public int speed_0f;
  public int attack2_10;
  public int magicAttack_11;
  public int defence_12;
  public int magicDefence_13;
  public int attackHit_14;
  public int magicHit_15;
  public int attackAvoid_16;
  public int magicAvoid_17;
  public int onHitStatusChance_18;
  public int _19;
  public int _1a;
  public int onHitStatus_1b;

  public EquipmentStatsEvent(final int charId, final Equipment equipment) {
    this.charId = charId;
    this.equipment = equipment;

    this.flags_00 = equipment.flags_00;
    this.slot = equipment.slot;
    this._02 = equipment._02;
    this.equipableFlags_03 = equipment.equipableFlags_03;
    this.attackElement_04 = equipment.attackElement_04;
    this._05 = equipment._05;
    this.elementalResistance_06 = equipment.elementalResistance_06;
    this.elementalImmunity_07 = equipment.elementalImmunity_07;
    this.statusResist_08 = equipment.statusResist_08;
    this._09 = equipment._09;
    this.attack1_0a = equipment.attack1_0a;
    this.mpPerPhysicalHit = equipment.mpPerPhysicalHit;
    this.spPerPhysicalHit = equipment.spPerPhysicalHit;
    this.mpPerMagicalHit = equipment.mpPerMagicalHit;
    this.spPerMagicalHit = equipment.spPerMagicalHit;
    this.hpMultiplier = equipment.hpMultiplier;
    this.mpMultiplier = equipment.mpMultiplier;
    this.spMultiplier = equipment.spMultiplier;
    this.magicalResistance = equipment.magicalResistance;
    this.physicalResistance = equipment.physicalResistance;
    this.magicalImmunity = equipment.magicalImmunity;
    this.physicalImmunity = equipment.physicalImmunity;
    this.revive = equipment.revive;
    this.hpRegen = equipment.hpRegen;
    this.mpRegen = equipment.mpRegen;
    this.spRegen = equipment.spRegen;
    this.escapeBonus = equipment.escapeBonus;
    this.icon_0e = equipment.icon_0e.resolve().icon;
    this.speed_0f = equipment.speed_0f;
    this.attack2_10 = equipment.attack2_10;
    this.magicAttack_11 = equipment.magicAttack_11;
    this.defence_12 = equipment.defence_12;
    this.magicDefence_13 = equipment.magicDefence_13;
    this.attackHit_14 = equipment.attackHit_14;
    this.magicHit_15 = equipment.magicHit_15;
    this.attackAvoid_16 = equipment.attackAvoid_16;
    this.magicAvoid_17 = equipment.magicAvoid_17;
    this.onHitStatusChance_18 = equipment.onHitStatusChance_18;
    this._19 = equipment._19;
    this._1a = equipment._1a;
    this.onHitStatus_1b = equipment.onHitStatus_1b;
  }
}
