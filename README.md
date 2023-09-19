# Wound healing image analysis

## Description

This project is about the quantification of wound healing in automated high-throughput microscopy scratch assays.

## Installation instructions

### Install Fiji

- [Install Fiji](https://fiji.sc/)
- Start Fiji and [add the following update sites]():
  - IJPB-Plugins (MorpholibJ)

### Download analysis code and example data

To download the example data and the analysis scripts please download this repository to your computer, either using `git clone` or by downloading this [zip file](https://git.embl.de/grp-cba/wound-healing-htm-screen/-/archive/main/wound-healing-htm-screen-main.zip).

## Input image data

Two small example data sets that can be used for running the analysis pipeline are contained within this repository in the `/data/input` folder.

All data sets corresponding to the publication have been published at ... TODO.

## Analysis workflow

### Analyse one dataset in the Fiji GUI

- Open Fiji
- Drag and drop the analysis script from this repo onto the Fiji Menu bar: `code/groovy/src/main/measureWoundClosing.groovy`
- The Fiji script editor should have opened
- In the script editor press the `Run` button
-   

### Analyse one dataset headles on the command line

`${FIJI_EXE_PATH} --ij2 --headless --run ${GROOVY_SCRIPT_PATH} 'inputDir="${INPUT_DIR}",datasetId="${DATASET_ID}",headless="true"'`

- `${FIJI_EXE_PATH}`: Fiji exectuable on your computer, e.g., `"/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx"`
- `${GROOVY_SCRIPT_PATH}`: Wound healing groovy script on your computer, e.g., "/Users/tischer/Documents/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy"
- `${INPUT_DIR}`: Folder containing the image data, e.g., `"/Users/tischer/Documents/daniel-heid-wound-healing/data/input"` 
- `${DATASET_ID}`: Name of the data set to be analysed, e.g., "A3ROI2_Slow"

### Perform batch analysis using nextflow

- go to https://jupyterhub.embl.de
- choose "Image Analysis GPU"
- open a terminal window
- fetch this repo (only need to do this the first time): 
  - `git clone https://git.embl.de/grp-cba/wound-healing-htm-screen.git` 
- `cd daniel-heid-wound-healing`
- update all scripts:
  - `git pull`
- go to the nextflow folder:
  - `cd ./code/nextflow`

#### General batch analysis

Inside the repositories nextflow folder please execute below command.
You need to replace all parameters starting with a `$` sign be actual paths!

 `rm -rf work; nextflow run woundHealing.nf --inputDir "$INPUT_DATA_DIR" --fijiScript "$FIJI_SCRIPT_DIR/measureWoundClosing.groovy" --fiji "$FIJI"; cat work/*/*/.command.log; cat .nextflow.log; rm -rf .next*; rm -rf work`
 
#### Batch analysis on Tischi's Mac

`rm -rf work; nextflow run woundHealing.nf --inputDir "/Users/tischer/Documents/daniel-heid-wound-healing/data/input" --fijiScript "/Users/tischer/Documents/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy" --fiji "/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx"; cat work/*/*/.command.log; cat .nextflow.log; rm -rf .next*; rm -rf work`

#### Batch analysis on Jupyter Desktop (tischi)

`rm -rf work; nextflow run woundHealing.nf --inputDir "/home/tischer/daniel-heid-wound-healing/data/input" --fijiScript "/home/tischer/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy" --fiji fiji;cat work/*/*/.command.log; cat .nextflow.log; rm -rf .next*; rm -rf work`


## Tests

To check whether the code is working, ...

(...not sure yet how to implement this for all cases, but we should think about it...)

## Materials and methods

Describe the methods that are used. This text could be used for publication.

