package com.parkit.parkingsystem;

import org.junit.jupiter.api.Test;

import static com.parkit.parkingsystem.util.NumbersUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumbersUtilTest {

    @Test
    public void roundDecimalsAtTwoWhenDecimalIsAboveHalf() {
        double result = roundDecimals(1.256, 2);
        assertEquals(1.26, result);
    }

    @Test
    public void roundDecimalsAtTwoWhenDecimalIsBelowHalf() {
        double result = roundDecimals(1.254, 2);
        assertEquals(1.25, result);
    }

    @Test
    public void roundDecimalsAtTwoWhenNoDecimal() {
        double result = roundDecimals(1, 2);
        assertEquals(1.00, result);
    }

    @Test
    public void doubleToStringWithZeroWhenNoDecimal() {
        String result = doubleToStringWithZero(1);
        assertEquals("1.00", result);
    }

    @Test
    public void doubleToStringWithZeroWhenDecimal() {
        String result = doubleToStringWithZero(1.25);
        assertEquals("1.25", result);
    }
}
