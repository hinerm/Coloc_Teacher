# Coloc Teacher

Educational Fiji plugin for teaching colocalization analysis with both synthetic data generation and real image analysis, featuring a comprehensive guided wizard interface.

## LLM Use

This repository was built in VS Code using GitHub Copilot with Claude Sonnet 4 in agentic mode.

The development progressed through several phases:
1. **Exploration**: Investigation of Fiji's built-in colocalization tools and options
2. **Script Development**: Creation of educational Groovy scripts for colocalization analysis with synthetic image generation
3. **Plugin Architecture**: Translation to a full ImageJ2-style Java plugin with wizard-based interface
4. **Comprehensive Implementation**: Complete educational platform with dual learning modes (synthetic vs. real images)
5. **Documentation**: Thorough documentation and conversation archival

A full record of the conversation can be found in [ColocTeacherChat.md](doc/chats/ColocTeacherChat.md)
A CSS-powered pretty record of the conversation can be found in [ColocTeacherChat-pretty.md](doc/chats/ColocTeacherChat-pretty.md)

### Adding future chats

1. Right-click the chat window and click `Copy All`
2. Paste into a `.txt` file in `doc/chats`
3. From the `doc/chats` directory, choose your conversion script:
   - **GitHub-friendly**: `python convert-chat-md.py yourFile.txt` (renders well on GitHub)
   - **Pretty styling**: `python convert-chat-md-pretty.py yourFile.txt` (CSS chat bubbles for browsers)
4. Remove the `.txt` file and update the `README` as appropriate!

## Overview

Coloc Teacher is an educational plugin that extends the functionality of Fiji's Coloc_2 plugin by:

1. **Providing two learning modes**: synthetic images for controlled learning OR user's own images for real-world analysis
2. **Offering a step-by-step wizard interface** that guides users through all colocalization parameters
3. **Running automated colocalization analysis** using the established Coloc_2 algorithms
4. **Providing comprehensive interpretation guidance** to help users understand the statistical results

## Features

### 🧙‍♂️ Interactive Wizard Interface
- **Welcome Step**: Introduction to colocalization concepts and choice between synthetic vs. real images
- **Image Setup**: Either synthetic image parameter configuration OR user image selection
- **Costes Configuration**: Significance testing parameters with educational explanations
- **Statistics Selection**: Choose which colocalization metrics to calculate
- **Display Options**: Configure output preferences and visualization settings

### 🔬 Dual Learning Modes

#### Synthetic Image Mode
- **Controllable parameters**: Number of spots, spot radius, and overlap fraction (0-1)
- **Realistic features**: Gaussian-like spot profiles with background noise
- **Known ground truth**: Perfect for understanding how parameters affect results
- **Educational value**: Learn colocalization concepts with controlled examples

#### Real Image Mode
- **Use your own data**: Analyze actual two-channel fluorescence images
- **Image validation**: Automatic checking for compatible dimensions
- **Real-world application**: Apply learned concepts to research data
- **Image requirements**: Guidance on proper image preparation and registration

### 📊 Comprehensive Colocalization Analysis
- Full integration with Coloc_2 algorithms
- All standard colocalization metrics:
  - Pearson's correlation coefficient
  - Li's ICQ (Intensity Correlation Quotient)
  - Manders' coefficients (M1 and M2)
  - Spearman rank correlation
  - Kendall's tau
  - Costes' significance test with randomization

### 📚 Educational Components
- **Step-by-step guidance**: Each wizard step includes detailed explanations
- **Parameter education**: Learn what each setting does and when to use it
- **Interpretation guide**: Comprehensive explanation of all statistical results
- **Best practices**: Tips for avoiding common colocalization analysis pitfalls

## Installation

1. Download the compiled JAR file
2. Copy it to your Fiji plugins folder (`Fiji.app/plugins/`)
3. Restart Fiji
4. The plugin will appear under `Plugins > Colocalization > Coloc Teacher`

## Building from Source

This project uses Maven:

```bash
mvn clean package
```

## Usage

### Getting Started
1. Open Fiji and select `Plugins > Colocalization > Coloc Teacher`
2. **Welcome Step**: Read the introduction and choose your learning mode:
   - ✅ **Synthetic images**: Recommended for first-time users and learning
   - ❌ **Your own images**: For applying concepts to real data (requires 2+ images to be open)

