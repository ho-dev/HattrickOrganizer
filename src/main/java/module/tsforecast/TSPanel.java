package module.tsforecast;

/*
 * TSPanel.java
 *
 * Created on 15.March 2006, 11:04
 *
 *Version 0.8
 *history :
 *15.03.06  Version 0.1  Creation
 *16.03.06  Version 0.2
 *19.03.06  Version 0.3
 *25.03.06  Version 0.4  Draw scala
 *03.04.06  Version 0.5  Draw thick line
 *26.08.06  Version 0.51 rebuilt
 *04.09.06  Version 0.6  Scale of confidence curve
 *22.02.07  Version 0.7  Show day of match on x-scale
 *16.07.08  Version 0.8  Fixed scaling problems
 *17.09.09  Version 0.9  Fixed scaling and refresh problems
 */

/**
 *
 * @author  michael.roux
 */

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.constants.player.PlayerAbility;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.IMatchDetails;
import core.util.HelperWrapper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.*;


// Referenced classes of package hoplugins.tsforecast:
//            Curve

class TSPanel extends JPanel {


	private static final long serialVersionUID = 1L;
	static final int DXFrame = 10;
	static final int DXAxis = 5;
	static final int DYFrame = 20;
	static final int DYAxis = 5;

  static int m_iMaxX = 600;
  static int m_iMaxY = 500;

  static double m_dMaxTextWidth = 20D;
  static double m_dFactor = 2D;
  static int m_iCoordX0 = 0;
  static final double m_dValues = 10.2D;

  static boolean m_bInited = false;

  public static final Color lightBlue   = new Color(220, 220, 255);
  public static final Color lightGreen  = new Color(200, 255, 200);
  public static final Color darkGreen   = new Color(0, 180, 0);
  public static final Color lightRed    = new Color(255, 230, 230);
  public static final Color lightYellow = new Color(255, 255, 200);

  private final Icon m_newImage   = ThemeManager.getIcon(HOIconName.MATCHICONS[2]);
  private final Icon m_startImage = ThemeManager.getIcon(HOIconName.MANUELLSMILIES[7]);


  private GregorianCalendar m_startDate  = null;
  private GregorianCalendar m_endDate    = null;
  private int m_iDaysToDisplay           = 0;
  private int m_iTodayPosition           = 0;
  private boolean m_bShowTeamspiritScale = true;
  private boolean m_bShowConfidenceScale = true;

  private ArrayList<Curve> m_Curves = new ArrayList<Curve>();


  TSPanel() {
    setDoubleBuffered( true);
    setBackground(ThemeManager.getColor(HOColorName.STAT_PANEL_BG));
  }

  void addCurve( Curve curve, boolean first) {
    if( first) m_Curves.add( 0, curve);
    else m_Curves.add( curve);
  }

  void addCurve( Curve curve) { addCurve( curve, false); }
  boolean removeCurve( Curve curve) { return m_Curves.remove( curve); }
  void showTeamspiritScale( boolean b) { m_bShowTeamspiritScale = b; }
  void showConfidenceScale( boolean b) { m_bShowConfidenceScale = b; }

//-- protected ------------------------------------------------------------------------

  @Override
protected void paintComponent( Graphics g) {
    super.paintComponent(g);

    if( !m_bInited ) {
      m_dMaxTextWidth = getMaxTextWidth( (Graphics2D)g);
      m_iCoordX0 = (int)m_dMaxTextWidth + DXFrame + DXAxis;
      m_bInited = true;
    }

    // Dynamically calculate size information
    Rectangle rectangle = getBounds();
    m_iMaxX = rectangle.width - 2*DXFrame;
    m_iMaxY = rectangle.height - 2*DYFrame - UserParameter.instance().schriftGroesse - 2 - DXAxis;
    setStartEndDate();
    m_dFactor = (double)(m_iMaxX - m_iCoordX0) / (double)m_iDaysToDisplay;
    if(m_dFactor < 1.0D) m_dFactor = 1.0D;

    drawDiagram((Graphics2D)g);
  }

//-- private ------------------------------------------------------------------------

  private void drawDiagram( Graphics2D graphics2d) {
    graphics2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    drawCoordSystem( graphics2d);
      for(int i = 0; i < m_Curves.size(); i++) {
        if(m_Curves.get(i) instanceof TrainerCurve) {
          drawIndicators(graphics2d, m_Curves.get(i));
        }  else {
          drawCurve(graphics2d, m_Curves.get(i));
        }
      }
  }


