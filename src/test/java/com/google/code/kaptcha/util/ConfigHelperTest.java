package com.google.code.kaptcha.util;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.NoiseProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author cliffano
 */
public class ConfigHelperTest {
    private ConfigHelper helper;

    @BeforeEach
    public void setUp() {
        helper = new ConfigHelper();
    }

    @Test
    public void testGetColorWithNullValueGivesDefaultColor() {
        assertEquals(Color.YELLOW, helper.getColor(Constants.KAPTCHA_BORDER_COLOR, null, Color.YELLOW));
    }

    @Test
    public void testGetColorWithEmptyValueGivesDefaultColor() {
        assertEquals(Color.YELLOW, helper.getColor(Constants.KAPTCHA_BORDER_COLOR, "", Color.YELLOW));
    }

    @Test
    public void testGetColorWithCommaSeparatedValuesGivesExpectedColor() {
        assertEquals(Color.RED, helper.getColor(Constants.KAPTCHA_BORDER_COLOR, "255,0,0", Color.YELLOW));
    }

    @Test
    public void testGetColorWithFieldValueGivesExpectedColor() {
        assertEquals(Color.RED, helper.getColor(Constants.KAPTCHA_BORDER_COLOR, "RED", Color.YELLOW));
    }

    @Test
    public void testCreateColorFromCommaSeparatedValuesWithRgbValuesGivesTheCorrespondingColor() {
        Color color = helper.createColorFromCommaSeparatedValues(Constants.KAPTCHA_BORDER_COLOR, "255,123,5");
        assertEquals(255, color.getRed());
        assertEquals(123, color.getGreen());
        assertEquals(5, color.getBlue());
    }

    @Test
    public void testCreateColorFromCommaSeparatedValuesWithRgbAndAlphaValuesGivesTheCorrespondingColor() {
        Color color = helper.createColorFromCommaSeparatedValues(Constants.KAPTCHA_BORDER_COLOR, "255,123,5,10");
        assertEquals(255, color.getRed());
        assertEquals(123, color.getGreen());
        assertEquals(5, color.getBlue());
        assertEquals(10, color.getAlpha());
    }

    @Test
    public void testCreateColorFromCommaSeparatedValuesWithOutOfRangeRgbValuesThrowsConfigException() {
        try {
            helper.createColorFromCommaSeparatedValues(Constants.KAPTCHA_BORDER_COLOR, "255,123,280");
            fail("ConfigException should've been thrown.");
        } catch (ConfigException ce) {
            assertEquals("Invalid value '255,123,280' for config parameter 'kaptcha.border.color'.", ce.getMessage());
        }
    }

    @Test
    public void testCreateColorFromCommaSeparatedValuesWithInvalidNumberOfColorValuesThrowsConfigException() {
        try {
            helper.createColorFromCommaSeparatedValues(Constants.KAPTCHA_BORDER_COLOR, "255,123,20,10,222");
            fail("ConfigException should've been thrown.");
        } catch (ConfigException ce) {
            assertEquals("Invalid value '255,123,20,10,222' for config parameter 'kaptcha.border.color'. Color can only have 3 (RGB) or 4 (RGB with Alpha) values.", ce.getMessage());
        }

        try {
            helper.createColorFromCommaSeparatedValues(Constants.KAPTCHA_BORDER_COLOR, "255,123");
            fail("ConfigException should've been thrown.");
        } catch (ConfigException ce) {
            assertEquals("Invalid value '255,123' for config parameter 'kaptcha.border.color'.", ce.getMessage());
        }

        try {
            helper.createColorFromCommaSeparatedValues(Constants.KAPTCHA_BORDER_COLOR, "255,a,100");
            fail("ConfigException should've been thrown.");
        } catch (ConfigException ce) {
            assertEquals("Invalid value '255,a,100' for config parameter 'kaptcha.border.color'.", ce.getMessage());
        }
    }

    @Test
    public void testCreateColorFromFieldValueWithFieldValueGivesTheCorrespondingColor() {
        assertEquals(Color.blue, helper.createColorFromFieldValue(Constants.KAPTCHA_BORDER_COLOR, "blue"));
        assertEquals(Color.RED, helper.createColorFromFieldValue(Constants.KAPTCHA_BORDER_COLOR, "RED"));
    }

