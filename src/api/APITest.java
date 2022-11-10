package api;

public class APITest {
    private static final String TEST_STRING = "lord of the rings";

    public static void main(String[] args) {
        test();
        try {
            System.out.println(Book.getBookByIsbn("9780788789830"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void test() {
        try {
            Book.getBooksByTitle(APITest.TEST_STRING).forEach(System.out::println);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
