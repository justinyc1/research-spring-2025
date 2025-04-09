package JC_code.javacode; // **remove this if this file is NOT in a folder called 'javacode'

/* TODO: 
 * look into exceptional cycles
 * extract code into methods
 * 
 * Write code that can identify all indecomposable cycles in V^d_m.
 * Generate some data, for example:
 *   Start with m = p, where p is prime, How large can p get before the computation time is too much? Would we get any exceptional cycles? (we shouldn’t?) Are there indecomposable elements? (there shouldn’t be?)
 *   Try m = p^2, where p is prime. (i.e. m = 9). How many exceptional cycles would we see, and for what values of d? Are there any patterns?
 *   Are there patterns if we fix the prime p and consider m = p, p^2, p^3, ...
 * Try to convert the recursive algorithm for finding all d-length ascending-order combinations for the set U^n_m into an iterative algorithm.
 * Think about ways to ’remember’ data to possibly increase efficiency and for data reusability.
 * Try to optimize the code in any reasonable way for better time efficiency.
 * Consider outputting program runtime to keep track of time efficiency as inputs gets larger.
 * Consider trying to identify the time complexity of the algorithms.
 * Explore the second conjecture, possibly writing some code that can test it.
 * Organize the GitHub repository.
 * 
 */
//===== libraries that are used for this program =====
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Iterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

public class Project {
    static long startTimeInNano = -1;
    static int maxSecondsAllowed = -1;
    static boolean skipToNextM = false;
    static boolean allowOverwrite = false;
    static boolean validate_split_in_halves = false;
    static int valid_split_in_half_count = 0;
    static PrintWriter debugOutput = null;
    public static void main(String[] args) throws ProjectException, IOException {
        // validate_set_V(5, 2);
        // validate_set_V(45, 4);
        // validate_set_V(69, 4);
        
        // validate_set_V(27, 4, true);
        // validate_set_V(77, 4, true);

        // test_all_m_and_d_combinations(49, 49, 15, 15, true, true, false, -1, false);

        test_all_m_and_d_combinations(3, 3, 1, 999, false, true, false, 600, false);
        

        // Tuple myTuple1 = new Tuple(new int[] {1,2,3,4,5,6,7,8,9});
        // for (int i = 0; i < myTuple1.size(); i++) { System.out.println("index of " + i + " is " + myTuple1.indexOf(i)); }
    }

    public static void test_all_m_and_d_combinations(int m_start, int m_end, int d_start, int d_end) throws IOException {
        test_all_m_and_d_combinations(m_start, m_end, d_start, d_end, false, false, false, -1, false);
    }

    public static void test_all_m_and_d_combinations(int m_start, int m_end, int d_start, int d_end, boolean print_outputs) throws IOException {
        test_all_m_and_d_combinations(m_start, m_end, d_start, d_end, print_outputs, false, false, -1, false);
    }

    public static void test_all_m_and_d_combinations(int m_start, int m_end, int d_start, int d_end, boolean print_outputs, boolean automated) throws IOException {
        test_all_m_and_d_combinations(m_start, m_end, d_start, d_end, print_outputs, automated, false, -1, false);
    }

    public static void test_all_m_and_d_combinations(int m_start, int m_end, int d_start, int d_end, boolean print_outputs, boolean automated, boolean overwrite_outputs, int max_seconds_allowed, boolean validate_halves) throws IOException {
        allowOverwrite = overwrite_outputs;
        maxSecondsAllowed = max_seconds_allowed;
        validate_split_in_halves = validate_halves;
        Scanner sc = new Scanner(System.in);
        if (!automated) System.out.println("Press the Enter Key to process the next m and d values");
        sc.useDelimiter("\r"); // a single enter press is now the separator.
        for (int i = m_start; i <= m_end; ++i) {
            skipToNextM = false;
            for (int j = d_start; j <= (i-1)/2 && j <= d_end; ++j) {
                FileHelper.deleteAllEmptyFiles(new File(FileHelper.outputsDir)); // delete empty files
                if (skipToNextM) {
                    System.out.println("Skipping m = " + i);
                    break;
                }
                System.out.println("Starting time for m = " + i + ", d = " + j + " is " + new Date());
                try {
                    validate_set_V(i, j, print_outputs); // m = 21, 2 <= d <= 8 is very interesting
                } catch (ProjectException e) {
                    // e.printStackTrace(); // DEBUG
                    continue;
                }
                System.out.println("Ending time for m = " + i + ", d = " + j + " is " + new Date());
                if (!automated) sc.next();
            } 
        }
        sc.close();
    }

    public static void validate_set_V(int m, int d) throws ProjectException, IOException {
        validate_set_V(m, d, false);
    }

