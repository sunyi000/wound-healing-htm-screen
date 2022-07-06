/**
 * This script runs in: Fiji
 * It needs the Fiji update sites:
 * - IJPB-Plugins
 *
 *
 */


import fiji.threshold.Auto_Threshold
import ij.IJ
import ij.ImageJ
import ij.io.FileSaver
import ij.plugin.FolderOpener
import ij.plugin.frame.RoiManager
import inra.ijpb.binary.BinaryImages
import inra.ijpb.label.LabelImages
import inra.ijpb.segment.Threshold

//#@ String (label = "Regular expression") regExp
//#@ Boolean (label = "Show intermediate results") showIntermediateResults

// INPUT PARAMETERS
//
def regExp = "M1_D5_.*";
def filterRadiusPixels = 20
def inputDir = new File("/Users/tischer/Desktop/daniel-heid/single-images/")
def showResults = false;

// OTHER PARAMETERS
//

// CODE
//


if (showResults) new ImageJ().setVisible(true)

// open
def imp = FolderOpener.open(inputDir.toString(), " filter=("+regExp+")")

// binarise cells
IJ.run("Options...", "iterations=1 count=1 black");
IJ.run(imp, "Variance...", "radius=" + filterRadiusPixels + " stack");
IJ.run(imp, "Invert", "stack");
def otsu = Auto_Threshold.Otsu(imp.getProcessor().getHistogram())
imp = Threshold.threshold( imp, otsu, 65535 )

// create scratch ROI
imp.setPosition(1)
firstFrameImp = imp.crop("whole-slice");
firstFrameImp.setTitle("First frame")
firstFrameImp = BinaryImages.keepLargestRegion(firstFrameImp)
IJ.run(firstFrameImp, "Fill Holes", "");
IJ.run(firstFrameImp, "Minimum...", "radius=" + filterRadiusPixels );
IJ.run(firstFrameImp, "Maximum...", "radius=" + filterRadiusPixels);
IJ.run(firstFrameImp, "Create Selection", "");
def scratchROI = firstFrameImp.getRoi()

// measure occupancy of scratch ROI
IJ.run("Set Measurements...", "area_fraction redirect=None decimal=0");
def rm = new RoiManager(false)
rm.addRoi(scratchROI)
rm.select(0)
def rt = rm.multiMeasure(imp)

// show
if (showResults) {
    rt.show("Results")
    imp.show()
    imp.setRoi(scratchROI, true)
    firstFrameImp.show()
}

// save

// create output directory
def outputDir = new File( inputDir.getParent(), "analysis" );
outputDir.mkdir()
rt.save( new File( outputDir, regExp + ".csv" ).toString() );
imp.setRoi(scratchROI, false)
IJ.save(imp, new File( outputDir, regExp + ".tif" ).toString() );

println("Analysis of "+regExp+" is done!")
