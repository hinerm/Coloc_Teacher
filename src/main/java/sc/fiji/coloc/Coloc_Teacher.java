/*-
 * #%L
 * Educational Fiji plugin for teaching colocalization analysis.
 * %%
 * Copyright (C) 2025 Mark Hiner.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without eve        sb.append("\nSYNTHETIC DATA PARAMETERS USED:\n");
        sb.append("• Number of spots: " + settings.getNumSpots() + "\n");
        sb.append("• Spot radius: " + settings.getSpotRadius() + " pixels\n");
        sb.append("• Overlap fraction: " + (settings.getOverlapFraction() * 100) + "%\n");
        sb.append("• Expected colocalization: " + 
                 (settings.getOverlapFraction() > 0.7 ? "High" : 
                  settings.getOverlapFraction() > 0.3 ? "Moderate" : "Low") + "\n");mplied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package sc.fiji.coloc;

import ij.ImagePlus;

import java.util.Random;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandModule;
import org.scijava.command.CommandService;
import org.scijava.convert.ConvertService;
import org.scijava.log.LogService;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

/**
 * Educational Fiji plugin for teaching colocalization analysis with synthetic data generation.
 * 
 * This plugin uses a multi-step wizard interface to guide users through parameter selection
 * while providing educational context at each step.
 *
 * @author Mark Hiner
 */
@Plugin(type = Command.class, headless = false, 
    menuPath = "Plugins>Colocalization>Coloc Teacher")
public class Coloc_Teacher implements Command {

    // Services
    @Parameter
    private LogService log;

    @Parameter
    private ConvertService convertService;

    @Parameter
    private DatasetService datasetService;

    @Parameter
    private UIService uiService;

    @Parameter
    private CommandService commandService;

    @Parameter  
    private ModuleService moduleService;

    // Test mode field - when true, skips wizard and uses default values
    // This is only for automated testing and should never be visible to users
    private boolean testMode = false;

    // Outputs
    @Parameter(type = ItemIO.OUTPUT)
    private Dataset channel1;

    @Parameter(type = ItemIO.OUTPUT)
    private Dataset channel2;

    @Parameter(type = ItemIO.OUTPUT)
    private String interpretationGuide;

    // Internal parameter storage (collected via wizard)
    private WizardSettings settings = new WizardSettings();
    
    /**
     * Default constructor for SciJava plugin framework
     */
    public Coloc_Teacher() {
        this.testMode = false;
    }
    
    /**
     * Constructor for testing with specified test mode
     * @param testMode if true, skips wizard and uses default values
     */
    public Coloc_Teacher(boolean testMode) {
        this.testMode = testMode;
    }

    @Override
    public void run() {
        log.info("Starting Coloc_Teacher" + (testMode ? " in test mode" : " wizard") + "...");
        
        try {
            if (testMode) {
                // Test mode: use default values without wizard
                log.info("Test mode: using default parameters");
                executeAnalysis();
            } else {
                // Normal mode: run wizard to collect parameters
                runWizard();
            }
            
        } catch (Exception e) {
            log.error("Error during execution", e);
            uiService.showDialog("Error: " + e.getMessage());
        }
    }
    
