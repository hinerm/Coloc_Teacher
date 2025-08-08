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

import java.util.Random;

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

import ij.ImagePlus;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Educational Fiji plugin for teaching colocalization analysis with synthetic
 * data generation.
 *
 * This plugin uses a multi-step wizard interface to guide users through
 * parameter selection while providing educational context at each step.
 *
 * @author Mark Hiner
 */
@Plugin( type = Command.class, headless = false, menuPath = "Plugins>Colocalization>Coloc Teacher" )
public class Coloc_Teacher implements Command
{

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
    @Parameter( type = ItemIO.OUTPUT )
    private Dataset channel1;

    @Parameter( type = ItemIO.OUTPUT )
    private Dataset channel2;

    @Parameter( type = ItemIO.OUTPUT )
    private String interpretationGuide;

    // Internal parameter storage (collected via wizard)
    private WizardSettings settings = new WizardSettings();

    /**
     * Default constructor for SciJava plugin framework
     */
    public Coloc_Teacher()
    {
        this.testMode = false;
    }

    /**
     * Constructor for testing with specified test mode
     *
     * @param testMode if true, skips wizard and uses default values
     */
    public Coloc_Teacher( boolean testMode )
    {
        this.testMode = testMode;
    }

    @Override
    public void run()
    {
        log.info( "Starting Coloc_Teacher" + ( testMode ? " in test mode" : " wizard" ) + "..." );

        try
        {
            if ( testMode )
            {
                // Test mode: use default values without wizard
                log.info( "Test mode: using default parameters" );
                executeAnalysis();
            }
            else
            {
                // Normal mode: run wizard to collect parameters
                runWizard();
            }

        }
        catch ( Exception e )
        {
            log.error( "Error during execution", e );
            uiService.showDialog( "Error: " + e.getMessage() );
        }
    }

    /**
     * Helper function to generate wizard progress text
     *
     * @param stepNumber current step number (1-based)
     * @param totalSteps total number of steps
     * @return formatted progress string
     */
    private String createProgressText( int stepNumber, int totalSteps )
    {
        return "Step " + stepNumber + " of " + totalSteps;
    }

    /**
     * Creates a cancellation message for the wizard.
     *
     * @param currentStep the current step number
     * @return a cancellation message string
     */
    private String createCancellationText( int currentStep )
    {
        return "Wizard cancelled by user at step " + currentStep;
    }

