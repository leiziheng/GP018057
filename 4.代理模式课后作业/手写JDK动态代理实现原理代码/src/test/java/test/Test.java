package test;

public class Test {
    public static void main(String[] args) {

        String s = "abcabcbb";
        int count = 0;

        char[] chars = s.toCharArray();
        System.out.println(chars);
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < chars.length; j++) {
                if (chars[i] == chars[j] && i != j) {
                    count++;
                }
            }
        }
        System.out.println(count);
    }
}
