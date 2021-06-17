/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Devin
 */
public class msa2aces extends Application {
    
        private Desktop desktop = Desktop.getDesktop();    
    
    	/**
         * This method was written by CC and modified by DJC, based upon code from StackOverflow user Kip.
         * It takes a string and writes it to a specified file, appending said file.
         * The purpose of this method is to save information and results as they are generated, reducing memory load.
         * 
         * @param content The string to be written
         * @param file The destination file for the string output
         */
	private void writeFileN(String content, File file){
                try (FileWriter fw = new FileWriter(file, true)) {
                    content += "" + System.lineSeparator();
                    fw.write(content);//

                } catch (IOException ex) 
                    {
		     Logger.getLogger(msa2aces.class.getName()).log(Level.SEVERE, null, ex);
		    }
		         
            }        

	private void writeFile(String content, File file){
                try (FileWriter fw = new FileWriter(file, true)) {
                    fw.write(content);//

                } catch (IOException ex) 
                    {
		     Logger.getLogger(msa2aces.class.getName()).log(Level.SEVERE, null, ex);
		    }
		         
            }        
        
	/**
         * This method was written by CC
         * 
         * @param file The desired file to be opened.
         */
        private void openFile(File file) {
            try {
		 desktop.open(file);
		} catch (IOException ex) {
		Logger.getLogger(
                 msa2aces.class.getName()).log(
                 Level.SEVERE, null, ex
                 );
		 }
            }
        
        // Configures the File Chooser, from Oracle Documentation Example 26-5
	private static void configureFileChooser(
            final FileChooser fileChooser) {      
		fileChooser.setTitle("View Files");
		fileChooser.setInitialDirectory(
			new File(System.getProperty("user.dir"))
			);                 
		fileChooser.getExtensionFilters().addAll(
		new FileChooser.ExtensionFilter("Plain Text", "*.txt"),
		new FileChooser.ExtensionFilter("FASTA", "*.fasta"),
		new FileChooser.ExtensionFilter("Rich Text Format", "*.rtf"),
		new FileChooser.ExtensionFilter("All Files", "*.*")
		);
            }        

     /**
     * A timestamp, used for generating unique file IDs
     */			 
    final long timeUnique = System.currentTimeMillis();
    public String dirName = Long.toString(timeUnique);
    public String filePath = "";    
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Generate Co-evolution Constraints");

	// Create Grid pane, FileChooser, and Button
        final GridPane inputGridPane = new GridPane();		
        final FileChooser fileChooser = new FileChooser();	        
        final Button processingButton = new Button("Open MSA File");	
        final Button idButton = new Button("Generate New Job ID");	
 
 		// Text Fields and Labels. Some of this framework contributed by Christopher Camenares
		Label lbl1 = new Label("Job ID#:");
		lbl1.setMinHeight(50);
		lbl1.setMinWidth(250);                 
                
		Label lbl2 = new Label("Awaiting File Selection");
		lbl2.setMinHeight(50);
		lbl2.setMinWidth(100);               
		
		TextField jobID = new TextField();
		jobID.setText(dirName);
		jobID.setMinHeight(50);
		jobID.setMinWidth(200);                  

		CheckBox chck1;
		chck1 = new CheckBox("Debugging Mode");            
                
                final ToggleGroup outputChoice = new ToggleGroup();
                
		RadioButton rb1 = new RadioButton();
                rb1.setText("Folder named job ID");
                rb1.setSelected(true);
                rb1.setToggleGroup(outputChoice);
                rb1.setUserData("id");
                
		RadioButton rb2 = new RadioButton();
                rb2.setText("Common output folder");
                rb2.setToggleGroup(outputChoice);
                rb2.setUserData("common");
                
