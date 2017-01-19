/**
 * VHX a screen pixel ruler - free to use
 * @author Mel Tearle - December, 2016
 * 
 * 1. Rounded down both the height and width to the nearest 100 and
 *    centered them only if full size.
 * 2. Set the upper limit for both rulers based on the upper most offset
 *    of the vertical ruler as it's almost the same height as the menu/program
 *    bar.
 * 3. Set a 50 pixel buffer for keeping rulers on the screen and positioned
 *    the ruler as close to the cursor as possible. 
 * 
 **/
package vhx;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;

public class vhx extends Application {
    
    Stage stage;
    
    Scene scene1;
    Scene scene2;
    
    StackPane horizontalPane;
    StackPane verticalPane;

    double xOffset;
    double yOffset;
    
    double currentWidth;    
    double currentHeight;  
 
    boolean isHorizontal = true; // -- default
        
    final double Fixed = 50;  // -- used as fixed width or height
    final double MinRulerSize = 200;
    
    // -- On a Mac, Cmd-H hides the current window
    // -- Cmd-Tab and Tab to restore the java app
    
    final KeyCombination altH = new KeyCodeCombination( KeyCode.H,
                                    KeyCombination.ALT_DOWN );
    
    final KeyCombination ctlH = new KeyCodeCombination( KeyCode.H,
                                    KeyCombination.CONTROL_DOWN );
    
    final KeyCombination metaH = new KeyCodeCombination( KeyCode.H,
                                     KeyCombination.META_DOWN ); 
    
    final KeyCombination altV = new KeyCodeCombination( KeyCode.V,
                                    KeyCombination.ALT_DOWN );
    
    final KeyCombination ctlV = new KeyCodeCombination( KeyCode.V,
                                    KeyCombination.CONTROL_DOWN ); 
        
    final KeyCombination metaV = new KeyCodeCombination( KeyCode.V,
                                     KeyCombination.META_DOWN ); 
    
    final KeyCombination altX = new KeyCodeCombination( KeyCode.X,
                                    KeyCombination.ALT_DOWN ); 
    
    final KeyCombination ctlX = new KeyCodeCombination( KeyCode.X,
                                    KeyCombination.CONTROL_DOWN );
    
    final KeyCombination metaX = new KeyCodeCombination( KeyCode.X,
                                     KeyCombination.META_DOWN ); 
    
    final Toolkit toolkit = Toolkit.getDefaultToolkit();
  
    final Dimension screenSize = toolkit.getScreenSize();

    final double ScreenHeight = ( int )screenSize.getHeight();
    final double ScreenWidth  = ( int )screenSize.getWidth();  
    
    // -- give the ruler some space so it isn't touching the screen's edge
    final double MaxRulerHeight = ( int )( ScreenHeight / 100 ) * 100;  
    final double MaxRulerWidth  = ( int )( ScreenWidth / 100 ) * 100; 
    
    final double Vbuff =  ( ScreenHeight - MaxRulerHeight ) *.50;
    
  public static void main( String[] args ) {
         Application.launch( args );
  }

  @Override 
  public void start( final Stage Primary ) {   
    stage = Primary;
 
    // -- changes thruout
    currentWidth  = MaxRulerWidth;    
    currentHeight = MaxRulerHeight;
 
    // -- default on startup
    horizontalPane = new StackPane();
    horizontalPane.getChildren().add( makeHorizontal( MaxRulerWidth ) );
 
    scene1 = new Scene( horizontalPane, MaxRulerWidth, Fixed ); 
  
    stage.setScene( scene1 );

    stage.initStyle( StageStyle.TRANSPARENT );    
    stage.setAlwaysOnTop( true );
    stage.setOpacity( .75 );
    stage.setX( ( ScreenWidth - MaxRulerWidth ) *.50 );
    stage.setY( MouseInfo.getPointerInfo().getLocation().y - ( Fixed *.50 ) );
    stage.show();
   
    // -- setup vertical       
    verticalPane = new StackPane();
    scene2 = new Scene( verticalPane, Fixed, MaxRulerHeight );
  
/////////////////////////// mouse events /////////////////////////////
 stage.addEventFilter( MouseEvent.MOUSE_PRESSED, me -> {
    xOffset = me.getSceneX();
    yOffset = me.getSceneY();
 });
 
 horizontalPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, me ->  {  
    stage.setX( constrain( me.getScreenX() - xOffset, currentWidth, 
                ScreenWidth, Fixed ) );  
    if ( me.getScreenY()- yOffset >= Vbuff ) {  
         stage.setY( constrain( me.getScreenY() - yOffset, currentHeight, 
                     ScreenHeight, Fixed ) );     
    }
    else {
         stage.setY( Vbuff );  // -- same distance as vertical from top
    }
    me.consume();
 });

 verticalPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, me -> {
    stage.setY( constrain( me.getScreenY() - yOffset, currentHeight, 
               ScreenHeight, Fixed ) );  
    if ( me.getScreenX()- xOffset >= 0 ) {
         stage.setX( constrain( me.getScreenX() - xOffset, 
                     currentWidth, ScreenWidth, Fixed ) );
    }
    else {
         stage.setX( 0 );   
    }
    me.consume();
 }); 
 
 stage.addEventFilter( MouseEvent.MOUSE_CLICKED, me -> {
    if ( me.getButton().equals( MouseButton.PRIMARY ) && 
         me.getClickCount() == 2 ) {
         Platform.exit();
    }   
 });   // end mouse events 

//////////////////////////// key events /////////////////////////////    
 stage.addEventHandler( KeyEvent.KEY_PRESSED, key -> {          
    switch( key.getCode().toString() )  { // -- handle VHX keystrokes 
      case "H": 
        setFixedWidth( MaxRulerWidth );
        break;                 
      case "V":
        setFixedHeight( MaxRulerHeight );
        break;           
      case "X": 
      case "ESCAPE":     
        Platform.exit();
        break;
      default:  
        // System.out.println( key.getCode().toString() );
        break;
    }    
    key.consume(); 
 });  

 stage.addEventFilter( KeyEvent.KEY_PRESSED, key -> {
    if ( altH.match( key ) || metaH.match( key ) || // watch for metaH on Mac 
              ctlH.match( key ) ) {
              setFixedWidth( MaxRulerWidth *.50 );   
              key.consume();
    }
    else if ( altV.match( key ) || metaV.match( key ) || 
              ctlV.match( key ) ) {                
              setFixedHeight( MaxRulerHeight *.50 );
              key.consume();
    }    
    else if ( ctlX.match( key ) || metaX.match( key ) || 
              altX.match( key ) ) {
              key.consume();
    }
 });  // end handle alt-keys
 
 stage.addEventHandler( KeyEvent.KEY_PRESSED, key -> { 
    if ( isHorizontal ) {
         if ( "MINUS".equals( key.getCode().toString() ) ) {
               setZoomWidth( .93 );
         } else if ( "EQUALS".equals( key.getCode().toString() ) ) {
               setZoomWidth( 1.08 );
         }  
   key.consume();     
   }
 });
 
 stage.addEventHandler( KeyEvent.KEY_PRESSED, key -> { 
    if ( !isHorizontal ) {    
         if ( "MINUS".equals( key.getCode().toString() ) ) {
               setZoomHeight( .94 );
         } else if ( "EQUALS".equals(key.getCode().toString() ) ) {          
               setZoomHeight( 1.08 );
         }    
    key.consume();    
    }
 });  

 stage.addEventHandler( KeyEvent.KEY_PRESSED, key -> {
    if ( key.getCode() == KeyCode.LEFT ) {
         key.consume(); 
         if ( isHorizontal && currentWidth - 10 >= MinRulerSize ) { 
              setFixedWidth( currentWidth - 10 );
         }  
         else if ( !isHorizontal && currentHeight - 10 >= MinRulerSize ){
              setFixedHeight( currentHeight - 10 );
         }
    }
 });  // arrow left
 
 stage.addEventHandler( KeyEvent.KEY_PRESSED, key -> {
    if ( key.getCode() == KeyCode.RIGHT ) {
         key.consume(); 
         if ( isHorizontal && currentWidth + 10 <= MaxRulerWidth ) {
               setFixedWidth( currentWidth + 10 );
         }  
         else if ( !isHorizontal && currentHeight + 10 <= MaxRulerHeight ) {
               setFixedHeight( currentHeight + 10 );
         }    
    }
  });  // arrow right
 
///////////////////////////// touchpad events ///////////////////////////  
 horizontalPane.setOnZoom( e -> { 
    e.consume(); 
      if ( currentWidth * e.getZoomFactor() > MinRulerSize && 
           currentWidth * e.getZoomFactor() < MaxRulerWidth + 5.0 ) {  // pad it   
           setZoomWidth( e.getZoomFactor() );
      }  
 });

 verticalPane.setOnZoom( e -> {  
    e.consume(); 
      if ( currentHeight * e.getZoomFactor() > MinRulerSize && 
           currentHeight * e.getZoomFactor() < MaxRulerHeight + 5.0  ) {  
           setZoomHeight( e.getZoomFactor() );
      }  
 });   // end touchpad zooms 
        
 }   // -- end start -- //
 
