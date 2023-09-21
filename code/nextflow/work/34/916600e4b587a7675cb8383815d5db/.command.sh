#!/bin/bash -ue
echo "Processing dataset:" A3ROI2_Slow
echo "Contained files:" A3ROI2_Slow_0010.tif A3ROI2_Slow_0004.tif A3ROI2_Slow_0005.tif A3ROI2_Slow_0011.tif A3ROI2_Slow_0007.tif A3ROI2_Slow_0006.tif A3ROI2_Slow_0002.tif A3ROI2_Slow_0003.tif A3ROI2_Slow_0001.tif A3ROI2_Slow_0000.tif A3ROI2_Slow_0008.tif A3ROI2_Slow_0009.tif
"/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx" --ij2 --headless --run "../groovy/src/main/measureWoundClosing.groovy" 'inputDir="../../data/input",datasetId="A3ROI2_Slow",threshold="25",outDirName="output",headless="true",saveResults="true"'
