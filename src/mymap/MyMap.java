package mymap;

import java.io.File;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;



import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.CoordinateFormatter;
import javafx.geometry.Point2D;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import java.util.List;
//import java.util.concurrent.ExecutionException;

import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
//jdbc
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
//import java.io.IOException;
//import javafx.geometry.Pos;
import javafx.scene.control.Label;
//import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
//import javafx.scene.layout.Pane;

public class MyMap extends Application {

    private MapView mapView;
    
    
     private static GraphicsOverlay graphicsOverlay;
     private ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics;
     
    @Override
    public void start(Stage MyStage) throws Exception {
        StackPane pane = new StackPane();
        Scene sc = new Scene(pane);
       
        ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        
        mapView = new MapView();
        mapView.setMap(map);

        File shapefile = new File("C:\\Users\\Shashank\\Documents\\ArcGIS\\M_final.shp");
        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(shapefile.getAbsolutePath());
        
        SpatialReference sr = SpatialReferences.getWgs84();
       
       
        // use the shapefile feature table to create a feature layer
        FeatureLayer featureLayer = new FeatureLayer(shapefileFeatureTable);
        featureLayer.addDoneLoadingListener(() -> {
            if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                // zoom to the area containing the layer's features
                mapView.setViewpointGeometryAsync(featureLayer.getFullExtent());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, featureLayer.getLoadError().getMessage());
                alert.show();
            }
        });
        
        
        graphicsOverlay = new GraphicsOverlay();
       
          mapView.addSpatialReferenceChangedListener(src -> createPoints(sr)); 
        
         createPoints(sr);
         
         mapView.setOnMouseClicked(e -> {
        if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {
           
          // create a point from location clicked
          Point2D mapViewPoint = new Point2D(e.getX(), e.getY());
          
          
          Point pn = mapView.screenToLocation(mapViewPoint);
          
          // identify graphics on the graphics overlay
          identifyGraphics = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, mapViewPoint, 10, false);
            
          identifyGraphics.addDoneListener(() -> createGraphicDialog(pn));
        }
      });
         
         
        // add the feature layer to the map
        //map.getOperationalLayers().add(featureLayer);
         map.getOperationalLayers().add(featureLayer);
          mapView.getGraphicsOverlays().add(graphicsOverlay);
        pane.getChildren().addAll(mapView);
        

        MyStage.setTitle("Interactive tree/shrub map of NIIT University");
        MyStage.setWidth(800);
        MyStage.setHeight(700);
        MyStage.setScene(sc);
        MyStage.show();
    }

     public static void createPoints(SpatialReference SPATIAL_REFERENCE) {

    // create a red (0xFFFF0000) circle simple marker symbol
    PictureMarkerSymbol redCircleSymbol = new PictureMarkerSymbol("https://vignette.wikia.nocookie.net/frontierville/images/4/41/Oak_Tree_Large-icon.png/revision/latest?cb=20100902001634");
    redCircleSymbol.setHeight(25);
    redCircleSymbol.setWidth(25);

    // create graphics and add to graphics overlay
    Graphic graphic;
    
    int i=0;
    try{
        Class.forName("com.mysql.jdbc.Driver");
	Connection con1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/map","root","pandu1089");
        Statement st1 = con1.createStatement();
        Statement st2 = con1.createStatement();
        ResultSet rs = st1.executeQuery("SELECT xcord,ycord from info");
        List<Double> lsx = new ArrayList<>();
        List<Double> lsy = new ArrayList<>();
        ResultSet rs2 = st2.executeQuery("SELECT COUNT(xcord) from info");
        rs2.next();
        int y = rs2.getInt(1);
        System.out.println(y);
        int count=1;
        rs.next();
        while(count<=y)
                {
                   double dx = Double.valueOf(rs.getString(1));
                   double dy = Double.valueOf(rs.getString(2));
                    lsx.add(dx);
                    lsy.add(dy);
                    rs.next();
                    count++;
                }
        for(i=0;i<lsx.size();i++)
        {
            System.out.println(lsx.get(i));
        }
        
        for(i=0;i<lsx.size();i++)
        {
            //System.out.println(lsx.get(i)+ " " + lsy.get(i));
            graphic = new Graphic(new Point(lsx.get(i),lsy.get(i), SPATIAL_REFERENCE), redCircleSymbol);
            graphicsOverlay.getGraphics().add(graphic);
        }
       
            
    }
        catch(Exception e){
                e.printStackTrace();
                }

  }
     public String px = "";
     public String py = "";
     
     //Popup Stage
    
     public Stage popup = new Stage();
     public Stage popup2 = new Stage();
     public GridPane gpop = new GridPane();
     public Scene scp = new Scene(gpop,300,520);
     public GridPane gpop2 = new GridPane();
     public Scene scp2 = new Scene(gpop2,500,330);
     public Stage popup3 = new Stage();
     public GridPane gpop3 = new GridPane();
     public Scene scp3 = new Scene(gpop3,800,600);
     
     public void createGraphicDialog(Point p) {

    try {
      // get the list of graphics returned by identify
      IdentifyGraphicsOverlayResult result = identifyGraphics.get();
      List<Graphic> graphics = result.getGraphics();
           Graphic g = graphics.get(0);
          Point p0 = (Point)g.getGeometry();
         
          px = String.valueOf(p0.getX());
          py = String.valueOf(p0.getY());
          if(px.length()<=8){
              px += "0";
          }
          if(py.length()<=8){
              py += "0";
          }
          System.out.println(px.substring(0, 9));
          System.out.println(py.substring(0, 9));
       //String latLonDecimalDegrees = CoordinateFormatter.toLatitudeLongitude(p0, CoordinateFormatter
        //.LatitudeLongitudeFormat.DECIMAL_DEGREES, 6);
        
       
      if (!graphics.isEmpty()) 
      {
        Class.forName("com.mysql.jdbc.Driver");
	Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/map","root","pandu1089");
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM INFO WHERE xcord='"+ px.substring(0, 9) +"' and ycord='"+py.substring(0, 9) +"'");
        rs.next();  
        gpop.getChildren().clear();
        String imag = rs.getString("img");
        String imag2 = rs.getString("image");
      //  System.out.println(imag);
        
        //Image imge = new Image("C:\\Users\\Shashank\\Desktop\\R&D_images\\image_1043.jpg");
        
           ImageView imgv = new ImageView(new File(imag).toURI().toString());
           
         imgv.setFitHeight(200);
         imgv.setFitWidth(200);
         ImageView imgv2;
         if(imag2.length()<10){
              imgv2 = new ImageView("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/300px-No_image_available.svg.png");
         }
         else{
              imgv2 = new ImageView(new File(imag2).toURI().toString());
         }
         imgv2.setFitHeight(200);
         imgv2.setFitWidth(200);
         
         ImageView imgv3 = new ImageView(new File(imag).toURI().toString());
         imgv3.setFitHeight(200);
         imgv3.setFitWidth(200);
         
         Button uploadB = new Button("Update");
        
        uploadB.setOnAction(new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event){
			
            FileChooser fc = new FileChooser();
           FileChooser.ExtensionFilter ext1 = new FileChooser.ExtensionFilter("JPG files(*.jpg)","*.JPG");
           FileChooser.ExtensionFilter ext2 = new FileChooser.ExtensionFilter("PNG files(*.png)","*.PNG");
           fc.getExtensionFilters().addAll(ext1,ext2);
           File file = fc.showOpenDialog(popup3);
          System.out.println(file);
          String str = "";
      boolean bool = false;
      
      try {
    
         bool = file.exists();
         if(bool) {
         str = file.toString();
      System.out.println("pathname string: "+str);
         }
       
                   PreparedStatement ps = con.prepareStatement("update info set image = ? where xcord='"+ px.substring(0, 9) +"' and ycord='"+py.substring(0, 9) +"'");
                   ps.setString(1,str);
                 
                   int status = ps.executeUpdate();
                   if(status>0)
                   {
                        Alert alert = new Alert(AlertType.INFORMATION);
                          alert.setTitle("Information Dialog");
                          alert.setHeaderText("Information dialog");
                          alert.setContentText("Photo saved successfully");
                          alert.showAndWait();
                          
                   }
                   else
                   {
                          Alert alert = new Alert(AlertType.ERROR);
                          alert.setTitle("Error Dialog.");
                          alert.setHeaderText("Error Information");
                          alert.showAndWait();
                   }
                 
            } catch (SQLException ex) {
               ex.printStackTrace();
            }
               
                                        }
        });
         
         Label l1 = new Label("Scientific Name : "+rs.getString("sci_name"));
         //System.out.print(l1);
         Label l2 = new Label("Category : "+rs.getString("category"));
         Label l3 = new Label("Uses : "+rs.getString("uses"));
         Label l4 = new Label("When Planted");
         Label l5 = new Label("Current");
         Label l7 = new Label("Planted by : ");
         Label l8 = new Label("Date :");
         Label l6 = new Label("        ");
          l3.setWrapText(true);
          l3.setStyle("-fx-padding: 10;" );
          l2.setStyle("-fx-padding: 10;" );
           Button timeline = new Button("Timeline");    
          // timeline.setOnAction(e -> popup.setScene(scp2));
          
          timeline.setOnAction(new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event){
						
						popup2.show();
					}
				});
          l1.setStyle("-fx-padding: 10 ;" );
            l1.setTextFill(Color.web("#0076a3"));  
            l2.setTextFill(Color.web("#0076a3"));   
            l3.setTextFill(Color.web("#0076a3"));
             l4.setStyle("-fx-padding: 10 20 20 60;" );
            l4.setTextFill(Color.web("#0076a3")); 
             l5.setStyle("-fx-padding: 10 20 20 60;" );
            l5.setTextFill(Color.web("#0076a3")); 
             l7.setStyle("-fx-padding: 10 ;" );
            l7.setTextFill(Color.web("#0076a3"));  
             l8.setStyle("-fx-padding: 10 ;" );
            l8.setTextFill(Color.web("#0076a3"));  
            timeline.setStyle("-fx-padding: 10 ;" );
            gpop.setStyle("-fx-padding: 10 20 10 30;" + 
                      "-fx-border-style: solid inside;" + 
                      "-fx-border-width: 2;" +
                      "-fx-border-insets: 5;" + 
                      "-fx-border-radius: 5;" + 
                      "-fx-border-color: black;");
            gpop2.setStyle("-fx-padding: 30 20 10 30;" + 
                      "-fx-border-style: solid inside;" + 
                      "-fx-border-width: 2;" +
                      "-fx-border-insets: 5;" + 
                      "-fx-border-radius: 5;" + 
                      "-fx-border-color: black;");
            imgv2.setStyle("-fx-padding:10");
            imgv3.setStyle("-fx-padding:10");
         GridPane.setConstraints(imgv,2,1);
         GridPane.setConstraints(l1,2,3);
         GridPane.setConstraints(l2,2,5);
         GridPane.setConstraints(l3,2,11);
         GridPane.setConstraints(l7,2,7);
         GridPane.setConstraints(l8,2,9);
         GridPane.setConstraints(timeline,2,18);
        
         
         GridPane.setConstraints(imgv2,2,1);
        GridPane.setConstraints(imgv3,4,1);
        GridPane.setConstraints(l4,2,2);
          GridPane.setConstraints(l5,4,2);
          GridPane.setConstraints(l6,3,2);
          GridPane.setConstraints(uploadB,2,3);
          
          gpop.getChildren().add(imgv);
         gpop.getChildren().add(l1);
         gpop.getChildren().add(l2);
         gpop.getChildren().add(l3);
         gpop.getChildren().add(l7);
         gpop.getChildren().add(l8);
         gpop.getChildren().add(timeline);
       
        gpop2.getChildren().add(imgv2);
        gpop2.getChildren().add(imgv3);
        gpop2.getChildren().add(l4);
        gpop2.getChildren().add(l5);
          gpop2.getChildren().add(l6);
          gpop2.getChildren().add(uploadB);
      
         popup.setScene(scp);
      
         popup.setTitle("INFO");
         popup2.setScene(scp2);
         popup2.setTitle("Timeline");
         
         popup.show();
         
      }
 
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
    
    
    @Override
    public void stop() throws Exception {

        if (mapView != null) {
            mapView.dispose();
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
       
    }

}
