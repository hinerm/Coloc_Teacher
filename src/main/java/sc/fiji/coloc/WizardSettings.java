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

import ij.ImagePlus;

/**
 * Configuration settings for the Coloc Teacher wizard.
 * 
 * This class holds all parameters collected from the multi-step wizard interface,
 * including synthetic image generation parameters, statistical analysis options,
 * and display preferences.
 * 
 * @author Mark Hiner
 */
public class WizardSettings {
    
    // === Wizard Flow Parameters ===
    
    /** Whether to use synthetic images (true) or user-provided images (false) */
    private boolean useSyntheticImages = true;
    
    /** User-selected channel 1 image (only used when useSyntheticImages is false) */
    private ImagePlus userChannel1Image;
    
    /** User-selected channel 2 image (only used when useSyntheticImages is false) */
    private ImagePlus userChannel2Image;
    
    // === Synthetic Image Parameters ===
    
    /** Number of synthetic spots to generate in each channel */
    private int numSpots = 50;
    
    /** Radius of each synthetic spot in pixels */
    private double spotRadius = 5.0;
    
    /** Fraction of spots that overlap between channels (0.0 to 1.0) */
    private double overlapFraction = 0.7;
    
    /** Whether to add Gaussian noise to the synthetic images */
    private boolean addNoise = true;
    
    /** Standard deviation of Gaussian noise to add */
    private double noiseStdDev = 10.0;
    
    /** Base intensity value for background pixels */
    private int baseIntensity = 30;
    
    /** Maximum intensity value for spot pixels */
    private int maxIntensity = 200;
    
    /** Width of generated images in pixels */
    private int width = 256;
    
    /** Height of generated images in pixels */
    private int height = 256;
    
    // === Costes Significance Test Parameters ===
    
    /** Number of randomizations to perform for Costes significance test */
    private int nrCostesRandomisations = 100;
    
    /** Point spread function value for Costes test */
    private double psf = 3.0;
    
    /** Whether to perform Costes significance test */
    private boolean useCostes = true;
    
    // === Statistical Analysis Parameters ===
    
    /** Whether to calculate Manders' colocalization coefficients */
    private boolean useManders = true;
    
    /** Whether to calculate Li's Intensity Correlation Quotient (ICQ) */
    private boolean useLiICQ = true;
    
    /** Whether to calculate Spearman rank correlation coefficient */
    private boolean useSpearmanRank = true;
    
    /** Whether to calculate Kendall's tau correlation coefficient */
    private boolean useKendallTau = false;
    
    // === Display Options ===
    
    /** Whether to generate and display scatterplot */
    private boolean useScatterplot = true;
    
    /** Whether to display the generated synthetic images */
    private boolean displayImages = false;
    
    /** Whether to display shuffled images from Costes test */
    private boolean displayShuffledCostes = false;
    
    /**
     * Default constructor with sensible default values.
     */
    public WizardSettings() {
        // All fields are already initialized with defaults above
    }
    
    /**
     * Copy constructor for creating a duplicate of existing settings.
     * 
     * @param other the settings to copy
     */
    public WizardSettings(WizardSettings other) {
        this.numSpots = other.getNumSpots();
        this.spotRadius = other.getSpotRadius();
        this.overlapFraction = other.getOverlapFraction();
        this.addNoise = other.isAddNoise();
        this.noiseStdDev = other.getNoiseStdDev();
        this.baseIntensity = other.getBaseIntensity();
        this.maxIntensity = other.getMaxIntensity();
        this.width = other.getWidth();
        this.height = other.getHeight();
        
        this.nrCostesRandomisations = other.getNrCostesRandomisations();
        this.psf = other.getPsf();
        this.useCostes = other.isUseCostes();
        
        this.useManders = other.isUseManders();
        this.useLiICQ = other.isUseLiICQ();
        this.useSpearmanRank = other.isUseSpearmanRank();
        this.useKendallTau = other.isUseKendallTau();
        
        this.useScatterplot = other.isUseScatterplot();
        this.displayImages = other.isDisplayImages();
        this.displayShuffledCostes = other.isDisplayShuffledCostes();
    }
    
