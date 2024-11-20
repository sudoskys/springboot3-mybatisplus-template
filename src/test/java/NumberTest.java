import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Number {

    public int addition(int a, int b) {
        return a + b;
    }

    public int subtraction(int a, int b) {
        return a - b;
    }

    public int multiplication(int a, int b) {
        return a * b;
    }

    public int division(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return a / b;
    }
}

public class NumberTest {

    private final Number number = new Number();

    @Test
    public void testAdditionCorrect() {
        // 测试两个数相加
        assertEquals(5, number.addition(2, 3)); // Expected: 5
    }

    @Test
    public void testAdditionIncorrect() {
        // 测试两个数相加
        assertNotEquals(6, number.addition(2, 3)); // Not Expected: 6
    }

    @Test
    public void testSubtractionCorrect() {
        // 测试两个数相减
        assertEquals(1, number.subtraction(3, 2)); // Expected: 1
    }

    @Test
    public void testSubtractionIncorrect() {
        // 测试两个数相减
        assertNotEquals(2, number.subtraction(3, 2)); // Not Expected: 2
    }

    @Test
    public void testMultiplicationCorrect() {
        // 测试两个数相乘
        assertEquals(6, number.multiplication(2, 3)); // Expected: 6
    }

    @Test
    public void testMultiplicationIncorrect() {
        // 测试两个数相乘
        assertNotEquals(7, number.multiplication(2, 3)); // Not Expected: 7
    }

    @Test
    public void testDivisionCorrect() {
        // 测试两个数相除
        assertEquals(2, number.division(6, 3)); // Expected: 2
    }

    @Test
    public void testDivisionByZero() {
        // 测试除数为0的情况
        assertThrows(ArithmeticException.class, () -> number.division(6, 0)); // Expected: Exception
    }
}