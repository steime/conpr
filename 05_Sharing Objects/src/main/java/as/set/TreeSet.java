package as.set;

public class TreeSet<E extends Comparable<E>> implements ITreeSet<E> {

    public final E root;
    public final ITreeSet<E> left;
    public final ITreeSet<E> right;

    public TreeSet() {
        root = null;
        left = null;
        right = null;
    }

    public TreeSet(E root, ITreeSet<E> left, ITreeSet<E> right) {
        this.root = root;
        this.left = left;
        this.right = right;
    }

    public static <E extends Comparable<E>> ITreeSet<E> empty() {
        return new TreeSet<>();
    }

    @Override
    public ITreeSet<E> insert(E e) {
        boolean contains = contains(e);
        if (contains) {
            return this;
        } else {
            if (isEmpty()) {
                return  new TreeSet<>(e,empty(),empty());
            }
            int res =  root.compareTo(e);
            if (res > 0) {
                return new TreeSet<>(e, this, empty());
            } else {
                return new TreeSet<>(e, empty(), this);
            }
        }
    }

    @Override
    public boolean contains(E e) {
        if (isEmpty()) {
            return false;
        }
        int res =  root.compareTo(e);
        if (res == 0) {
            return true;
        } else if (res > 0) {
            return right.contains(e);
        } else {
            return left.contains(e);
        }
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }
}
