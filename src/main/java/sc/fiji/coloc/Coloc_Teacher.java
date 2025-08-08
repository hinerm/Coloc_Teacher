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
import org.scijava.convert.ConvertService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

/**
 * Educational Fiji plugin for teaching colocalization analysis with synthetic data generation.
 * 
 * This plugin generates synthetic colocalization test images and provides educational 
 * guidance on interpreting colocalization analysis results.
 *
 * @author Mark Hiner
 */
@Plugin(type = Command.class, headless = true, 
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

    // Parameters for synthetic image generation
    @Parameter(label = "Number of spots", min = "10", max = "500")
    private int numSpots = 100;

    @Parameter(label = "Spot radius (pixels)", min = "2", max = "50")
    private int spotRadius = 12;

    @Parameter(label = "Overlap fraction", min = "0.0", max = "1.0", stepSize = "0.1")
    private double overlapFraction = 0.7;

    @Parameter(label = "Base intensity", min = "0", max = "100")
    private int baseIntensity = 30;

    @Parameter(label = "Max intensity", min = "100", max = "1000")
    private int maxIntensity = 200;

    @Parameter(label = "Image width", min = "64", max = "1024")
    private int width = 256;

    @Parameter(label = "Image height", min = "64", max = "1024")
    private int height = 256;

    // Coloc_2 parameters
    @Parameter(label = "Costes randomizations", min = "1", max = "100")
    private int nrCostesRandomisations = 10;

    @Parameter(label = "PSF (for Costes test)", min = "1", max = "10")
    private int psf = 4;

    @Parameter(label = "Use Manders' coefficients")
    private boolean useManders = true;

    @Parameter(label = "Use Li's ICQ")
    private boolean useLiICQ = true;

    @Parameter(label = "Use Spearman rank correlation")
    private boolean useSpearmanRank = true;

    @Parameter(label = "Use Kendall's tau")
    private boolean useKendallTau = false;

    @Parameter(label = "Show scatterplot")
    private boolean useScatterplot = true;

    @Parameter(label = "Use Costes' significance test")
    private boolean useCostes = true;

    @Parameter(label = "Display result images")
    private boolean displayImages = false;

    @Parameter(label = "Display shuffled Costes images")
    private boolean displayShuffledCostes = false;

    // Outputs
    @Parameter(type = ItemIO.OUTPUT)
    private Dataset channel1;

    @Parameter(type = ItemIO.OUTPUT)
    private Dataset channel2;

    @Parameter(type = ItemIO.OUTPUT)
    private String interpretationGuide;

    @Override
    public void run() {
        try {
            log.info("Generating synthetic colocalization test images...");
            
            // Generate synthetic images
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

        log.info("Generating " + numSpots + " synthetic spots with " + 
                (overlapFraction * 100) + "% overlap");

        // Create datasets
        final String name1 = "Synthetic Channel 1";
        final String name2 = "Synthetic Channel 2";
        final long[] dims = { width, height };
        final AxisType[] axes = { Axes.X, Axes.Y };

        channel1 = datasetService.create(new FloatType(), dims, name1, axes);
        channel2 = datasetService.create(new FloatType(), dims, name2, axes);

        // Get images for processing
        @SuppressWarnings("unchecked")
        Img<FloatType> img1 = (Img<FloatType>) channel1.getImgPlus();
        @SuppressWarnings("unchecked")
        Img<FloatType> img2 = (Img<FloatType>) channel2.getImgPlus();

        // Initialize with background intensity
        addBackground(img1, baseIntensity);
        addBackground(img2, baseIntensity);

        // Generate spots
        for (int i = 0; i < numSpots; i++) {
            // Random center for spot 1
            int x = rand.nextInt(width - 2 * spotRadius) + spotRadius;
            int y = rand.nextInt(height - 2 * spotRadius) + spotRadius;

            // Variable intensity for more realistic distribution
            float intensity1 = baseIntensity + rand.nextInt(maxIntensity - baseIntensity);

            // Add Gaussian-like spot to channel 1
            addGaussianSpot(img1, x, y, intensity1, spotRadius);

            // For overlapFraction, add spot to channel 2 at same location, else random offset
            int x2, y2;
            if (rand.nextDouble() < overlapFraction) {
                x2 = x;
                y2 = y;
            } else {
                x2 = rand.nextInt(width - 2 * spotRadius) + spotRadius;
                y2 = rand.nextInt(height - 2 * spotRadius) + spotRadius;
            }

            float intensity2 = baseIntensity + rand.nextInt(maxIntensity - baseIntensity);

            // Add Gaussian-like spot to channel 2
            addGaussianSpot(img2, x2, y2, intensity2, spotRadius);
        }

        // Clamp intensities
        clampIntensities(img1, maxIntensity + baseIntensity);
        clampIntensities(img2, maxIntensity + baseIntensity);
    }

    private void addBackground(Img<FloatType> img, float value) {
        Cursor<FloatType> cursor = img.cursor();
        while (cursor.hasNext()) {
            cursor.next().set(value);
        }
    }

    private void addGaussianSpot(Img<FloatType> img, int centerX, int centerY, 
                                 float intensity, int radius) {
        RandomAccess<FloatType> ra = img.randomAccess();
        
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist <= radius) {
                    double falloff = Math.exp(-dist * dist / (radius * radius / 4.0));
                    int px = centerX + dx;
                    int py = centerY + dy;
                    
                    if (px >= 0 && px < width && py >= 0 && py < height) {
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
                displayImages,                 // displayImages
                displayShuffledCostes,         // displayShuffledCostes
                useLiICQ,                      // useLiCh1
                useLiICQ,                      // useLiCh2
                useLiICQ,                      // useLiICQ
                useSpearmanRank,               // useSpearmanRank
                useManders,                    // useManders
                useKendallTau,                 // useKendallTau
                useScatterplot,                // useScatterplot
                useCostes,                     // useCostes
                psf,                           // psf
                nrCostesRandomisations         // nrCostesRandomisations
            );
            
            if (!success) {
                log.error("Failed to initialize Coloc_2 settings");
                return;
            }
            
            // Log the colocalization analysis parameters
            log.info("Colocalization analysis parameters:");
            log.info("  Costes randomizations: " + nrCostesRandomisations);
            log.info("  PSF: " + psf);
            log.info("  Use Manders: " + useManders);
            log.info("  Use Li ICQ: " + useLiICQ);
            log.info("  Use Spearman: " + useSpearmanRank);
            log.info("  Use Kendall tau: " + useKendallTau);
            log.info("  Use scatterplot: " + useScatterplot);
            log.info("  Use Costes test: " + useCostes);
            
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
        sb.append("• Number of spots: " + numSpots + "\n");
        sb.append("• Spot radius: " + spotRadius + " pixels\n");
        sb.append("• Overlap fraction: " + (overlapFraction * 100) + "%\n");
        sb.append("• Expected colocalization: " + 
                 (overlapFraction > 0.7 ? "High" : 
                  overlapFraction > 0.3 ? "Moderate" : "Low") + "\n");
        sb.append("============================================================\n");

        interpretationGuide = sb.toString();
    }
}
