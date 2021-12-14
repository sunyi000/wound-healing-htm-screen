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

import ij.IJ;


/*
If you uncomment below line of code and run the script within Fiji's script editor, it will generate a UI.
Unfortunately the #@ syntax is not understood by IntelliJ and one
thus currently cannot run such code within IntelliJ.
See discussion here: https://forum.image.sc/t/develop-groovy-scripts-for-imagej/58481/41
*/

//#@ Integer (label = "Please enter a small positive number", min="0", max="10") number

IJ.log("You entered: " + number)