    /**
     * Runs the wizard to collect user input.
     *
     * @throws Exception
     */
    private void runWizard() throws Exception
    {
        // Define wizard structure - total steps depend on user choice
        int currentStep = 1;
        int totalSteps = 5; // Welcome, Image Setup, Costes, Statistics, Display

        // Step 1: Welcome and choice between synthetic vs user images
        log.info( "Step " + currentStep + ": Welcome to Coloc Teacher..." );

        CommandModule welcomeModule;
        try
        {
            WizardStep.PROGRESS_MESSAGE = createProgressText( currentStep, totalSteps );
            welcomeModule = commandService.run( WelcomeWizard.class, true ).get();
        }
        catch ( Exception e )
        {
            log.info( createCancellationText( currentStep ) );
            return;
        }

        // Check if cancelled
        if ( welcomeModule == null || welcomeModule.isCanceled() )
        {
            log.info( createCancellationText( currentStep ) );
            return;
        }

        // Get the user's choice
        WelcomeWizard welcomeResult = ( WelcomeWizard ) welcomeModule.getCommand();
        boolean useSyntheticImages = welcomeResult.useSyntheticImages;
        settings.setUseSyntheticImages( useSyntheticImages );

        // Step 2: Image Setup (Synthetic or User Images)
        currentStep++;

        if ( useSyntheticImages )
        {
            // Synthetic Image Setup
            log.info( "Step " + currentStep + ": Configuring synthetic image parameters..." );

            CommandModule syntheticModule;
            try
            {
                WizardStep.PROGRESS_MESSAGE = createProgressText( currentStep, totalSteps );
                syntheticModule = commandService.run( SyntheticImageWizard.class, true ).get();
            }
            catch ( Exception e )
            {
                log.info( createCancellationText( currentStep ) );
                return;
            }

            // Check if cancelled
            if ( syntheticModule == null || syntheticModule.isCanceled() )
            {
                log.info( createCancellationText( currentStep ) );
                return;
            }

            // Get the parameters from the completed wizard step
            SyntheticImageWizard syntheticResult = ( SyntheticImageWizard ) syntheticModule.getCommand();
            settings.setWidth( syntheticResult.width );
            settings.setHeight( syntheticResult.height );
            settings.setNumSpots( syntheticResult.numSpots );
            settings.setSpotRadius( syntheticResult.spotRadius );
            settings.setOverlapFraction( syntheticResult.overlapFraction );
            settings.setAddNoise( syntheticResult.addNoise );
            settings.setNoiseStdDev( syntheticResult.noiseStdDev );
        }
        else
        {
            // User Image Selection
            log.info( "Step " + currentStep + ": Selecting your images for analysis..." );

            CommandModule imageSelectionModule;
            try
            {
                WizardStep.PROGRESS_MESSAGE = createProgressText( currentStep, totalSteps );
                imageSelectionModule = commandService.run( ImageSelectionWizard.class, true ).get();
            }
            catch ( Exception e )
            {
                log.info( createCancellationText( currentStep ) );
                return;
            }

            // Check if cancelled
            if ( imageSelectionModule == null || imageSelectionModule.isCanceled() )
            {
                log.info( createCancellationText( currentStep ) );
                return;
            }

            // Get the selected images
            ImageSelectionWizard imageResult = ( ImageSelectionWizard ) imageSelectionModule.getCommand();
            settings.setUserChannel1Image( imageResult.channel1Image );
            settings.setUserChannel2Image( imageResult.channel2Image );
        }

        // Step 3: Costes Test Setup
        currentStep++;
        log.info( "Step " + currentStep + ": Configuring Costes significance test..." );

        CommandModule costesModule;
        try
        {
            WizardStep.PROGRESS_MESSAGE = createProgressText( currentStep, totalSteps );
            costesModule = commandService.run( CostesWizard.class, true ).get();
        }
        catch ( Exception e )
        {
            log.info( createCancellationText( currentStep ) );
            return;
        }

        // Check if cancelled
        if ( costesModule == null || costesModule.isCanceled() )
        {
            log.info( createCancellationText( currentStep ) );
            return;
        }

        CostesWizard costesResult = ( CostesWizard ) costesModule.getCommand();
        settings.setUseCostes( costesResult.useCostes );
        settings.setNrCostesRandomisations( costesResult.nrCostesRandomisations );
        settings.setPsf( costesResult.psf );
        settings.setDisplayShuffledCostes( costesResult.displayShuffledCostes );

        // Step 4: Statistical Methods
        currentStep++;
        log.info( "Step " + currentStep + ": Selecting statistical methods..." );

        CommandModule statsModule;
        try
        {
            WizardStep.PROGRESS_MESSAGE = createProgressText( currentStep, totalSteps );
            statsModule = commandService.run( StatisticsWizard.class, true ).get();
        }
        catch ( Exception e )
        {
            log.info( createCancellationText( currentStep ) );
            return;
        }

        // Check if cancelled
        if ( statsModule == null || statsModule.isCanceled() )
        {
            log.info( createCancellationText( currentStep ) );
            return;
        }

        StatisticsWizard statsResult = ( StatisticsWizard ) statsModule.getCommand();
        settings.setUseLiICQ( statsResult.useLiICQ );
        settings.setUseSpearmanRank( statsResult.useSpearmanRank );
        settings.setUseManders( statsResult.useManders );
        settings.setUseKendallTau( statsResult.useKendallTau );

        // Step 5: Display Options
        currentStep++;
        log.info( "Step " + currentStep + ": Configuring display options..." );

        CommandModule displayModule;
        try
        {
            WizardStep.PROGRESS_MESSAGE = createProgressText( currentStep, totalSteps );
            displayModule = commandService.run( DisplayWizard.class, true ).get();
        }
        catch ( Exception e )
        {
            log.info( createCancellationText( currentStep ) );
            return;
        }

        // Check if cancelled
        if ( displayModule == null || displayModule.isCanceled() )
        {
            log.info( createCancellationText( currentStep ) );
            return;
        }

        DisplayWizard displayResult = ( DisplayWizard ) displayModule.getCommand();
        settings.setDisplayImages( displayResult.displayImages );
        settings.setUseScatterplot( displayResult.useScatterplot );

        // Now execute the analysis with collected settings
        log.info( "Wizard complete. Starting analysis with selected parameters..." );
        executeAnalysis();
    }

