package javaCode;
import java.util.Set;
import java.util.HashSet;

public class ValidateSetV {
    public static void main(String[] args) throws ProjectException {
        validateSetV(5, 2);
        validateSetV(9, 2);
        validateSetV(15, 3);
    }

    /**
     * 
     * @param m where m is: odd, equal to p*q where p and q are different primes, or equal to p^n where n >= 2
     * @param d where d in the range: 1 <= d <= (m-1)/2
     */
    public static void validateSetV(int m, int d) throws ProjectException {

        if (m % 2 == 0) throw new ProjectException("m is not odd: m = " + m);
        // to be implemented: code that check and ensures that m = p*q
        // to be implemented: code that check and ensures that m = p^n where n >= 2
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

        // deal with B_m^n and U_m^n where n:
        int n = (2 * d) - 2;
        // the U set contains alpha sets, where each alpha set 
        // for set alpha, range of each term: 1 <= a_i <= m-1
        int alphaSize = n + 2; // a_0, a_1, ... a_n, a_n+1
        Set<HashSet<Integer>> U_set = new HashSet<>(); // contains all valid alpha sets

        // int ZmmZs_alpha_diff = Z_mod_m_Z_star.size() - alphaSize;
        // int total_possible_combinations = ZmmZs_alpha_diff

        Set<Integer> alpha = new HashSet<>(); 

        /*
         * given m = 9 and d = 2, n = 2
         * Z/mZ* = {1, 2, 4, 5, 7, 8} 
         * alpha = {a_0, a_1, a_2, a_3}
         * |t*a| = (n/2)+1 = 2
         * possible alpha combinations:
         * 1, 2, 4, 5    sum(a_i/m) = 1/9 + 2/9 + 4/9 + 5/9 = (1 + 2 + 4 + 5)/9 = 12/9
         * 1, 2, 4, 7    sum(a_i/m) = 1/9 + 2/9 + 4/9 + 7/9 = (1 + 2 + 4 + 7)/9 = 14/9
         * 1, 2, 4, 8    sum(a_i/m) = 1/9 + 2/9 + 4/9 + 8/9 = (1 + 2 + 4 + 8)/9 = 15/9
         * 1, 2, 5, 7    sum(a_i/m) = 1/9 + 2/9 + 5/9 + 7/9 = (1 + 2 + 5 + 7)/9 = 15/9
         * 1, 2, 5, 8    sum(a_i/m) = 1/9 + 2/9 + 5/9 + 8/9 = (1 + 2 + 5 + 8)/9 = 16/9
         * 1, 2, 7, 8    sum(a_i/m) = 1/9 + 2/9 + 7/9 + 8/9 = (1 + 2 + 7 + 8)/9 = 18/9 = 2
         * 1, 4, 5, 7    sum(a_i/m) = 1/9 + 4/9 + 5/9 + 7/9 = (1 + 4 + 5 + 7)/9 = 17/9
         * 1, 4, 5, 8    sum(a_i/m) = 1/9 + 4/9 + 5/9 + 8/9 = (1 + 4 + 5 + 8)/9 = 18/9 = 2
         * 1, 4, 7, 8    sum(a_i/m) = 1/9 + 4/9 + 7/9 + 8/9 = (1 + 4 + 7 + 8)/9 = 20/9
         * 1, 5, 7, 8    sum(a_i/m) = 1/9 + 5/9 + 7/9 + 8/9 = (1 + 5 + 7 + 8)/9 = 21/9
         * 2, 4, 5, 7    sum(a_i/m) = 2/9 + 4/9 + 5/9 + 7/9 = (2 + 4 + 5 + 7)/9 = 18/9 = 2
         * 2, 4, 5, 8    sum(a_i/m) = 2/9 + 4/9 + 5/9 + 8/9 = (2 + 4 + 5 + 8)/9 = 19/9
         * 2, 4, 7, 8    sum(a_i/m) = 2/9 + 4/9 + 7/9 + 8/9 = (2 + 4 + 7 + 8)/9 = 21/9
         * 2, 5, 7, 8    sum(a_i/m) = 2/9 + 5/9 + 7/9 + 8/9 = (2 + 5 + 7 + 8)/9 = 22/9
         * 4, 5, 7, 8    sum(a_i/m) = 4/9 + 5/9 + 7/9 + 8/9 = (4 + 5 + 7 + 8)/9 = 24/9
         * 
         * idea: do a nested (possibly use recursion) loop, for each pass use the values given and calculate if divisible by m, then subtract it from the sum for next possible set
         */

        // findAllCombinations(Z_mod_m_Z_star, alphaSize);
        recursivelyFindAllCombinations(U_set, Z_mod_m_Z_star, m, alphaSize);
        System.out.println("Print U set: " + U_set);

        System.out.println("no errors");
        return;
    }

