/**
 *
 * Repository:
 *
 *
 * Creation:
 * Date: Month 2021
 * By: firstname.lastname@embl.de
 * For: firstname.lastname@embl.de
 *
 * Requirements
 * - ImageJ
 *
 * inputs:
 * - input number 1
 * - input number 2
 * - ......
 *
 * outputs:
 * - output number 1
 * - output number 2
 * - ....
 *
 */

import ij.IJ
import ij.ImagePlus;


/*
Please toggle the commenting of below two lines,
depending on whether you would like to hard-cde the image file,
or have a UI created.
Note that creating the UI will not work in IntelliJ.
See discussion here: https://forum.image.sc/t/develop-groovy-scripts-for-imagej/58481/49
*/

// INPUT IMAGE FILE PARAMETER
//
def imageFile = new File("blobs.jpg")
//#@ File (label = "Image file") imageFile

// PROCESSING PARAMETERS
//
class Parameters {
    static Integer number = 10;
}

// CODE
//
def imagePlus = IJ.openImage(imageFile.toString())
imagePlus.show()
IJ.log("The number is " + Parameters.number);