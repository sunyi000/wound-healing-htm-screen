#!/bin/bash -ue
echo "Processing dataset:" C4ROI1_Fast
echo "Contained files:" C4ROI1_Fast_0009.tif C4ROI1_Fast_0008.tif C4ROI1_Fast_0005.tif C4ROI1_Fast_0011.tif C4ROI1_Fast_0010.tif C4ROI1_Fast_0004.tif C4ROI1_Fast_0006.tif C4ROI1_Fast_0007.tif C4ROI1_Fast_0003.tif C4ROI1_Fast_0002.tif C4ROI1_Fast_0000.tif C4ROI1_Fast_0001.tif
"/Users/tischer/Desktop/Fiji/Fiji.app/Contents/MacOS/ImageJ-macosx" --ij2 --headless --run "../groovy/src/main/measureWoundClosing.groovy" 'inputDir="../../data/input",datasetId="C4ROI1_Fast",threshold="25",outDirName="output",headless="true",saveResults="true"'