    /**
     * TODO: possibly rename the method to a more meaningful name
     * @param m - An positve odd integer
     * @param d - An positive integer in the range: 1 <= d <= (m-1)/2
     */
    public static void validate_set_V(int m, int d, boolean print_outputs) throws ProjectException, IOException {
        System.out.println("Running method validate_set_V(" + redString("m = ", m) + ", " + redString("d = ", d) + "):\n");
        
        validate_m_and_d(m, d);

        String filepath = "JC_code\\outputs\\";
        
        String filename = "output_for_m_" + m + "_d_" + d + ".txt";
        File currentFile = new File(filepath + filename);
        if (print_outputs && !allowOverwrite && currentFile.exists() && !currentFile.isDirectory()) {
            throw new ProjectException("overwrite_outputs disabled and printing to file enabled, while file for m = " + m + ", d = " + d + " already exists.");
        }
        PrintWriter pw = new PrintWriter(filepath + "test.txt");
        if (print_outputs) {
            pw = new PrintWriter(currentFile);
        }

        if (print_outputs) pw.println("Running method validate_set_V(m = " + m + ", d = " + d + "):\n");

        long startTime = System.nanoTime();
        startTimeInNano = startTime;

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

        // System.out.println("Z/mZ:  " + Z_mod_m_Z.toString()); //DEBUG
        // System.out.println("Z/mZ*: " + Z_mod_m_Z_star.toString()); //DEBUG

        // deal with B_m^n and U_m^n where n:
        int n = (2 * d) - 2;
        // the U set contains alpha tuples, where each alpha tuple 
        // for alpha tuple, range of each term: 1 <= a_i <= m-1
        int alpha_length = n + 2; // a_0, a_1, ... a_n, a_n+1

        // Set<Tuple> U_set = new HashSet<>(); // contains all alpha tuples that are valid for the U set
        // by this point the U_set will have all valid alpha tuples


        // B_m^n := {alpha in U_m^n such that |t * alpha| = n/2 +1 for all t in Z/mZ*}
        // Set<Tuple> B_set = new HashSet<>(); // contains all alpha tuples that are valid for the B set
        //because all alpha tuples in B set is already in ascending order (due to the find all combination algorithm), all alpha tuples in set B are valid tuples for set V
        Set<Tuple> V_set = new HashSet<>(); // contains all tuples from the B set that are valid for the V set
        find_all_valid_alpha_combinations(V_set, Z_mod_m_Z_star, m, alpha_length);
        // int n_halved_plus_one = (n / 2) + 1;
        // System.out.println("n/2 + 1 = " + n_halved_plus_one);//DEBUG

        // TODO: could use regular for loops for slightly faster time (?)
        // for (Tuple alpha : U_set) {
        //     put_in_V_set_if_valid(V_set, Z_mod_m_Z_star, alpha, m, n_halved_plus_one);
        // }

        // for (Tuple tuple : B_set) {
        //     // if the tuple is in ascending order, in the range [1, m-1], it is valid tuple for V set
        //     V_set.add(tuple); // every tuple in B set is already in ascending order
        // }

        //TODO: can we find pairs by indices? i.e. a_i and a_(n-i) are pairs or there are no pairs
        /* Exceptional Cycles:
         *   an exceptional cycle is a tuple that is not coming exclusively from pairs that add to m
         *   example: for tuple (1,4,6,7), it does not contain exclusively pairs that add to m, in fact there are no pairs that add to m at all
         *   thus: a tuple is an exceptional cycle if it is not only pairs of elements that add to m; if there is at least 1 value that don't have a pair, then it is not an exceptional cycle
         * 
         *   guess: if m is prime, there are no exceptional cycles
         */
        
        
        Set<Tuple> all_are_pairs = new HashSet<>();  // contains all tuples from the V set that have  ONLY PAIRS  adding up to m
        Set<Tuple> some_are_pairs = new HashSet<>(); // contains all tuples from the V set that have  SOME PAIRS  adding up to m (but not all pairs)
        Set<Tuple> none_are_pairs = new HashSet<>(); // contains all tuples from the V set that have  NO PAIRS    adding up to m
        Set<Tuple> indecomposable = new HashSet<>(); // contains all tuples from the V set that have  NO SUBSETS  adding up to m
        Set<Tuple> decomposable_but_no_pairs = new HashSet<>(); // contains all tuples from the V set that HAVE SUBSETS & NO PAIRS  adding up to m
        Set<Tuple> exceptional_cycles = new HashSet<>(); // contains all tuples from the V set that are not made up of exclusively pairs
        // TODO: check code for indecomposable; check indecomposable definitions
        // populate_indecomposable(V_set, indecomposable, m); // TODO: BUG: THIS MAY HAVE CAUSED WRONG indecomposable set VALUES
        
        // TODO: should be a way to make this faster? (actually prob not)
        for (Tuple tuple : V_set) { // for each tuple
            // System.out.println("for tuple " + tuple); //DEBUG

            // some boolean variables to keep track of each tuple's traits
            boolean has_all_pairs = true;  // assume true, if any element don't have a pair, set to false
            boolean has_one_pair = false; // assume false, if any element have a pair, set to true
            boolean has_no_pairs = true;   // assume true, if any element have a pair, set to false
            // TODO: has_one_pair and has_all_pair are inverses; A = B'

            for (int i = 0; i < tuple.size(); ++i) { // for an element at i of tuple
                boolean ith_element_has_pair = false;

                // for any element at i, j loop makes sure to set ith_element_has_pair to true if found a pair, or ith_element_has_pair remains false, which means the tuple is an exceptional cycle
                for (int j = 0; j < tuple.size(); ++j) { // check every element (as j)
                    // System.out.print("  at i: " + tuple.get(i) + "  at j: " + tuple.get(j) + "  and m = " + m); //DEBUG
                    if ((tuple.get(i) + tuple.get(j)) % m == 0) {
                        // System.out.println("   PAIR FOUND"); //DEBUG
                        ith_element_has_pair = true;
                        break;
                    }
                    // System.out.println();
                }

                if (ith_element_has_pair) { // if just one element has a pair then
                    has_no_pairs = false; 
                    has_one_pair = true; // we assume theres at least one pair
                } else { // an element don't have a pair
                    has_all_pairs = false;
                }

            }

            if (has_all_pairs) { // has_all_pairs remains true if every element has a pair
                all_are_pairs.add(tuple);
            } else { // not every element have a pair:
                exceptional_cycles.add(tuple);
                if (has_one_pair) { // if there is at least one pair (but not all elements are pairs) then SOME elements are pairs
                    some_are_pairs.add(tuple);
                } else if (has_no_pairs) { // if no pairs but has subsets, then only add to the no pairs set
                    none_are_pairs.add(tuple);
                }
            }
        }

        StringBuilder no_pair_print_buffer = new StringBuilder();
        int min_subset_size = 4;
        int max_subset_size = 2 * d - 2;

        // TODO: what to do with tuple in no pairs when d = 1,2 (d=1 => 2 elements = all pairs, d=2 =>  )
        // TODO: for d >= 3, check subtuples of 'half size' instead of full size (?)
        if (d >= 3) { // when d = 2 or less, alpha have at most 4 elements, so there is no indecomposables nor decomposable but no pairs 
            for (Tuple alpha : none_are_pairs) { // each element is an alpha with no pairs
                boolean divides_m = false;
                Tuple subtuple = Tuple.EMPTY_TUPLE;
                for (int size = min_subset_size; size <= max_subset_size; size+=2) { // for each possible subtuple length:
                    // go though each possible subtuple combination from alpha to find a subtuple that adds to multiple of m

                    // init subtuple
                    subtuple = alpha.getSubtuple(0, size);
                    // System.out.println("For tuple " + alpha + ", check subtuple " + subtuple);

                    // check init values divides m
                    if (subtuple.sum() % m == 0) {
                        divides_m = true;
                        break;
                    }

                    // check every subset combinations if they divides m
                    while (subtuple != null) {
                        if (subtuple.sum() % m == 0) {
                            // System.out.println("for " + alpha + ", the subtuple " + subtuple + " divides m = " + m); // DEBUG
                            divides_m = true;
                            break;
                        }
                        subtuple = alpha.getNextAscendingTupleAfter(subtuple);
                        // System.out.println("For tuple " + alpha + ", check subtuple " + subtuple);
                    }

                    if (divides_m) break;
                }
            // back to for each alpha
                if (divides_m) {
                    // System.out.println("adding to decomposable but no pairs set: " + alpha + ", since subtuple = " + subtuple);
                    // if (print_outputs) pw.println("adding to decomposable but no pairs set: " + alpha);
                    no_pair_print_buffer.append("adding to decomposable but no pairs set: " + alpha + ", since subtuple = " + subtuple + "\n");
                    decomposable_but_no_pairs.add(alpha);
                    continue;
                } else {
                    // System.out.println("adding to indecomposable set: " + alpha + ", since subtuple = " + subtuple);
                    // if (print_outputs) pw.println("adding to indecomposable set: " + alpha);
                    no_pair_print_buffer.append("adding to indecomposable set: " + alpha + "\n");
                    indecomposable.add(alpha);
                    continue;
                }
            }
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime; // in nano seconds (10^-9)
        
        // derive from nanoseconds elapsed from the start of the calculation operations of the program 
        // long tempSec  = elapsedTime / (1000*1000*1000);
        long allNanoSec  = elapsedTime;
        long allMicroSec =  elapsedTime / 1000;
        long allMiliSec  =  elapsedTime / (1000*1000);
        long allSec      = (elapsedTime / (1000*1000)) / 1000;
        long allMin      = (elapsedTime / (1000*1000)) / (1000*60);
        long allHour     = (elapsedTime / (1000*1000)) / (1000*60*60);
        long allDay      = (elapsedTime / (1000*1000)) / (1000*60*60*24);
        
        long nanoSec     =   allNanoSec % 1000; // in  nano seconds (10^-9)
        long microSec    =  allMicroSec % 1000; // in micro seconds (10^-6)
        long miliSec     =   allMiliSec % 1000; // in  mili seconds (10^-3)
        long sec         =       allSec % 60;
        long min         =       allMin % 60;
        long hour        =      allHour % 24;
        // System.out.println(nanoSec);
        // System.out.println(microSec);
        // System.out.println(miliSec);
        // System.out.println(sec + " seconds");
        // System.out.println(min);
        // System.out.println(hour);
        // System.out.println(allDay);
        String formattedElapsedTime = String.format(
            "%d day%s, %d hour%s, %d minute%s, %d second%s, %d milisecond%s, %d microsecond%s, %d nanosecond%s", 
            allDay, plural(allDay), hour, plural(hour), min, plural(min), sec, plural(sec), 
            miliSec, plural(miliSec), microSec, plural(microSec), nanoSec, plural(nanoSec));

        // System.out.println();
        // System.out.println("Print \"reduced\" " + redString("U") + " set (contains " + redString(U_set.size()) + " tuples): " + U_set); //DEBUG
        
        // System.out.println();
        // System.out.println("Print \"reduced\" " + redString("B") + " set (contains " + redString(B_set.size()) + " tuples): " + B_set); //DEBUG
        
        // System.out.println();
        // System.out.println("Print " + redString("V") + " set (contains " + redString(V_set.size()) + " tuples): " + V_set); //DEBUG
        
        // System.out.println();
        // System.out.println("Print " + redString("all") + "_are_pairs" + " (contains " + redString(all_are_pairs.size()) + " tuples): " + all_are_pairs); //DEBUG
        
        // System.out.println();
        // System.out.println("Print " + redString("some") + "_are_pairs" + " (contains " + redString(some_are_pairs.size()) + " tuples): " + some_are_pairs); //DEBUG
        
        // System.out.println();
        // System.out.println("Print " + redString("none") + "_are_pairs" + " (contains " + redString(none_are_pairs.size()) + " tuples): " + none_are_pairs); //DEBUG
        
        // System.out.println();
        // System.out.println("Print " + redString("indecomposable") + " (contains " + redString(indecomposable.size()) + " tuples): " + indecomposable); //DEBUG
        
        // System.out.println();
        // System.out.println("Print " + redString("decomposable but no pairs") + " (contains " + redString(decomposable_but_no_pairs.size()) + " tuples): " + decomposable_but_no_pairs); //DEBUG
        
        // System.out.println();
        // System.out.println("Print " + redString("exceptional") + " cycles (contains " + redString(exceptional_cycles.size()) + " tuples): " + exceptional_cycles); //DEBUG

        System.out.println();


        // extra summary section, for when sets gets too large (using space for spacing/aligning instead of printf)
        System.out.println("Summary:");
        System.out.println("given " + redString("m = ", m) + ", " + redString("d = ", d));
        System.out.println("Calculations took " + formattedElapsedTime + ".");
        System.out.println("All " + redString("ascending & non-repeating") + " tuple (" + redString("size ", 2*d) + ") combinations possible for the U set: " + find_num_of_ascending_nonrepeating_tuples_in_U_set(Z_mod_m_Z, d));
        System.out.println("All " + redString("ascending & non-repeating") + " tuple (" + redString("size 1 to ", 2*d) + ") combinations possible for the U set: " + (new BigInteger("2").pow(Z_mod_m_Z.size()-1).subtract(BigInteger.ONE)));
        // System.out.println("            " +"\"reduced\" " + redString("U") + " set: contains " + redString(U_set.size()) + " tuples");
        // System.out.println("            " + "\"reduced\" " + redString("B") + " set: contains " + redString(B_set.size()) + " tuples");
        System.out.println("                      " + redString("V") + " set: contains " + redString(V_set.size()) + " tuples");
        System.out.println("          " + redString("all") + "_are_pairs" + " set: contains " + redString(all_are_pairs.size()) + " tuples");
        System.out.println("         " + redString("some") + "_are_pairs" + " set: contains " + redString(some_are_pairs.size()) + " tuples");
        System.out.println("         " + redString("none") + "_are_pairs" + " set: contains " + redString(none_are_pairs.size()) + " tuples");
        System.out.println("         " + redString("indecomposable") + " set: contains " + redString(indecomposable.size()) + " tuples");
        System.out.println(redString("decomposable & no pairs") + " set: contains " + redString(decomposable_but_no_pairs.size()) + " tuples");
        System.out.println("     " + redString("exceptional") + "_cycles" + " set: contains " + redString(exceptional_cycles.size()) + " tuples");

        if (print_outputs) pw.println("Summary:");
        if (print_outputs) pw.println("given m = " + m + ", d = " + d);
        if (print_outputs) pw.println("Calculations took " + formattedElapsedTime + ".");
        if (print_outputs) pw.println("Z/mZ:  " + Z_mod_m_Z.toString());
        if (print_outputs) pw.println("Z/mZ*: " + Z_mod_m_Z_star.toString());
        if (print_outputs) pw.println("All ascending & non-repeating tuple (size " + 2*d + ") combinations possible for the U set: " + find_num_of_ascending_nonrepeating_tuples_in_U_set(Z_mod_m_Z, d));
        if (print_outputs) pw.println("All ascending & non-repeating tuple (size 1 to " + 2*d + ") combinations possible for the U set: " + (new BigInteger("2").pow(Z_mod_m_Z.size()-1).subtract(BigInteger.ONE)));
        // if (print_outputs) pw.println("            " +"\"reduced\" U set: contains " + U_set.size() + " tuples");
        // if (print_outputs) pw.println("            \"reduced\" B set: contains " + B_set.size() + " tuples");
        if (print_outputs) pw.println("                      V set: contains " + V_set.size() + " tuples");
        if (print_outputs) pw.println("          all_are_pairs set: contains " + all_are_pairs.size() + " tuples");
        if (print_outputs) pw.println("         some_are_pairs set: contains " + some_are_pairs.size() + " tuples");
        if (print_outputs) pw.println("         none_are_pairs set: contains " + none_are_pairs.size() + " tuples");
        if (print_outputs) pw.println("         indecomposable set: contains " + indecomposable.size() + " tuples");
        if (print_outputs) pw.println("decomposable & no pairs set: contains " + decomposable_but_no_pairs.size() + " tuples");
        if (print_outputs) pw.println("     exceptional_cycles set: contains " + exceptional_cycles.size() + " tuples");

        // if (print_outputs) pw.println();
        // if (print_outputs) pw.println("Print \"reduced\" U set (contains " + U_set.size() + " tuples): " + toStringSorted(U_set, "\n")); //DEBUG

        // if (print_outputs) pw.println();
        // if (print_outputs) pw.println("Print \"reduced\" B set (contains " + B_set.size() + " tuples): " + toString(B_set, "\n")); //DEBUG

        if (print_outputs) pw.println();
        if (print_outputs) pw.println("Print V set (contains " + V_set.size() + " tuples): " + toString(V_set, "\n")); //DEBUG

        if (print_outputs) pw.println();
        if (print_outputs) pw.println("Print all_are_pairs (contains " + all_are_pairs.size() + " tuples): " + toString(all_are_pairs, "\n")); //DEBUG

        if (print_outputs) pw.println();
        if (print_outputs) pw.println("Print some_are_pairs (contains " + some_are_pairs.size() + " tuples): " + toString(some_are_pairs, "\n")); //DEBUG

        if (print_outputs) pw.println();
        if (print_outputs) pw.println("Print none_are_pairs (contains " + none_are_pairs.size() + " tuples): " + toString(none_are_pairs, "\n")); //DEBUG

        if (print_outputs) pw.println();
        if (print_outputs) pw.println("Print indecomposable (contains " + indecomposable.size() + " tuples): " + toString(indecomposable, "\n")); //DEBUG

        if (print_outputs) pw.println();
        if (print_outputs) pw.println("Print decomposable but no pairs (contains " + decomposable_but_no_pairs.size() + " tuples): " + toString(decomposable_but_no_pairs, "\n")); //DEBUG

        if (print_outputs) pw.println();
        if (print_outputs) pw.println("Print exceptional cycles (contains " + exceptional_cycles.size() + " tuples): " + toString(exceptional_cycles, "\n")); //DEBUG

        if (print_outputs) pw.println();

        if (print_outputs) pw.println("Below are debug outputs for each alpha in V whether it was put in the indecomposable set or the decomposable but no pairs set:");
        if (print_outputs) pw.println(no_pair_print_buffer.toString());

        System.out.println("\nmethod validate_set_V(" + redString(m) + ", " + redString(d) + ") ran to completion.");
        if (print_outputs) pw.println("\nmethod validate_set_V(" + m + ", " + d + ") ran to completion.");

        pw.close();
        return;
    }

    /** Makes sure m and d are valid 
     * 
     * @param m where m is: odd, or equal to p*q (where p and q are different primes), or equal to p^n where n >= 2
     * @param d where d is in the range: 1 <= d <= (m-1)/2
     * @throws ProjectException if m or d is invalid
     */
    public static void validate_m_and_d(int m, int d) throws ProjectException {
        //===== =====
        if (m % 2 == 0) throw new ProjectException("m is not odd: m = " + m);
        if (d < 1) throw new ProjectException("d is not greater than or equal to 1: d = " + d);
        if (d > (m-1)/2) throw new ProjectException("d is not less than or equal to (m-1)/2: d = " + d + ", (m-1)/2 = " + (m-1)/2);
    }

    /** Calls the recursive algorithm to find all valid ascending combinations of alpha tuple
     * 
     * @param V_set
     * @param m - the upper limit (exclusive) of Z/mZ when finding combinations
     * @param alpha_length - length of each alpha tuple, determined by n
     * @throws IOException 
     */
    public static void find_all_valid_alpha_combinations(Set<Tuple> V_set, Set<Integer> ZmmZ_star, int m, int alpha_length) throws ProjectException, IOException {
        int[] this_combination = new int[alpha_length];
        if (validate_split_in_halves) {
            debugOutput = new PrintWriter(new FileWriter("JC_code\\outputs\\" + "debug_output.txt", true));
            recursively_find_all_check_halves(V_set, ZmmZ_star, m, this_combination, 0, 1, (alpha_length-2)/2+1);
            debugOutput.close();
        } else {
            recursively_find_all(V_set, ZmmZ_star, m, this_combination, 0, 1, (alpha_length-2)/2+1);
        }
    }

    /** Recursively find all valid ascending combinations of alpha tuple, using the values of Z/mZ (excluding 0)
     * 
     * @param V_set
     * @param m 
     * @param this_combination - an array that contains all the elements in the alpha tuple for the current loop
     * @param sum - stores the sum of the elements in this_combination
     * @param depth - the current level of the recursion method
     */
    private static void recursively_find_all(Set<Tuple> V_set, Set<Integer> ZmmZ_star, int m, int[] this_combination, int sum, int depth, int n_halved_plus_one) throws ProjectException {
        if (depth > this_combination.length) {
            // System.out.println("depth = " + depth + "   returning"); // DEBUG
            return;
        }
        
        int prev = 0;
        if (depth > 1) prev = this_combination[depth-2];
        // System.out.println("for comb " + toString(this_combination) + " the prev is " + prev); // DEBUG
        for (int i = Integer.max(depth, prev + 1); i <= m - 1 - this_combination.length + depth; ++i) {
            // if (depth == 1) System.out.println((this_combination[0]+1) + " out of " + (m-this_combination.length)); // DEBUG 
            this_combination[depth-1] = i;
            sum += i;

            // System.out.println(toString(this_combination, depth) + "   depth = " + depth + "   sum: " + sum); // DEBUG

            // if (this_combination.size() == alpha_length) System.out.println(this_combination.toString() + "   sum: " + sum); //DEBUG
            if (depth == this_combination.length && sum % m == 0) {
                // V_set.add(new Tuple(this_combination));
                put_in_V_set_if_valid(V_set, ZmmZ_star, this_combination, m, n_halved_plus_one);
            }
            
            recursively_find_all(V_set, ZmmZ_star, m, this_combination, sum, depth+1, n_halved_plus_one);
            
            // System.out.print("subtracting from sum = " + sum + " by last element at index " + depth + "-1 = " + this_combination[depth-1]); // DEBUG
            // sum -= this_combination[depth-1];
            sum -= i;
            // System.out.println("   sum is now = " + sum); // DEBUG
            // this_combination[depth-1] = 0; // dont need to be removed, but could get garbage values based on implementation
        }
    }

    private static void recursively_find_all_check_halves(Set<Tuple> V_set, Set<Integer> ZmmZ_star, int m, int[] this_combination, int sum, int depth, int n_halved_plus_one) throws ProjectException, FileNotFoundException {
        if (depth > this_combination.length) {
            // System.out.println("depth = " + depth + "   returning"); // DEBUG
            return;
        }
        
        int prev = 0;
        if (depth > 1) prev = this_combination[depth-2];
        // System.out.println("for comb " + toString(this_combination) + " the prev is " + prev); // DEBUG
        for (int i = Integer.max(depth, prev + 1); i <= m - 1 - this_combination.length + depth; ++i) {
            // if (depth == 1) System.out.println((this_combination[0]+1) + " out of " + (m-this_combination.length)); // DEBUG 
            this_combination[depth-1] = i;
            sum += i;

            // debugOutput.println(depth + " " + toString(this_combination));
            // System.out.println(depth + " " + toString(this_combination));
    
            // System.out.println(toString(this_combination, depth) + "   depth = " + depth + "   sum: " + sum); // DEBUG

            // if (this_combination.size() == alpha_length) System.out.println(this_combination.toString() + "   sum: " + sum); //DEBUG
            if (depth == this_combination.length && sum % m == 0) {
                // V_set.add(new Tuple(this_combination));
                // validate_split_halves(this_combination, m); // DEBUG
                put_in_V_set_if_valid_and_check_halves(V_set, ZmmZ_star, this_combination, m, n_halved_plus_one);
            }
            
            recursively_find_all_check_halves(V_set, ZmmZ_star, m, this_combination, sum, depth+1, n_halved_plus_one);
            
            // System.out.print("subtracting from sum = " + sum + " by last element at index " + depth + "-1 = " + this_combination[depth-1]); // DEBUG
            // sum -= this_combination[depth-1];
            sum -= i;
            // System.out.println("   sum is now = " + sum); // DEBUG
            // this_combination[depth-1] = 0; // dont need to be removed, but could get garbage values based on implementation
        }
    }

    public static void put_in_V_set_if_valid(Set<Tuple> V_set, Set<Integer> Z_mod_m_Z_star, int[] alpha, int m, int n_halved_plus_one) throws ProjectException {
        if (maxSecondsAllowed > 0) { // has time limit
            terminate_if_longer_than_n_seconds(maxSecondsAllowed);
        }
        boolean this_alpha_is_valid = true;
        // System.out.println(n_halved_plus_one);
        for (int t : Z_mod_m_Z_star) {

            List<Integer> t_times_alpha_reduced_elements = new ArrayList<>();
            double t_times_alpha_reduced_sum = 0;

            for (int i = 0; i < alpha.length; ++i) {
                int reduced_mod_m = (t * alpha[i]) % m;
                t_times_alpha_reduced_elements.add(reduced_mod_m);
                t_times_alpha_reduced_sum += reduced_mod_m;
            }
            t_times_alpha_reduced_sum /= m;

            // System.out.println("for tuple " + alpha + " and t = " + t + ", t*a = " + t_times_alpha_reduced_elements + " and |t*a| = " + t_times_alpha_reduced_sum);//DEBUG
            // System.out.printf("for tuple %-16s and t = %2d, t*a = %-16s and |t*a| = %1.0f\n", alpha.toString(), t, t_times_alpha_reduced_elements.toString(), t_times_alpha_reduced_sum);//DEBUG
            if (t_times_alpha_reduced_sum != n_halved_plus_one) {
                this_alpha_is_valid = false;
                break; // if |t * alpha| != n/2 +1 for just one t, this alpha tuple is not valid for the B set
            }
        }
        if (this_alpha_is_valid) {
            // B_set.add(alpha);
            V_set.add(new Tuple(alpha));
        }
    }

    public static void put_in_V_set_if_valid_and_check_halves(Set<Tuple> V_set, Set<Integer> Z_mod_m_Z_star, int[] alpha, int m, int n_halved_plus_one) throws ProjectException {
        if (maxSecondsAllowed > 0) { // has time limit
            terminate_if_longer_than_n_seconds(maxSecondsAllowed);
        }
        boolean this_alpha_is_valid = true;
        // System.out.println(n_halved_plus_one);
        for (int t : Z_mod_m_Z_star) {

            List<Integer> t_times_alpha_reduced_elements = new ArrayList<>();
            double t_times_alpha_reduced_sum = 0;

            for (int i = 0; i < alpha.length; ++i) {
                int reduced_mod_m = (t * alpha[i]) % m;
                t_times_alpha_reduced_elements.add(reduced_mod_m);
                t_times_alpha_reduced_sum += reduced_mod_m;
            }
            t_times_alpha_reduced_sum /= m;

            // System.out.println("for tuple " + alpha + " and t = " + t + ", t*a = " + t_times_alpha_reduced_elements + " and |t*a| = " + t_times_alpha_reduced_sum);//DEBUG
            // System.out.printf("for tuple %-16s and t = %2d, t*a = %-16s and |t*a| = %1.0f\n", alpha.toString(), t, t_times_alpha_reduced_elements.toString(), t_times_alpha_reduced_sum);//DEBUG
            if (t_times_alpha_reduced_sum != n_halved_plus_one) {
                this_alpha_is_valid = false;
                break; // if |t * alpha| != n/2 +1 for just one t, this alpha tuple is not valid for the B set
            }
        }
        if (this_alpha_is_valid) {
            // B_set.add(alpha);
            validate_split_halves(alpha, m); // DEBUG
            V_set.add(new Tuple(alpha));
        }
    }

    public static void validate_split_halves(int[] tuple_array, int m) throws ProjectException {
        int n = tuple_array.length;
        int m_minus_one_divided_by_two = (m-1)/2;
        int invalid_index = -1;
        for (int j = 0; j < n; j++) {
            if (j < n/2) { // left half
                if (tuple_array[j] > m_minus_one_divided_by_two) {
                    invalid_index = j;
                    break;
                }
            } else { // right half
                if (tuple_array[j] < m_minus_one_divided_by_two) {
                    invalid_index = j;
                    break;
                }
            }
        }
        if (invalid_index == -1) { // normal
            valid_split_in_half_count++;
            if (valid_split_in_half_count % 1000 == 0) {
                System.out.println(valid_split_in_half_count + " tuples checked are valid halves.");
            }
        } else { // anomaly
            System.out.printf("For m = %d, d = %d, the tuple %s at index %d violates the two halves observation, after %d normal combinations in a row.\n", m, tuple_array.length, toString(tuple_array), invalid_index, valid_split_in_half_count);
            debugOutput.append(String.format("\nFor m = %d, d = %d, the tuple in U set %s at index %d violates the two halves observation, after %d normal combinations in a row.\n", m, tuple_array.length, toString(tuple_array), invalid_index, valid_split_in_half_count));
            valid_split_in_half_count = 0;
        }
    }

    public static void populate_indecomposable(Set<Tuple> V_set, Set<Tuple> indecomposable, int m) {
        for (Tuple tuple : V_set) {
            boolean has_subset = is_indecomposable_recursively(indecomposable, m, tuple, new ArrayList<Integer>(), 0, 0, 1);
            if (!has_subset) {
                indecomposable.add(tuple);
            }
        }
    }

    private static boolean is_indecomposable_recursively(Set<Tuple> indecomposable, int m, Tuple tuple, List<Integer> this_combination, int sum, int begin, int depth) {
        // only return true if subset found, else return false
        if (depth >= tuple.size()) return false;

        int size = tuple.size();
        for (int i = begin; i < size; ++i) {
            this_combination.add(tuple.get(i));
            sum += tuple.get(i);

            // System.out.println(this_combination.toString() + "   sum: " + sum); //DEBUG
            if (size % 2 == 0 && sum % m == 0) return true; // check if sum of tuple up to this element divides m

            // if not, check if any subsets that includes this subset is valid:
            if (is_indecomposable_recursively(indecomposable, m, tuple, this_combination, sum, i+1, depth+1)) return true;
            
            // none of subsets that includes this subset is valid, remove some elements and try other ones
            this_combination.remove(tuple.get(i));
            sum -= tuple.get(i);
        }
        return false;
    }

    public static void terminate_if_longer_than_n_seconds(int n) throws ProjectException {
        if (startTimeInNano == -1) {
            throw new ProjectException("Error: StartTimeInNano was never set.");
        }
        long elapsedInNano = System.nanoTime() - startTimeInNano;
        long elapsedInSeconds = elapsedInNano / 1000000000L;
        if (elapsedInSeconds > n) {
            skipToNextM = true;
            throw new ProjectException("Time limit exceeded " + n + " seconds.");
        }
    }

    public static String redString(Object obj) {
        String str = "";
        if (obj instanceof String) str = obj.toString();
        if (obj instanceof Integer) str = String.valueOf(obj);
        return "\u001b[31m" + str + "\u001b[0m";
    }
    
    public static String redString(String str, int num) {
        return "\u001b[31m" + str + num + "\u001b[0m";
    }

    public static String toStringSorted(Set<Tuple> set) {
        return toString(new TreeSet<Tuple>(set), ", ");
    }

    public static String toStringSorted(Set<Tuple> set, String delimiter) {
        return toString(new TreeSet<Tuple>(set), delimiter);
    }

    public static String toString(Set<Tuple> set) {
            return toString(set, ", ");
    }

    public static String toString(Set<Tuple> set, String delimiter) {
        StringBuilder sb = new StringBuilder("{");
        Iterator<Tuple> iter = set.iterator();
        if (iter.hasNext()) {
            sb.append(delimiter).append(iter.next().toString()); // element first
        }
        while (iter.hasNext()) {
            sb.append(delimiter).append(iter.next().toString()); // delimiter if theres another element
        }
        sb.append("}");
        return sb.toString();
    }

    
    public static String toString(int[] arr) throws ProjectException {
        return toString(arr, arr.length);
    }

    /**
     * 
     * @param arr
     * @param cut_off exclusive
     * @return
     * @throws ProjectException
     */
    public static String toString(int[] arr, int cut_off) throws ProjectException {
        if (cut_off < 0 || cut_off > arr.length) {
            throw new ProjectException("cut off = " + cut_off + " is invalid");
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < cut_off-1; ++i) {
            sb.append(arr[i]).append(", ");
        }
        return sb.append(arr[cut_off-1]).append("]").toString();
    }

    public static String find_num_of_ascending_nonrepeating_tuples_in_U_set(Set<Integer> Z_mod_m_Z, int d) {
        BigInteger result = nCr(BigInteger.valueOf(Z_mod_m_Z.size()-1), BigInteger.valueOf(2*d));
        String str = result.toString();
        return str;
    }

    public static int sumOf(List<Integer> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); ++i) {
            sum += list.get(i);
        }
        return sum;
    }

    public static String plural(long num) {
        return num == 1 ? "" : "s";
    }

    public static String plural(int num) {
        return num == 1 ? "" : "s";
    }

    static boolean isPrime(int n) {
        if (n <= 1) return false;

        if (n == 2 || n == 3) return true;

        if (n % 2 == 0 || n % 3 == 0) return false;
        
        for (int i = 5; i <= Math.sqrt(n); i = i + 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }

        return true;
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

    public static BigInteger factorial(BigInteger n) {
        if (n.compareTo(BigInteger.ONE) < 1) return BigInteger.ONE;
        return n.multiply(factorial(n.subtract(BigInteger.ONE)));
    }

    public static BigInteger nCr(BigInteger n, BigInteger r) {
        BigInteger numer = factorial(n);
        BigInteger denom = (factorial(r).multiply(factorial(n.subtract(r))));
        return numer.divide(denom);
    }
}
