import java.text.DecimalFormat;

public class ArrayDeque<T> implements Deque<T> {
    private T[] items;
    private int size;  // the size of the array currently available
    private double usageFactor = 0.25;
    private int fp;    // front pointer
    private int ep;    // end pointer
    private int num;   // current length

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 8;
        fp = -1;
        ep = -1;
        num = 0;
    }

    private int incPointer(int pointer) {
        if (pointer == size - 1) {
            pointer = 0;
        } else {
            pointer += 1;
        }
        return pointer;
    }

    private int decPointer(int pointer) {
        if (pointer == 0) {
            pointer = size - 1;
        } else {
            pointer -= 1;
        }
        return pointer;
    }

    @Override
    public void addFirst(T item) {
        if (fp < 0) {
            fp = 0;
            ep = 0;
        }
        fp = decPointer(fp);
        items[fp] = item;
        num += 1;
        if (fp == ep) {
            resize(size * 2);
        }
    }

    @Override
    public void addLast(T item) {
        if (fp < 0) {
            fp = 0;
            ep = 0;
        }
        items[ep] = item;
        ep = incPointer(ep);
        num += 1;
        if (fp == ep) {
            resize(size * 2);
        }
    }

    @Override
    public T removeFirst() {
        if (num == 0) {
            return null;
        }
        T ret = get(0);
        items[fp] = null;
        num -= 1;
        if (num == 0) {
            fp = -1;
            ep = -1;
        } else {
            fp = incPointer(fp);
        }
        DecimalFormat df = new DecimalFormat("0.0000");
        if (size >= 16 && Float.parseFloat(df.format((float) num / size)) < usageFactor) {
            resize(appropriateSize(num));
        }
        return ret;
    }

    @Override
    public T removeLast() {
        if (num == 0) {
            return null;
        }
        T ret = get(num - 1);
        num -= 1;
        ep = decPointer(ep);
        items[ep] = null;
        if (num == 0) {
            fp = -1;
            ep = -1;
        }
        DecimalFormat df = new DecimalFormat("0.0000");
        if (size >= 16 && Float.parseFloat(df.format((float) num / size)) < usageFactor) {
            resize(appropriateSize(num));
        }
        return ret;
    }

    @Override
    public int size() {
        return num;
    }

    @Override
    public boolean isEmpty() {
        return (num == 0);
    }

    @Override
    public T get(int index) {
        if (index >= num || index < 0) {
            return null;
        }
        if (fp <= size - 1 - index) {
            return items[fp + index];
        } else {
            return items[fp + index - size];
        }
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < num; i++) {
            System.out.print(get(i));
            if (i != num - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    private void resize(int newSize) {
        T[] a = (T[]) new Object[newSize];
        if (num + fp <= size) {
            System.arraycopy(items, fp, a, 0, num);
        } else {
            System.arraycopy(items, fp, a, 0, size - fp);
            System.arraycopy(items, 0, a, size - fp, num - size + fp);
        }
        fp = 0;
        ep = num;
        size = newSize;
        items = a;
    }

    private int appropriateSize(int number) {
        return (number + 1) * 2;
    }
}
