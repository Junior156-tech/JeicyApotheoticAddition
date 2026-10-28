import org.jeicy.apotheoticadditions.OriginalSocketRules;
public class TestOriginalSocketRules {
 public static void main(String[] args) {
  assert OriginalSocketRules.ignisApplies("sword");
  assert OriginalSocketRules.ignisApplies("trident");
  assert !OriginalSocketRules.ignisApplies("heavy_weapon");
  assert !OriginalSocketRules.nagaApplies("sword");
  assert OriginalSocketRules.nagaApplies("heavy_weapon");
  assert OriginalSocketRules.maledictusApplies("pickaxe");
  assert !OriginalSocketRules.maledictusApplies("shovel");
  System.out.println("OK original weapon-target membership");
 }
}