    public static void findAllCombinations(Set<Integer> Z_mod_m_Z_star, int alphaSize) {
        HashSet<Integer> thisSet = new HashSet<>();
        int sum = 0;
        Object[] Z_mod_m_Z_star_array = Z_mod_m_Z_star.toArray();
        for (int i = 0; i < Z_mod_m_Z_star.size() - alphaSize + 1; ++i) {
            thisSet.add((int)Z_mod_m_Z_star_array[i]);
            sum += (int)Z_mod_m_Z_star_array[i];
            for (int j = i+1; j < Z_mod_m_Z_star.size() - alphaSize + 2; ++j) {
                thisSet.add((int)Z_mod_m_Z_star_array[j]);
                sum += (int)Z_mod_m_Z_star_array[j];
                for (int k = j+1; k < Z_mod_m_Z_star.size() - alphaSize + 3; ++k) {
                    thisSet.add((int)Z_mod_m_Z_star_array[k]);
                    sum += (int)Z_mod_m_Z_star_array[k];
                    for (int h = k+1; h < Z_mod_m_Z_star.size() - alphaSize + 4; ++h) {
                        thisSet.add((int)Z_mod_m_Z_star_array[h]);
                        sum += (int)Z_mod_m_Z_star_array[h];
                        if (thisSet.size() == alphaSize) System.out.println(thisSet.toString() + "     sum: " + sum + "    i,j,k,h: " + i + "," + j + "," + k + "," + h);
                        thisSet.remove((int)Z_mod_m_Z_star_array[h]);
                        sum -= (int)Z_mod_m_Z_star_array[h];
                    }
                    thisSet.remove((int)Z_mod_m_Z_star_array[k]);
                    sum -= (int)Z_mod_m_Z_star_array[k];
                }
                thisSet.remove((int)Z_mod_m_Z_star_array[j]);
                sum -= (int)Z_mod_m_Z_star_array[j];
            }
            thisSet.remove((int)Z_mod_m_Z_star_array[i]);
            sum -= (int)Z_mod_m_Z_star_array[i];
        }
    }

    public static void recursivelyFindAllCombinations(Set<HashSet<Integer>> U_set, Set<Integer> Z_mod_m_Z_star, int m, int alphaSize) {
        Object[] ZmmZs_array = Z_mod_m_Z_star.toArray();
        HashSet<Integer> thisSet = new HashSet<>();
        int sum = 0;
        rFAC(U_set, ZmmZs_array, m, alphaSize, thisSet, sum, 0, 1);
    }

    private static void rFAC(Set<HashSet<Integer>> U_set, Object[] ZmmZs_array, int m, int alphaSize, HashSet<Integer> thisSet, int sum, int begin, int depth) {
        if (depth > alphaSize) return;

        for (int i = begin; i < ZmmZs_array.length - alphaSize + depth; ++i) {
            thisSet.add((int)ZmmZs_array[i]);
            sum += (int)ZmmZs_array[i];
            if (thisSet.size() == alphaSize) System.out.println(thisSet.toString() + "   sum: " + sum);
            if (thisSet.size() == alphaSize && sum % m == 0) U_set.add(new HashSet<Integer>(thisSet));
            rFAC(U_set, ZmmZs_array, m, alphaSize, thisSet, sum, i+1, depth+1);
            thisSet.remove((int)ZmmZs_array[i]);
            sum -= (int)ZmmZs_array[i];
        }
    }

    public static int gcd(int a, int b) {
        if (a == 0) return b;
        return gcd(b % a, a);
    }
}