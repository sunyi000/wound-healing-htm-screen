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
import ij.gui.Roi
import ij.measure.Measurements
import ij.measure.ResultsTable
import ij.plugin.FolderOpener
import ij.plugin.filter.Analyzer
import ij.plugin.filter.ThresholdToSelection
import ij.plugin.frame.RoiManager
import inra.ijpb.binary.BinaryImages
import inra.ijpb.morphology.Morphology
import inra.ijpb.morphology.Reconstruction
import inra.ijpb.morphology.strel.SquareStrel
import inra.ijpb.segment.Threshold

// INPUT UI
//
#@ File (label="Input directory", style="directory") inputDir
#@ String (label="Dataset id") datasetId
#@ Boolean (label="Run headless", default="false") headless

//#@ File (label = "Input directory", style="directory") inputDir
//#@ Boolean (label = "Show results", default="false") showResults

// INPUT PARAMETERS
// for developing in an IDE
//def inputDir = new File("/Users/tischer/Desktop/daniel-heid/single-images/")
//def regExp = "M1_D5_.*";
//def showResults = false;

def cellDiameter = 20
def scratchDiameter = 500
def binningFactor = 2

// DERIVED OR FIXED PARAMETERS
//
def cellFilterRadius = cellDiameter/2.0F/binningFactor
def scratchFilterRadius = scratchDiameter/10.0F/binningFactor


// CODE
//
if (!headless) new ImageJ().setVisible(true)

// open
println("Opening " + datasetId + "...")
def imp = FolderOpener.open(inputDir.toString(), " filter=(.*"+datasetId+".*)")
if ( imp == null  || imp.getNSlices() == 0 ) {
    println("Could not find any files matching the pattern!")
    System.exit(1)
}
// process
println("Creating binary image...")
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
println("Creating scratch ROI...")
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
println("Performing measurements...")
IJ.run("Set Measurements...", "area_fraction redirect=None decimal=0");
// RoiManager does not work headless: https://github.com/imagej/imagej-legacy/issues/153
def rt = multiMeasure(binaryImp, scratchROI)

// show
if (!headless) {
    rt.show("Results")
    binaryImp.show()
    binaryImp.setRoi(scratchROI, true)
}

// save
// TODO: what to save exactly?

// create output directory
def outputDir = new File( inputDir.getParent(), "analysis" );
println("Ensuring existence of output directory: " + outputDir)
outputDir.mkdir()
rt.save( new File( outputDir, datasetId + ".csv" ).toString() );
binaryImp.setRoi(scratchROI, false)
IJ.save(binaryImp, new File( outputDir, datasetId + ".tif" ).toString() );

println("Analysis of "+datasetId+" is done!")
if ( headless ) System.exit(0)

// copied from RoiManager
// https://forum.image.sc/t/make-multimeasure-public-in-roimanager/69273
private static ResultsTable multiMeasure(ImagePlus imp, Roi[] rois) {
    int nSlices = imp.getStackSize();
    Analyzer aSys = new Analyzer(imp); // System Analyzer
    ResultsTable rtSys = Analyzer.getResultsTable();
    ResultsTable rtMulti = new ResultsTable();
    rtMulti.showRowNumbers(true);
    rtSys.reset();
    int currentSlice = imp.getCurrentSlice();
    for (int slice=1; slice<=nSlices; slice++) {
        int sliceUse = slice;
        if (nSlices==1) sliceUse = currentSlice;
        imp.setSliceWithoutUpdate(sliceUse);
        rtMulti.incrementCounter();
        if ((Analyzer.getMeasurements()& Measurements.LABELS)!=0)
            rtMulti.addLabel("Label", imp.getTitle());
        int roiIndex = 0;
        for (int i=0; i<rois.length; i++) {
            imp.setRoi(rois[i]);
            roiIndex++;
            aSys.measure();
            for (int j=0; j<=rtSys.getLastColumn(); j++){
                float[] col = rtSys.getColumn(j);
                String head = rtSys.getColumnHeading(j);
                String suffix = ""+roiIndex;
                Roi roi = imp.getRoi();
                if (roi!=null) {
                    String name = roi.getName();
                    if (name!=null && name.length()>0 && (name.length()<9||!Character.isDigit(name.charAt(0))))
                        suffix = "("+name+")";
                }
                if (head!=null && col!=null && !head.equals("Slice"))
                    rtMulti.addValue(head+suffix, rtSys.getValue(j,rtSys.getCounter()-1));
            }
        }
    }
    return rtMulti;
}