  private void drawCurve( Graphics2D graphics2d, Curve curve) {
    graphics2d.setColor( curve.getColor());
    if(curve.first()) {
      Polygon polygon1 = new Polygon();
      Polygon polygon2 = new Polygon();
      GregorianCalendar HRFDate = new GregorianCalendar();
      boolean flag = curve.next();

      HRFDate.setTime( curve.getDate());

      int deltaX = m_startDate.get( Calendar.DAY_OF_YEAR);
      int lastYear = m_startDate.get( Calendar.YEAR);
      int deltaYear = 0;
      double x = 0D;
      int iSpirit = 40;
      int dayOffset = m_startDate.get( Calendar.DAY_OF_WEEK) - 1;
      if( dayOffset < 0) dayOffset += 7;

      for(; flag; flag = curve.next()) {
        HRFDate.setTime( curve.getDate());
        if( lastYear != HRFDate.get( Calendar.YEAR)) {
          deltaYear += 365;
          if( HRFDate.isLeapYear( lastYear))
            deltaYear++;
          lastYear = HRFDate.get( Calendar.YEAR);
        }
        x = (double)HRFDate.get( Calendar.DAY_OF_YEAR) + (double)HRFDate.get( Calendar.HOUR_OF_DAY)/24D
            - (double)deltaX + (double)deltaYear + (double)dayOffset;
        iSpirit = (int)((curve.getSpirit() * (double)m_iMaxY) / m_dValues);

        if( curve.getPointType() == Curve.RESET_PT) {
          graphics2d.drawString( "R", (int)(x * m_dFactor + (double)m_iCoordX0), m_iMaxY + DYFrame - 2 - iSpirit);
        }
        else if( curve.getAttitude() != IMatchDetails.EINSTELLUNG_NORMAL
              && curve.getAttitude() != IMatchDetails.EINSTELLUNG_UNBEKANNT) {
          if( curve.getAttitude() == IMatchDetails.EINSTELLUNG_PIC)
            graphics2d.drawString( "P", (int)(x * m_dFactor + (double)m_iCoordX0), m_iMaxY + DYFrame - 2 - iSpirit);
          else
            graphics2d.drawString("M", (int)(x * m_dFactor + (double)m_iCoordX0), m_iMaxY + DYFrame - 2 - iSpirit);
        }

        polygon1.addPoint((int)(x * m_dFactor + (double)m_iCoordX0), m_iMaxY + DYFrame - iSpirit);
        polygon2.addPoint((int)(x * m_dFactor + (double)m_iCoordX0), m_iMaxY + DYFrame - iSpirit + 1);
      }

      graphics2d.drawPolyline(polygon1.xpoints, polygon1.ypoints, polygon1.npoints);
      graphics2d.drawPolyline(polygon2.xpoints, polygon2.ypoints, polygon2.npoints);
    }
  }


