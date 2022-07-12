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
import ij.plugin.ImageCalculator
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
//#@ File (label="Input directory", style="directory") inputDir
//#@ String (label="Dataset id") datasetId
//#@ Boolean (label="Run headless", default="false") headless

// for developing in an IDE
def inputDir = new File("/Users/tischer/Documents/daniel-heid-wound-healing/data/input")
def datasetId = "C4ROI1_Fast"; // C4ROI1_Fast A3ROI2_Slow
def headless = false;
new ImageJ().setVisible(true)

// INPUT PARAMETERS
def cellDiameter = 20
def scratchDiameter = 500
def binningFactor = 2

// DERIVED OR FIXED PARAMETERS
//
def cellFilterRadius = cellDiameter/binningFactor
def scratchFilterRadius = scratchDiameter/5/binningFactor

// CODE
//

// open
IJ.run("Close All");
println("Opening " + datasetId + "...")
def imp = FolderOpener.open(inputDir.toString(), " file="+datasetId)
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
def binnedImp = imp.duplicate() // keep for saving
// enhance cells
IJ.run(imp, "32-bit", "");
// sdev
def sdevImp = imp.duplicate()
sdevImp.setTitle("sdev")
IJ.run(sdevImp, "Find Edges", "stack"); // removes larger structures, such as dirt in the background
IJ.run(sdevImp, "Variance...", "radius=" + cellFilterRadius + " stack");
IJ.run(sdevImp, "Square Root", "stack");
if (!headless) sdevImp.duplicate().show()
// mean
def meanImp = imp.duplicate()
IJ.run(meanImp, "Mean...", "radius=" + cellFilterRadius + " stack");
// cov
def covImp = ImageCalculator.run(sdevImp, meanImp, "Divide create 32-bit stack");
covImp.setTitle("cov")
IJ.run(covImp, "Enhance Contrast", "saturated=0.35");
IJ.run(covImp, "8-bit", ""); // otherwise the thresholding does not seem to work
if (!headless) covImp.duplicate().show()
// create binary image (cell-free regions are foreground)
//
IJ.run("Options...", "iterations=1 count=1 black");
// determine threshold in first frame, because there we are
// closest to a 50/50 occupancy of the image with signal,
// which is best for most auto-thresholding algorithms
covImp.setPosition(1)
def histogram = covImp.getProcessor().getHistogram()
// use Huang method
// multiply threshold with a fixed factor (as is done in CellProfiler),
// based on the observation that the threshold is consistently
// , in our case, a bit too high, which may be due to
// the fact that the majority of the image is foreground
def threshold = Auto_Threshold.Huang(histogram) * 0.8
// create binary image of whole movie,
// using the threshold of the first image
// defining the cell free regions as foreground
def binaryImp = Threshold.threshold(covImp, 0, threshold)

// create scratch ROI
//
println("Creating scratch ROI...")
binaryImp.setPosition(1) // scratch is most visible in first frame
def scratchIp = binaryImp.crop("whole-slice").getProcessor();
// identify largest cell free region as scratch region
scratchIp = BinaryImages.keepLargestRegion(scratchIp)
// remove cells inside scratch region
scratchIp = Reconstruction.fillHoles(scratchIp)
// disconnect from cell free regions outside scratch
scratchIp = Morphology.opening(scratchIp, SquareStrel.fromRadius((int) scratchFilterRadius))
// in case the opening helped to cut off some
// areas outside the scratch we again only keep the largest region
scratchIp = BinaryImages.keepLargestRegion(scratchIp)
// smoothen scratch edges
scratchIp = Morphology.closing(scratchIp, SquareStrel.fromRadius((int) scratchFilterRadius))
// dilate to accommodate spurious cells at scratch borders
scratchIp = Morphology.dilation(scratchIp, SquareStrel.fromRadius( 2 * (int)cellFilterRadius))

// convert binary image to ROI, which is handy for measurements
def scratchImp = new ImagePlus("Binary Scratch", scratchIp)
IJ.run(scratchImp, "Create Selection", "");
def scratchROI = scratchImp.getRoi()

// measure occupancy of scratch ROI
// `area_fraction` returns the fraction of foreground pixels
// (cell free area) within the measurement ROI
println("Performing measurements...")
IJ.run("Set Measurements...", "area_fraction redirect=None decimal=2");
// RoiManager does not work headless: https://github.com/imagej/imagej-legacy/issues/153
def rt = multiMeasure(binaryImp, scratchROI)

// show
if (!headless) {
    rt.show("Results")
    binnedImp.show()
    binnedImp.setRoi(scratchROI, true)
    binaryImp.show()
    binaryImp.setRoi(scratchROI, true)
}

// save
//

// create output directory
def outputDir = new File( inputDir.getParent(), "analysis" );
println("Ensuring existence of output directory: " + outputDir)
outputDir.mkdir()
// save table
rt.save( new File( outputDir, datasetId + ".csv" ).toString() );

// save binned image with ROI
binnedImp.setRoi(scratchROI, false)
IJ.save(binnedImp, new File( outputDir, datasetId + ".tif" ).toString() );

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


// copied from Auto_Threshold
public static int Percentile(int [] data, double ptile) {
    // W. Doyle, "Operation useful for similarity-invariant pattern recognition,"
    // Journal of the Association for Computing Machinery, vol. 9,pp. 259-267, 1962.
    // ported to ImageJ plugin by G.Landini from Antti Niemisto's Matlab code (GPL)
    // Original Matlab code Copyright (C) 2004 Antti Niemisto
    // See http://www.cs.tut.fi/~ant/histthresh/ for an excellent slide presentation
    // and the original Matlab code.

    int threshold = -1;
    double [] avec = new double [data.length];

    for (int i=0; i<data.length; i++)
        avec[i]=0.0;

    double total = Auto_Threshold.partialSum(data, data.length - 1);
    double temp = 1.0;
    for (int i=0; i<data.length; i++){
        avec[i]=Math.abs((Auto_Threshold.partialSum(data, i)/total)- ptile);
        IJ.log("Ptile["+i+"]:"+ avec[i] + ", temp:"+temp);
        if (avec[i]<temp) {
            temp = avec[i];
            threshold = i;
        }
    }
    return threshold;
}
