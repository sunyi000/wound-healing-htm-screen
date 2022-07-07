#!/bin/bash -ue
echo "Processing dataset: " M1_D5_
echo "Contained files: " M1_D5_0040.tif M1_D5_0044.tif M1_D5_0020.tif M1_D5_0008.tif M1_D5_0036.tif M1_D5_0032.tif M1_D5_0024.tif M1_D5_0028.tif M1_D5_0000.tif M1_D5_0016.tif M1_D5_0012.tif M1_D5_0004.tif
/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx --headless --run /Users/tischer/Documents/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy "inputDir='/Users/tischer/Documents/daniel-heid-wound-healing/data/input',regExp='M1_D5_.*',headless='true'"