    private void runWizard() throws Exception {
        // Step 1: Synthetic Image Setup
        log.info("Step 1: Configuring synthetic image parameters...");
        
        CommandModule step1Module;
        try {
            step1Module = commandService.run(SyntheticImageWizard.class, true).get();
        } catch (Exception e) {
            log.info("Wizard cancelled by user at step 1");
            return;
        }
        
        // Check if cancelled
        if (step1Module == null || step1Module.isCanceled()) {
            log.info("Wizard cancelled by user at step 1");
            return;
        }
        
        // Get the parameters from the completed wizard step
        SyntheticImageWizard step1 = (SyntheticImageWizard) step1Module.getCommand();
        settings.setWidth(step1.width);
        settings.setHeight(step1.height);
        settings.setNumSpots(step1.numSpots);
        settings.setSpotRadius(step1.spotRadius);
        settings.setOverlapFraction(step1.overlapFraction);
        settings.setAddNoise(step1.addNoise);
        settings.setNoiseStdDev(step1.noiseStdDev);
        
        // Step 2: Costes Test Setup
        log.info("Step 2: Configuring Costes significance test...");
        
        CommandModule step2Module;
        try {
            step2Module = commandService.run(CostesWizard.class, true).get();
        } catch (Exception e) {
            log.info("Wizard cancelled by user at step 2");
            return;
        }
        
        // Check if cancelled
        if (step2Module == null || step2Module.isCanceled()) {
            log.info("Wizard cancelled by user at step 2");
            return;
        }
        
        CostesWizard step2 = (CostesWizard) step2Module.getCommand();
        settings.setUseCostes(step2.useCostes);
        settings.setNrCostesRandomisations(step2.nrCostesRandomisations);
        settings.setPsf(step2.psf);
        settings.setDisplayShuffledCostes(step2.displayShuffledCostes);
        
        // Step 3: Statistical Methods
        log.info("Step 3: Selecting statistical methods...");
        
        CommandModule step3Module;
        try {
            step3Module = commandService.run(StatisticsWizard.class, true).get();
        } catch (Exception e) {
            log.info("Wizard cancelled by user at step 3");
            return;
        }
        
        // Check if cancelled
        if (step3Module == null || step3Module.isCanceled()) {
            log.info("Wizard cancelled by user at step 3");
            return;
        }
        
        StatisticsWizard step3 = (StatisticsWizard) step3Module.getCommand();
        settings.setUseLiICQ(step3.useLiICQ);
        settings.setUseSpearmanRank(step3.useSpearmanRank);
        settings.setUseManders(step3.useManders);
        settings.setUseKendallTau(step3.useKendallTau);
        
        // Step 4: Display Options
        log.info("Step 4: Configuring display options...");
        
        CommandModule step4Module;
        try {
            step4Module = commandService.run(DisplayWizard.class, true).get();
        } catch (Exception e) {
            log.info("Wizard cancelled by user at step 4");
            return;
        }
        
        // Check if cancelled
        if (step4Module == null || step4Module.isCanceled()) {
            log.info("Wizard cancelled by user at step 4");
            return;
        }
        
        DisplayWizard step4 = (DisplayWizard) step4Module.getCommand();
        settings.setDisplayImages(step4.displayImages);
        settings.setUseScatterplot(step4.useScatterplot);
        
        // Now execute the analysis with collected settings
        log.info("Wizard complete. Starting analysis with selected parameters...");
        executeAnalysis();
    }

    private void executeAnalysis() {
        try {
            log.info("Generating synthetic colocalization test images...");
            
            // Generate synthetic images using settings
            generateSyntheticImages();

            // Run colocalization analysis
            runColocalizationAnalysis();

            // Generate interpretation guide
            generateInterpretationGuide();

            log.info("Coloc Teacher completed successfully!");

        } catch (Exception e) {
            log.error("Error in Coloc Teacher: " + e.getMessage(), e);
            if (uiService != null) {
                uiService.showDialog("An error occurred: " + e.getMessage(), "Coloc Teacher Error");
            }
        }
    }

    private void generateSyntheticImages() {
        Random rand = new Random();

        log.info("Generating " + settings.getNumSpots() + " synthetic spots with " + 
                (settings.getOverlapFraction() * 100) + "% overlap");

        // Create datasets
        final String name1 = "Synthetic Channel 1";
        final String name2 = "Synthetic Channel 2";
        final long[] dims = { settings.getWidth(), settings.getHeight() };
        final AxisType[] axes = { Axes.X, Axes.Y };

        channel1 = datasetService.create(new FloatType(), dims, name1, axes);
        channel2 = datasetService.create(new FloatType(), dims, name2, axes);

        // Get images for processing
        @SuppressWarnings("unchecked")
        Img<FloatType> img1 = (Img<FloatType>) channel1.getImgPlus();
        @SuppressWarnings("unchecked")
        Img<FloatType> img2 = (Img<FloatType>) channel2.getImgPlus();

        // Initialize with background intensity
        addBackground(img1, settings.getBaseIntensity());
        addBackground(img2, settings.getBaseIntensity());

        // Generate spots
        for (int i = 0; i < settings.getNumSpots(); i++) {
            // Random center for spot 1
            int spotRadiusInt = (int)Math.ceil(settings.getSpotRadius());
            int x = rand.nextInt(settings.getWidth() - 2 * spotRadiusInt) + spotRadiusInt;
            int y = rand.nextInt(settings.getHeight() - 2 * spotRadiusInt) + spotRadiusInt;

            // Variable intensity for more realistic distribution
            float intensity1 = settings.getBaseIntensity() + rand.nextInt(settings.getMaxIntensity() - settings.getBaseIntensity());

            // Add Gaussian-like spot to channel 1
            addGaussianSpot(img1, x, y, intensity1, settings.getSpotRadius());

            // For overlapFraction, add spot to channel 2 at same location, else random offset
            int x2, y2;
            if (rand.nextDouble() < settings.getOverlapFraction()) {
                x2 = x;
                y2 = y;
            } else {
                x2 = rand.nextInt(settings.getWidth() - 2 * spotRadiusInt) + spotRadiusInt;
                y2 = rand.nextInt(settings.getHeight() - 2 * spotRadiusInt) + spotRadiusInt;
            }

            float intensity2 = settings.getBaseIntensity() + rand.nextInt(settings.getMaxIntensity() - settings.getBaseIntensity());

            // Add Gaussian-like spot to channel 2
            addGaussianSpot(img2, x2, y2, intensity2, settings.getSpotRadius());
        }

        // Clamp intensities
        clampIntensities(img1, settings.getMaxIntensity() + settings.getBaseIntensity());
        clampIntensities(img2, settings.getMaxIntensity() + settings.getBaseIntensity());
        
        // Add noise if requested
        if (settings.isAddNoise() && settings.getNoiseStdDev() > 0) {
            log.info("Adding Gaussian noise with standard deviation: " + settings.getNoiseStdDev());
            addGaussianNoise(img1, settings.getNoiseStdDev(), rand);
            addGaussianNoise(img2, settings.getNoiseStdDev(), rand);
        }
    }