       		inputGridPane.add(lbl1, 0, 0);
       		inputGridPane.add(jobID, 0, 1);
       		inputGridPane.add(idButton, 0, 2);
                inputGridPane.add(rb1, 0, 3);
                inputGridPane.add(rb2, 0, 4);                
                inputGridPane.add(chck1, 0, 6);                 
       		inputGridPane.add(lbl2, 0, 7);
       		inputGridPane.add(processingButton, 0, 8);
        
        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));
 
        /**
         * Defines button action: generates new job ID
         */
        idButton.setOnAction(
			new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(final ActionEvent e) {
						final long timeButton = System.currentTimeMillis();                   
						jobID.setText(Long.toString(timeButton));
                    }
                }
            );
       
        
       processingButton.setOnAction(
	new EventHandler<ActionEvent>() {
         @Override
          public void handle(final ActionEvent e) {
           File file1 = fileChooser.showOpenDialog(primaryStage);
                
// Begin processing of file!!

            if (file1 != null) {
             filePath = file1.getPath();
             
             final long timeStart = System.currentTimeMillis();
             
             boolean debugModeOn = chck1.isSelected();
             
             // Get new name for directory, initialize directory
             String jobIDtxt = jobID.getText();
             String dirName = "";
             if(outputChoice.getSelectedToggle().getUserData().toString().equals("common")){
                 dirName = "output";
             }
             else
             {
                 dirName = jobIDtxt;
             }

               File dir = new File(dirName);
               dir.mkdir();        

             //Initalize final files
             /**
              * Recipient file for runtime and processing information
              */
               File runTime = new File(dirName + "\\Runtime_Info_" + jobIDtxt + ".txt");                                          

//             File debugLog = new File(dirName + "\\Debug_log_" + jobIDtxt + ".txt");

               File resultF = new File(dirName + "\\Results_" + jobIDtxt + ".txt");                                                         
             
             boolean startup = false;
             boolean buildSeq = false;

             String aminoAcids = "M, C, D, E, K, R, N, Q, T, S, H, P, G, W, F, Y, V, A, I, L, -, X, O";
             String[] aaS = aminoAcids.split(", ");
             
             int[][] msaData = new int[aaS.length][];
             int seqCounter = 0;
             int seqLength = 0;
             
             try (BufferedReader br = new BufferedReader(new FileReader(file1))) {
              String line;
              String sequence = "";
              while ((line = br.readLine()) != null) {

                  if(line.contains(">") && !buildSeq)
                  {
                      startup = true;
                      buildSeq = true;
                      seqCounter++;
//System.out.println("Let's Begin!");                        
                      continue;
                  }
                  else if(line.contains(">") && buildSeq && !startup)
                  {
//System.out.println("Previous sequence: " + sequence);
//System.out.println("Another Sequence Added");                         
                    buildUpArray(sequence, aaS, msaData);
                    seqCounter++;                    
                    sequence = "";
//System.out.println(printArray(msaData));
                    
                  }                  
                  else if(line.contains(">") && startup && buildSeq)
                  {
//System.out.println("Previous sequence: " + sequence);
                      seqLength = sequence.length();
                      int[] seqSize = new int[seqLength];
                      seqCounter++;
                      
                      for(int i = 0; i < seqSize.length; i++)
                      {
                          seqSize[i] = 0;
                      }
                      
                      for(int i = 0 ; i < msaData.length; i++)
                      {
                          msaData[i] = seqSize;
                      }
//System.out.println(printArray(msaData));
//System.out.println("First Sequence Done!");  
                      buildUpArray(sequence, aaS, msaData);   
//System.out.println(printArray(msaData));                      
                      startup = false;
                      sequence = "";
                  }                  
                  else if(!line.contains(">") && buildSeq)
                  {
                      sequence += line;
                  }
                            
              }
//System.out.println("Previous sequence: " + sequence);              
             } 
             catch (FileNotFoundException ex) {
              Logger.getLogger(msa2aces.class.getName()).log(Level.SEVERE, null, ex);
             } 
             catch (IOException ex) {
              Logger.getLogger(msa2aces.class.getName()).log(Level.SEVERE, null, ex);
             }                              

             /*
             Code to run after all the information has been collected; Run through each organism, make sequence and print
             
             The code to follow uses several steps of logical to select a particular residue identity
             It starts by looping through all organisms
             */

//System.out.println(printArray(msaData));
//System.out.println(seqCounter + " sequences of " + seqLength + " residues each");

            /*
            This variable is an array that contains the conservation amount for each residue in the sequence. This is what will be formatted and ultimately reported as constraints.
            */
            int[] conservation = consCalc(msaData, seqCounter, seqLength);
            String middle = ", #, #, ";
            String end = ", 0";
                    
            for (int i = 0; i < conservation.length; i++)
            {
                writeFileN((i+1) + middle + conservation[i] + end, resultF);
            }
            
//System.out.println(printArray(conservation));

		final long timeEnd = System.currentTimeMillis();
		long runMinutesL = (timeEnd - timeStart) / 60000;
		int runMinutesI = (int) runMinutesL;             


                writeFileN("Job ID#: " + jobIDtxt, runTime);
                writeFileN("Input file " + filePath, runTime);                
		writeFileN(Long.toString(timeEnd - timeStart) + " milliseconds of processing time", runTime);
                writeFileN("- or about " + Integer.toString(runMinutesI) + " minutes", runTime);                

                openFile(dir);
                
                System.out.println("Done!");
            }
          }
        });
        
        Scene scene = new Scene(rootGroup, 400, 400);
        
        primaryStage.setTitle("MSA 2 aCES");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }   
    
    /**
     * Checks to see if the input string is an integer - uses it if it is good and within range, otherwise it will choose randomly according to boundary set.
     * @param input
     * @param mol1
     * @return 
     */
    public int checkInteger(String input, int upperLimit, int lowerLimit)
     {
        int value = 0;
        double rando = Math.random();
        double newValue = (upperLimit - lowerLimit) * rando; 
        
        try
        {
         value = Integer.parseInt(input);  
          if(value < upperLimit && value > lowerLimit)
           {             
            return value;
           }
          else
           {
            value = (int)newValue;
            value += lowerLimit;         
            return value;             
           }
        }
        catch(Exception e)
        {
         value = (int)newValue;
         value += lowerLimit;         
         return value;
        }

    }    
    
    public String printArray(int[][] array)
    {
        String res = "";
        for (int i = 0; i < array.length; i++)
        {
           res += printArray(array[i]);
           res += System.lineSeparator();
        }
        res += "***";        
        return res;
    }
    
    public void printArray(String[] array)
    {
            for (int j = 0; j < array.length; j++)
            {
                System.out.print(array[j] + "\t");
            }
                System.out.println("***");        
    }

    public String printArray(int[] array)
    {
            String res = "";
            int sum1 = 0;
            for (int j = 0; j < array.length; j++)
            {
                res += array[j];
                res += "\t";
                sum1 += array[j];
            }
                res += "* ";
                res += sum1;
            return res;
    }
    
    public boolean logicXOR(boolean x, boolean y)
    {
        return ((x || y) && !(x && y));
    }

    public double distance3d(double[] a, double[] b)
    {
        return distance3d(a[0], a[1], a[2], b[0], b[1], b[2]);
    }    
    
    public double distance3d(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2) + Math.pow(y1-y2, 2));
    }
    
    public double average (double[] array)
    {
        double sum = 0;
        for (int i = 0; i < array.length; i++)
        {
            sum += array[i];
        }
        return sum/array.length;   
    }

    public double average (double[][] array)
    {
        double sum = 0.0;
        int counter = 0;
        for (int i = 0; i < array.length; i++)
        {
            for (int j = 0; j < array[i].length; j++)
            {
             sum += array[i][j];
             counter ++;
            }

        }
        return sum/counter;   
    }    
    
    public double average (ArrayList<Double> array)
    {
        double sum = 0;
        for (int i = 0; i < array.size(); i++)
        {
            sum += array.get(i);
        }
        return sum/array.size();   
    }
    
    public double[][] arraySubtract (double[][] array, double minus)
    {
        for (int i = 0; i < array.length; i++){
            for (int j = 0; j < array[i].length; j++){
                array[i][j] -= minus;
                if(array[i][j] < 0.0)
                {
                    array[i][j] = 0.0;
                }
            }
        }
        return array;
    }
    
    public double findMax (double[][] array)
    {
        double result = 0.0;
        for (int i = 0; i < array.length; i++){
            for (int j = 0; j < array[i].length; j++){
                result = Math.max(result, array[i][j]);
            }
        }
        return result;
    }

    public int findMax (int[] array)
    {
        int result = 0;
        for (int i = 0; i < array.length; i++)
        {
                result = Math.max(result, array[i]);
        }
        return result;
    }
    
    public double[][] normalize (double[][] array)
    {
        double maxNum = findMax(array);
        for (int i = 0; i < array.length; i++){
            for (int j = 0; j < array[i].length; j++){
                array[i][j] /= maxNum;
            }
        }
        return array;       
    }
    
    public static int[] getColumn(int[][] arr, int column)
    {
        ArrayList<Integer> temp = new ArrayList<>();
        
        for (int i = 0; i < arr.length; i++)
        {
            temp.add(arr[i][column]);
        }
        
        int[] res = new int[temp.size()];
        
        for (int i = 0; i < temp.size(); i++)
        {
            res[i] = temp.get(i);
        }
        
        return res;
    }
    
    public static int[][] buildUpArray(String seq, String[] cat, int[][] data)
    {

            for (int i = 0; i < data.length; i++)
             {
                 int[] thisOne = findChar(cat[i], seq);
                 data[i] = addArrays(data[i], thisOne);
             }           

        return data;
    }
    
    public static int[] findChar(String cat, String seq)
    {
        int[] res = new int[seq.length()];
        
        for (int j = 0; j < seq.length(); j++)
        {
                boolean matchFound = (seq.charAt(j) == cat.charAt(0));

                if(matchFound)
                 {                   
                  res[j]++;
                 }        
        }
        return res;
    }
    
    public static int[] addArrays(int[] a1, int[] a2)
    {
        int[] a3 = new int[a1.length];
        for (int i = 0; i < a1.length; i++)
        {
            a3[i] = a1[i] + a2[i];
        }
        return a3;
    }
    
    public int[][] transposeArray(int[][] data)
    {
        int[][] res = new int[data[0].length][data.length];

        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[i].length; j++)
            {
                res[j][i] += data[i][j];
            }
        }
        
        return res;
    }
    
    public int[] consCalc(int[][] data, int total, int seqLength)
    {
        int[] res = new int[seqLength];
              
        for (int i = 0; i < res.length; i++)
        {
            int[] thisColumn = getColumn(data, i);
            int maxValue = findMax(thisColumn);
            
            res[i] = (int)((100*maxValue)/total);
        }
        
        return res;
    }
    
}
