package core.gui.theme;

import com.github.weisj.darklaf.icons.*;
import core.icon.OverlayIcon;
import core.icon.TextIcon;
import core.model.UserParameter;
import core.model.WorldDetailLeague;
import core.model.WorldDetailsManager;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;

import java.awt.*;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;

public class ImageUtilities {

    /** Hashtable mit Veränderungspfeilgrafiken nach Integer als Key */
    private static Hashtable<Integer,ImageIcon> m_clPfeilCache = new Hashtable<Integer,ImageIcon>();
    private static Hashtable<Integer,ImageIcon> m_clPfeilWideCache = new Hashtable<Integer,ImageIcon>();
    private static Hashtable<Integer,ImageIcon> m_clPfeilLightCache = new Hashtable<Integer,ImageIcon>();
    private static Hashtable<Integer,ImageIcon> m_clPfeilWideLightCache = new Hashtable<Integer,ImageIcon>();
    /** Cache für Transparent gemachte Bilder */
    public static HashMap<Image,Image> m_clTransparentsCache = new HashMap<Image,Image>();
    public static ImageIcon MINILEER = new ImageIcon(new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB));

	/**
	 * Tauscht eine Farbe im Image durch eine andere
	 *
	 */
	public static Image changeColor(Image im, Color original, Color change) {
	    final ImageProducer ip = new FilteredImageSource(im.getSource(),
	    		new ColorChangeFilter(original, change));
	    return Toolkit.getDefaultToolkit().createImage(ip);
	}

	/**
	 * paint a cross over an image
	 */
	public static Image getImageDurchgestrichen(Image image,Color helleFarbe, Color dunkleFarbe) {
	    try {
	        final BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	        final java.awt.Graphics2D g2d = (java.awt.Graphics2D) bufferedImage.getGraphics();

	        g2d.drawImage(image, 0, 0, null);

	        //Kreuz zeichnen
	        g2d.setColor(helleFarbe);
	        g2d.drawLine(0, 0, bufferedImage.getWidth() - 1, bufferedImage.getHeight());
	        g2d.drawLine(bufferedImage.getWidth() - 1, 0, 0, bufferedImage.getHeight());
	        g2d.setColor(dunkleFarbe);
	        g2d.drawLine(1, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
	        g2d.drawLine(bufferedImage.getWidth(), 0, 1, bufferedImage.getHeight());
	        return bufferedImage;
	    } catch (Exception e) {
	        return image;
	    }
	}

	/**
	 * Makes a colour in the image transparent.
	 */
	public static Image makeColorTransparent(Image im, int minred, int mingreen,
	                                                  int minblue, int maxred, int maxgreen,
	                                                  int maxblue) {
	    final ImageProducer ip = new FilteredImageSource(im.getSource(),
	    		new FuzzyTransparentFilter(minred, mingreen, minblue, maxred, maxgreen, maxblue));
	    return Toolkit.getDefaultToolkit().createImage(ip);
	}

	/**
	 * Makes a colour in the image transparent.
	 */
	public static Image makeColorTransparent(Image im, Color color) {
		Image image = null;

		//Cache durchsuchen
		image = (Image) m_clTransparentsCache.get(im);

		//Nicht im Cache -> laden
		if (image == null) {
			final ImageProducer ip = new FilteredImageSource(im.getSource(), new TransparentFilter(color));
			image = Toolkit.getDefaultToolkit().createImage(ip);

			//Bild in den Cache hinzufügen
			m_clTransparentsCache.put(im, image);
		}

		return image;
	}

	/**
	 * Copies the second image on the first image.
	 */
	public static Image merge(Image background, Image foreground) {
	    final BufferedImage image = new BufferedImage(
	    		background.getWidth(null), background.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    image.getGraphics().drawImage(background, 0, 0, null);
	    image.getGraphics().drawImage(foreground, 0, 0, null);
	
	    return image;
	}

	public static ImageIcon getImageIcon4Veraenderung(int wert, boolean aktuell) {
	        ImageIcon icon = null;
	        final Integer keywert = Integer.valueOf(wert);
	        int xPosText = 3;
	
	        // Nicht im Cache
	        if ((!m_clPfeilCache.containsKey(keywert) && aktuell)
	            || (!m_clPfeilLightCache.containsKey(keywert) && !aktuell)) {
	            final BufferedImage image = new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB);
	
	            //Pfeil zeichnen
	            final java.awt.Graphics2D g2d = (java.awt.Graphics2D) image.getGraphics();
	
	            //g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
	            if (wert == 0) {
	                //                g2d.setColor ( Color.darkGray );
	                //                g2d.drawLine ( 3, 8, 9, 8 );
	            } else if (wert > 0) {
	                final int[] xpoints = {0, 6, 7, 13, 10, 10, 3, 3, 0};
	                final int[] ypoints = {6, 0, 0, 6, 6, 13, 13, 6, 6};
	
	                //Polygon füllen
	                if (!aktuell) {
	                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
	                }
	
	                int farbwert = Math.min(240, 90 + (50 * wert));
	                g2d.setColor(new Color(0, farbwert, 0));
	                g2d.fillPolygon(xpoints, ypoints, xpoints.length);
	
	                //Polygonrahmen
	                farbwert = Math.min(255, 105 + (50 * wert));
	                g2d.setColor(new Color(40, farbwert, 40));
	                g2d.drawPolygon(xpoints, ypoints, xpoints.length);
	
	                //Wert eintragen
	                if (!aktuell) {
	                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	                }
	
	                g2d.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 10));
	
	                //Für 1 und 2 Weisse Schrift oben
	                if (wert < 3) {
	                    g2d.setColor(Color.black);
	                    g2d.drawString(wert + "", xPosText, 11);
	                    g2d.setColor(Color.white);
	                    g2d.drawString(wert + "", xPosText + 1, 11);
	                }
	                //Sonst Schwarze Schrift oben (nur bei Positiven Veränderungen)
	                else {
	                    //Position bei grossen Zahlen weiter nach vorne
	                    if (wert > 9) {
	                        xPosText = 0;
	                    }
	
	                    g2d.setColor(Color.white);
	                    g2d.drawString(wert + "", xPosText, 11);
	                    g2d.setColor(Color.black);
	                    g2d.drawString(wert + "", xPosText + 1, 11);
	                }
	            } else if (wert < 0) {
	                final int[] xpoints = {0, 6, 7, 13, 10, 10, 3, 3, 0};
	                final int[] ypoints = {7, 13, 13, 7, 7, 0, 0, 7, 7};
	
	                //Polygon füllen
	                if (!aktuell) {
	                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
	                }
	
	                int farbwert = Math.min(240, 90 - (50 * wert));
	                g2d.setColor(new Color(farbwert, 0, 0));
	                g2d.fillPolygon(xpoints, ypoints, xpoints.length);
	
	                //Polygonrahmen
	                farbwert = Math.min(255, 105 - (50 * wert));
	                g2d.setColor(new Color(farbwert, 40, 40));
	                g2d.drawPolygon(xpoints, ypoints, xpoints.length);
	
	                //Wert eintragen
	                if (!aktuell) {
	                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	                }
	
	                g2d.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 10));
	
	                //Position bei grossen Zahlen weiter nach vorne
	                if (wert < -9) {
	                    xPosText = 0;
	                }
	
	                g2d.setColor(Color.black);
	                g2d.drawString(Math.abs(wert) + "", xPosText, 11);
	                g2d.setColor(Color.white);
	                g2d.drawString(Math.abs(wert) + "", xPosText + 1, 11);
	            }
	
	            //Icon erstellen und in den Cache packen
	            icon = new ImageIcon(image);
	//            String desc = "(";
	//            if (wert>0) {
	//            	desc = desc + "+";
	//            }
	//            desc = desc + wert + ")";
	//            icon.setIconDescription(desc);
	
	            if (aktuell) {
	                m_clPfeilCache.put(keywert, icon);
	            } else {
	                m_clPfeilLightCache.put(keywert, icon);
	            }
	
	            //HOLogger.instance().log(Helper.class, "Create Pfeil: " + wert );
	        }
	        //Im Cache
	        else {
	            if (aktuell) {
	                icon = m_clPfeilCache.get(keywert);
	            } else {
	                icon = m_clPfeilLightCache.get(keywert);
	            }
	
	            //HOLogger.instance().log(Helper.class, "Use Pfeilcache: " + wert );
	        }
	
	        return icon;
	    }

	/**
	 * Creates a wide image for use where value can be greater than 99
	 * @param value the Value
	 * @param current
	 * @return an icon representation of the value
	 */
	public static ImageIcon getWideImageIcon4Veraenderung(int value, boolean current) {
        ImageIcon icon = null;
        final Integer keywert = Integer.valueOf(value);
        int xPosText = 8;

        // Not in cache
        if ((!m_clPfeilWideCache.containsKey(keywert) && current)
            || (!m_clPfeilWideCache.containsKey(keywert) && !current)) {
            final BufferedImage image = new BufferedImage(24, 14, BufferedImage.TYPE_INT_ARGB);
            final java.awt.Graphics2D g2d = (java.awt.Graphics2D) image.getGraphics();
            if (value != 0)
            {
               if (value > 0) {
	                final int[] xpoints = {5, 11, 12, 18, 15, 15, 8, 8, 5};
	                final int[] ypoints = {6, 0, 0, 6, 6, 13, 13, 6, 6};
	                //Fill polygon
	                if (!current) {
	                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
	                }
	                int farbwert = Math.min(240, 90 + (50 * value));
	                g2d.setColor(new Color(0, farbwert, 0));
	                g2d.fillPolygon(xpoints, ypoints, xpoints.length);
	
	                //Polygon Frame
	                farbwert = Math.min(255, 105 + (50 * value));
	                g2d.setColor(new Color(40, farbwert, 40));
	                g2d.drawPolygon(xpoints, ypoints, xpoints.length);
	
	                //Enter value
	                if (!current) {
	                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	                }
	                g2d.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 10));
	
	                //For 1 and 2, use white at top
	                if (value < 3) {
	                    g2d.setColor(Color.black);
	                    g2d.drawString(value + "", xPosText, 11);
	                    g2d.setColor(Color.white);
	                    g2d.drawString(value + "", xPosText + 1, 11);
	                }
	                // Black writing (only for positive)
	                else {
	                    // Reposition by value length
	                	if (value > 9)
	                		xPosText -= ((Integer.toString(value).length() - 1) * 3);
	                	
	                    g2d.setColor(Color.white);
	                    g2d.drawString(value + "", xPosText, 11);
	                    g2d.setColor(Color.black);
	                    g2d.drawString(value + "", xPosText + 1, 11);
	                }
	            } else {
	                final int[] xpoints = {5, 11, 12, 18, 15, 15, 8, 8, 5};
	                final int[] ypoints = {7, 13, 13, 7, 7, 0, 0, 7, 7};
	
	                //Fill Polygon
	                if (!current) {
	                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
	                }
	
	                int farbwert = Math.min(240, 90 - (50 * value));
	                g2d.setColor(new Color(farbwert, 0, 0));
	                g2d.fillPolygon(xpoints, ypoints, xpoints.length);
	
	                //Polygon Frame
	                farbwert = Math.min(255, 105 - (50 * value));
	                g2d.setColor(new Color(farbwert, 40, 40));
	                g2d.drawPolygon(xpoints, ypoints, xpoints.length);
	
	                //Enter value
	                if (!current) {
	                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	                }
	
	                g2d.setFont(new java.awt.Font("sansserif", java.awt.Font.PLAIN, 10));
	                // No need to worry about space for - as absolute value is used.
	                if (Math.abs(value) > 9)
	            		xPosText -= ((Integer.toString(Math.abs(value)).length() - 1) * 3);
	                g2d.setColor(Color.black);
	                g2d.drawString(Math.abs(value) + "", xPosText, 11);
	                g2d.setColor(Color.white);
	                g2d.drawString(Math.abs(value) + "", xPosText + 1, 11);
	            }
            }
            //Make the Icon and cache it
            icon = new ImageIcon(image);
            if (current) {
            	m_clPfeilWideCache.put(keywert, icon);
            } else {
                m_clPfeilWideLightCache.put(keywert, icon);
            }
        }
        //In Cache
        else {
            if (current) {
                icon = m_clPfeilWideCache.get(keywert);
            } else {
                icon = m_clPfeilWideLightCache.get(keywert);
            }
        }
        return icon;
    }
	
	public static ImageIcon NOIMAGEICON = new ImageIcon(new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB));

	/**
	 * Return ImageIcon for Position
	 *
	 */
	public static Icon getImage4Position(MatchRoleID position, int trickotnummer) {
	    if (position == null) {
	        return ImageUtilities.getImage4Position(0, (byte) 0, trickotnummer);
	    }
	
	    return ImageUtilities.getImage4Position(position.getId(), position.getTaktik(), trickotnummer);
	}

	/**
	 * Return ImageIcon for Position
	 *
	 */
	public static Icon getImage4Position(int posid, byte taktik, int trickotnummer) {
		Color trickotfarbe = null;
		Image trickotImage = null;
		Icon komplettIcon = null;
		StringBuilder key = new StringBuilder(20);
		// Im Cache nachsehen
		key.append("trickot_").append(posid).append("_").append(taktik).append("_").append(trickotnummer);
		komplettIcon = ThemeManager.getIcon(key.toString());
		
		if (komplettIcon == null) {
			trickotfarbe = getJerseyColorByPosition(posid);

			// Bild laden, transparenz hinzu, trikofarbe wechseln
			trickotImage = changeColor(
					changeColor(
							makeColorTransparent(
									iconToImage(ThemeManager.getIcon(HOIconName.TRICKOT)),
									Color.WHITE
							),
							Color.WHITE,
							trickotfarbe),
					new Color(100, 100, 100),
					trickotfarbe.brighter());
			komplettIcon = new ImageIcon(trickotImage);
			BufferedImage largeImage = new BufferedImage(28, 14, BufferedImage.TYPE_INT_ARGB);
			// Large Icon
			largeImage = (BufferedImage) merge(largeImage, iconToImage(komplettIcon));
			komplettIcon = new ImageIcon(largeImage);
	
		// return new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
		// Trickotnummer
			if ((trickotnummer > 0) && (trickotnummer < 100)) {
				BufferedImage image = new BufferedImage(28, 14, BufferedImage.TYPE_INT_ARGB);

				// 5;
				int xPosText = 20;
	
				// Helper.makeColorTransparent( image, Color.white );
				final Graphics2D g2d = (Graphics2D) image.getGraphics();
	
				// Wert eintragen
				// g2d.setComposite ( AlphaComposite.getInstance(
				// AlphaComposite.SRC_OVER, 1.0f ) );
				g2d.setRenderingHint(
						RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setRenderingHint(
						RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, UserParameter.instance().schriftGroesse));
	
				// Position bei grossen Zahlen weiter nach vorne
				if (trickotnummer > 9) {
					xPosText = 13;
				}
	
				g2d.setColor(Color.black);
				g2d.drawString(trickotnummer + "", xPosText, 13);
	
				// Zusammenführen
				image = (BufferedImage) merge(image, iconToImage(komplettIcon));
	
				// Icon erstellen und in den Cache packen
				komplettIcon = new ImageIcon(image);
				
			}
			// In den Cache hinzufügen
			ThemeManager.instance().put(key.toString(), komplettIcon);
		} // komplettIcon == null 
	
		return komplettIcon;
	}

	public static ImageIcon getCountryFlagIcon(int iCountryID) {
		WorldDetailLeague leagueDetail = WorldDetailsManager.instance().getWorldDetailLeagueByCountryId(iCountryID);
	    if ( leagueDetail != null ) return getLeagueFlagIcon(leagueDetail.getLeagueId());
	    return  null;
	}

	public static ImageIcon getLeagueFlagIcon(int iLeague) {
		return ThemeManager.instance().classicSchema.loadImageIcon("flags/"+ iLeague + "flag.png");
	}

	public static BufferedImage toBufferedImage(Icon icon) {
		return toBufferedImage(iconToImage(icon));
	}

	public static BufferedImage toBufferedImage(Image image) {
	    if (image instanceof BufferedImage) {
	        return (BufferedImage)image;
	    }

	    // This code ensures that all the pixels in the image are loaded
	    image = new ImageIcon(image).getImage();

	    // Determine if the image has transparent pixels; for this method's
	    // implementation, see Determining If an Image Has Transparent Pixels
	    boolean hasAlpha = hasAlpha(image);

	    // Create a buffered image with a format that's compatible with the screen
	    BufferedImage bimage = null;
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    try {
	        // Determine the type of transparency of the new buffered image
	        int transparency = Transparency.OPAQUE;
	        if (hasAlpha) {
	            transparency = Transparency.BITMASK;
	        }

	        // Create the buffered image
	        GraphicsDevice gs = ge.getDefaultScreenDevice();
	        GraphicsConfiguration gc = gs.getDefaultConfiguration();
	        bimage = gc.createCompatibleImage(
	            image.getWidth(null), image.getHeight(null), transparency);
	    } catch (HeadlessException e) {
	        // The system does not have a screen
	    }

	    if (bimage == null) {
	        // Create a buffered image using the default color model
	        int type = BufferedImage.TYPE_INT_RGB;
	        if (hasAlpha) {
	            type = BufferedImage.TYPE_INT_ARGB;
	        }
	        bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	    }

	    // Copy image to buffered image
	    Graphics g = bimage.createGraphics();

	    // Paint the image onto the buffered image
	    g.drawImage(image, 0, 0, null);
	    g.dispose();

	    return bimage;
	}

