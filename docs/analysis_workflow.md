## Wound healing analysis workflow

The analysis workflow automatically quantifies the closing of a scratch area (the wound) over time in brightfield microscopy images of a cell monolayer. To this end the regions of the image containing cells are segmented in all time points, using the fact that the local variance in the gray values of the image is higher in regions with cells. The central cell free are of the first timepoint will be taken as a reference ("the lesion"). The script measures the fraction of the lesion that is free of cells for each timepoint. This percentage will decrease over time. The resulting data, i.e., cell free percentage as a function of time, is output as a CSV table, which is then further analysed... 

TODO add example images!

The analysis is implemented as a groovy script that can be executed within Fiji. For batch analysis of multiple data sets we implemented a Nextflow pipeline. 

## Installation

To run the analysis workflow you need to download this repository to your computer, either using `git clone` or by downloading this [zip file](https://git.embl.de/grp-cba/wound-healing-htm-screen/-/archive/main/wound-healing-htm-screen-main.zip).

Some analysis steps require additional dependencies, they will be mentioned in the respective "Installation" drop downs.

## Example data

- Please see `/data/input` for example input
- Please see `/data/output` for example output

## Run the analysis 

### Analyse one dataset in the Fiji GUI

<details>
<summary>Installation instructions</summary>

- [Install Fiji](https://fiji.sc/)
- Start Fiji and [add the following update sites](https://imagej.net/update-sites/):
  - IJPB-Plugins (MorpholibJ)
</details>

Run the analysis:
- Open Fiji
- Drag and drop the `code/groovy/src/main/measureWoundClosing.groovy` script onto the Fiji Menu bar
- The Fiji script editor will open
- In the script editor press the `Run` button
- Set the script parameters, similar as in the below screenshot

![](documentation/fiji-script-screenshot.jpg]

Input parameters:
- `Input directory`: Folder containing sequences of 2D TIFF images, with naming scheme `${DATASET_ID}_${TIMEPOINT}.tif`
- `Dataset ID`: Only images containing this dataset ID will be analysed
- `CoV threshold (-1: auto)`: Threshold for segmenting cells after covariance filtering; use -1 for automated thresholding; for the analysis in the publication we used a value of `25`
- `Run headless`: Check this to not show any images during the analysis
- `Save results`: Check this to save the results into the below directory 
- `Output directory name (will be created next to input directory)`: Name of the output directory (this is not a full path, but just a single name, such as "output")

Output:
- TIFF file containing the raw movie with the lesion ImageJ ROI overlayed
- CSV file containing the percent of cell free area in the lesion ROI for each time-point
 

### Analyse one dataset headles on the command line

For automated batch analysis, e.g. in Nextflow, the analysis script needs to be executed from the command line. Below code and explanations show how to do this.

Command line call:

```
${FIJI} --ij2 --headless --run ${GROOVY_SCRIPT_PATH} 'inputDir="${INPUT_DIR}",datasetId="${DATASET_ID}",headless="true"'
```

Parameters:

- `${FIJI}`: Fiji exectuable on your computer, e.g., `"/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx"`
- `${GROOVY_SCRIPT_PATH}`: Wound healing groovy script on your computer, e.g., "/Users/tischer/Documents/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy"
- `${INPUT_DIR}`: Folder containing the image data, e.g., `"/Users/tischer/Documents/daniel-heid-wound-healing/data/input"`
- `${DATASET_ID}`: Name of the data set to be analysed, e.g., "A3ROI2_Slow"

### Batch analysis using Nextflow

<details>
<summary>Nextflow on EMBL Jupyterhub (internal only)</summary>
- Go to https://jupyterhub.embl.de
- Choose "Image Analysis GPU"
- Open a terminal window
- Nextflow will be available 
</details>

<details>
<summary>Netxflow installation instructions</summary>
- For installing Nextflow, we recommend
  - Only once:
    - Install [mambaforge](https://github.com/conda-forge/miniforge#mambaforge)
    - `mamba create -n nextflow -c bioconda nextflow`
  - Every time:
    - `mamba active nextflow`
</details>

Run the analysis:
- Open a terminal window
- Navigate to the `code/nextflow` folder of this repository
- Execute `nextflow run woundHealing.nf --inputDir "../../data/input" --fijiScript "../groovy/src/main/measureWoundClosing.groovy" --outDirName "output" --fiji "${FIJI}"`
  - `${FIJI}`: Replace with a Fiji exectuable (containg the MorpholibJ update site) on your computer, e.g., `"/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx"`  
  - If you want to run the analysis on other data, please replace the text behind the `--inputDir` parameter.
- The output will be saved in a folder called `output` next to the input directory.

Notes:

- If you want to always clean up temporary Nextflow files you can use this extended command: `rm -rf work; nextflow run woundHealing.nf --inputDir "${INPUT_DIR}" --fijiScript "${FIJI_SCRIPT}" --fiji "${FIJI}"; cat work/*/*/.command.log; cat .nextflow.log; rm -rf .next*; rm -rf work`
