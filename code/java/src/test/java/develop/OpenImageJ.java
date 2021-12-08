package develop;

import net.imagej.ImageJ;

public class OpenImageJ
{
	public static void main( String... args )
	{
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();
	}
}