//  This method returns true if the specified image has transparent pixels  
    private static boolean hasAlpha(Image image) {  
        // If buffered image, the color model is readily available  
        if (image instanceof BufferedImage) {  
            BufferedImage bimage = (BufferedImage)image;  
            return bimage.getColorModel().hasAlpha();  
        }  
      
        // Use a pixel grabber to retrieve the image's color model;  
        // grabbing a single pixel is usually sufficient  
         PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);  
        try {  
            pg.grabPixels();  
        } catch (InterruptedException e) {  
        }  
      
        // Get the image's color model  
        ColorModel cm = pg.getColorModel();  
        return cm.hasAlpha();  
    }

	public static Icon getJerseyIcon(MatchRoleID position, int trickotnummer) {
		if (position == null) {
			return ImageUtilities.getJerseyIcon(0, (byte) 0, trickotnummer);
		}

		return ImageUtilities.getJerseyIcon(position.getId(), position.getTaktik(), trickotnummer);
	}

    public static Icon getJerseyIcon(int posid, byte taktik, int trickotnummer) {
        return getJerseyIcon(posid, taktik, trickotnummer, 20);
    }

    public static Icon getJerseyIcon(int posid, byte taktik, int trickotnummer, int size) {
        String key = "trickot_" + posid + "_" + taktik + "_" + trickotnummer + "_" + size;
        Icon komplettIcon = ThemeManager.instance().getIcon(key);


        if (komplettIcon == null) {
            Color jerseyColor = getJerseyColorByPosition(posid);

            double brightness = ImageUtilities.getBrightness(jerseyColor);
            Color textColor = brightness < 130 ? Color.WHITE : Color.BLACK;

            Map<Object, Object> colorMap = Map.of("jerseyColor", jerseyColor,
                                                  "collarColor", jerseyColor,
                                                  "outlineColor", textColor);
            int width = size;
            int height = Math.round(size * 16f / 20f);
            Icon jerseyIcon = IconLoader.get().loadSVGIcon("gui/bilder/jerseys.svg",
                                                           width, height, true, colorMap);

            Icon numberIcon = EmptyIcon.create(0);
            if (trickotnummer > 0 && trickotnummer < 50) {
                int baseline = Math.round(height * 13f / 16f);
                int fontSize = Math.round(height * 8f / 16f);
                numberIcon = new TextIcon(String.valueOf(trickotnummer),
                                          textColor,
                                          new Font(Font.SANS_SERIF, Font.BOLD, fontSize),
                                          width, height, baseline);
            }
            komplettIcon = new OverlayIcon(jerseyIcon, numberIcon, size, size);
            ThemeManager.instance().put(key, komplettIcon);
        }

        return komplettIcon;
    }

	private static Color getJerseyColorByPosition(int posid) {
		Color trickotfarbe;
		switch (posid) {
			case IMatchRoleID.keeper: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_KEEPER);
				break;
			}

			case IMatchRoleID.rightCentralDefender:
			case IMatchRoleID.leftCentralDefender:
			case IMatchRoleID.middleCentralDefender: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_CENTRALDEFENCE);
				break;
			}

			case IMatchRoleID.leftBack:
			case IMatchRoleID.rightBack: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_WINGBACK);
				break;
			}

			case IMatchRoleID.rightInnerMidfield:
			case IMatchRoleID.leftInnerMidfield:
			case IMatchRoleID.centralInnerMidfield: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_MIDFIELD);
				break;
			}

			case IMatchRoleID.leftWinger:
			case IMatchRoleID.rightWinger: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_WING);
				break;
			}

			case IMatchRoleID.rightForward:
			case IMatchRoleID.leftForward:
			case IMatchRoleID.centralForward: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_FORWARD);
				break;
			}

			case IMatchRoleID.substGK1: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_SUBKEEPER);
				break;
			}

			case IMatchRoleID.substCD1: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_SUBDEFENCE);
				break;
			}

			case IMatchRoleID.substIM1: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_SUBMIDFIELD);
				break;
			}

			case IMatchRoleID.substWI1: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_SUBWING);
				break;
			}

			case IMatchRoleID.substFW1: {
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT_SUBFORWARD);
				break;
			}

			default:
				trickotfarbe = ThemeManager.getColor(HOColorName.SHIRT);
		}
		return trickotfarbe;
	}

    public static Icon getSvgIcon(String image) {
        return getSvgIcon(image, 24, 24);
    }

    public static Icon getSvgIcon(String key, int width, int height) {
		final String index = key + "_" + width + "_" + height;
		Icon icon = ThemeManager.getIcon(index);

		if (icon == null) {
			Object imagePath = ThemeManager.getIconPath(key);

			icon = IconLoader.get().getIcon(Objects.requireNonNull(imagePath).toString(), width, height);
			ThemeManager.instance().put(index, icon);
		}

		return icon;
    }

	/**
	 * Transforms an icon into an image.
	 * Cf. 	https://stackoverflow.com/a/5831357
	 * @param icon
	 * @return
	 */
	public static Image iconToImage(Icon icon) {
		if (icon instanceof ImageIcon) {
			return ((ImageIcon)icon).getImage();
		}
		else {
			int w = icon.getIconWidth();
			int h = icon.getIconHeight();
			GraphicsEnvironment ge =
					GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			BufferedImage image = gc.createCompatibleImage(w, h, Transparency.BITMASK);
			Graphics2D g = image.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
			return image;
		}
	}

	public static double getBrightness(Color colour) {
		return Math.sqrt(0.241 * colour.getRed()*colour.getRed()
				+ 0.691 * colour.getGreen()*colour.getGreen()
				+ 0.068 * colour.getBlue()*colour.getBlue());
	}

	private static String getHexColor(Color colour) {
		return "#" + String.format("%1$02X", colour.getRed()) +
				String.format("%1$02X", colour.getGreen()) +
				String.format("%1$02X", colour.getBlue());
	}

	public static Color getColorFromHex(String hexColour) {
		return new Color(Integer.valueOf(hexColour.substring(1, 3), 16),
				Integer.valueOf(hexColour.substring(3, 5), 16),
				Integer.valueOf(hexColour.substring(5, 7), 16));
	}

	public static Icon getScaledIcon(Icon icon, int width, int height) {
		if (icon instanceof DerivableIcon) {
			return ((DerivableIcon<Icon>) icon).derive(width, height);
		} else {
			return new DerivableImageIcon(iconToImage(icon), width, height, Image.SCALE_SMOOTH);
		}
	}
}