    @Test
    public void testCreateColorFromFieldValueWithInvalidFieldValueThrowsConfigException() {
        try {
            helper.createColorFromFieldValue(Constants.KAPTCHA_BORDER_COLOR, "Katie Lloyd");
            fail("ConfigException should've been thrown.");
        } catch (ConfigException ce) {
            assertEquals("Invalid value 'Katie Lloyd' for config parameter 'kaptcha.border.color'.", ce.getMessage());
            assertTrue(ce.getCause() instanceof NoSuchFieldException);
        }
    }

    @Test
    public void testGetClassInstanceWithCustomClass() {
        Object instance = helper.getClassInstance(Constants.KAPTCHA_BACKGROUND_IMPL, "com.google.code.kaptcha.util.CustomNoiseProducer", new Object(), new Config(new Properties()));
        assertTrue(instance instanceof CustomNoiseProducer);
    }

    @Test
    public void testGetClassInstanceWithNullValueGivesDefaultInstance() {
        Object defaultInstance = new Object();
        assertEquals(defaultInstance, helper.getClassInstance(Constants.KAPTCHA_BACKGROUND_IMPL, null, defaultInstance, new Config(new Properties())));
    }

    @Test
    public void testGetClassInstanceWithEmptyValueGivesDefaultInstance() {
        Object defaultInstance = new Object();
        assertEquals(defaultInstance, helper.getClassInstance(Constants.KAPTCHA_BACKGROUND_IMPL, "", defaultInstance, new Config(new Properties())));
    }

    @Test
    public void testGetClassInstanceWithNonExistantClassThrowsConfigException() {
        try {
            helper.getClassInstance(Constants.KAPTCHA_BACKGROUND_IMPL, "com.google.code.kaptcha.BostonLegal", new Object(), new Config(new Properties()));
            fail("ConfigException should've been thrown.");
        } catch (ConfigException ce) {
            assertEquals("Invalid value 'com.google.code.kaptcha.BostonLegal' for config parameter 'kaptcha.background.impl'.", ce.getMessage());
            assertTrue(ce.getCause() instanceof ClassNotFoundException);
        }
    }

    @Test
    public void testGetFontsWithNullValueGivesDefaultFonts() {
        Font[] defaultFonts = new Font[]{
                new Font("Arial", Font.BOLD, 11),
                new Font("Courier", Font.BOLD, 11)
        };
        Font[] fonts = helper.getFonts(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, null, 12, defaultFonts);
        assertEquals("Arial", fonts[0].getFamily());
        assertEquals(Font.BOLD, fonts[0].getStyle());
        assertEquals(11, fonts[0].getSize());

        assertEquals("Courier", fonts[1].getFamily());
        assertEquals(Font.BOLD, fonts[1].getStyle());
        assertEquals(11, fonts[1].getSize());
    }

    @Test
    public void testGetFontsWithEmptyValueGivesDefaultFonts() {
        Font[] defaultFonts = new Font[]{
                new Font("Arial", Font.BOLD, 11),
                new Font("Courier", Font.BOLD, 11)
        };
        Font[] fonts = helper.getFonts(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, "", 12, defaultFonts);
        assertEquals("Arial", fonts[0].getFamily());
        assertEquals(Font.BOLD, fonts[0].getStyle());
        assertEquals(11, fonts[0].getSize());

        assertEquals("Courier", fonts[1].getFamily());
        assertEquals(Font.BOLD, fonts[1].getStyle());
        assertEquals(11, fonts[1].getSize());
    }

