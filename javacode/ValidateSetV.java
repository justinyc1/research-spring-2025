package javaCode;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class ValidateSetV {
    public static void main(String[] args) throws ProjectException {
        validateSetV(5, 2);
        validateSetV(9, 2);
        validateSetV(15, 2);
    }

    /**
     * 
     * @param m where m is: odd, equal to p*q where p and q are different primes, or equal to p^n where n >= 2
     * @param d where d in the range: 1 <= d <= (m-1)/2
     */
    public static void validateSetV(int m, int d) throws ProjectException {
        if (m % 2 == 0) throw new ProjectException("m is not odd: m = " + m);
        // code that check and ensures that m = p*q
        // code that check and ensures that m = p^n where n >= 2
        if (d < 1) throw new ProjectException("d is not greater than or equal to 1: d = " + d);
        if (d > (m-1)/2) throw new ProjectException("d is not less than or equal to (m-1)/2: d = " + d + ", (m-1)/2 = " + (m-1)/2);

        Set<Integer> Z_mod_m_Z = new HashSet<>();
        Set<Integer> Z_mod_m_Z_star = new HashSet<>();
        for (int i = 0; i < m; ++i) {
            Z_mod_m_Z.add(i);
            if (gcd(i, m) == 1) {
                Z_mod_m_Z_star.add(i);
            }
        }

        System.out.println(Z_mod_m_Z.toString());
        System.out.println(Z_mod_m_Z_star.toString());



        System.out.println("no errors");
        return;
    }

    public static int gcd(int a, int b) {
        if (a == 0) return b;
        return gcd(b % a, a);
    }
}