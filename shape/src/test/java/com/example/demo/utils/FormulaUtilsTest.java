package com.example.demo.utils;

import com.example.demo.exception.RequestValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)


class FormulaUtilsTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


        @Test
        public void testCalculateCustomValue_Addition() {
            String formula = "parameters[0] + parameters[1]";
            List<Double> parameters = Arrays.asList(2.0, 3.5);
            double expected = 5.5;
            double actual = FormulaUtils.calculateCustomValue(formula, parameters);
            assertEquals(expected, actual, 0.0001);
        }

        @Test
        public void testCalculateCustomValue_Subtraction() {
            String formula = "parameters[0] - parameters[1]";
            List<Double> parameters = Arrays.asList(7.0, 3.5);
            double expected = 3.5;
            double actual = FormulaUtils.calculateCustomValue(formula, parameters);
            assertEquals(expected, actual, 0.0001);
        }

        @Test
        public void testCalculateCustomValue_Multiplication() {
            String formula = "parameters[0] * parameters[1]";
            List<Double> parameters = Arrays.asList(2.0, 3.5);
            double expected = 7.0;
            double actual = FormulaUtils.calculateCustomValue(formula, parameters);
            assertEquals(expected, actual, 0.0001);
        }

        @Test
        public void testCalculateCustomValue_Division() {
            String formula = "parameters[0] / parameters[1]";
            List<Double> parameters = Arrays.asList(10.0, 2.0);
            double expected = 5.0;
            double actual = FormulaUtils.calculateCustomValue(formula, parameters);
            assertEquals(expected, actual, 0.0001);
        }

        @Test
        public void testCalculateCustomValue_InvalidOperator() {
            String formula = "parameters[0] % parameters[1]";
            List<Double> parameters = Arrays.asList(10.0, 3.0);
            assertThrows(IllegalArgumentException.class, () -> FormulaUtils.calculateCustomValue(formula, parameters));
        }

        @Test
        public void testCalculateCustomValue_InvalidFormulaFormat() {
            String formula = "parameters[0] + parameters[1]";
            List<Double> parameters = Arrays.asList(2.0);
            assertThrows(RequestValidationException.class, () -> FormulaUtils.calculateCustomValue(formula, parameters));
        }
    }
