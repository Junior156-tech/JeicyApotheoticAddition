import org.jeicy.apotheoticadditions.OriginalGemRank;

public final class TestOriginalGemRank {
    public static void main(String[] args) {
        OriginalGemRank[] all = OriginalGemRank.values();
        if (all.length != 9) throw new AssertionError("Original has 9 rarity ranks");
        for (int i = 0; i < all.length; i++) {
            OriginalGemRank rank = all[i];
            if (rank.stableId() != i || OriginalGemRank.fromIndex(i) != rank
                || OriginalGemRank.fromOriginalId(rank.originalId()) != rank)
                throw new AssertionError("ID mismatch " + rank);
            if (i < all.length - 1 && rank.next() != all[i + 1])
                throw new AssertionError("Upgrade mismatch " + rank);
        }
        boolean rejected = false;
        try { OriginalGemRank.ESOTERIC.next(); }
        catch (IllegalStateException expected) { rejected = true; }
        if (!rejected) throw new AssertionError("Esoteric must never advance");
        for (int invalid : new int[]{-1, 9, 90}) {
            rejected = false;
            try { OriginalGemRank.fromIndex(invalid); }
            catch (IllegalArgumentException expected) { rejected = true; }
            if (!rejected) throw new AssertionError("Bad integer rank accepted: " + invalid);
        }
        System.out.println("PASS: nine stable original gem ranks + strict transitions");
    }
}
