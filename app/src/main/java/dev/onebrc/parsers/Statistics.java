package dev.onebrc.parsers;


public final class Statistics {

    public int min = Integer.MAX_VALUE;
    public int max = Integer.MIN_VALUE;
    public int sum = 0;
    public int count = 0;


    public void record(int value) {
        min = Math.min(min, value);
        max = Math.max(max, value);
        sum += value;
        count++;
    }


    public void mergeIn(Statistics other) {
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
        sum += other.sum;
        count += other.count;
    }

}
