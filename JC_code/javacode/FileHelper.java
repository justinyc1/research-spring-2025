package JC_code.javacode;

import java.io.File;
public class FileHelper {
    static String myRootDir = "JC_code";
    static String outputsDir = "JC_code\\outputs";
    static String myTestDir = "JC_code\\.myTest";
    public static void main(String[] args) {
        // printDirectoryAndContents(new File(myRootDir));
        deleteAllEmptyFiles(new File(outputsDir));
    }

    public static void deleteAllEmptyFiles(File directory) {
        for (File filename : directory.listFiles()) {
            if (filename.isDirectory()) {
                deleteAllEmptyFiles(filename);
            } else {
                if (filename.length() == 0) {
                    if (filename.delete()) {
                        System.out.println("\"" + filename + "\" was deleted.");
                    }
                }
            }
        }
    }

    /**Given a directory, print its name, and the name of all its contents (including files and more directories) recursively.
     * 
     * @param directory - a File object
     */
    public static void printDirectoryAndContents(File directory) {
        printDirectoryAndContents(directory, 0);
    }
    
    public static void printDirectoryAndContents(File directory, int depth) {
        System.out.println(nIndent(depth) + directory.getName() + ": ");
        for (File filename : directory.listFiles()) {
            if (filename.isDirectory()) {
                printDirectoryAndContents(filename, depth+1);
            } else {
                System.out.println(nIndent(depth+1) + filename.getName());
            }
        }
    }

    /** 
     * 
     * @param n - number of two-spaced indents to return
     * @return a String, representing two-spaced indents, repeated n times
     */
    public static String nIndent(int n) {
        String indent = "  ";
        if (n == 0) return "";
        if (n == 1) return indent;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(indent);
        }
        return sb.toString();
    }
}