    @Test
    public void testGetFontsGivesExpectedFonts() {
        Font[] fonts = helper.getFonts(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Verdana,Arial", 12, new Font[]{});
        assertEquals(2, fonts.length);
        assertEquals("Verdana", fonts[0].getFamily());
        assertEquals(Font.BOLD, fonts[0].getStyle());
        assertEquals(12, fonts[0].getSize());
        assertEquals("Arial", fonts[1].getFamily());
        assertEquals(Font.BOLD, fonts[1].getStyle());
        assertEquals(12, fonts[1].getSize());
    }

    @Test
    public void testGetFontsWithInvalidFontFamilyGivesClosestFontFamily() {
        Font[] fonts = helper.getFonts(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Whitney Rome", 12, new Font[]{});
        assertEquals(1, fonts.length);
        assertEquals("Dialog", fonts[0].getFamily());
        assertEquals(Font.BOLD, fonts[0].getStyle());
        assertEquals(12, fonts[0].getSize());
    }

    @Test
    public void testGetPositiveIntWithNullValueGivesDefaultInt() {
        assertEquals(40, helper.getPositiveInt(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, null, 40));
    }

    @Test
    public void testGetPositiveIntWithEmptyValueGivesDefaultInt() {
        assertEquals(40, helper.getPositiveInt(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "", 40));
    }

    @Test
    public void testGetPositiveIntGivesExpectedValue() {
        assertEquals(50, helper.getPositiveInt(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "50", 40));
    }

    @Test
    public void testGetPositiveIntWitNegativeValueThrowsConfigException() {
        try {
            helper.getPositiveInt(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "-1", 40);
            fail("ConfigException should've been thrown.");
        } catch (ConfigException ce) {
            assertEquals("Invalid value '-1' for config parameter 'kaptcha.textproducer.font.size'. Value must be greater than or equals to 1.", ce.getMessage());
        }
    }

    @Test
    public void testGetPositiveIntWitNonNumericValueThrowsConfigException() {
        try {
            helper.getPositiveInt(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "Lorraine Weller", 40);
            fail("ConfigException should've been thrown.");
        } catch (ConfigException ce) {
            assertEquals("Invalid value 'Lorraine Weller' for config parameter 'kaptcha.textproducer.font.size'.", ce.getMessage());
            assertTrue(ce.getCause() instanceof NumberFormatException);
        }
    }

    @Test
    public void testGetCharsWithNullValueGivesDefaultChars() {
        assertEquals("abcde2345678gfynmnpwx", new String(helper.getChars(
                Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, null,
                "abcde2345678gfynmnpwx".toCharArray())));
    }

    @Test
    public void testGetCharsWithEmptyValueGivesDefaultChars() {
        assertEquals("abcde2345678gfynmnpwx", new String(helper.getChars(
                Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, "",
                "abcde2345678gfynmnpwx".toCharArray())));
    }

    @Test
    public void testGetCharsGivesExpectedChars() {
        assertEquals("abcdefghij", new String(helper.getChars(
                Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, "abcdefghij",
                "abcde2345678gfynmnpwx".toCharArray())));
    }

    @Test
    public void testGetBoolean() {
        assertTrue(helper.getBoolean(Constants.KAPTCHA_BORDER, "yes", true));
        assertFalse(helper.getBoolean(Constants.KAPTCHA_BORDER, "no", true));
    }

    @Test
    public void testGetBooleanWithNullOrEmptyValueDefaultsToTrue() {
        assertTrue(helper.getBoolean(Constants.KAPTCHA_BORDER, null, true));
        assertTrue(helper.getBoolean(Constants.KAPTCHA_BORDER, "", true));
    }

    @Test
    public void testGetBooleanWithInvalidValueThrowsConfigException() {
        try {
            helper.getBoolean(Constants.KAPTCHA_BORDER, "Carl Sack", true);
            fail("ConfigException should've been thrown.");
        } catch (ConfigException ce) {
            assertEquals("Invalid value 'Carl Sack' for config parameter 'kaptcha.border'. Value must be either yes or no.", ce.getMessage());
        }
    }
}

class CustomNoiseProducer extends Configurable implements NoiseProducer {
    boolean isSetConfigManagerCalled = false;

    public void makeNoise(BufferedImage image, float factorOne,
                          float factorTwo, float factorThree, float factorFour) {
    }

    public void setConfigManager(Config config) {
        isSetConfigManagerCalled = true;
    }

    public boolean isSetConfigManagerCalled() {
        return isSetConfigManagerCalled;
    }
}
