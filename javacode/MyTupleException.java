public class MyTupleException extends RuntimeException {
    public MyTupleException(String className, String methodName, String message) {
        super("method: " + methodName + ", message: " + message);
    }
}
