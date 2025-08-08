# Coloc Teacher

Educational Fiji plugin for teaching colocalization analysis with synthetic data generation and guided interpretation.

## Overview

Coloc Teacher is an educational plugin that extends the functionality of Fiji's Coloc_2 plugin by:

1. **Generating synthetic test images** with controllable colocalization parameters
2. **Running automated colocalization analysis** using the established Coloc_2 algorithms
3. **Providing comprehensive interpretation guidance** to help users understand the statistical results

## Features

### Synthetic Image Generation
- Controllable number of spots and spot radius
- Adjustable overlap fraction (0-1) to simulate different colocalization scenarios
- Realistic intensity distributions with background and variable spot intensities
- Gaussian-like spot profiles for more realistic synthetic data

### Colocalization Analysis
- Full integration with Coloc_2 algorithms
- All standard colocalization metrics:
  - Pearson's correlation coefficient
  - Li's ICQ (Intensity Correlation Quotient)
  - Manders' coefficients
  - Spearman rank correlation
  - Kendall's tau
  - Costes' significance test

### Educational Components
- Interactive parameter dialog for learning about analysis settings
- Comprehensive interpretation guide explaining what each statistic means
- Practical guidance on result interpretation and data quality assessment

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

1. Open Fiji and select `Plugins > Colocalization > Coloc Teacher`
2. Configure synthetic image parameters:
   - Number of spots: Controls density of features
   - Overlap fraction: Sets the degree of colocalization (0=no overlap, 1=perfect overlap)
   - Intensity parameters: Control signal strength and background levels
3. Configure analysis parameters as desired
4. Click OK to generate images and run analysis
5. Review the generated images and colocalization results
6. Study the interpretation guide to understand the statistical measures

## Educational Value

This plugin is designed for:
- Teaching image analysis concepts in courses
- Training researchers new to colocalization analysis
- Quality control and method validation
- Understanding the relationship between image parameters and statistical outcomes

## License

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.
