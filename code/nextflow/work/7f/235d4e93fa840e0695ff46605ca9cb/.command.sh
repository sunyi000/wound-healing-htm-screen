#!/bin/bash -ue
echo "Processing dataset: " M1_C2_
echo "Contained files: " M1_C2_0009.tif M1_C2_0008.tif M1_C2_0000.tif M1_C2_0001.tif M1_C2_0003.tif M1_C2_0002.tif M1_C2_0006.tif M1_C2_0007.tif M1_C2_0011.tif M1_C2_0005.tif M1_C2_0004.tif M1_C2_0010.tif
/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx --headless --run /Users/tischer/Documents/daniel-heid-wound-healing/code/groovy/src/main/measureWoundClosing.groovy "inputDir='/Users/tischer/Documents/daniel-heid-wound-healing/data/input',regExp='M1_C2_.*',headless='true'"
