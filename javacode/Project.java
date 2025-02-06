package javacode; // **remove this if this file is NOT in a folder called 'javacode'

/* TODO: 
 * look into exceptional cycles
 * extract code into methods
 * 
 */

//===== libraries that are used for this program =====
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

public class Project {
    public static void main(String[] args) throws ProjectException {
        // validate_set_V(5, 2);
        // validate_set_V(9, 2);
        validate_set_V(15, 2);
    }

    /**
     * TODO: possibly rename the method to a more meaningful name
     * @param m - An positve odd integer
     * @param d - An positive integer in the range: 1 <= d <= (m-1)/2
     */
    public static void validate_set_V(int m, int d) throws ProjectException {
        validate_m_and_d(m, d);

        //TODO: extract this section into a method
        //===== create and put valid values in 'Z/mZ' and 'Z/mZ*' set =====
        Set<Integer> Z_mod_m_Z = new HashSet<>();
        Set<Integer> Z_mod_m_Z_star = new HashSet<>();
        for (int i = 0; i < m; ++i) {
            Z_mod_m_Z.add(i);
            if (gcd(i, m) == 1) {
                Z_mod_m_Z_star.add(i);
            }
        }

        // System.out.println(Z_mod_m_Z.toString()); //DEBUG
        // System.out.println(Z_mod_m_Z_star.toString()); //DEBUG


        // deal with B_m^n and U_m^n where n:
        int n = (2 * d) - 2;
        // the U set contains alpha sets, where each alpha set 
        // for set alpha, range of each term: 1 <= a_i <= m-1
        int alpha_length = n + 2; // a_0, a_1, ... a_n, a_n+1

        Set<Tuple<Integer>> U_set = new HashSet<>(); // contains all alpha sets that are valid for the U set
        find_all_valid_alpha_combinations(U_set, Z_mod_m_Z, m, alpha_length);
        // by this point the U_set will have all valid alpha tuples


        // B_m^n := {alpha in U_m^n such that |t * alpha| = n/2 +1 for all t in Z/mZ*}
        Set<Tuple<Integer>> B_set = new HashSet<>(); // contains all alpha sets that are valid for the B set
        int n_halved_plus_one = (n / 2) + 1;
        System.out.println("n/2 + 1 = " + n_halved_plus_one);

        // TODO: could use regular for loops for slightly faster time
        for (Tuple<Integer> alpha_tuple : U_set) {
            boolean this_alpha_tuple_is_valid = true;
            for (int t : Z_mod_m_Z_star) {

                List<Integer> t_times_alpha_reduced_elements = new ArrayList<>();
                double t_times_alpha_reduced_sum = 0;

                for (int i = 0; i < alpha_tuple.size(); ++i) {
                    int reduced_mod_m = (t * alpha_tuple.get(i)) % m;
                    t_times_alpha_reduced_elements.add(reduced_mod_m);
                    t_times_alpha_reduced_sum += reduced_mod_m;
                }
                t_times_alpha_reduced_sum /= m;

                System.out.println("for tuple " + alpha_tuple + " and t = " + t + ", t*a = " + t_times_alpha_reduced_elements + " and |t*a| = " + t_times_alpha_reduced_sum);//DEBUG
                if (t_times_alpha_reduced_sum != n_halved_plus_one) {
                    this_alpha_tuple_is_valid = false;
                    break; // if |t * alpha| != n/2 +1 for just one t, this alpha tuple is not valid for the B set
                }
            }
            if (this_alpha_tuple_is_valid) {
                B_set.add(alpha_tuple);
            }
        }

        // System.out.println("Print U set: " + U_set); //DEBUG
        System.out.println("U set size: " + U_set.size()); //DEBUG
        
        System.out.println("Print B set: " + B_set); //DEBUG
        System.out.println("B set size: " + B_set.size()); //DEBUG

        System.out.println("method validate_set_V(" + m + ", " + d + ") ran to completion.");
        return;
    }

    /** Makes sure m and d are valid 
     * 
     * @param m where m is: odd, or equal to p*q (where p and q are different primes), or equal to p^n where n >= 2
     * @param d where d is in the range: 1 <= d <= (m-1)/2
     * @throws ProjectException
     */
    public static void validate_m_and_d(int m, int d) throws ProjectException {
        //===== =====
        if (m % 2 == 0) throw new ProjectException("m is not odd: m = " + m);
        // TODO: code that check and ensures that m = p*q
        // TODO: code that check and ensures that m = p^n where n >= 2
        if (d < 1) throw new ProjectException("d is not greater than or equal to 1: d = " + d);
        if (d > (m-1)/2) throw new ProjectException("d is not less than or equal to (m-1)/2: d = " + d + ", (m-1)/2 = " + (m-1)/2);
    }

    /** Calls the recursive algorithm to find all valid ascending combinations of alpha tuple
     * 
     * @param U_set - the set that contains all valid alpha tuples
     * @param Z_mod_m_Z
     * @param m
     * @param alpha_length - length of each alpha tuple, determined by n
     */
    public static void find_all_valid_alpha_combinations(Set<Tuple<Integer>> U_set, Set<Integer> Z_mod_m_Z, int m, int alpha_length) {
        Object[] ZmmZ_array = Z_mod_m_Z.toArray();
        List<Integer> this_combination = new ArrayList<>();
        int sum = 0;
        recursively_find_all(U_set, ZmmZ_array, m, alpha_length, this_combination, sum, 1, 1);
    }

    /** Recursively find all valid ascending combinations of alpha tuple, using the values of Z/mZ (excluding 0)
     * 
     * @param U_set - the set that contains all valid alpha tuples
     * @param ZmmZ_array - Z/mZ as an array
     * @param m 
     * @param alpha_length - length of each alpha tuple, determined by n
     * @param this_combination - a List that contains all the elements in the alpha tuple for the current loop
     * @param sum - stores the sum of the elements in this_combination
     * @param begin - the index of the smallest value that this current position can be
     * @param depth - the current level of the recursion method
     */
    private static void recursively_find_all(Set<Tuple<Integer>> U_set, Object[] ZmmZ_array, int m, int alpha_length, List<Integer> this_combination, int sum, int begin, int depth) {
        if (depth > alpha_length) return;

        for (int i = begin; i < ZmmZ_array.length - alpha_length + depth; ++i) {
            this_combination.add((int)ZmmZ_array[i]);
            sum += (int)ZmmZ_array[i];

            if (this_combination.size() == alpha_length) System.out.println(this_combination.toString() + "   sum: " + sum); //DEBUG
            if (this_combination.size() == alpha_length && sum % m == 0) U_set.add(new Tuple<Integer>(this_combination));
            
            recursively_find_all(U_set, ZmmZ_array, m, alpha_length, this_combination, sum, i+1, depth+1);
            
            this_combination.remove((Integer)ZmmZ_array[i]);
            sum -= (int)ZmmZ_array[i];
        }
    }

    /** Return the gcd of a and b using the euclidean algorithm
     * 
     * @param a - an integer
     * @param b - an integer
     * @return the greatest common divisor of a and b
     */
    public static int gcd(int a, int b) {
        if (a == 0) return b;
        return gcd(b % a, a);
    }
}

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