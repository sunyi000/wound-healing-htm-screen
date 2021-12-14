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


/*
If you copy and paste below code into Fiji's script editor, it will run!
 */

import ij.IJ
import org.scijava.Context
import org.scijava.command.Command
import org.scijava.command.CommandService
import org.scijava.plugin.Parameter

class MyCommand implements Command
{
    @Parameter (label="Please enter a small positive number", min="0", max="10")
    public Integer number;

    @Override
    void run() {
        IJ.log("You entered: " + number)
    }

    static void main(String[] args) {
        def context = (Context) IJ.runPlugIn("org.scijava.Context", "");
        def commandService = context.getService(CommandService.class);
        commandService.run( MyCommand.class, true );
    }
}