### For Synthetic Image Mode:
3. **Image Parameters**: Configure your test images:
   - **Number of spots**: Controls feature density (20-100 recommended)
   - **Overlap fraction**: Sets colocalization level (0=none, 0.5=moderate, 1=perfect)
   - **Spot radius**: Size of features (3-10 pixels typical)
   - **Noise settings**: Add realistic imaging noise
4. Continue through the remaining wizard steps
5. Study the generated images and results to understand the relationship between parameters and statistics

### For Real Image Mode:
3. **Before starting**: Ensure you have at least 2 images open in ImageJ/Fiji
4. **Image Selection**: Choose your Channel 1 (reference) and Channel 2 (test) images
   - Images must have the same dimensions
   - Should represent the same field of view
   - Ideally pre-registered and background-subtracted
5. Continue through the remaining wizard steps
6. Apply the learned concepts to interpret your real data

### Analysis Configuration (Both Modes):
- **Costes Test**: Configure significance testing (recommended: 100+ randomizations)
- **Statistics**: Select which metrics to calculate (Pearson's and Manders' are most common)
- **Display**: Choose output format and visualization options

### Understanding Results:
- Review the automatically generated interpretation guide
- Pay attention to the educational explanations for each statistic
- Compare results between different parameter settings (synthetic mode)
- Use the knowledge gained to better understand your research data

## Educational Value

This plugin is designed for:
- **Teaching image analysis concepts** in microscopy courses
- **Training researchers new to colocalization analysis**
- **Quality control and method validation** with known ground truth data
- **Understanding the relationship** between image parameters and statistical outcomes
- **Bridging the gap** between theoretical knowledge and practical application

## Advanced Features

### Test Mode (for developers)
The plugin includes a test mode that bypasses the wizard interface for automated testing:
```java
Coloc_Teacher teacher = new Coloc_Teacher(true); // Test mode
teacher.run();
```

## License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

---

## 🔧 Developer Documentation

### Architecture Overview

The Coloc Teacher plugin uses a modular wizard-based architecture built on the SciJava framework:

```
src/main/java/sc/fiji/coloc/
├── Coloc_Teacher.java          # Main plugin entry point
├── WizardStep.java             # Abstract base class for wizard steps
├── WelcomeWizard.java          # Step 1: Introduction and mode selection
├── SyntheticImageWizard.java   # Step 2a: Synthetic image parameters
├── ImageSelectionWizard.java   # Step 2b: Real image selection
├── CostesWizard.java           # Step 3: Costes significance test
├── StatisticsWizard.java       # Step 4: Statistical methods selection
├── DisplayWizard.java          # Step 5: Display options
└── WizardSettings.java         # Parameter storage and validation

src/test/java/sc/fiji/coloc/
├── Coloc_TeacherTest.java      # Main functionality tests
└── WizardSettingsTest.java     # Parameter validation tests
```

#### Key Components

- **Main Plugin** (`Coloc_Teacher.java`): Orchestrates the wizard flow and analysis execution
- **Wizard Steps**: Individual UI steps that collect user parameters with educational content
- **Settings Management** (`WizardSettings.java`): Centralized parameter storage with validation
- **Base Classes** (`WizardStep.java`): Common functionality and progress display management

### Adding New Wizard Steps

To add a new wizard step to the plugin:

#### 1. Create the Wizard Step Class

```java
@Plugin(type = Command.class, name = "Coloc Teacher: Your Step Name")
public class YourNewWizard extends WizardStep {

    @Parameter(label = "Educational Content", style = TextWidget.AREA_STYLE, 
               persist = false, required = false, visibility = ItemVisibility.MESSAGE)
    private String educationalContent = "<html>Your educational content here...</html>";

    @Parameter(label = "Your Parameter")
    public YourParameterType yourParameter = defaultValue;

    @Override
    public String getStepTitle() {
        return "Your Step Title";
    }
}
```

#### 2. Add Parameters to WizardSettings.java

```java
// Add field
private YourParameterType yourParameter = defaultValue;

// Add getter
public YourParameterType getYourParameter() {
    return yourParameter;
}

// Add setter with validation
public void setYourParameter(YourParameterType yourParameter) {
    // Add validation if needed
    this.yourParameter = yourParameter;
}
```

