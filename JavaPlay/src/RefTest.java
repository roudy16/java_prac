/**
 * Demonstrates the pass-by-value nature of Java. Objects should be considered to be passed
 * by value reference.
 */
public class RefTest {
    private static class Name {
        String data;

        public Name(String name) {
            data = name;
        }

        @Override
        public String toString() {
            return data;
        }
    }
    private static Name ChangeWordFoo(Name word) {
        word.data = "Foo";
        word = new Name("Somtin");
        return word;
    }

    private static String ChangeWordFoo(String word) {
        word = "Foo";
        String newWord = new String("Somtin");
        return newWord;
    }

    public static void main(String[] args) {
        Name nameObj = new Name("Steve");
        System.out.println("Name Object before: " + nameObj);
        Name newNameObj = ChangeWordFoo(nameObj);
        System.out.println("Name Object after: " + nameObj);
        System.out.println("NewName Object: " + newNameObj);

        String nameStr = "Steve";
        System.out.println("Name String before: " + nameStr);
        String newNameStr = ChangeWordFoo(nameStr);
        System.out.println("Name String after: " + nameStr);
        System.out.println("NewName String: " + newNameStr);
    }
}