    /**
     * Validates the settings and returns any validation errors.
     * 
     * @return null if valid, or error message if invalid
     */
    public String validate() {
        if (numSpots <= 0) {
            return "Number of spots must be positive";
        }
        if (spotRadius <= 0) {
            return "Spot radius must be positive";
        }
        if (overlapFraction < 0.0 || overlapFraction > 1.0) {
            return "Overlap fraction must be between 0.0 and 1.0";
        }
        if (noiseStdDev < 0) {
            return "Noise standard deviation cannot be negative";
        }
        if (baseIntensity < 0 || maxIntensity < 0) {
            return "Intensity values cannot be negative";
        }
        if (baseIntensity >= maxIntensity) {
            return "Base intensity must be less than maximum intensity";
        }
        if (width <= 0 || height <= 0) {
            return "Image dimensions must be positive";
        }
        if (nrCostesRandomisations <= 0) {
            return "Number of Costes randomisations must be positive";
        }
        if (psf <= 0) {
            return "PSF value must be positive";
        }
        
        return null; // No validation errors
    }
    
    // === Getters and Setters ===
    
    // Wizard Flow Parameters
    
    public boolean isUseSyntheticImages() {
        return useSyntheticImages;
    }
    
    public void setUseSyntheticImages(boolean useSyntheticImages) {
        this.useSyntheticImages = useSyntheticImages;
    }
    
    public ImagePlus getUserChannel1Image() {
        return userChannel1Image;
    }
    
    public void setUserChannel1Image(ImagePlus userChannel1Image) {
        this.userChannel1Image = userChannel1Image;
    }
    
    public ImagePlus getUserChannel2Image() {
        return userChannel2Image;
    }
    
    public void setUserChannel2Image(ImagePlus userChannel2Image) {
        this.userChannel2Image = userChannel2Image;
    }
    
    // Synthetic Image Parameters
    
    public int getNumSpots() {
        return numSpots;
    }
    
    public void setNumSpots(int numSpots) {
        if (numSpots <= 0) {
            throw new IllegalArgumentException("Number of spots must be positive");
        }
        this.numSpots = numSpots;
    }
    
    public double getSpotRadius() {
        return spotRadius;
    }
    
    public void setSpotRadius(double spotRadius) {
        if (spotRadius <= 0) {
            throw new IllegalArgumentException("Spot radius must be positive");
        }
        this.spotRadius = spotRadius;
    }
    
    public double getOverlapFraction() {
        return overlapFraction;
    }
    
    public void setOverlapFraction(double overlapFraction) {
        if (overlapFraction < 0.0 || overlapFraction > 1.0) {
            throw new IllegalArgumentException("Overlap fraction must be between 0.0 and 1.0");
        }
        this.overlapFraction = overlapFraction;
    }
    
    public boolean isAddNoise() {
        return addNoise;
    }
    
    public void setAddNoise(boolean addNoise) {
        this.addNoise = addNoise;
    }
    
    public double getNoiseStdDev() {
        return noiseStdDev;
    }
    
    public void setNoiseStdDev(double noiseStdDev) {
        if (noiseStdDev < 0) {
            throw new IllegalArgumentException("Noise standard deviation cannot be negative");
        }
        this.noiseStdDev = noiseStdDev;
    }
    
    public int getBaseIntensity() {
        return baseIntensity;
    }
    
    public void setBaseIntensity(int baseIntensity) {
        if (baseIntensity < 0) {
            throw new IllegalArgumentException("Base intensity cannot be negative");
        }
        if (baseIntensity >= maxIntensity) {
            throw new IllegalArgumentException("Base intensity must be less than maximum intensity");
        }
        this.baseIntensity = baseIntensity;
    }
    
    public int getMaxIntensity() {
        return maxIntensity;
    }
    
