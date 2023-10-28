package module.ifa.gif;

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.IOException;

/**
 * Instances of this Gif89Frame subclass are constructed from RGB image info,
 * either in the form of an Image object or a pixel array.
 * <p>
 * There is an important restriction to note. It is only permissible to add
 * DirectGif89Frame objects to a Gif89Encoder constructed without an explicit
 * color map. The GIF color table will be automatically generated from pixel
 * information.
 * 
 * @version 0.90 beta (15-Jul-2000)
 * @author J. M. G. Elliott (tep@jmge.net)
 * @see Gif89Encoder
 * @see Gif89Frame
 * @see IndexGif89Frame
 */
public class DirectGif89Frame extends Gif89Frame {

	private int[] argbPixels;

	/**
	 * Construct an DirectGif89Frame from a Java image.
	 * 
	 * @param img
	 *            A java.awt.Image object that supports pixel-grabbing.
	 * @exception IOException
	 *                If the image is unencodable due to failure of
	 *                pixel-grabbing.
	 */
	public DirectGif89Frame(Image img) throws IOException {
		PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, true);

		String errmsg = null;
		try {
			if (!pg.grabPixels())
				errmsg = "can't grab pixels from image";
		} catch (InterruptedException e) {
			errmsg = "interrupted grabbing pixels from image";
		}

		if (errmsg != null)
			throw new IOException(errmsg + " (" + getClass().getName() + ")");

		theWidth = pg.getWidth();
		theHeight = pg.getHeight();
		argbPixels = (int[]) pg.getPixels();
		ciPixels = new byte[argbPixels.length];
	}

	/**
	 * Construct an DirectGif89Frame from ARGB pixel data.
	 * 
	 * @param width
	 *            Width of the bitmap.
	 * @param height
	 *            Height of the bitmap.
	 * @param argb_pixels
	 *            Array containing at least width*height pixels in the format
	 *            returned by java.awt.Color.getRGB().
	 */
	public DirectGif89Frame(int width, int height, int argb_pixels[]) {
		theWidth = width;
		theHeight = height;
		argbPixels = new int[theWidth * theHeight];
		System.arraycopy(argb_pixels, 0, argbPixels, 0, argbPixels.length);
		ciPixels = new byte[argbPixels.length];
	}

	@Override
	Object getPixelSource() {
		return argbPixels;
	}
}