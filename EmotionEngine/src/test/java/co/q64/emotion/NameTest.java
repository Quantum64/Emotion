package co.q64.emotion;

import co.q64.emotion.util.SimpleEmotionTest;
import org.junit.Test;

public class NameTest {
    @Test
    public void testName() {
        new SimpleEmotionTest("test1 = load 15", "test2 = load 10", "result = +(test1, test2)", "print(result)", "terminate").execute("25");
    }
}