    /**
     * Executes the analysis with the collected settings.
     */
    private void executeAnalysis()
    {
        try
        {
            if ( settings.isUseSyntheticImages() )
            {
                log.info( "Generating synthetic colocalization test images..." );
                // Generate synthetic images using settings
                generateSyntheticImages();
            }
            else
            {
                log.info( "Using user-provided images for colocalization analysis..." );
                // Convert user images to datasets
                convertUserImages();
            }

            // Run colocalization analysis
            runColocalizationAnalysis();

            // Generate interpretation guide
            generateInterpretationGuide();

            log.info( "Coloc Teacher completed successfully!" );

        }
        catch ( Exception e )
        {
            log.error( "Error in Coloc Teacher: " + e.getMessage(), e );
            if ( uiService != null )
            {
                uiService.showDialog( "An error occurred: " + e.getMessage(), "Coloc Teacher Error" );
            }
        }
    }

    /**
     * Generates synthetic images based on the current settings.
     */
    private void generateSyntheticImages()
    {
        Random rand = new Random();

        log.info( "Generating " + settings.getNumSpots() + " synthetic spots with "
                + ( settings.getOverlapFraction() * 100 ) + "% overlap" );

        // Create datasets
        final String name1 = "Synthetic Channel 1";
        final String name2 = "Synthetic Channel 2";
        final long[] dims = { settings.getWidth(), settings.getHeight() };
        final AxisType[] axes = { Axes.X, Axes.Y };

        channel1 = datasetService.create( new FloatType(), dims, name1, axes );
        channel2 = datasetService.create( new FloatType(), dims, name2, axes );

        // Get images for processing
        @SuppressWarnings( "unchecked" )
        Img< FloatType > img1 = ( Img< FloatType > ) channel1.getImgPlus();
        @SuppressWarnings( "unchecked" )
        Img< FloatType > img2 = ( Img< FloatType > ) channel2.getImgPlus();

        // Initialize with background intensity
        addBackground( img1, settings.getBaseIntensity() );
        addBackground( img2, settings.getBaseIntensity() );

        // Generate spots
        for ( int i = 0; i < settings.getNumSpots(); i++ )
        {
            // Random center for spot 1
            int spotRadiusInt = ( int ) Math.ceil( settings.getSpotRadius() );
            int x = rand.nextInt( settings.getWidth() - 2 * spotRadiusInt ) + spotRadiusInt;
            int y = rand.nextInt( settings.getHeight() - 2 * spotRadiusInt ) + spotRadiusInt;

            // Variable intensity for more realistic distribution
            float intensity1 = settings.getBaseIntensity() + rand.nextInt( settings.getMaxIntensity() - settings.getBaseIntensity() );

            // Add Gaussian-like spot to channel 1
            addGaussianSpot( img1, x, y, intensity1, settings.getSpotRadius() );

            // For overlapFraction, add spot to channel 2 at same location, else random offset
            int x2, y2;
            if ( rand.nextDouble() < settings.getOverlapFraction() )
            {
                x2 = x;
                y2 = y;
            }
            else
            {
                x2 = rand.nextInt( settings.getWidth() - 2 * spotRadiusInt ) + spotRadiusInt;
                y2 = rand.nextInt( settings.getHeight() - 2 * spotRadiusInt ) + spotRadiusInt;
            }

            float intensity2 = settings.getBaseIntensity() + rand.nextInt( settings.getMaxIntensity() - settings.getBaseIntensity() );

            // Add Gaussian-like spot to channel 2
            addGaussianSpot( img2, x2, y2, intensity2, settings.getSpotRadius() );
        }

        // Clamp intensities
        clampIntensities( img1, settings.getMaxIntensity() + settings.getBaseIntensity() );
        clampIntensities( img2, settings.getMaxIntensity() + settings.getBaseIntensity() );

        // Add noise if requested
        if ( settings.isAddNoise() && settings.getNoiseStdDev() > 0 )
        {
            log.info( "Adding Gaussian noise with standard deviation: " + settings.getNoiseStdDev() );
            addGaussianNoise( img1, settings.getNoiseStdDev(), rand );
            addGaussianNoise( img2, settings.getNoiseStdDev(), rand );
        }

        // Display the generated images for user inspection
        if ( !testMode && uiService != null )
        {
            uiService.show( name1, channel1 );
            uiService.show( name2, channel2 );
            log.info( "Generated synthetic images displayed: " + name1 + " and " + name2 );
        }
    }

