/*-
 * #%L
 * Educational Fiji plugin for teaching colocalization analysis.
 * %%
 * Copyright (C) 2025 Your Name.
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;

import net.imagej.Dataset;

/**
 * Tests for the Coloc_Teacher plugin using direct instantiation and getters.
 *
 * @author Mark Hiner
 */
public class Coloc_TeacherTest {

    private Context context;

    @Before
    public void setUp() {
        context = new Context();
    }

    @After
    public void tearDown() {
        if (context != null) {
            context.dispose();
        }
    }

    @Test
    public void testPluginInstantiation() {
        Coloc_Teacher plugin = new Coloc_Teacher();
        assertNotNull("Plugin should be instantiated", plugin);
    }
    
    @Test
    public void testPluginExecutionWithTestMode() {
        // Create plugin in test mode
        Coloc_Teacher plugin = new Coloc_Teacher(true);
        
        // Inject context dependencies
        context.inject(plugin);
        
        // Run the plugin
        plugin.run();
        
        // Test outputs using getters
        Dataset ch1 = plugin.getChannel1();
        Dataset ch2 = plugin.getChannel2();
        String guide = plugin.getInterpretationGuide();
        
        assertNotNull("Channel 1 should be generated", ch1);
        assertNotNull("Channel 2 should be generated", ch2);
        assertNotNull("Interpretation guide should be generated", guide);
        
        // Verify image dimensions (using default values in test mode)
        assertEquals("Channel 1 width should match default", 256L, ch1.dimension(0));
        assertEquals("Channel 1 height should match default", 256L, ch1.dimension(1));
        assertEquals("Channel 2 width should match default", 256L, ch2.dimension(0));
        assertEquals("Channel 2 height should match default", 256L, ch2.dimension(1));
        
        // Verify interpretation guide contains expected content
        assertTrue("Should contain correlation analysis", guide.contains("correlation"));
        assertTrue("Should contain colocalization info", guide.contains("colocalization"));
    }
    
    @Test
    public void testSettingsAccess() {
        // Create plugin in test mode
        Coloc_Teacher plugin = new Coloc_Teacher(true);
        
        // Inject context dependencies
        context.inject(plugin);
        
        // Run the plugin
        plugin.run();
        
        // Test settings access
        Coloc_Teacher.WizardSettings settings = plugin.getSettings();
        assertNotNull("Settings should be accessible", settings);
        
        // Verify default settings values
        assertEquals("Default number of spots", 50, settings.numSpots);
        assertEquals("Default spot radius", 5.0, settings.spotRadius, 0.01);
        assertEquals("Default overlap fraction", 0.7, settings.overlapFraction, 0.01);
        assertEquals("Default image width", 256, settings.width);
        assertEquals("Default image height", 256, settings.height);
        assertTrue("Noise should be enabled by default", settings.addNoise);
        assertEquals("Default noise std dev", 10.0, settings.noiseStdDev, 0.01);
    }
    
    @Test
    public void testMultipleExecutions() {
        // Test that multiple executions work correctly
        Coloc_Teacher plugin1 = new Coloc_Teacher(true);
        context.inject(plugin1);
        plugin1.run();
        
        Coloc_Teacher plugin2 = new Coloc_Teacher(true);
        context.inject(plugin2);
        plugin2.run();
        
        // Both should generate valid outputs
        Dataset ch1_1 = plugin1.getChannel1();
        Dataset ch1_2 = plugin2.getChannel1();
        
        assertNotNull("First execution should generate channel 1", ch1_1);
        assertNotNull("Second execution should generate channel 1", ch1_2);
        
        // Outputs should be independent (different objects)
        assertNotSame("Outputs should be independent", ch1_1, ch1_2);
    }
    
    @Test
    public void testInterpretationGuideContent() {
        Coloc_Teacher plugin = new Coloc_Teacher(true);
        context.inject(plugin);
        plugin.run();
        
        String guide = plugin.getInterpretationGuide();
        assertNotNull("Interpretation guide should not be null", guide);
        
        // Check for key educational content
        assertTrue("Guide should contain Pearson's R explanation", guide.contains("Pearson's R"));
        assertTrue("Guide should contain Li's ICQ explanation", guide.contains("Li's ICQ"));
        assertTrue("Guide should contain Manders' explanation", guide.contains("Manders'"));
        assertTrue("Guide should contain Kendall's tau explanation", guide.contains("Kendall's tau"));
        assertTrue("Guide should mention colocalization level", guide.contains("colocalization"));
        assertTrue("Guide should contain synthetic data parameters", guide.contains("SYNTHETIC DATA PARAMETERS"));
    }
}