#### 3. Update the Main Wizard Flow in Coloc_Teacher.java

```java
private void runWizard() throws Exception {
    // Update total steps count
    int totalSteps = 6; // Increment for new step
    
    // Add your step in the appropriate location
    currentStep++;
    log.info("Step " + currentStep + ": Your step description...");
    
    CommandModule yourModule;
    try {
        WizardStep.PROGRESS_MESSAGE = createProgressText(currentStep, totalSteps);
        yourModule = commandService.run(YourNewWizard.class, true).get();
    } catch (Exception e) {
        log.info(createCancellationText(currentStep));
        return;
    }
    
    if (yourModule == null || yourModule.isCanceled()) {
        log.info(createCancellationText(currentStep));
        return;
    }
    
    // Extract parameters
    YourNewWizard yourResult = (YourNewWizard) yourModule.getCommand();
    settings.setYourParameter(yourResult.yourParameter);
}
```

#### 4. Best Practices for Wizard Steps

- **Educational Content**: Always include explanatory text using HTML formatting
- **Parameter Validation**: Add validation in the setter methods
- **Error Handling**: Handle cancellation and errors gracefully
- **Consistent Styling**: Use the established patterns for UI layout
- **Documentation**: Include comprehensive JavaDoc comments

### Code Organization

#### WizardStep Base Class
- Contains common functionality for all wizard steps
- Handles progress display via static `PROGRESS_MESSAGE` 
- Provides cancellation support
- Abstract `getStepTitle()` method for step identification

#### Parameter Management
- All parameters stored in `WizardSettings.java`
- Validation logic included in setter methods
- Support for both synthetic and real image modes
- Comprehensive error checking and user feedback

#### Wizard Flow Control
- Sequential step execution with proper error handling
- Conditional branching (synthetic vs. real images)
- Progress tracking and user feedback
- Graceful cancellation at any step

### Testing

#### Unit Tests
```bash
mvn test
```

#### Manual Testing
1. Test both synthetic and real image modes
2. Verify cancellation works at each step
3. Test parameter validation
4. Ensure educational content displays properly
5. Verify final analysis runs correctly

#### Adding Tests for New Steps
```java
@Test
public void testYourNewWizardValidation() {
    WizardSettings settings = new WizardSettings();
    // Test parameter validation
    settings.setYourParameter(validValue);
    assertEquals(validValue, settings.getYourParameter());
    
    // Test invalid values
    assertThrows(IllegalArgumentException.class, () -> {
        settings.setYourParameter(invalidValue);
    });
}
```

### Building and Deployment

#### Development Build
```bash
mvn clean compile
```

#### Production Build
```bash
mvn clean package
```

#### Installation
Copy `target/Coloc_Teacher-*.jar` to `Fiji.app/plugins/`

### Integration Points

#### SciJava Framework
- Uses `@Plugin` annotations for automatic discovery
- Leverages `@Parameter` for UI generation
- Integrates with ImageJ's service framework

#### ImageJ Integration
- Compatible with both ImageJ1 and ImageJ2
- Supports ImagePlus and Dataset objects
- Integrates with existing colocalization tools

### Troubleshooting

#### Common Issues
- **Progress message not showing**: Check `WizardStep.PROGRESS_MESSAGE` assignment
- **Parameter not appearing**: Verify `@Parameter` annotation and visibility settings
- **Wizard cancellation**: Ensure proper null checking in wizard flow
- **Image conversion errors**: Validate image compatibility before conversion

#### Debugging
- Enable debug logging: `log.setLevel(LogLevel.DEBUG)`
- Use test mode to bypass wizard: `new Coloc_Teacher(true)`
- Check parameter validation in `WizardSettings`

## Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the repository** and create a feature branch
2. **Follow the existing code style** and architecture patterns
3. **Add comprehensive tests** for new functionality
4. **Update documentation** including this README
5. **Test thoroughly** with both synthetic and real image modes
6. **Submit a pull request** with clear description of changes

### Development Setup

1. Clone the repository
2. Import into your IDE as a Maven project
3. Ensure Java 8+ and Maven 3.6+ are installed
4. Run `mvn clean compile` to verify setup
5. Use `mvn test` to run the test suite

For questions or issues, please open a GitHub issue with detailed information about your environment and the problem encountered.
