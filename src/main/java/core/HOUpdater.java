package core;
import javax.swing.*;


public class HOUpdater {

	public static void main(String[] args) {

		if (args.length > 0) {
			switch (args[0]) {
				case "DECOMPRESS" -> decompress(args[1], args[2]);
				default -> System.err.println("command " + args[0] + " not understood, it should be one of ['UNZIP', ... ] ");
			}
		}
	}

		private static void decompress(String updateFile, String toDir) {
			JOptionPane.showMessageDialog(new JFrame(),"Decompress() method called with parameters" + updateFile + " and " + toDir);
		}

}