    /**
     * Converts user-provided ImagePlus objects to Dataset objects for analysis.
     */
    private void convertUserImages()
    {
        if ( settings.getUserChannel1Image() == null || settings.getUserChannel2Image() == null )
        {
            throw new IllegalArgumentException( "Both channel images must be selected for analysis" );
        }

        try
        {
            // Convert ImagePlus to Dataset using the convert service
            channel1 = convertService.convert( settings.getUserChannel1Image(), Dataset.class );
            channel2 = convertService.convert( settings.getUserChannel2Image(), Dataset.class );

            if ( channel1 == null || channel2 == null )
            {
                throw new RuntimeException( "Failed to convert ImagePlus to Dataset" );
            }

            // Validate that the images have compatible dimensions
            if ( channel1.dimension( 0 ) != channel2.dimension( 0 )
                    || channel1.dimension( 1 ) != channel2.dimension( 1 ) )
            {
                throw new IllegalArgumentException( "Images must have the same dimensions. "
                        + "Channel 1: " + channel1.dimension( 0 ) + "×" + channel1.dimension( 1 )
                        + ", Channel 2: " + channel2.dimension( 0 ) + "×" + channel2.dimension( 1 ) );
            }

            log.info( "Successfully converted user images to datasets for analysis" );
            log.info( "Image dimensions: " + channel1.dimension( 0 ) + "×" + channel1.dimension( 1 ) );

        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Error converting user images: " + e.getMessage(), e );
        }
    }

    /**
     * Adds a background intensity to the entire image.
     *
     * @param img the image to modify
     * @param value the background intensity value
     */
    private void addBackground( Img< FloatType > img, float value )
    {
        Cursor< FloatType > cursor = img.cursor();
        while ( cursor.hasNext() )
        {
            cursor.next().set( value );
        }
    }

    /**
     * Adds a Gaussian-like spot to the image.
     *
     * @param img the image to modify
     * @param centerX the x-coordinate of the spot center
     * @param centerY the y-coordinate of the spot center
     * @param intensity the intensity of the spot
     * @param radius the radius of the spot
     */
    private void addGaussianSpot( Img< FloatType > img, int centerX, int centerY,
            float intensity, double radius )
    {
        RandomAccess< FloatType > ra = img.randomAccess();

        int intRadius = ( int ) Math.ceil( radius );
        for ( int dx = -intRadius; dx <= intRadius; dx++ )
        {
            for ( int dy = -intRadius; dy <= intRadius; dy++ )
            {
                double dist = Math.sqrt( dx * dx + dy * dy );
                if ( dist <= radius )
                {
                    double falloff = Math.exp( -dist * dist / ( radius * radius / 4.0 ) );
                    int px = centerX + dx;
                    int py = centerY + dy;

                    if ( px >= 0 && px < settings.getWidth() && py >= 0 && py < settings.getHeight() )
                    {
                        ra.setPosition( px, 0 );
                        ra.setPosition( py, 1 );
                        float currentVal = ra.get().get();
                        ra.get().set( currentVal + ( float ) ( intensity * falloff ) );
                    }
                }
            }
        }
    }

    /**
     * Clamps the pixel intensities in the image to a maximum value.
     *
     * @param img the image to modify
     * @param maxValue the maximum intensity value
     */
    private void clampIntensities( Img< FloatType > img, float maxValue )
    {
        Cursor< FloatType > cursor = img.cursor();
        while ( cursor.hasNext() )
        {
            FloatType pixel = cursor.next();
            if ( pixel.get() > maxValue )
            {
                pixel.set( maxValue );
            }
        }
    }

