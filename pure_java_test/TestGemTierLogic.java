import org.jeicy.apotheoticadditions.OriginalGemTierLogic;
import java.util.Map;
public class TestGemTierLogic {
 public static void main(String[] args){
  var t=OriginalGemTierLogic.TIERS;
  if (t.size()!=9) throw new AssertionError("Original should have nine rarity levels");
  for(int i=0;i<8;i++)if(!OriginalGemTierLogic.next(t.get(i)).equals(t.get(i+1)))throw new AssertionError(i);
  if(OriginalGemTierLogic.originalValue(Map.of("apotheotic_additions:heirloom", 0.95),"apotheotic_additions:heirloom")!=0.95)throw new AssertionError("lookup");
  try{OriginalGemTierLogic.next("apotheotic_additions:esoteric");throw new AssertionError("max tier");}catch(IllegalStateException expected){}
  if(OriginalGemTierLogic.sameGem("apotheotic_additions:crafted/ignis","apotheotic_additions:crafted/naga"))throw new AssertionError("identity");
  System.out.println("OK: 9 original tiers, 8 transitions, no post-Esoteric, ID isolation, exact bonuses");
 }
}
