package pl.pw.mini.mininavigator.utilities;

public class Array {

    public static Integer[] toIntegerArray(int[] array) {
        Integer[] output = new Integer[array.length];

        for (int i = 0; i < array.length; i++) {
            output[i] = array[i];
        }

        return output;
    }

    public static int[] toIntArray(Integer[] array) {
        int[] output = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            output[i] = array[i];
        }

        return output;
    }
}
