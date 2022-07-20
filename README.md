# Wound healing assay HTM analysis

## Description

This project is about quantification of wound healing in microscopy scratch assays.

## Collaborators

- daniel.heid@embl.de
- christian.tischer@embl.de

## Installation instructions


### Running headless on a single data set

#### On Tischi's Mac

`"/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx" --ij2 --headless --run "/Users/tischer/Documents/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy" 'inputDir="/Users/tischer/Documents/daniel-heid-wound-healing/data/input",datasetId="A3ROI2_Slow",headless="true",outDirName="analysis"'`

### Batch analysis with nextflow

- go to https://jupyterhub.embl.de
- choose "Image Analysis GPU"
- open a terminal window
- fetch this repo (only need to do this the first time): 
  - `git clone https://git.embl.de/grp-cba/daniel-heid-wound-healing.git` 
- `cd daniel-heid-wound-healing`
- update all scripts:
  - `git pull`
- go to the nextflow folder:
  - `cd ./code/nextflow`

#### General

Inside the repositories nextflow folder please execute below command.
You need to replace all parameters starting with a `$` sign be actual paths!

 `rm -rf work; nextflow run woundHealing.nf --inputDir "$INPUT_DATA_DIR" --fijiScript "$FIJI_SCRIPT_DIR/measureWoundClosing.groovy" --fiji "$FIJI"; cat work/*/*/.command.log; cat .nextflow.log; rm -rf .next*; rm -rf work`
 
#### On Tischi's Mac

`rm -rf work; nextflow run woundHealing.nf --inputDir "/Users/tischer/Documents/daniel-heid-wound-healing/data/input" --fijiScript "/Users/tischer/Documents/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy" --fiji "/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx"; cat work/*/*/.command.log; cat .nextflow.log; rm -rf .next*; rm -rf work`
    - please change PLEASE_ADAPT to the respective directory on your computer. 

## Tests

To check whether the code is working, ...

(...not sure yet how to implement this for all cases, but we should think about it...)

## Materials and methods

Describe the methods that are used. This text could be used for publication.