/////////////////////////////// constrain ///////////////////////////////
 public double constrain( double drag, double current, double screen, 
                          double buffer )   {
    if ( drag > 0 && drag > ( screen - buffer ) ) {
         return screen - buffer;
    }
    else if ( drag < 0 && drag < ( buffer - current ) )  {
         return buffer - current; 
    }
    else {
         return drag;
    }
 }  // constrain
 
//////////////////////////////////////////////////////////////////////////
 public void setFixedWidth( double width )  {
//////////////////////////////////////////////////////////////////////////
    if ( !isHorizontal ) {
         verticalPane.getChildren().clear();
         isHorizontal = true;
    }
    currentWidth = width;
     
    setHorizontal();
 }
    
///////////////////////////////////////////////////////////////////////
 public void setZoomWidth( double zoom )  {
///////////////////////////////////////////////////////////////////////
    currentWidth = currentWidth * zoom;
     
    if ( currentWidth > MaxRulerWidth ) {
         currentWidth = MaxRulerWidth;
    }
    else if ( currentWidth < MinRulerSize )  {
         currentWidth = MinRulerSize;     
    }
    else {         // -- round down by 10 to make it even
         currentWidth = ( int )( currentWidth / 10 ) * 10; 
    }

    setHorizontal();
 } 
 
//////////////////////////////////////////////////////////////////////////
 public void setHorizontal()  {
//////////////////////////////////////////////////////////////////////////    
    double x = MouseInfo.getPointerInfo().getLocation().x;
    double y = MouseInfo.getPointerInfo().getLocation().y - ( Fixed *.50 );            
    
    if ( currentWidth == MaxRulerWidth ) { 
         x = ( ScreenWidth - MaxRulerWidth ) *.50;
    }
    else {
         x = x - ( currentWidth *.50 );
    }          
    
    // -- alternate edge handling  
    if ( x < Fixed && currentWidth != MaxRulerWidth )  { 
         x = Fixed;
    } else if ( x + currentWidth > ScreenWidth ) {        
         x = ( ScreenWidth - currentWidth ) - Fixed;
    } 
    
    // -- adjust y as well from going off screen
    if ( y + Fixed > ScreenHeight ) { 
         y = ScreenHeight - Fixed;
    }                   
    else if ( y < Vbuff || y + Vbuff < Vbuff )  {  
         y = Vbuff;
    }
     
    stage.setWidth( currentWidth );
    stage.setHeight( Fixed ); 
    stage.setScene( scene1 );
    stage.setX( x );
    stage.setY( y );
    
    horizontalPane.getChildren().add( makeHorizontal( currentWidth ) );     
 }

//////////////////////// add a ruler - horizontal ///////////////////////
 public Group makeHorizontal( double width )  {
//////////////////////////////////////////////////////////////////////////
    Group g = new Group(); 
    // -- locate 50 pixel mark closest to center
    int Xoff = ( int )( ( ( width + 50 ) / 50 ) / 2 ) * 50 ;  
      
    Rectangle ruler = new Rectangle( 0, 0, width, Fixed );  
    ruler.setFill( Color.CHARTREUSE );
    ruler.toBack(); 
    
    g.getChildren().add( ruler );   
    // -- screen width and height
    Text hxt = new Text( String.format( "%5d", (int)ScreenWidth ) );
    hxt.setFont( Font.font( "Sans Serif", 12 ) );

    hxt.setFill( Color.BLACK );
    hxt.setLayoutX( Xoff - 35 );  
    hxt.setLayoutY( 30 );
    
    hxt.toFront();
    g.getChildren().add( hxt );
    
    Text vxt = new Text( String.format( "%-5d", (int)ScreenHeight ) );
    vxt.setFont( Font.font(  "Sans Serif", 12 ) );

    vxt.setFill( Color.BLACK );
    vxt.setLayoutX( Xoff + 2 ); 
    vxt.setLayoutY( 30 );
             
    vxt.toFront();
    g.getChildren().add( vxt );
    
    // -- draw ruler lines - use total width to display last number
    for ( int i = 10; i <= width; i += 10 )  {
          if ( i % 50 == 0 ) {   // -- at fifty pixels
               Line line = new Line();
               line.setStartX( i );
               line.setEndX( i );
               line.setStartY( 5 );   // -- assumes Fixed pixel height/width
               line.setEndY( 45 );
               
               line.setFill( Color.BLACK );
               line.toFront();

               Text txt = new Text();
               txt.setFont( Font.font( "Sans Serif", 12 ) );

               txt.setFill( Color.BLACK );
               txt.setLayoutX( i - 35 ); 
               txt.setLayoutY( 45 );

               txt.setText( String.format( "%5d", i ) );
               txt.toFront();

               g.getChildren().addAll( line, txt );
          }
          if ( i % 10 == 0 && i % 50 != 0 ) {  // -- at 10 pixels
               Line line = new Line();
               line.setStartX( i );
               line.setEndX( i );
               line.setStartY( 5 );  // -- assumes Fixed pixel height/width
               line.setEndY( 15 );
               
               line.setFill( Color.BLACK );
               line.toBack();
               
               g.getChildren().add( line );               
          }    
    }
    return g;
  }  // add horz
 
