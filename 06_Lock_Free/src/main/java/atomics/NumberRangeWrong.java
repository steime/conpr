package atomics;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class IntPair {
    public final int low;
    public final int up;

    public IntPair(int low, int up) {
        this.low = low;
        this.up = up;
    }
}

public class NumberRangeWrong {

    // INVARIANT: lower <= upper is NOT GUARANTEED
    private final AtomicInteger lower = new AtomicInteger(0);
    private final AtomicInteger upper = new AtomicInteger(0);
    private final AtomicReference<IntPair> values = new AtomicReference<>(new IntPair(0,0));

    public int getLower() {
        return values.get().low;
    }


    public void setLower(int i) {
        while (true) {
            IntPair oldValue = values.get();
            if (i > oldValue.up)
                throw new IllegalArgumentException();
            IntPair newValue = new IntPair(i,oldValue.up);
            if (values.compareAndSet(oldValue, newValue))
                return;
        }
    }

    public int getUpper() {
        return values.get().up;
    }

    public void setUpper(int i) {
        while (true) {
            IntPair oldValue = values.get();
            if (i < oldValue.low)
                throw new IllegalArgumentException();
            IntPair newValue = new IntPair(oldValue.low,i);
            if (values.compareAndSet(oldValue, newValue))
                return;
        }
    }

    public boolean contains(int i) {
        return getLower() <= i && i <= getUpper();
    }
}
