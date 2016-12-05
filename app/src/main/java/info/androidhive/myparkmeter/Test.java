package info.androidhive.myparkmeter;

/**
 * Created by raul on 08/11/2016.
 */

public class Test {

    public static void main (String args[]) {
        String test1 = "cbaabbb";

        int total = 0;
        /*for (int i = 0; i < test1.length(); i++) {
            System.out.println(test1.charAt(i));
        }*/
        //System.out.println(isPalindrome("abccba"));
        /*for (int i = 0; i < test1.length(); i++) {
            //System.out.println(test1.charAt(i));
            System.out.println(isPalindrome("" + test1.charAt(i)));
        }*/
        System.out.println("---------------1--------------------");
        for (int i = 0; i < test1.length() - 1; i++) {
            //System.out.println(test1.charAt(i));
            //System.out.println(isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1)));
            if (isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1))) {
                total++;
            }
        }
        System.out.println("---------------2--------------------");
        for (int i = 0; i < test1.length() - 2; i++) {
            //System.out.println(test1.charAt(i));
            try {
                //System.out.println(isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2)));
                if (isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2))) {
                    total++;
                }
            } catch (Exception e) {}
        }
        System.out.println("---------------3--------------------");
        for (int i = 0; i < test1.length() - 3; i++) {
            //System.out.println(test1.charAt(i));
            try {
                //System.out.println(isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3)));
                if (isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3))) {
                    total++;
                }
            } catch (Exception e) {}
        }
        System.out.println("---------------4--------------------");
        for (int i = 0; i < test1.length() - 4; i++) {
            //System.out.println(test1.charAt(i));
            try {
                //System.out.println(isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3) + test1.charAt(i + 4)));
                if (isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3) + test1.charAt(i + 4))) {
                    total ++;
                }
            } catch (Exception e) {}
        }
        System.out.println("---------------5--------------------");
        for (int i = 0; i < test1.length() - 5; i++) {
            //System.out.println(test1.charAt(i));
            try {
                //System.out.println(isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3) + test1.charAt(i + 4) + test1.charAt(i + 5)));
                if (isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3) + test1.charAt(i + 4) + test1.charAt(i + 5))) {
                    total ++;
                }
            } catch (Exception e) {}
        }
        System.out.println("---------------6--------------------");
        for (int i = 0; i < test1.length() - 6; i++) {
            //System.out.println(test1.charAt(i));
            try {
                //System.out.println(isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3) + test1.charAt(i + 4) + test1.charAt(i + 5)));
                if (isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3) + test1.charAt(i + 4) + test1.charAt(i + 5) + test1.charAt(i + 6))) {
                    total ++;
                }
            } catch (Exception e) {}
        }
        System.out.println("---------------7--------------------");
        for (int i = 0; i < test1.length() - 7; i++) {
            //System.out.println(test1.charAt(i));
            try {
                //System.out.println(isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3) + test1.charAt(i + 4) + test1.charAt(i + 5)));
                if (isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3) + test1.charAt(i + 4) + test1.charAt(i + 5) + test1.charAt(i + 6) + test1.charAt(i + 7))) {
                    total ++;
                }
            } catch (Exception e) {}
        }
        System.out.println("---------------8--------------------");
        for (int i = 0; i < test1.length() - 8; i++) {
            //System.out.println(test1.charAt(i));
            try {
                //System.out.println(isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3) + test1.charAt(i + 4) + test1.charAt(i + 5)));
                if (isPalindrome("" + test1.charAt(i) + test1.charAt(i + 1) + test1.charAt(i + 2) + test1.charAt(i + 3) + test1.charAt(i + 4) + test1.charAt(i + 5) + test1.charAt(i + 6) + test1.charAt(i + 7) + test1.charAt(i + 8))) {
                    total ++;
                }
            } catch (Exception e) {}
        }
        System.out.println((total + test1.length()));
    }

    public static boolean isPalindrome(String string) {
        if (string == null) {
            return false;
        }
        System.out.println(string);
        int left  = 0;
        int right = string.length() - 1;
        while (left < right) {
            if (string.charAt(left) != string.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

}