    private void addBackground(Img<FloatType> img, float value) {
        Cursor<FloatType> cursor = img.cursor();
        while (cursor.hasNext()) {
            cursor.next().set(value);
        }
    }

    private void addGaussianSpot(Img<FloatType> img, int centerX, int centerY, 
                                 float intensity, double radius) {
        RandomAccess<FloatType> ra = img.randomAccess();
        
        int intRadius = (int) Math.ceil(radius);
        for (int dx = -intRadius; dx <= intRadius; dx++) {
            for (int dy = -intRadius; dy <= intRadius; dy++) {
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist <= radius) {
                    double falloff = Math.exp(-dist * dist / (radius * radius / 4.0));
                    int px = centerX + dx;
                    int py = centerY + dy;
                    
                    if (px >= 0 && px < settings.getWidth() && py >= 0 && py < settings.getHeight()) {
                        ra.setPosition(px, 0);
                        ra.setPosition(py, 1);
                        float currentVal = ra.get().get();
                        ra.get().set(currentVal + (float) (intensity * falloff));
                    }
                }
            }
        }
    }

    private void clampIntensities(Img<FloatType> img, float maxValue) {
        Cursor<FloatType> cursor = img.cursor();
        while (cursor.hasNext()) {
            FloatType pixel = cursor.next();
            if (pixel.get() > maxValue) {
                pixel.set(maxValue);
            }
        }
    }

    private void addGaussianNoise(Img<FloatType> img, double stdDev, Random rand) {
        Cursor<FloatType> cursor = img.cursor();
        while (cursor.hasNext()) {
            FloatType pixel = cursor.next();
            double noise = rand.nextGaussian() * stdDev;
            float newValue = pixel.get() + (float) noise;
            // Ensure non-negative values
            pixel.set(Math.max(0, newValue));
        }
    }

    private void runColocalizationAnalysis() {
        log.info("Running colocalization analysis...");
        
        try {
            // Convert ImageJ2 Datasets to ImageJ1 ImagePlus objects for Coloc_2
            ImagePlus imp1 = convertService.convert(channel1, ImagePlus.class);
            ImagePlus imp2 = convertService.convert(channel2, ImagePlus.class);
            
            if (imp1 == null || imp2 == null) {
                log.error("Failed to convert Dataset to ImagePlus for colocalization analysis");
                return;
            }
            
            // Set appropriate titles for display
            imp1.setTitle("Synthetic Channel 1");
            imp2.setTitle("Synthetic Channel 2");
            
            // Create and initialize Coloc_2 instance using the proper API
            Coloc_2<net.imglib2.type.numeric.real.FloatType> coloc2 = new Coloc_2<>();
            
            // Initialize settings using the proper API from Coloc_2
            boolean success = coloc2.initializeSettings(
                imp1,                          // img1
                imp2,                          // img2
                0,                             // indexMask (no mask)
                0,                             // indexRegr (regression method)
                false,                         // autoSavePdf
                settings.isDisplayImages(),    // displayImages
                settings.isDisplayShuffledCostes(), // displayShuffledCostes
                settings.isUseLiICQ(),         // useLiCh1
                settings.isUseLiICQ(),         // useLiCh2
                settings.isUseLiICQ(),         // useLiICQ
                settings.isUseSpearmanRank(),  // useSpearmanRank
                settings.isUseManders(),       // useManders
                settings.isUseKendallTau(),    // useKendallTau
                settings.isUseScatterplot(),   // useScatterplot
                settings.isUseCostes(),        // useCostes
                (int)settings.getPsf(),        // psf - cast to int
                settings.getNrCostesRandomisations() // nrCostesRandomisations
            );
            
            if (!success) {
                log.error("Failed to initialize Coloc_2 settings");
                return;
            }
            
            // Log the colocalization analysis parameters
            log.info("Colocalization analysis parameters:");
            log.info("  Costes randomizations: " + settings.getNrCostesRandomisations());
            log.info("  PSF: " + settings.getPsf());
            log.info("  Use Manders: " + settings.isUseManders());
            log.info("  Use Li ICQ: " + settings.isUseLiICQ());
            log.info("  Use Spearman: " + settings.isUseSpearmanRank());
            log.info("  Use Kendall tau: " + settings.isUseKendallTau());
            log.info("  Use scatterplot: " + settings.isUseScatterplot());
            log.info("  Use Costes test: " + settings.isUseCostes());
            
            try {
                Coloc_2<FloatType>.MaskInfo maskInfo = coloc2.masks.get(0);
                coloc2.colocalise(coloc2.img1, coloc2.img2, maskInfo.roi, maskInfo.mask);
                log.info("Colocalization analysis completed successfully");
            } catch (sc.fiji.coloc.algorithms.MissingPreconditionException e) {
                log.error("Colocalization analysis failed due to missing precondition: " + e.getMessage());
            }
            
        } catch (Exception e) {
            log.error("Error during colocalization analysis", e);
            // Provide fallback message
            log.info("Note: For full colocalization analysis, you can:");
            log.info("1. Use the generated images with Fiji's Coloc_2 plugin");
            log.info("2. Or implement pure ImageJ2 colocalization algorithms");
        }
    }

    private void generateInterpretationGuide() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("HOW TO INTERPRET COLOC2 RESULTS\n");
        sb.append("============================================================\n");
        sb.append("DROPDOWN MENU AT TOP:\n");
        sb.append("• 'Li - Ch1' & 'Li - Ch2': Shows Li's ICQ intensity distribution for each channel\n");
        sb.append("• '2D intensity histogram': Scatterplot of Ch1 vs Ch2 pixel intensities\n");
        sb.append("\n");
        sb.append("KEY STATISTICS TO INTERPRET:\n");
        sb.append("• Pearson's R (no threshold): Overall correlation (-1 to +1)\n");
        sb.append("  - Near +1: Strong positive correlation (colocalization)\n");
        sb.append("  - Near 0: No correlation\n");
        sb.append("  - Near -1: Strong negative correlation (segregation)\n");
        sb.append("\n");
        sb.append("• Li's ICQ value: Degree of colocalization (-0.5 to +0.5)\n");
        sb.append("  - Positive: Colocalization above random\n");
        sb.append("  - Near 0: Random distribution\n");
        sb.append("  - Negative: Segregation below random\n");
        sb.append("\n");
        sb.append("• Kendall's tau: Non-parametric correlation coefficient (-1 to +1)\n");
        sb.append("  - Similar to Pearson's R but less sensitive to outliers\n");
        sb.append("  - More robust for non-linear relationships\n");
        sb.append("  - Near +1: Strong positive correlation\n");
        sb.append("  - Near 0: No correlation\n");
        sb.append("  - Near -1: Strong negative correlation\n");
        sb.append("\n");
        sb.append("• Manders' coefficients (tM1, tM2): Overlap above threshold (0 to 1)\n");
        sb.append("  - 1.0: Perfect overlap\n");
        sb.append("  - 0.5: Half the pixels overlap\n");
        sb.append("  - 0.0: No overlap\n");
        sb.append("\n");
        sb.append("• Costes P-Value: Statistical significance\n");
        sb.append("  - < 0.05: Correlation is statistically significant\n");
        sb.append("  - > 0.05: Correlation could be due to chance\n");
        sb.append("\n");
        sb.append("• % saturated pixels: Should be low (< 1%) for reliable analysis\n");
        sb.append("• b to y-mean ratio: Should be close to 0 for good data quality\n");
        sb.append("============================================================\n");
        sb.append("\n");
        sb.append("SYNTHETIC DATA PARAMETERS USED:\n");
        sb.append("• Number of spots: " + settings.getNumSpots() + "\n");
        sb.append("• Spot radius: " + settings.getSpotRadius() + " pixels\n");
        sb.append("• Overlap fraction: " + (settings.getOverlapFraction() * 100) + "%\n");
        sb.append("• Expected colocalization: " + 
                 (settings.getOverlapFraction() > 0.7 ? "High" : 
                  settings.getOverlapFraction() > 0.3 ? "Moderate" : "Low") + "\n");
        sb.append("============================================================\n");

        interpretationGuide = sb.toString();
    }
    
    // Getter methods for testing
    
    /**
     * Get the generated channel 1 dataset.
     * @return the synthetic channel 1 dataset, or null if not yet generated
     */
    public Dataset getChannel1() {
        return channel1;
    }
    
    /**
     * Get the generated channel 2 dataset.
     * @return the synthetic channel 2 dataset, or null if not yet generated
     */
    public Dataset getChannel2() {
        return channel2;
    }
    
    /**
     * Get the interpretation guide text.
     * @return the interpretation guide, or null if not yet generated
     */
    public String getInterpretationGuide() {
        return interpretationGuide;
    }
    
    /**
     * Get the current wizard settings (for testing purposes).
     * @return a copy of the current settings
     */
    public WizardSettings getSettings() {
        return settings;
    }
}