    /**
     * Adds Gaussian noise to the image.
     *
     * @param img the image to modify
     * @param stdDev the standard deviation of the Gaussian noise
     * @param rand a Random object for generating noise
     */
    private void addGaussianNoise( Img< FloatType > img, double stdDev, Random rand )
    {
        Cursor< FloatType > cursor = img.cursor();
        while ( cursor.hasNext() )
        {
            FloatType pixel = cursor.next();
            double noise = rand.nextGaussian() * stdDev;
            float newValue = pixel.get() + ( float ) noise;
            // Ensure non-negative values
            pixel.set( Math.max( 0, newValue ) );
        }
    }

    /**
     * Runs the colocalization analysis.
     */
    private void runColocalizationAnalysis()
    {
        log.info( "Running colocalization analysis..." );

        try
        {
            // Convert ImageJ2 Datasets to ImageJ1 ImagePlus objects for Coloc_2
            ImagePlus imp1 = convertService.convert( channel1, ImagePlus.class );
            ImagePlus imp2 = convertService.convert( channel2, ImagePlus.class );

            if ( imp1 == null || imp2 == null )
            {
                log.error( "Failed to convert Dataset to ImagePlus for colocalization analysis" );
                return;
            }

            // Set appropriate titles for display
            imp1.setTitle( "Synthetic Channel 1" );
            imp2.setTitle( "Synthetic Channel 2" );

            // Create and initialize Coloc_2 instance using the proper API
            Coloc_2< net.imglib2.type.numeric.real.FloatType > coloc2 = new Coloc_2<>();

            // Initialize settings using the proper API from Coloc_2
            boolean success = coloc2.initializeSettings(
                    imp1, // img1
                    imp2, // img2
                    0, // indexMask (no mask)
                    0, // indexRegr (regression method)
                    false, // autoSavePdf
                    settings.isDisplayImages(), // displayImages
                    settings.isDisplayShuffledCostes(), // displayShuffledCostes
                    settings.isUseLiICQ(), // useLiCh1
                    settings.isUseLiICQ(), // useLiCh2
                    settings.isUseLiICQ(), // useLiICQ
                    settings.isUseSpearmanRank(), // useSpearmanRank
                    settings.isUseManders(), // useManders
                    settings.isUseKendallTau(), // useKendallTau
                    settings.isUseScatterplot(), // useScatterplot
                    settings.isUseCostes(), // useCostes
                    ( int ) settings.getPsf(), // psf - cast to int
                    settings.getNrCostesRandomisations() // nrCostesRandomisations
            );

            if ( !success )
            {
                log.error( "Failed to initialize Coloc_2 settings" );
                return;
            }

            // Log the colocalization analysis parameters
            log.info( "Colocalization analysis parameters:" );
            log.info( "  Costes randomizations: " + settings.getNrCostesRandomisations() );
            log.info( "  PSF: " + settings.getPsf() );
            log.info( "  Use Manders: " + settings.isUseManders() );
            log.info( "  Use Li ICQ: " + settings.isUseLiICQ() );
            log.info( "  Use Spearman: " + settings.isUseSpearmanRank() );
            log.info( "  Use Kendall tau: " + settings.isUseKendallTau() );
            log.info( "  Use scatterplot: " + settings.isUseScatterplot() );
            log.info( "  Use Costes test: " + settings.isUseCostes() );

            try
            {
                Coloc_2< FloatType >.MaskInfo maskInfo = coloc2.masks.get( 0 );
                coloc2.colocalise( coloc2.img1, coloc2.img2, maskInfo.roi, maskInfo.mask );
                log.info( "Colocalization analysis completed successfully" );
            }
            catch ( sc.fiji.coloc.algorithms.MissingPreconditionException e )
            {
                log.error( "Colocalization analysis failed due to missing precondition: " + e.getMessage() );
            }

        }
        catch ( Exception e )
        {
            log.error( "Error during colocalization analysis", e );
            // Provide fallback message
            log.info( "Note: For full colocalization analysis, you can:" );
            log.info( "1. Use the generated images with Fiji's Coloc_2 plugin" );
            log.info( "2. Or implement pure ImageJ2 colocalization algorithms" );
        }
    }

