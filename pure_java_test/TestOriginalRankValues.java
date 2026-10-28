import java.util.Map;
import org.jeicy.apotheoticadditions.OriginalGemRank;
import org.jeicy.apotheoticadditions.OriginalRankValues;

public class TestOriginalRankValues {
    public static void main(String[] args) {
        // Original Warden gem: no Ancient/Mythic intermediate value was defined.
        var warden = new OriginalRankValues(Map.of(
          "apotheotic_additions:artifact", 4.5,
          "apotheotic_additions:heirloom", 5.0,
          "apotheotic_additions:esoteric", 6.0
        ));
        if (warden.get(OriginalGemRank.ARTIFACT) != 4.5) throw new AssertionError("Artifact value");
        if (warden.get(OriginalGemRank.HEIRLOOM) != 5.0) throw new AssertionError("Heirloom value");
        if (warden.get(OriginalGemRank.ESOTERIC) != 6.0) throw new AssertionError("Esoteric value");
        if (warden.get(OriginalGemRank.ANCIENT) != null) throw new AssertionError("Invented Ancient value");
        if (warden.serialized().size() != 3) throw new AssertionError("Lost a tier");
        try { new OriginalRankValues(Map.of("perfect", 9.0)); throw new AssertionError("Accepted fake tier"); }
        catch (IllegalArgumentException expected) {}
        try { new OriginalRankValues(Map.of("common", Double.NaN)); throw new AssertionError("NaN"); }
        catch (IllegalArgumentException expected) {}
        System.out.println("Original rank values OK: exact ranks, no extrapolation, rejects invalid input");
    }
}