  private void drawIndicators( Graphics2D graphics2d, Curve curve) {
    graphics2d.setColor(Color.BLACK);
    if(curve.first()) {
      GregorianCalendar gregoriancalendar = new GregorianCalendar();
      GregorianCalendar gregoriancalendar1 = new GregorianCalendar();
      boolean flag = curve.next();
      gregoriancalendar1.setTime(curve.getDate());
      gregoriancalendar.setTime(curve.getDate());
      int i = m_startDate.get(6);
      int j = m_startDate.get(1);
      int k = 0;
      //boolean flag1 = false;
      //byte byte0 = 40;
      int j1 = m_startDate.get(7) - 1;
      if(j1 < 0) j1 += 7;
      HelperWrapper ihelper = HelperWrapper.instance();
      for(; flag; flag = curve.next()) {
        gregoriancalendar.setTime(curve.getDate());
        if(j != gregoriancalendar.get(1)) {
          k += 365;
          if(gregoriancalendar.isLeapYear(j))
            k++;
          j = gregoriancalendar.get(1);
        }
        int l = (gregoriancalendar.get(6) - i) + k + j1;
        int i1 = (int)(((curve.getSpirit() + 0.5D) * (double)m_iMaxY) / m_dValues);
        switch(curve.getPointType()) {
          case Curve.TRAINER_DOWN_PT:
            graphics2d.drawImage(   ImageUtilities.getImageIcon4Veraenderung(-1,true).getImage(),
                                   (int)((double)l * m_dFactor + (double)m_iCoordX0), (m_iMaxY + 10) - 2 - i1,
                                   ImageUtilities.getImageIcon4Veraenderung(-1,true).getImageObserver());
            graphics2d.drawString( HOVerwaltung.instance().getLanguageString("trainer_down"),
                                   (int)((double)l * m_dFactor + (double)m_iCoordX0), (m_iMaxY + 10) - 2 - i1 - 2);
            graphics2d.drawString( PlayerAbility.getNameForSkill((int)curve.getSpirit(), true),
                                   (int)((double)l * m_dFactor + (double)m_iCoordX0), ((m_iMaxY + 10) - 2 - i1) + 24);
            break;
          case Curve.NEW_TRAINER_PT:
            graphics2d.drawImage(  ImageUtilities.iconToImage(m_newImage),
                                   (int)((double)l * m_dFactor + (double)m_iCoordX0), (m_iMaxY + 10) - 2 - i1,
                    ((ImageIcon)m_newImage).getImageObserver());
            graphics2d.drawString( HOVerwaltung.instance().getLanguageString("trainer_exchange"),
                                   (int)((double)l * m_dFactor + (double)m_iCoordX0), (m_iMaxY + 10) - 2 - i1 - 2);
            graphics2d.drawString( PlayerAbility.getNameForSkill((int)curve.getSpirit(), true),
                                   (int)((double)l * m_dFactor + (double)m_iCoordX0), ((m_iMaxY + 10) - 2 - i1) + 24);
            break;
          case Curve.START_TRAINER_PT:
            graphics2d.drawImage(  ImageUtilities.iconToImage(m_startImage),
                                   (int)((double)l * m_dFactor + (double)m_iCoordX0), (m_iMaxY + 10) - 2 - i1,
                    ((ImageIcon)m_startImage).getImageObserver());
            graphics2d.drawString( HOVerwaltung.instance().getLanguageString("ls.team.coachingskill"),
                                   (int)((double)l * m_dFactor + (double)m_iCoordX0), (m_iMaxY + 10) - 2 - i1 - 2);
            graphics2d.drawString( PlayerAbility.getNameForSkill((int)curve.getSpirit(), true),
                                   (int)((double)l * m_dFactor + (double)m_iCoordX0), ((m_iMaxY + 10) - 2 - i1) + 24);
            break;
          }
      }
    }
  }


  private void drawCoordSystem( Graphics2D graphics2d) {
    FontRenderContext fontrendercontext = graphics2d.getFontRenderContext();
    Font font = new Font( "SansSerif", 1, UserParameter.instance().schriftGroesse);
    HelperWrapper ihelper = HelperWrapper.instance();
    Rectangle2D rectangle2d = null;

    drawSeason( graphics2d);
    drawWeeks( graphics2d);

    // y-axis text and helplines
    String str = null;
    graphics2d.setFont( font);
    for( int i = 0; i < m_dValues; i++) { // values 0-10.2
      graphics2d.setColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
      graphics2d.drawLine( m_iCoordX0, (int)(m_iMaxY+DYFrame - i*m_iMaxY/m_dValues),
                           m_iMaxX,    (int)(m_iMaxY+DYFrame - i*m_iMaxY/m_dValues));

      // draw scale for teamspirit
      if( m_bShowTeamspiritScale) {
        graphics2d.setColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
        str = TeamSpirit.toString( i);
        rectangle2d = new TextLayout( str, font, fontrendercontext).getBounds();
        if( i < m_dValues-1)
          graphics2d.drawString( str, m_iCoordX0 - (int)rectangle2d.getWidth() - 5,
                                 (int)(m_iMaxY+DYFrame - i*m_iMaxY/m_dValues - m_iMaxY/(2*m_dValues)
                                 + UserParameter.instance().schriftGroesse/2 -3));
        else
          graphics2d.drawString( str, m_iCoordX0 - (int)rectangle2d.getWidth() - 5,
                                 (m_iMaxY+DYFrame - m_iMaxY + 3));
      }

      // draw scale for confidence
	if( m_bShowConfidenceScale && i < m_dValues-1) {
        graphics2d.setColor(ThemeManager.getColor(HOColorName.TSFORECAST_ALT_COLOR));
        str = TeamConfidence.toString( i);
        rectangle2d = new TextLayout( str, font, fontrendercontext).getBounds();
        graphics2d.drawString( str, m_iCoordX0 - (int)rectangle2d.getWidth() - 5,
                               (int)(m_iMaxY+DYFrame - (i+1)*m_iMaxY/m_dValues + 3));
	}
      graphics2d.setColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
      graphics2d.drawLine( m_iCoordX0-3, (int)(m_iMaxY+DYFrame - i*m_iMaxY/m_dValues),
                           m_iCoordX0+3, (int)(m_iMaxY+DYFrame - i*m_iMaxY/m_dValues));
    }

    graphics2d.setColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
    // x-axis
    graphics2d.drawLine(m_iCoordX0-3, m_iMaxY+DYFrame, m_iMaxX, m_iMaxY+DYFrame);
    graphics2d.drawLine(m_iCoordX0-3, DYFrame, m_iMaxX, DYFrame);
    // y-axis
    graphics2d.drawLine(m_iCoordX0, m_iMaxY+DYFrame+3, m_iCoordX0, DYFrame-3);
  }


