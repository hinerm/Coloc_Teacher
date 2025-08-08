// @Integer(label="Number of Costes randomizations", value=10) nrCostesRandomisations
// @Integer(label="PSF (for Costes test)", value=4) psf
// @Boolean(label="Use Manders' coefficients", value=true) useManders
// @Boolean(label="Use Li's ICQ", value=true) useLiICQ
// @Boolean(label="Use Spearman rank correlation", value=true) useSpearmanRank
// @Boolean(label="Use Kendall's tau", value=false) useKendallTau
// @Boolean(label="Show scatterplot", value=true) useScatterplot
// @Boolean(label="Use Costes' significance test", value=true) useCostes
// @Boolean(label="Display result images", value=false) displayImages
// @Boolean(label="Display shuffled Costes images", value=false) displayShuffledCostes
// @LogService log
// @OUTPUT String interpretationGuide

import ij.IJ
import ij.plugin.Duplicator
import sc.fiji.coloc.Coloc_2

// Step 1: Prepare images
// Shift imp2 by 20 pixels right and 10 pixels down
println "Generating synthetic test images with better signal distribution."
import ij.ImagePlus
import ij.process.FloatProcessor
import java.util.Random

width = 256
height = 256
numSpots = 100
spotRadius = 12
overlapFraction = 0.7 // Fraction of spots that overlap
baseIntensity = 30 // Background level to reduce zero pixels
maxIntensity = 200 // Avoid saturation (instead of 255)
rand = new Random()

// Create images with background level
fp1 = new FloatProcessor(width, height)
fp2 = new FloatProcessor(width, height)
fp1.add(baseIntensity)
fp2.add(baseIntensity)

for (i = 0; i < numSpots; i++) {
    // Random center for spot 1
    x = rand.nextInt(width - 2*spotRadius) + spotRadius
    y = rand.nextInt(height - 2*spotRadius) + spotRadius
    
    // Variable intensity for more realistic distribution
    intensity1 = baseIntensity + rand.nextInt(maxIntensity - baseIntensity)
    
    // Add Gaussian-like spot to channel 1 (softer edges)
    for (dx = -spotRadius; dx <= spotRadius; dx++) {
        for (dy = -spotRadius; dy <= spotRadius; dy++) {
            dist = Math.sqrt(dx*dx + dy*dy)
            if (dist <= spotRadius) {
                falloff = Math.exp(-dist*dist / (spotRadius*spotRadius/4))
                px = x + dx
                py = y + dy
                if (px >= 0 && px < width && py >= 0 && py < height) {
                    currentVal = fp1.getPixelValue(px, py)
                    fp1.setf(px, py, (currentVal + intensity1 * falloff) as float)
                }
            }
        }
    }

    // For overlapFraction, add spot to channel 2 at same location, else random offset
    if (rand.nextDouble() < overlapFraction) {
        x2 = x
        y2 = y
    } else {
        x2 = rand.nextInt(width - 2*spotRadius) + spotRadius
        y2 = rand.nextInt(height - 2*spotRadius) + spotRadius
    }
    
    intensity2 = baseIntensity + rand.nextInt(maxIntensity - baseIntensity)
    
    // Add Gaussian-like spot to channel 2
    for (dx = -spotRadius; dx <= spotRadius; dx++) {
        for (dy = -spotRadius; dy <= spotRadius; dy++) {
            dist = Math.sqrt(dx*dx + dy*dy)
            if (dist <= spotRadius) {
                falloff = Math.exp(-dist*dist / (spotRadius*spotRadius/4))
                px = x2 + dx
                py = y2 + dy
                if (px >= 0 && px < width && py >= 0 && py < height) {
                    currentVal = fp2.getPixelValue(px, py)
                    fp2.setf(px, py, (currentVal + intensity2 * falloff) as float)
                }
            }
        }
    }
}

// Ensure no values exceed maxIntensity + baseIntensity
for (x = 0; x < width; x++) {
    for (y = 0; y < height; y++) {
        if (fp1.getPixelValue(x, y) > maxIntensity + baseIntensity) {
            fp1.setf(x, y, (maxIntensity + baseIntensity) as float)
        }
        if (fp2.getPixelValue(x, y) > maxIntensity + baseIntensity) {
            fp2.setf(x, y, (maxIntensity + baseIntensity) as float)
        }
    }
}

imp1 = new ImagePlus("Synthetic Channel 1", fp1)
imp2 = new ImagePlus("Synthetic Channel 2", fp2)
imp1.show()
imp2.show()

// Step 2: Set up Coloc2
coloc2 = new Coloc_2()
coloc2.initializeSettings(
    imp1,
    imp2,
    0,  // indexMask
    0,  // indexRegr
    false,  // autoSavePdf
    displayImages,  // displayImages
    displayShuffledCostes,  // displayShuffledCostes
    useLiICQ,  // useLiCh1
    useLiICQ,  // useLiCh2
    useLiICQ,
    useSpearmanRank,
    useManders,
    useKendallTau,
    useScatterplot,
    useCostes,
    psf,
    nrCostesRandomisations)

// Step 3: Run Coloc2
img1 = coloc2.img1
img2 = coloc2.img2
box = coloc2.masks[0].roi
mask = coloc2.masks[0].mask
results = coloc2.colocalise(img1, img2, box, mask, null)

// Step 4: Output results using LogService
log.info("Colocalization results:")
for (v in results.values()) {
    log.info("  ${v.name} = " + (v.isNumber ? v.number : v.value))
}

log.info("\nHistograms:")
for (h in results.histograms()) {
    log.info("  ${h}")
}

log.info("\n--- Colocalization analysis complete. ---")

// Create interpretation guide as OUTPUT string
interpretationGuide = """
${"="*60}
HOW TO INTERPRET COLOC2 RESULTS
${"="*60}
DROPDOWN MENU AT TOP:
• 'Li - Ch1' & 'Li - Ch2': Shows Li's ICQ intensity distribution for each channel
• '2D intensity histogram': Scatterplot of Ch1 vs Ch2 pixel intensities

KEY STATISTICS TO INTERPRET:
• Pearson's R (no threshold): Overall correlation (-1 to +1)
  - Near +1: Strong positive correlation (colocalization)
  - Near 0: No correlation
  - Near -1: Strong negative correlation (segregation)

• Li's ICQ value: Degree of colocalization (-0.5 to +0.5)
  - Positive: Colocalization above random
  - Near 0: Random distribution
  - Negative: Segregation below random

• Kendall's tau: Non-parametric correlation coefficient (-1 to +1)
  - Similar to Pearson's R but less sensitive to outliers
  - More robust for non-linear relationships
  - Near +1: Strong positive correlation
  - Near 0: No correlation
  - Near -1: Strong negative correlation

• Manders' coefficients (tM1, tM2): Overlap above threshold (0 to 1)
  - 1.0: Perfect overlap
  - 0.5: Half the pixels overlap
  - 0.0: No overlap

• Costes P-Value: Statistical significance
  - < 0.05: Correlation is statistically significant
  - > 0.05: Correlation could be due to chance

• % saturated pixels: Should be low (< 1%) for reliable analysis
• b to y-mean ratio: Should be close to 0 for good data quality
${"="*60}
"""