//////////////////////////////////////////////////////////////////////////
 public void setFixedHeight( double height )  {
//////////////////////////////////////////////////////////////////////////
    if ( isHorizontal ) {     
         horizontalPane.getChildren().clear();
         isHorizontal  = false;
    }        
    currentHeight = height;

    setVertical();
 }
 
////////////////////////////////////////////////////////////////////////
 public void setZoomHeight( double zoom )  {
////////////////////////////////////////////////////////////////////////  
    currentHeight = currentHeight * zoom;    
    
    if ( currentHeight > MaxRulerHeight ) {
         currentHeight = MaxRulerHeight;
    }
    else if ( currentHeight < MinRulerSize )  {
         currentHeight = MinRulerSize;
    }    
    else {               // -- round down by 10
         currentHeight = ( int )( currentHeight / 10 ) * 10; 
    }
    
    setVertical();
 } 

////////////////////////////////////////////////////////////////////////
 public void setVertical()  {
///////////////////////////////////////////////////////////////////////
    double x = MouseInfo.getPointerInfo().getLocation().x - ( Fixed *.50 ); 
    double y = MouseInfo.getPointerInfo().getLocation().y;
  
    if ( currentHeight == MaxRulerHeight ) { 
         y = ( ScreenHeight - MaxRulerHeight ) *.50;
    } 
    else {
         y = y - ( currentHeight *.50 );  
    }    
    
    // -- alternate edge handling  
    if ( y < Fixed && currentHeight != MaxRulerHeight )  { 
         y = Fixed;
    } else if ( y + currentHeight > ScreenHeight ) {        
         y = ( ScreenHeight - currentHeight ) - Fixed;
    } 
        
    // -- take menu bar into account
    if ( y + currentHeight > ScreenHeight - Vbuff ) {   
         y = ( ScreenHeight - currentHeight ) - Vbuff;
    } 
      
    // -- adjust x from going off screen
    if ( x + Fixed > ScreenWidth ) { 
         x = ScreenWidth - Fixed;
    }                                    
    else if ( x < 0 ) {  
         x = 0;
    }
    
    stage.setWidth( Fixed );
    stage.setHeight( currentHeight ); 
    stage.setScene( scene2 );
    stage.setX( x ); 
    stage.setY( y );  

    verticalPane.getChildren().add( makeVertical( currentHeight ) );
 }
     
//////////////////////////// add a ruler ////////////////////////////////
 public Group makeVertical( double height )  {
/////////////////////////////////////////////////////////////////////////     
    Group g = new Group();
    
    Rectangle ruler = new Rectangle( 0, 0, Fixed, height );  
    ruler.setFill( Color.CHARTREUSE );
    ruler.toBack(); 

    g.getChildren().add( ruler );    

    for ( int i = 10; i <= height; i += 10 )  {
          if ( i % 50 == 0 )  {
               Line line = new Line();
               line.setStartX( 5 );
               line.setEndX( 45 );
               line.setStartY( i );  // -- assumes Fixed pixel height/width
               line.setEndY( i );
               
               line.setFill( Color.BLACK );
               line.toFront();

               Text txt = new Text();
               txt.setFont( Font.font( "Sans Serif", 12 ) );
 
               txt.setFill( Color.BLACK );
               txt.setLayoutX( 15 ); 
               txt.setLayoutY( i - 3 );

               txt.setText( String.format( "%5d", i ) );
               txt.toFront();

               g.getChildren().addAll( line, txt );
          }
          if ( i % 10 == 0 && i % 50 != 0 ) {   
               Line line = new Line();
               line.setStartX( 5 );
               line.setEndX( 15 );
               line.setStartY( i );
               line.setEndY( i );
               
               line.setFill( Color.BLACK );
               line.toBack();
      
               g.getChildren().add( line );
          }
    }
    return g;  
 }  //  add vert
       
} //////////////////////////////// that's all ////////////////////////