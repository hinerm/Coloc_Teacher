/*-
 * #%L
 * Educational Fiji plugin for teaching colocalization analysis with synthetic data generation and guided interpretation.
 * %%
 * Copyright (C) 2025 ImageJ Developers
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package sc.fiji.coloc;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for the WizardSettings class.
 *
 * @author Mark Hiner
 */
public class WizardSettingsTest {

    @Test
    public void testDefaultConstructor() {
        WizardSettings settings = new WizardSettings();
        
        // Test default values
        assertEquals("Default number of spots", 50, settings.getNumSpots());
        assertEquals("Default spot radius", 5.0, settings.getSpotRadius(), 0.01);
        assertEquals("Default overlap fraction", 0.7, settings.getOverlapFraction(), 0.01);
        assertTrue("Noise should be enabled by default", settings.isAddNoise());
        assertEquals("Default noise std dev", 10.0, settings.getNoiseStdDev(), 0.01);
        assertEquals("Default base intensity", 30, settings.getBaseIntensity());
        assertEquals("Default max intensity", 200, settings.getMaxIntensity());
        assertEquals("Default width", 256, settings.getWidth());
        assertEquals("Default height", 256, settings.getHeight());
        
        // Test Costes defaults
        assertEquals("Default Costes randomizations", 100, settings.getNrCostesRandomisations());
        assertEquals("Default PSF", 3.0, settings.getPsf(), 0.01);
        assertTrue("Costes should be enabled by default", settings.isUseCostes());
        
        // Test statistical defaults
        assertTrue("Manders should be enabled by default", settings.isUseManders());
        assertTrue("Li ICQ should be enabled by default", settings.isUseLiICQ());
        assertTrue("Spearman should be enabled by default", settings.isUseSpearmanRank());
        assertFalse("Kendall tau should be disabled by default", settings.isUseKendallTau());
        
        // Test display defaults
        assertTrue("Scatterplot should be enabled by default", settings.isUseScatterplot());
        assertFalse("Image display should be disabled by default", settings.isDisplayImages());
        assertFalse("Costes shuffled display should be disabled by default", settings.isDisplayShuffledCostes());
    }
    
    @Test
    public void testCopyConstructor() {
        WizardSettings original = new WizardSettings();
        original.setNumSpots(75);
        original.setSpotRadius(7.5);
        original.setOverlapFraction(0.8);
        original.setAddNoise(false);
        original.setUseKendallTau(true);
        
        WizardSettings copy = new WizardSettings(original);
        
        assertEquals("Copied number of spots", 75, copy.getNumSpots());
        assertEquals("Copied spot radius", 7.5, copy.getSpotRadius(), 0.01);
        assertEquals("Copied overlap fraction", 0.8, copy.getOverlapFraction(), 0.01);
        assertFalse("Copied noise setting", copy.isAddNoise());
        assertTrue("Copied Kendall tau setting", copy.isUseKendallTau());
        
        // Verify it's a deep copy by modifying original
        original.setNumSpots(100);
        assertEquals("Copy should be independent", 75, copy.getNumSpots());
    }
    
    @Test
    public void testValidation() {
        WizardSettings settings = new WizardSettings();
        
        // Valid settings should pass
        assertNull("Valid settings should pass validation", settings.validate());
        
        // We can't test invalid states through setters anymore since they throw exceptions
        // Instead, let's test that setters reject invalid values (done in testValidationInSetters)
        // and test that validation works on the default valid settings
        
        // Verify all the validation messages exist by checking the implementation
        // (We can't test them directly since setters prevent invalid states)
        
        // Test that calling validation on valid settings returns null
        assertNull("Valid default settings should pass validation", settings.validate());
        
        // Test that changing to valid values still passes validation
        settings.setNumSpots(100);
        settings.setSpotRadius(3.0);
        settings.setOverlapFraction(0.5);
        settings.setBaseIntensity(20);
        settings.setMaxIntensity(200);
        settings.setWidth(512);
        settings.setHeight(512);
        
        assertNull("Modified valid settings should still pass validation", settings.validate());
    }
    
    @Test
    public void testGetSummary() {
        WizardSettings settings = new WizardSettings();
        String summary = settings.getSummary();
        
        assertNotNull("Summary should not be null", summary);
        assertTrue("Should contain spots info", summary.contains("Spots: 50"));
        assertTrue("Should contain radius info", summary.contains("Radius: 5.0"));
        assertTrue("Should contain overlap info", summary.contains("Overlap: 70%"));
        assertTrue("Should contain size info", summary.contains("Size: 256x256"));
        assertTrue("Should contain Costes info", summary.contains("Costes test"));
        assertTrue("Should contain Manders info", summary.contains("Manders"));
    }
    
    @Test
    public void testToString() {
        WizardSettings settings = new WizardSettings();
        String str = settings.toString();
        
        assertNotNull("toString should not be null", str);
        assertTrue("Should contain spots", str.contains("spots=50"));
        assertTrue("Should contain radius", str.contains("radius=5.0"));
        assertTrue("Should contain overlap percentage", str.contains("overlap=70%"));
        assertTrue("Should contain size", str.contains("size=256x256"));
    }
    
    @Test
    public void testNoiseSettings() {
        WizardSettings settings = new WizardSettings();
        
        // Test noise enabled
        settings.setAddNoise(true);
        settings.setNoiseStdDev(15.0);
        String summary = settings.getSummary();
        assertTrue("Should show noise std dev when enabled", summary.contains("15.0 std dev"));
        
        // Test noise disabled
        settings.setAddNoise(false);
        summary = settings.getSummary();
        assertTrue("Should show 'none' when noise disabled", summary.contains("none"));
        
        String str = settings.toString();
        assertTrue("toString should show 'none' when noise disabled", str.contains("noise=none"));
    }
    
    @Test
    public void testValidationInSetters() {
        WizardSettings settings = new WizardSettings();
        
        // Test invalid numSpots
        try {
            settings.setNumSpots(-1);
            fail("Should throw exception for negative numSpots");
        } catch (IllegalArgumentException e) {
            assertTrue("Error message should mention spots", e.getMessage().toLowerCase().contains("spots"));
        }
        
        // Test invalid spotRadius
        try {
            settings.setSpotRadius(-2.0);
            fail("Should throw exception for negative spotRadius");
        } catch (IllegalArgumentException e) {
            assertTrue("Error message should mention radius", e.getMessage().toLowerCase().contains("radius"));
        }
        
        // Test invalid overlapFraction
        try {
            settings.setOverlapFraction(1.5);
            fail("Should throw exception for overlapFraction > 1.0");
        } catch (IllegalArgumentException e) {
            assertTrue("Error message should mention overlap", e.getMessage().toLowerCase().contains("overlap"));
        }
        
        // Test valid values still work
        settings.setNumSpots(50);
        settings.setSpotRadius(3.0);
        settings.setOverlapFraction(0.5);
        
        assertEquals(50, settings.getNumSpots());
        assertEquals(3.0, settings.getSpotRadius(), 0.01);
        assertEquals(0.5, settings.getOverlapFraction(), 0.01);
    }
}