  private void drawSeason( Graphics2D graphics2d) {
    GregorianCalendar today = new GregorianCalendar();
    today.setTime( HOVerwaltung.instance().getModel().getBasics().getDatum());

    // Week starts at Saturday = 7, Sunday = 1
    int iDay = today.get( Calendar.DAY_OF_WEEK);

    int iSeason =HOVerwaltung.instance().getModel().getBasics().getSeason();

//    m_dFactor = (double)(m_iMaxX - m_iCoordX0 + DXFrame) / (double)m_iDaysToDisplay;
//    if(m_dFactor < 1.0D) m_dFactor = 1.0D;

    // Spieltag increases with game, therefore -1
    int iSeasonWeek = HOVerwaltung.instance().getModel().getBasics().getSpieltag()-1;
    // iSeasonWeek starts with 1 and the current week has already been subtracted by iDay
    int iCurrentSeasonStart = m_iTodayPosition -iDay -(iSeasonWeek-2)*7;
    int iSeasonLength = 16*7;

    int iXX0 = m_iCoordX0;
    int iXX1 = (int)((double)m_iCoordX0 + (double)(iCurrentSeasonStart - iSeasonLength)*m_dFactor);
    if( iXX1 < iXX0) iXX1 = iXX0;
    int iXX2 = (int)((double)m_iCoordX0 + (double) iCurrentSeasonStart                 *m_dFactor);
    if( iXX2 < iXX0) iXX2 = iXX0;
    int iXX3 = (int)((double)m_iCoordX0 + (double)(iCurrentSeasonStart + iSeasonLength)*m_dFactor);
    if( iXX3 < iXX0) iXX3 = iXX0;
    int iXX4 = m_iMaxX;
    if( iXX3 > iXX4) iXX3 = iXX4;
    int iYText = m_iMaxY+2*DYFrame+DYAxis+3;

    Font font = new Font("SansSerif", 1, UserParameter.instance().schriftGroesse+2);
    graphics2d.setFont(font);

    // current season
    graphics2d.setColor( lightBlue);
    graphics2d.fillRect( iXX2, DYFrame, iXX3-iXX2, m_iMaxY);
    graphics2d.setColor(ThemeManager.getColor(HOColorName.TSFORECAST_ALT_COLOR));
    graphics2d.drawString( HOVerwaltung.instance().getLanguageString( "Season") + " " + iSeason,
                           iXX2 + (iXX3-iXX2)/2, iYText);
//ErrorLog.writeln("Saisonstart(F): "+ (double) iCurrentSeasonStart*m_dFactor);
//ErrorLog.writeln("Saisonstart   : "+ iCurrentSeasonStart);

    // previous seasons
    if(iCurrentSeasonStart > 0) {
      graphics2d.setColor( lightGreen);
      if(iCurrentSeasonStart > iSeasonLength) {
        // season -1
        graphics2d.fillRect( iXX1, DYFrame, iXX2-iXX1, m_iMaxY);
        graphics2d.setColor( darkGreen);
        graphics2d.drawString( HOVerwaltung.instance().getLanguageString("Season") + " " + (iSeason - 1),
                               iXX1 + (iXX2-iXX1)/2, iYText);
        // season -2
        graphics2d.setColor( lightYellow);
        graphics2d.fillRect( iXX0, DYFrame, iXX1-iXX0, m_iMaxY);
      } else {
        // season -1
        graphics2d.fillRect( iXX1, DYFrame, iXX2-iXX1, m_iMaxY);
        graphics2d.setColor( darkGreen);
        graphics2d.drawString( HOVerwaltung.instance().getLanguageString("Season") + " " + (iSeason - 1),
                               iXX1 + (iXX2-iXX1)/2, iYText);
      }
    }

    // next season
    if( iCurrentSeasonStart + iSeasonLength < m_iDaysToDisplay) {
      graphics2d.setColor( lightRed);
      graphics2d.fillRect( iXX3, DYFrame, iXX4-iXX3, m_iMaxY);
    }
  }