    public void setMaxIntensity(int maxIntensity) {
        if (maxIntensity < 0) {
            throw new IllegalArgumentException("Maximum intensity cannot be negative");
        }
        if (baseIntensity >= maxIntensity) {
            throw new IllegalArgumentException("Maximum intensity must be greater than base intensity");
        }
        this.maxIntensity = maxIntensity;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Image width must be positive");
        }
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Image height must be positive");
        }
        this.height = height;
    }
    
    // Costes Parameters
    
    public int getNrCostesRandomisations() {
        return nrCostesRandomisations;
    }
    
    public void setNrCostesRandomisations(int nrCostesRandomisations) {
        if (nrCostesRandomisations <= 0) {
            throw new IllegalArgumentException("Number of Costes randomisations must be positive");
        }
        this.nrCostesRandomisations = nrCostesRandomisations;
    }
    
    public double getPsf() {
        return psf;
    }
    
    public void setPsf(double psf) {
        if (psf <= 0) {
            throw new IllegalArgumentException("PSF value must be positive");
        }
        this.psf = psf;
    }
    
    public boolean isUseCostes() {
        return useCostes;
    }
    
    public void setUseCostes(boolean useCostes) {
        this.useCostes = useCostes;
    }
    
    // Statistical Analysis Parameters
    
    public boolean isUseManders() {
        return useManders;
    }
    
    public void setUseManders(boolean useManders) {
        this.useManders = useManders;
    }
    
    public boolean isUseLiICQ() {
        return useLiICQ;
    }
    
    public void setUseLiICQ(boolean useLiICQ) {
        this.useLiICQ = useLiICQ;
    }
    
    public boolean isUseSpearmanRank() {
        return useSpearmanRank;
    }
    
    public void setUseSpearmanRank(boolean useSpearmanRank) {
        this.useSpearmanRank = useSpearmanRank;
    }
    
    public boolean isUseKendallTau() {
        return useKendallTau;
    }
    
    public void setUseKendallTau(boolean useKendallTau) {
        this.useKendallTau = useKendallTau;
    }
    
    // Display Options
    
    public boolean isUseScatterplot() {
        return useScatterplot;
    }
    
    public void setUseScatterplot(boolean useScatterplot) {
        this.useScatterplot = useScatterplot;
    }
    
    public boolean isDisplayImages() {
        return displayImages;
    }
    
    public void setDisplayImages(boolean displayImages) {
        this.displayImages = displayImages;
    }
    
    public boolean isDisplayShuffledCostes() {
        return displayShuffledCostes;
    }
    
    public void setDisplayShuffledCostes(boolean displayShuffledCostes) {
        this.displayShuffledCostes = displayShuffledCostes;
    }
    
    /**
     * Returns a summary string describing the key settings.
     * 
     * @return human-readable summary of settings
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Synthetic Image Settings:\n");
        sb.append("  Spots: ").append(getNumSpots()).append("\n");
        sb.append("  Radius: ").append(getSpotRadius()).append(" pixels\n");
        sb.append("  Overlap: ").append((int)(getOverlapFraction() * 100)).append("%\n");
        sb.append("  Size: ").append(getWidth()).append("x").append(getHeight()).append("\n");
        sb.append("  Noise: ").append(isAddNoise() ? getNoiseStdDev() + " std dev" : "none").append("\n");
        
        sb.append("\nAnalysis Settings:\n");
        sb.append("  Costes test: ").append(isUseCostes() ? getNrCostesRandomisations() + " randomizations" : "disabled").append("\n");
        sb.append("  Manders: ").append(isUseManders() ? "enabled" : "disabled").append("\n");
        sb.append("  Li ICQ: ").append(isUseLiICQ() ? "enabled" : "disabled").append("\n");
        sb.append("  Spearman: ").append(isUseSpearmanRank() ? "enabled" : "disabled").append("\n");
        sb.append("  Kendall tau: ").append(isUseKendallTau() ? "enabled" : "disabled").append("\n");
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "WizardSettings{" +
                "spots=" + getNumSpots() +
                ", radius=" + getSpotRadius() +
                ", overlap=" + (int)(getOverlapFraction() * 100) + "%" +
                ", size=" + getWidth() + "x" + getHeight() +
                ", noise=" + (isAddNoise() ? getNoiseStdDev() : "none") +
                '}';
    }
}
