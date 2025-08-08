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

import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.module.Module;

import net.imagej.Dataset;

/**
 * Tests for the Coloc_Teacher plugin using ImageJ2 Command testing patterns.
 *
 * @author Your Name
 */
public class Coloc_TeacherTest {

    private Context context;
    private CommandService commandService;

    @Before
    public void setUp() {
        context = new Context();
        commandService = context.getService(CommandService.class);
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
    public void testCommandExecution() throws InterruptedException, ExecutionException {
        // Test the command with default parameters
        Module module = commandService.run(Coloc_Teacher.class, true,
            "width", 512,
            "height", 512,
            "numSpots", 20,
            "spotRadius", 10,
            "overlapFraction", 0.5,
            "baseIntensity", 30,
            "maxIntensity", 200).get();
        
        assertNotNull("Module should be executed", module);
        
        // Check outputs
        Dataset ch1 = (Dataset) module.getOutput("channel1");
        Dataset ch2 = (Dataset) module.getOutput("channel2");
        String guide = (String) module.getOutput("interpretationGuide");
        
        assertNotNull("Channel 1 should be generated", ch1);
        assertNotNull("Channel 2 should be generated", ch2);
        assertNotNull("Interpretation guide should be generated", guide);
        
        // Verify image dimensions
        assertEquals("Channel 1 width should match input", 512L, ch1.dimension(0));
        assertEquals("Channel 1 height should match input", 512L, ch1.dimension(1));
        assertEquals("Channel 2 width should match input", 512L, ch2.dimension(0));
        assertEquals("Channel 2 height should match input", 512L, ch2.dimension(1));
        
        // Verify interpretation guide contains expected content
        assertTrue("Should contain correlation analysis", guide.contains("correlation"));
        assertTrue("Should contain colocalization info", guide.contains("colocalization"));
    }
    
    @Test
    public void testDifferentColocalizationLevels() throws InterruptedException, ExecutionException {
        // Test with high colocalization
        Module highColoc = commandService.run(Coloc_Teacher.class, true,
            "width", 256,
            "height", 256,
            "numSpots", 10,
            "spotRadius", 5,
            "overlapFraction", 0.9,
            "baseIntensity", 20,
            "maxIntensity", 150).get();
        
        assertNotNull("High colocalization module should execute", highColoc);
        
        // Test with low colocalization
        Module lowColoc = commandService.run(Coloc_Teacher.class, true,
            "width", 256,
            "height", 256,
            "numSpots", 10,
            "spotRadius", 5,
            "overlapFraction", 0.1,
            "baseIntensity", 20,
            "maxIntensity", 150).get();
        
        assertNotNull("Low colocalization module should execute", lowColoc);
        
        // Both should generate valid outputs
        Dataset highCh1 = (Dataset) highColoc.getOutput("channel1");
        Dataset lowCh1 = (Dataset) lowColoc.getOutput("channel1");
        
        assertNotNull("High colocalization should generate channel 1", highCh1);
        assertNotNull("Low colocalization should generate channel 1", lowCh1);
    }
    
    @Test
    public void testInterpretationGuideContent() throws InterruptedException, ExecutionException {
        Module module = commandService.run(Coloc_Teacher.class, true,
            "width", 128,
            "height", 128,
            "numSpots", 5,
            "spotRadius", 8,
            "overlapFraction", 0.7,
            "baseIntensity", 25,
            "maxIntensity", 175).get();
        
        String guide = (String) module.getOutput("interpretationGuide");
        assertNotNull("Interpretation guide should not be null", guide);
        
        // Check for key educational content
        assertTrue("Guide should contain Pearson's R explanation", guide.contains("Pearson's R"));
        assertTrue("Guide should contain Li's ICQ explanation", guide.contains("Li's ICQ"));
        assertTrue("Guide should contain Manders' explanation", guide.contains("Manders'"));
        assertTrue("Guide should contain Kendall's tau explanation", guide.contains("Kendall's tau"));
        assertTrue("Guide should mention colocalization level", guide.contains("colocalization"));
    }
}
