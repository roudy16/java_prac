import org.jetbrains.annotations.Contract;

import java.math.BigDecimal;

/**
 * Created by Roudy on 6/2/2017.
 */
public class Interview {
    public static void main (String[] args) {

    }

    @Contract(pure = true)
    public static String encode(String inStr) {
        String retStr = null;
        switch (inStr) {
            case "Public":
                retStr = "P";
                break;
            case "Private":
                retStr = "X";
                break;
            case "View":
                retStr = "V";
                break;
            case "Inherited":
                retStr = "I";
                break;
            default:
                // noop
                break;
        }
        return retStr;
    }

    @Contract(pure = true)
    public static String decode(String inStr) {
        String retStr = null;
        switch (inStr) {
            case "P":
                retStr = "Public";
                break;
            case "X":
                retStr = "Private";
                break;
            case "V":
                retStr = "View";
                break;
            case "I":
                retStr = "Inherited";
                break;
            default:
                // noop
                break;
        }
        return retStr;
    }

    public static class Item {
        String name;
        int quantity;
        BigDecimal price;

        public BigDecimal getTotalValue() {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public static class Cart {
        Item[] items;

        public BigDecimal getTotalValue() {
            BigDecimal val = BigDecimal.ZERO;
            for( Item item : items) {
                val = val.add(item.getTotalValue());
            }

            return val;
        }
    }

    @Contract(pure = true)
    public static BigDecimal getTotalValue(Cart cart) {
        return cart.getTotalValue();
    }
}
