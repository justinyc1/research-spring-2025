package javacode; // **remove this if this file is NOT in a folder called 'javacode'

/* TODO: 
 * look into exceptional cycles
 * extract code into methods
 * 
 */

//===== libraries that are used for this program =====
import java.util.Set;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
import java.util.ArrayList;

public class Project {
    public static void main(String[] args) throws ProjectException {
        // validate_set_V(5, 2);
        // validate_set_V(9, 2);
        // validate_set_V(15, 2);
        
        // validate_set_V(21, 2);

        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\\R"); // a single enter press is now the separator.
        for (int i = 5; i < 200; ++i) { 
            for (int j = 2; j <= i/2 - 1; ++j) {    
                try {
                    validate_set_V(i, j); // m = 21, 2 <= d <= 8 is very interesting
                } catch (ProjectException e) {
                    continue;
                }
                sc.next();
            }
        }

        // System.out.println(nCr(3, 1));
        // System.out.println(nCr(3, 2));
    }

    /**
     * TODO: possibly rename the method to a more meaningful name
     * @param m - An positve odd integer
     * @param d - An positive integer in the range: 1 <= d <= (m-1)/2
     */
    public static void validate_set_V(int m, int d) throws ProjectException {
        System.out.println("Running method validate_set_V(" + m + ", " + d + "):\n");
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

        Set<Tuple<Integer>> U_set = new HashSet<>(); // contains all alpha tuples that are valid for the U set
        find_all_valid_alpha_combinations(U_set, Z_mod_m_Z, m, alpha_length);
        // by this point the U_set will have all valid alpha tuples


        // B_m^n := {alpha in U_m^n such that |t * alpha| = n/2 +1 for all t in Z/mZ*}
        Set<Tuple<Integer>> B_set = new HashSet<>(); // contains all alpha tuples that are valid for the B set
        int n_halved_plus_one = (n / 2) + 1;
        // System.out.println("n/2 + 1 = " + n_halved_plus_one);//DEBUG

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

                // System.out.println("for tuple " + alpha_tuple + " and t = " + t + ", t*a = " + t_times_alpha_reduced_elements + " and |t*a| = " + t_times_alpha_reduced_sum);//DEBUG
                // System.out.printf("for tuple %-16s and t = %2d, t*a = %-16s and |t*a| = %1.0f\n", alpha_tuple.toString(), t, t_times_alpha_reduced_elements.toString(), t_times_alpha_reduced_sum);//DEBUG
                if (t_times_alpha_reduced_sum != n_halved_plus_one) {
                    this_alpha_tuple_is_valid = false;
                    break; // if |t * alpha| != n/2 +1 for just one t, this alpha tuple is not valid for the B set
                }
            }
            if (this_alpha_tuple_is_valid) {
                B_set.add(alpha_tuple);
            }
        }

        //because all alpha tuples in B set is already in ascending order (due to the find all combination algorithm), all alpha tuples in set B are valid tuples for set V
        Set<Tuple<Integer>> V_set = new HashSet<>(); // contains all tuples from the B set that are valid for the V set
        // TODO: use normal for loop for faster speed
        for (Tuple<Integer> tuple : B_set) {
            // if the tuple is in ascending order, in the range [1, m-1], it is valid tuple for V set
            V_set.add(tuple); // every tuple in B set is already in ascending order
        }


        /* Exceptional Cycles:
         *   an exceptional cycle is a tuple that is not coming exclusively from pairs that add to m
         *   example: for tuple (1,4,6,7), it does not contain exclusively pairs that add to m, in fact there are no pairs that add to m at all
         *   thus: a tuple is an exceptional cycle if it is not only pairs of elements that add to m; if there is at least 1 value that don't have a pair, then it is not an exceptional cycle
         * 
         *   guess: if m is prime, there are no exceptional cycles
         */
        Set<Tuple<Integer>> exceptional_cycles = new HashSet<>(); // contains all tuples from the V set that are exceptional cycles
        // TODO: should be a way to make this faster? (actually prob not)
        for (Tuple<Integer> tuple : V_set) { // for each tuple
            // System.out.println("for tuple " + tuple); //DEBUG

            for (int i = 0; i < tuple.size(); ++i) { // for an element at i
                boolean ith_tuple_has_pair = false;

                // for any element at i, j loop makes sure to set ith_element_has_pair to true if found a pair, or ith_element_has_pair remains false, which means the tuple is an exceptional cycle
                for (int j = 0; j < tuple.size(); ++j) { // check every element (as j)
                    // System.out.print("  at i: " + tuple.get(i) + "  at j: " + tuple.get(j) + "  and m = " + m); //DEBUG
                    if ((tuple.get(i) + tuple.get(j)) % m == 0) {
                        // System.out.println("   PAIR FOUND"); //DEBUG
                        ith_tuple_has_pair = true;
                        break;
                    }
                    // System.out.println();
                }

                if (!ith_tuple_has_pair) {
                    exceptional_cycles.add(tuple);
                    break;
                }

            }

        }


        System.out.println("Maximum alpha tuple combinations possible for the U set: " + factorial(Z_mod_m_Z.size() - 1));

        System.out.println();
        System.out.println("Print \"reduced\" \u001b[31mU\u001b[0m set (contains \u001b[31m" + U_set.size() + "\u001b[0m tuples): " + U_set); //DEBUG
        
        System.out.println();
        System.out.println("Print \"reduced\" \u001b[31mB\u001b[0m set (contains \u001b[31m" + B_set.size() + "\u001b[0m tuples): " + B_set); //DEBUG
        
        System.out.println();
        System.out.println("Print \u001b[31mV\u001b[0m set (contains \u001b[31m" + V_set.size() + "\u001b[0m tuples): " + V_set); //DEBUG
        
        System.out.println();
        System.out.println("Print \u001b[31mexceptional cycles\u001b[0m (contains \u001b[31m" + exceptional_cycles.size() + "\u001b[0m tuples): " + exceptional_cycles); //DEBUG

        System.out.println("\nmethod validate_set_V(" + m + ", " + d + ") ran to completion.");
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

            // if (this_combination.size() == alpha_length) System.out.println(this_combination.toString() + "   sum: " + sum); //DEBUG
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

    public static int factorial(int n) {
        if (n == 1) return 1;
        return n * factorial(n-1);
    }

    public static int nCr(int n, int r) {
        return factorial(n)/(factorial(r) * factorial(n-r));
    }
}
