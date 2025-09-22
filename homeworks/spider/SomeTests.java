package spider;

import java.util.Arrays;
import java.util.List;

public class SomeTests {
    public static void main(String[] args) {
        String[] strings = {
                "One",
                "Two",
                "Three",
        };
        List<String> list = Arrays.asList(strings);
        String joined = list.stream().reduce((prev, curr) -> prev + " - " + curr).get();
        System.out.println(joined);
    }
}
