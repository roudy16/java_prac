import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Dell on 6/2/2017.
 */
class InterviewTest {
    public static String str00 = "Private";
    public static String str01 = "Public";
    public static String str02 = "View";
    public static String str03 = "Inherited";
    public static String str04 = "X";
    public static String str05 = "P";
    public static String str06 = "V";
    public static String str07 = "I";

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void encode() {
        String out00 = Interview.encode(str00);
        String out01 = Interview.encode(str01);
        String out02 = Interview.encode(str02);
        String out03 = Interview.encode(str03);

        assertEquals(out00, str04);
        assertEquals(out01, str05);
        assertEquals(out02, str06);
        assertEquals(out03, str07);
    }

    @org.junit.jupiter.api.Test
    void decode() {
        String out04 = Interview.decode(str04);
        String out05 = Interview.decode(str05);
        String out06 = Interview.decode(str06);
        String out07 = Interview.decode(str07);

        assertEquals(out04, str00);
        assertEquals(out05, str01);
        assertEquals(out06, str02);
        assertEquals(out07, str03);
    }

    @org.junit.jupiter.api.Test
    void getTotalValue() {
        Interview.Item item01 = new Interview.Item();
        item01.name = "Beans";
        item01.quantity = 5;
        item01.price = BigDecimal.valueOf(3.03);
        Interview.Item item02 = new Interview.Item();
        item02.name = "Cheese";
        item02.quantity = 0;
        item02.price = BigDecimal.valueOf(100.00);
        Interview.Item item03 = new Interview.Item();
        item03.name = "Tamale";
        item03.quantity = 1;
        item03.price = BigDecimal.valueOf(10.00);

        Interview.Cart cart = new Interview.Cart();
        cart.items = new Interview.Item[]{item01, item02, item03};

        assertEquals(BigDecimal.valueOf(25.15), cart.getTotalValue());
    }

}