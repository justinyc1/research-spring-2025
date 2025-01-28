
public class ValidateSetV {
    public static void main(String[] args) throws ProjectException {
        validateSetV(5, 3);
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

        System.out.println("no errors");
        return;
    }
}