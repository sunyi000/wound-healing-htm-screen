# Wound healing assay HTM analysis

## Description

This project is about quantification of wound healing in microscopy scratch assays.

## Collaborators

- daniel.heid@embl.de
- christian.tischer@embl.de

## Installation instructions



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
- TODO: specify the input data directory
- run the nextflow script (replace the `--inputDir` and `--fijiScript` parameters)
  - `rm -rf work; nextflow run woundHealing.nf --inputDir "/home/tischer/daniel-heid-wound-healing/data/input" --fijiScript "/home/tischer/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy"; cat work/*/*/.command.log; rm -rf .next*; rm -rf work`
  - `rm -rf work; nextflow run woundHealing.nf --inputDir "/home/tischer/daniel-heid-wound-healing/data/input" --fijiScript "/home/tischer/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy" --fiji "XYZ/Fiji.app/ImageJ-linux64 --headless --run"; cat work/*/*/.command.log; rm -rf .next*; rm -rf work`


## Tests

To check whether the code is working, ...

(...not sure yet how to implement this for all cases, but we should think about it...)

## Materials and methods

Describe the methods that are used. This text could be used for publication.