  // draw weekend lines
  private void drawWeeks( Graphics2D graphics2d) {
    // calculate first week on screen
    int iSeasonWeek = Math.round( HOVerwaltung.instance().getModel().getBasics().getSpieltag() - (float)m_iTodayPosition / 7.0f);
    while( iSeasonWeek < 1 ) iSeasonWeek += 16;

    Font font = new Font("SansSerif", 1, UserParameter.instance().schriftGroesse);
    graphics2d.setFont(font);

    for( int saturday = Calendar.SATURDAY;
         (double)saturday*m_dFactor+(double)m_iCoordX0 < (double)m_iMaxX-2D*m_dFactor;
         saturday += 7 , iSeasonWeek++ )
    {
      if( iSeasonWeek > 16) {
        iSeasonWeek = 1;
      }
      graphics2d.setColor(Color.lightGray);
      graphics2d.drawRect( (int)((double)(saturday)*m_dFactor+(double)m_iCoordX0), DYFrame,
                           (int)(2D*m_dFactor-1.0D), m_iMaxY);
      graphics2d.setColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));
      graphics2d.drawString( iSeasonWeek+"",
                             (int)((double)(saturday+3)*m_dFactor+(double)m_iCoordX0),
                             m_iMaxY+2*DYFrame+DYAxis- (UserParameter.instance().schriftGroesse));
    }
  }

  //Calculate first and last day of diagram, as well as the currentday and the range of the diagram in days,
  //depending on which curves are switched on
  private void setStartEndDate() {
    m_startDate = new GregorianCalendar();

    m_startDate.setTime( HOVerwaltung.instance().getModel().getBasics().getDatum());
    m_endDate = (GregorianCalendar)m_startDate.clone();

    Curve curve = null;
    for( int i = 0; i < m_Curves.size(); i++) {
      curve = m_Curves.get(i);
      if( curve.first() && curve.next() && curve.getDate().before(m_startDate.getTime()))
        m_startDate.setTime( curve.getDate());
      if( curve.last() && curve.getDate().after(m_endDate.getTime()))
        m_endDate.setTime( curve.getDate());
    }

    // This can really happen!
    if(!m_startDate.before(m_endDate)) {
      m_startDate.add( Calendar.DAY_OF_YEAR, -7);
    }
    m_endDate.add( Calendar.DAY_OF_YEAR, 7);

    //calculate days to display
    m_iDaysToDisplay = 0;
    if( m_startDate.get( Calendar.YEAR) != m_endDate.get( Calendar.YEAR)) {
      m_iDaysToDisplay = 365;
      if( m_startDate.isLeapYear(m_startDate.get( Calendar.YEAR)))
        m_iDaysToDisplay++;
    }
    m_iDaysToDisplay += m_endDate.get( Calendar.DAY_OF_YEAR) - m_startDate.get( Calendar.DAY_OF_YEAR);

    //calculate days from start to today
    m_iTodayPosition = 0;
    GregorianCalendar today = new GregorianCalendar();
    today.setTime( HOVerwaltung.instance().getModel().getBasics().getDatum());
    if( m_startDate.get( Calendar.YEAR) != today.get( Calendar.YEAR)) {
      m_iTodayPosition = 365;
      if(m_startDate.isLeapYear(m_startDate.get( Calendar.YEAR)))
        m_iTodayPosition++;
    }
    m_iTodayPosition += today.get( Calendar.DAY_OF_YEAR) - m_startDate.get( Calendar.DAY_OF_YEAR);
  }

  //Calculate longest string at y-axis
  private double getMaxTextWidth( Graphics2D graphics2d) {
    Font font = new Font( "SansSerif", 1, UserParameter.instance().schriftGroesse);
    HelperWrapper ihelper = HelperWrapper.instance();

    FontRenderContext fontrendercontext = graphics2d.getFontRenderContext();
    Rectangle2D rectangle2d = null;
    double maxWidth = 0D;
    for( int i = 0; i < m_dValues; i++) {
      rectangle2d = ( new TextLayout( TeamSpirit.toString( i), font, fontrendercontext)).getBounds();
      if(rectangle2d.getWidth() > maxWidth) maxWidth = rectangle2d.getWidth();
      rectangle2d = ( new TextLayout( TeamConfidence.toString( i), font, fontrendercontext)).getBounds();
      if(rectangle2d.getWidth() > maxWidth) maxWidth = rectangle2d.getWidth();
    }
    return maxWidth;
	}

}