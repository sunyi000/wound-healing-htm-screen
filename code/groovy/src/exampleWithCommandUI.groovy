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
import org.scijava.ui.UIService

class FIXMECommand implements Command
{
    @Parameter (label="Some number", autoFill = false)
    public Integer number;

    @Parameter (label="2D single channel input image", autoFill = false)
    public File inputImageFile;

    @Override
    void run() {
        IJ.log("You entered: "+number)

        def imagePlus = IJ.openImage(inputImageFile.toString());
        imagePlus.show()
    }

    static void main(String[] args) {
        def context = (Context) IJ.runPlugIn("org.scijava.Context", "");
        def commandService = context.getService(CommandService.class);
        def uIService = context.getService(UIService.class);
        uIService.showUI();
        commandService.run( FIXMECommand.class, true );
    }
}
