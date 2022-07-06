/**
 * This script runs in Fiji
 * It needs the Fiji update sites:
 * - IJPB-Plugins
 *
 *
 */


import fiji.threshold.Auto_Threshold
import ij.IJ
import ij.ImageJ
import ij.ImagePlus
import ij.plugin.FolderOpener
import ij.plugin.frame.RoiManager
import inra.ijpb.binary.BinaryImages
import inra.ijpb.morphology.Morphology
import inra.ijpb.morphology.Reconstruction
import inra.ijpb.morphology.strel.SquareStrel
import inra.ijpb.segment.Threshold

// INPUT UI
//
//#@ String (label = "Regular expression") regExp
//#@ File (label = "Input directory", style="directory") inputDir
//#@ Boolean (label = "Show results", default="false") showResults

// INPUT PARAMETERS
// for developing in an IDE
def regExp = "M1_D5_.*";
def cellDiameter = 20
def scratchDiameter = 500
def binningFactor = 2
def inputDir = new File("/Users/tischer/Desktop/daniel-heid/single-images/")
def showResults = true;

// DERIVED OR FIXED PARAMETERS
//
def cellFilterRadius = cellDiameter/2.0F/binningFactor
def scratchFilterRadius = scratchDiameter/10.0F/binningFactor

// CODE
//
if (showResults) new ImageJ().setVisible(true)

// open
def imp = FolderOpener.open(inputDir.toString(), " filter=("+regExp+")")
// remove scaling to work in pixel units
IJ.run(imp,"Properties...", "pixel_width=1 pixel_height=1 voxel_depth=1");
// bin to save compute time
IJ.run(imp, "Bin...", "x=" + binningFactor + " y=" + binningFactor + " z=1 bin=Average");
// enhance cells
IJ.run(imp, "Variance...", "radius=" + cellFilterRadius + " stack");
// invert (we are interested in the cell free region)
IJ.run(imp, "Invert", "stack")
// create binary image (cell-free regions are foreground)
IJ.run("Options...", "iterations=1 count=1 black");
// determine threshold in first frame, because there we are
// closest to a 50/50 occupancy of the image with signal,
// which is best for most auto-thresholding algorithms
imp.setPosition(1)
def otsu = Auto_Threshold.Otsu(imp.getProcessor().getHistogram())
// create binary image (whole movie)
def binaryImp = Threshold.threshold(imp, otsu, Math.pow(2, imp.getBitDepth()))

// create scratch ROI
//
binaryImp.setPosition(1) // scratch is most visible in first frame
def scratchIp = binaryImp.crop("whole-slice").getProcessor();
// identify largest cell free region as scratch region
scratchIp = BinaryImages.keepLargestRegion(scratchIp)
// remove cells inside scratch region
scratchIp = Reconstruction.fillHoles(scratchIp)
// smoothen edges of scratch region
scratchIp = Morphology.opening(scratchIp, SquareStrel.fromRadius((int) scratchFilterRadius))
// convert binary image to ROI, which is handy for measurements
def scratchImp = new ImagePlus("Binary Scratch", scratchIp)
IJ.run(scratchImp, "Create Selection", "");
def scratchROI = scratchImp.getRoi()

// measure occupancy of scratch ROI
// `area_fraction` returns the fraction of foreground pixels
// (cell free area) within the measurement ROI
IJ.run("Set Measurements...", "area_fraction redirect=None decimal=0");
def rm = new RoiManager(false)
rm.addRoi(scratchROI)
rm.select(0)
// multiMeasure conveniently measures in each frame
def rt = rm.multiMeasure(binaryImp)

// show
if (showResults) {
    rt.show("Results")
    binaryImp.show()
    binaryImp.setRoi(scratchROI, true)
}

// save
// TODO: what to save exactly?

// create output directory
def outputDir = new File( inputDir.getParent(), "analysis" );
outputDir.mkdir()
def dataId = regExp.replace(".*", "")
rt.save( new File( outputDir, dataId + ".csv" ).toString() );
binaryImp.setRoi(scratchROI, false)
IJ.save(binaryImp, new File( outputDir, dataId + ".tif" ).toString() );

println("Analysis of "+regExp+" is done!")
