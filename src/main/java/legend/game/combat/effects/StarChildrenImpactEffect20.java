package legend.game.combat.effects;

import java.util.Arrays;

public class StarChildrenImpactEffect20 implements BttlScriptData6cSubBase1 {
//  public int count_00;
  public int _04;
  public final BttlScriptData6cSub20_2Suba8[] ptr_08;

  public StarChildrenImpactEffect20(final int count) {
    this.ptr_08 = new BttlScriptData6cSub20_2Suba8[count];
    Arrays.setAll(this.ptr_08, i -> new BttlScriptData6cSub20_2Suba8());
  }
}