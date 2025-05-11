package files;

// Assuming this helper class exists
// If not, you'll need to define it or adapt the return type
public class UnitAndCaetogryHistoryHelper {
    public int categoryIndex;
    public int unitIndex;

    public UnitAndCaetogryHistoryHelper(int categoryIndex, int unitIndex) {
        this.categoryIndex = categoryIndex;
        this.unitIndex = unitIndex;
    }

    public int getCategoryIndex() {
        return categoryIndex;
    }

    public int getUnitIndex() {
        return unitIndex;
    }
}