    /**
     * Generates a guide for interpreting Coloc_2 results.
     */
    private void generateInterpretationGuide()
    {
        //TODO could customize this based on results of wizard
        StringBuilder sb = new StringBuilder();
        sb.append( "============================================================\n" );
        sb.append( "HOW TO INTERPRET COLOC2 RESULTS\n" );
        sb.append( "============================================================\n" );
        sb.append( "DROPDOWN MENU AT TOP:\n" );
        sb.append( "• 'Li - Ch1' & 'Li - Ch2': Shows Li's ICQ intensity distribution for each channel\n" );
        sb.append( "• '2D intensity histogram': Scatterplot of Ch1 vs Ch2 pixel intensities\n" );
        sb.append( "\n" );
        sb.append( "KEY STATISTICS TO INTERPRET:\n" );
        sb.append( "• Pearson's R (no threshold): Overall correlation (-1 to +1)\n" );
        sb.append( "  - Near +1: Strong positive correlation (colocalization)\n" );
        sb.append( "  - Near 0: No correlation\n" );
        sb.append( "  - Near -1: Strong negative correlation (segregation)\n" );
        sb.append( "\n" );
        sb.append( "• Li's ICQ value: Degree of colocalization (-0.5 to +0.5)\n" );
        sb.append( "  - Positive: Colocalization above random\n" );
        sb.append( "  - Near 0: Random distribution\n" );
        sb.append( "  - Negative: Segregation below random\n" );
        sb.append( "\n" );
        sb.append( "• Kendall's tau: Non-parametric correlation coefficient (-1 to +1)\n" );
        sb.append( "  - Similar to Pearson's R but less sensitive to outliers\n" );
        sb.append( "  - More robust for non-linear relationships\n" );
        sb.append( "  - Near +1: Strong positive correlation\n" );
        sb.append( "  - Near 0: No correlation\n" );
        sb.append( "  - Near -1: Strong negative correlation\n" );
        sb.append( "\n" );
        sb.append( "• Manders' coefficients (tM1, tM2): Overlap above threshold (0 to 1)\n" );
        sb.append( "  - 1.0: Perfect overlap\n" );
        sb.append( "  - 0.5: Half the pixels overlap\n" );
        sb.append( "  - 0.0: No overlap\n" );
        sb.append( "\n" );
        sb.append( "• Costes P-Value: Statistical significance\n" );
        sb.append( "  - < 0.05: Correlation is statistically significant\n" );
        sb.append( "  - > 0.05: Correlation could be due to chance\n" );
        sb.append( "\n" );
        sb.append( "• % saturated pixels: Should be low (< 1%) for reliable analysis\n" );
        sb.append( "• b to y-mean ratio: Should be close to 0 for good data quality\n" );
        sb.append( "============================================================\n" );
        sb.append( "\n" );
        sb.append( "SYNTHETIC DATA PARAMETERS USED:\n" );
        sb.append( "• Number of spots: " + settings.getNumSpots() + "\n" );
        sb.append( "• Spot radius: " + settings.getSpotRadius() + " pixels\n" );
        sb.append( "• Overlap fraction: " + ( settings.getOverlapFraction() * 100 ) + "%\n" );
        sb.append( "• Expected colocalization: "
                + ( settings.getOverlapFraction() > 0.7 ? "High"
                        : settings.getOverlapFraction() > 0.3 ? "Moderate" : "Low" )
                + "\n" );
        sb.append( "============================================================\n" );

        interpretationGuide = sb.toString();
    }

    // Getter methods for testing
    /**
     * Get the generated channel 1 dataset.
     *
     * @return the synthetic channel 1 dataset, or null if not yet generated
     */
    public Dataset getChannel1()
    {
        return channel1;
    }

    /**
     * Get the generated channel 2 dataset.
     *
     * @return the synthetic channel 2 dataset, or null if not yet generated
     */
    public Dataset getChannel2()
    {
        return channel2;
    }

    /**
     * Get the interpretation guide text.
     *
     * @return the interpretation guide, or null if not yet generated
     */
    public String getInterpretationGuide()
    {
        return interpretationGuide;
    }

    /**
     * Get the current wizard settings (for testing purposes).
     *
     * @return a copy of the current settings
     */
    public WizardSettings getSettings()
    {
        return settings;
    }
}
